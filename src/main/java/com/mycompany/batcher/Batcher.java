/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package com.mycompany.batcher;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Christian
 */
public class Batcher {

    public static int totalCores = 4;
    public static int totalMemoryMb = 2048;

    public static int coresUsados = 0;
    public static int memoriaUsada = 0;

    public static List<Job> colaReady = new ArrayList<>();
    public static List<Job> colaRunning = new ArrayList<>();
    public static List<Job> colaDone = new ArrayList<>();

    public static void main(String[] args) throws InterruptedException {
        // 1. Cargar jobs
        List<Job> jobs = LectorYAML.cargarJobs("jobs");

        // 2. Poner en cola READY si caben
        for (Job job : jobs) {
            if (puedeEjecutar(job)) {
                //fago operacion de memoria e cpu
                asignarRecursos(job);
                colaReady.add(job);
                job.setEstado(Job.Estado.READY);
            } else {
                System.out.println("Non hay recursos para: " + job.getId());
            }
        }

        // 3. Ejecutar uno a uno (FCFS)
        while (!colaReady.isEmpty() || !colaRunning.isEmpty()) {
            ejecutarSiguiente();
            mostrarEstado();
            Thread.sleep(1000); // cada segundo
        }

        System.out.println("¡Todos los jobs terminados!");
    }

    public static boolean puedeEjecutar(Job job) {
        return coresUsados + job.getCpuCores() <= totalCores
                && memoriaUsada + job.getMemMB() <= totalMemoryMb;
    }

    public static void asignarRecursos(Job job) {
        coresUsados += job.getCpuCores();
        memoriaUsada += job.getMemMB();
    }

    public static void liberarRecursos(Job job) {
        coresUsados =coresUsados - job.getCpuCores();
        memoriaUsada = memoriaUsada- job.getMemMB();
    }

    public static void ejecutarSiguiente() {
        if (colaReady.isEmpty() || !colaRunning.isEmpty()) {
            return;
        }

        Job job = colaReady.remove(0);
        job.setEstado(Job.Estado.RUNNING);
        colaRunning.add(job);

        // Simular ejecución con Thread
        new Thread(() -> {
            try {
                Thread.sleep(job.getDuracionMs());
                job.setEstado(Job.Estado.DONE);
                colaRunning.remove(job);
                colaDone.add(job);
                liberarRecursos(job);
                System.out.println("Terminado: " + job.getId());
            } catch (Exception e) {
                job.setEstado(Job.Estado.FAILED);
            }
        }).start();
    }

    public static void mostrarEstado() {
        System.out.println("\n=== ESTADO DEL BATCHER ===");
        System.out.println("CPU: " + coresUsados + "/" + totalCores + " | RAM: " + memoriaUsada + "/" + totalMemoryMb + " MB");
        System.out.println("READY: " + colaReady);
        System.out.println("RUNNING: " + colaRunning);
        System.out.println("DONE: " + colaDone);
        System.out.println("==========================\n");
    }
}
