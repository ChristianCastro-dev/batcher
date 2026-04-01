/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package com.mycompany.batcher;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 *
 * @author Christian
 */
public class Batcher {

    private static final int TOTAL_CORES = 4;
    private static final int TOTAL_MEMORIA_MB = 2048;

    private static int coresUsados = 0;
    private static int memoriaUsada = 0;
    private static long inicioBatcherMs;

    private static final Deque<Job> colaReady = new ArrayDeque<Job>();
    private static final Deque<Job> colaWaiting = new ArrayDeque<Job>();
    private static final List<Job> colaRunning = new ArrayList<Job>();
    private static final List<Job> colaDone = new ArrayList<Job>();
    private static final List<Job> colaFailed = new ArrayList<Job>();

    // Inicializa, clasifica los jobs y refresca el monitor hasta que termine.
    public static void main(String[] args) throws InterruptedException {
        inicioBatcherMs = System.currentTimeMillis();

        List<Job> todos = LectorYAML.cargarJobs("jobs");

        for (int i = 0; i < todos.size(); i++) {
            Job job = todos.get(i);
            long ahora = System.currentTimeMillis();
            job.setCreadoEnMs(ahora);

            if (puedeEntrarAlSistema(job)) {
                job.setEstado(Job.Estado.READY);
                job.setEntroReadyEnMs(ahora);
                colaReady.addLast(job);
            } else {
                job.setEstado(Job.Estado.WAITING);
                colaWaiting.addLast(job);
            }
        }

        mostrarEstadoEnPantalla();

        while (quedaAlgoPorHacer()) {
            moverWaitingAReady();
            lanzarTodosLosQuePuedanAhora();
            recogerLosQueYaTerminaron();
            mostrarEstadoEnPantalla();
            Thread.sleep(1000);
        }
    }

    // Comprueba si el job cabe en la capacidad total del sistema.
    private static boolean puedeEntrarAlSistema(Job job) {
        return job.getCpuCores() <= TOTAL_CORES && job.getMemMB() <= TOTAL_MEMORIA_MB;
    }

    // Comprueba si el job puede arrancar ahora mismo con los recursos libres actuales.
    private static boolean puedeEjecutar(Job job) {
        return (coresUsados + job.getCpuCores() <= TOTAL_CORES)
                && (memoriaUsada + job.getMemMB() <= TOTAL_MEMORIA_MB);
    }

    // Reserva CPU y memoria para un job que pasa a RUNNING.
    private static synchronized void reservarRecursos(Job job) {
        coresUsados = coresUsados + job.getCpuCores();
        memoriaUsada = memoriaUsada + job.getMemMB();
    }

    // Devuelve al pool los recursos usados por un job terminado o fallido.
    private static synchronized void liberarRecursos(Job job) {
        coresUsados = coresUsados - job.getCpuCores();
        memoriaUsada = memoriaUsada - job.getMemMB();
    }

    // Reintenta mover jobs de WAITING a READY cuando el sistema puede aceptarlos.
    private static void moverWaitingAReady() {
        int jobsEnEspera = colaWaiting.size();

        for (int i = 0; i < jobsEnEspera; i++) {
            Job job = colaWaiting.pollFirst();
            if (job == null) {
                return;
            }

            if (puedeEntrarAlSistema(job)) {
                job.setEstado(Job.Estado.READY);
                if (job.getEntroReadyEnMs() == 0L) {
                    job.setEntroReadyEnMs(System.currentTimeMillis());
                }
                colaReady.addLast(job);
            } else {
                colaWaiting.addLast(job);
            }
        }
    }

    // Lanza todos los jobs READY que caben con los recursos disponibles en este ciclo.
    private static void lanzarTodosLosQuePuedanAhora() {
        int jobsPendientes = colaReady.size();

        for (int i = 0; i < jobsPendientes; i++) {
            Job job = colaReady.pollFirst();
            if (job == null) {
                return;
            }

            if (!puedeEjecutar(job)) {
                colaReady.addLast(job);
                continue;
            }

            job.setEstado(Job.Estado.RUNNING);
            job.setInicioEjecucionMs(System.currentTimeMillis());
            colaRunning.add(job);
            reservarRecursos(job);

            try {
                ProcessBuilder pb = new ProcessBuilder(
                        "java",
                        "-cp",
                        System.getProperty("java.class.path"),
                        JobsManager.class.getName(),
                        String.valueOf(job.getDuracionMs())
                );
                Process proceso = pb.start();
                job.setProceso(proceso);
            } catch (IOException e) {
                job.setEstado(Job.Estado.FAILED);
                job.setFinEjecucionMs(System.currentTimeMillis());
                colaRunning.remove(job);
                colaFailed.add(job);
                liberarRecursos(job);
            }
        }
    }

    // Revisa los procesos hijos y los mueve a DONE o FAILED cuando terminan.
    private static void recogerLosQueYaTerminaron() {
        for (int i = 0; i < colaRunning.size(); i++) {
            Job job = colaRunning.get(i);
            Process proceso = job.getProceso();

            if (proceso != null && !proceso.isAlive()) {
                colaRunning.remove(i);
                i--;
                job.setFinEjecucionMs(System.currentTimeMillis());

                if (proceso.exitValue() == 0) {
                    job.setEstado(Job.Estado.DONE);
                    colaDone.add(job);
                } else {
                    job.setEstado(Job.Estado.FAILED);
                    colaFailed.add(job);
                }

                liberarRecursos(job);
            }
        }
    }

    // Indica si todavia quedan jobs pendientes de ejecutar o finalizar.
    private static boolean quedaAlgoPorHacer() {
        return !colaReady.isEmpty() || !colaWaiting.isEmpty() || !colaRunning.isEmpty();
    }

    // Renderiza el monitor de estado de una forma simple.
    private static void mostrarEstadoEnPantalla() {
        System.out.println();
        System.out.println("================================");
        System.out.println("Estado del batcher");
        System.out.println("Tiempo: " + formatearDuracion(System.currentTimeMillis() - inicioBatcherMs));
        System.out.println("CPU: " + coresUsados + "/" + TOTAL_CORES
                + "  RAM: " + memoriaUsada + "/" + TOTAL_MEMORIA_MB + " MB");
        System.out.println("--------------------------------");
        System.out.println("READY: " + formatearCola(colaReady, false));
        System.out.println("WAITING: " + formatearCola(colaWaiting, false));
        System.out.println("DONE: " + formatearCola(colaDone, true));
        System.out.println("FAILED: " + formatearCola(colaFailed, true));
        System.out.println("--------------------------------");
        System.out.println("RUNNING:");

        if (colaRunning.isEmpty()) {
            System.out.println("  ninguno");
        } else {
            for (int i = 0; i < colaRunning.size(); i++) {
                System.out.println("  " + formatearRunning(colaRunning.get(i)));
            }
        }

        System.out.println("================================");
    }

    // Formatea una cola mostrando el job completo o solo su id si esta finalizado, se le pasa Iterable ya que asi nos sirve para List y Deque.
    private static String formatearCola(Iterable<Job> cola, boolean soloId) {
        StringBuilder sb = new StringBuilder("[");
        boolean primero = true;
        // Si no es el primero de los jobs, se le añade una ,
        for (Job job : cola) {
            if (!primero) {
                sb.append(", ");
            }
            if (soloId) {
                sb.append(job.getId());
            } else {
                sb.append(job);
            }
            primero = false;
        }

        sb.append("]");
        return sb.toString();
    }

    // Construye una linea para cada job en RUNNING.
    private static String formatearRunning(Job job) {
        long ahora = System.currentTimeMillis();
        long pid = 0L;
        if (job.getProceso() != null) {
            pid = job.getProceso().pid();
        }

        long esperaMs = Math.max(0L, job.getInicioEjecucionMs() - job.getCreadoEnMs());
        long ejecucionMs = Math.max(0L, ahora - job.getInicioEjecucionMs());

        int progreso = 0;
        if (job.getDuracionMs() > 0) {
            progreso = (int) Math.min(100L, (ejecucionMs * 100L) / job.getDuracionMs());
        }

        return job.getId()
                + " pid=" + pid
                + " prio=" + job.getPrioridad()
                + " cpu=" + job.getCpuCores()
                + " mem=" + job.getMemMB() + "MB"
                + " progreso=" + progreso + "%"
                + " espera=" + formatearDuracion(esperaMs)
                + " ejec=" + formatearDuracion(ejecucionMs);
    }

    // Convierte milisegundos al formato HH:mm:ss.
    private static String formatearDuracion(long duracionMs) {
        long totalSegundos = Math.max(0L, duracionMs / 1000L);
        long horas = totalSegundos / 3600L;
        long minutos = (totalSegundos % 3600L) / 60L;
        long segundos = totalSegundos % 60L;

        String horasTexto = String.format("%02d", horas);
        String minutosTexto = String.format("%02d", minutos);
        String segundosTexto = String.format("%02d", segundos);

        return horasTexto + ":" + minutosTexto + ":" + segundosTexto;
    }
}
