/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package com.mycompany.batcher;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 *
 * @author Christian
 */
public class Batcher {

    // Recursos totales del sistema
    private static final int TOTAL_CORES = 4;
    private static final int TOTAL_MEMORIA_MB = 2048;

    // Recursos usados en este momento
    private static int coresUsados = 0;
    private static int memoriaUsada = 0;

    // Las colas synchronized ya que fallaban
    private static final List<Job> colaReady = Collections.synchronizedList(new ArrayList<Job>());
    private static final List<Job> colaRunning = Collections.synchronizedList(new ArrayList<Job>());
    private static final List<Job> colaDone = Collections.synchronizedList(new ArrayList<Job>());
    private static final List<Job> colaFailed = Collections.synchronizedList(new ArrayList<Job>());

    public static void main(String[] args) throws InterruptedException {

        System.out.println("Arrancando el BATCHER )\n");

        // 1. Cargar todos los jobs
        List<Job> todos = LectorYAML.cargarJobs("jobs");

        // 2. Meter en READY 
        for (int i = 0; i < todos.size(); i++) {
            Job job = todos.get(i);
            if (puedeEjecutar(job)) {

                job.setEstado(Job.Estado.READY);
                colaReady.add(job);
                System.out.println("Job " + job.getId() + " metido en READY");
            } else {
                System.out.println("Job " + job.getId() + " no cabe");
            }
        }
        // Se muestra el inicio
        mostrarEstadoEnPantalla();

        // 3. Se lanzan
        while (quedaAlgoPorHacer()) {

            lanzarTodosLosQuePuedanAhora();
            recogerLosQueYaTerminaron();
            mostrarEstadoEnPantalla();

            Thread.sleep(1000); // esperamos 1 segundo
        }

        System.out.println("TODOS LOS JOBS HAN TERMINADO");
    }

    // Hay recursos
    private static boolean puedeEjecutar(Job job) {
        return (coresUsados + job.getCpuCores() <= TOTAL_CORES)
                && (memoriaUsada + job.getMemMB() <= TOTAL_MEMORIA_MB);
    }

    // Reservar recursos 
    private static synchronized void reservarRecursos(Job job) {
        coresUsados = coresUsados + job.getCpuCores();
        memoriaUsada = memoriaUsada + job.getMemMB();
    }

    // Liberar cuando termina
    private static synchronized void liberarRecursos(Job job) {
        coresUsados = coresUsados - job.getCpuCores();
        memoriaUsada = memoriaUsada - job.getMemMB();
    }

    // Lanzar todos los jobs de  READY
    private static void lanzarTodosLosQuePuedanAhora() {

        for (int i = 0; i < colaReady.size(); i++) {
            Job job = colaReady.get(i);

            if (puedeEjecutar(job)) {

                colaReady.remove(i);
                i--;

                job.setEstado(Job.Estado.RUNNING);
                colaRunning.add(job);
                reservarRecursos(job);

                try {
                    ProcessBuilder pb = new ProcessBuilder(
                            "java",
                            "-cp",
                            System.getProperty("java.class.path"),
                            "JobsManager",
                            String.valueOf(job.getDuracionMs())
                    );
                    Process proceso = pb.start();
                    job.setProceso(proceso);

                    System.out.println("LANZADO -> " + job.getId() + " (dura " + job.getDuracionMs() + " ms)");
                } catch (IOException e) {
                    System.out.println("No pude lanzar el job " + job.getId());
                }
            }
        }
    }

    // Recoger procesos
    private static void recogerLosQueYaTerminaron() {
        for (int i = 0; i < colaRunning.size(); i++) {
            Job job = colaRunning.get(i);
            Process p = job.getProceso();

            if (p != null && !p.isAlive()) {
                // Ya terminó
                colaRunning.remove(i);
                i--;

                if (p.exitValue() == 0) {
                    job.setEstado(Job.Estado.DONE);
                    colaDone.add(job);
                    System.out.println("TERMINADO -> " + job.getId());
                } else {
                    job.setEstado(Job.Estado.FAILED);
                    colaFailed.add(job);
                    System.out.println("FALLÓ -> " + job.getId());
                }
                liberarRecursos(job);
            }
        }
    }

    // 
    private static boolean quedaAlgoPorHacer() {
        if (!colaReady.isEmpty()) {
            return true;
        }
        if (!colaRunning.isEmpty()) {
            return true;
        }

        // algún proceso sigue vivo
        for (int i = 0; i < colaRunning.size(); i++) {
            Process p = colaRunning.get(i).getProceso();
            if (p != null && p.isAlive()) {
                return true;
            }
        }
        return false;
    }

    // Mostrar el estado 
    private static void mostrarEstadoEnPantalla() {
        System.out.println("\n+-------------------------------+");
        System.out.println("       ESTADO DEL BATCHER       ");
        System.out.println("+-------------------------------+");
        System.out.println(" CPU  : " + coresUsados + " / " + TOTAL_CORES + " núcleos");
        System.out.println(" RAM  : " + memoriaUsada + " / " + TOTAL_MEMORIA_MB + " MB");
        System.out.println("+-------------------------------+");
        System.out.println(" READY   : " + colaReady.size() + " jobs → " + colaReady);
        System.out.println(" RUNNING : " + colaRunning.size() + " jobs" + colaRunning);
        System.out.println(" DONE    : " + colaDone.size() + " jobs" + colaDone);
        System.out.println(" FAILED  : " + colaFailed.size() + " jobs" + colaFailed);
        System.out.println("+-------------------------------+\n");
    }
}
