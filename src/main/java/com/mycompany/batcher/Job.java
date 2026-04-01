/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.batcher;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author Christian
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Job {

    private String id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("priority")
    private int prioridad;
    @JsonProperty("cpu_cores")
    private int cpuCores;

    private int memMB;
    private int duracionMs;
    @JsonProperty("resources")
    private Recursos recursos = new Recursos();
    @JsonProperty("workload")
    private CargaTrabajo tiempoCarga = new CargaTrabajo();
    private Estado estado = Estado.NEW;
    private Process proceso;
    private long creadoEnMs;
    private long entroReadyEnMs;
    private long inicioEjecucionMs;
    private long finEjecucionMs;

    public enum Estado {
        NEW, READY, WAITING, RUNNING, DONE, FAILED
    };

    // Copia a campos simples los valores leídos desde las secciones anidadas del YAML.
    public void normalizar() {
        this.cpuCores = recursos.getCpu_cores();
        this.memMB = transformarMemoria(recursos.getMemoria());
        this.duracionMs = tiempoCarga.getDuracion_ms();
    }

    // Convierte cadenas como "256 MB" o "2 GB" a megabytes enteros.
    public int transformarMemoria(String memoria) {
        memoria = memoria.trim().toUpperCase();
        if (memoria.endsWith("GB")) {
            return Integer.parseInt(memoria.replace("GB", "").trim()) * 1024;
        } else if (memoria.endsWith("MB")) {
            return Integer.parseInt(memoria.replace("MB", "").trim());
        } else {
            throw new IllegalArgumentException("Memoria distina de GB e MB " + memoria);
        }
    }

    public Process getProceso() {
        return proceso;
    }

    public void setProceso(Process proceso) {
        this.proceso = proceso;
    }

    public long getCreadoEnMs() {
        return creadoEnMs;
    }

    public void setCreadoEnMs(long creadoEnMs) {
        this.creadoEnMs = creadoEnMs;
    }

    public long getEntroReadyEnMs() {
        return entroReadyEnMs;
    }

    public void setEntroReadyEnMs(long entroReadyEnMs) {
        this.entroReadyEnMs = entroReadyEnMs;
    }

    public long getInicioEjecucionMs() {
        return inicioEjecucionMs;
    }

    public void setInicioEjecucionMs(long inicioEjecucionMs) {
        this.inicioEjecucionMs = inicioEjecucionMs;
    }

    public long getFinEjecucionMs() {
        return finEjecucionMs;
    }

    public void setFinEjecucionMs(long finEjecucionMs) {
        this.finEjecucionMs = finEjecucionMs;
    }

    public int getCpuCores() {
        return cpuCores;
    }

    public void setCpuCores(int cpuCores) {
        this.cpuCores = cpuCores;
    }

    public int getMemMB() {
        return memMB;
    }

    public void setMemMB(int memMB) {
        this.memMB = memMB;
    }

    public int getDuracionMs() {
        return duracionMs;
    }

    public void setDuracionMs(int duracionMs) {
        this.duracionMs = duracionMs;
    }

    public Recursos getRecursos() {
        return recursos;
    }

    public void setRecursos(Recursos recursos) {
        this.recursos = recursos;
    }

    public CargaTrabajo getTiempoCarga() {
        return tiempoCarga;
    }

    public void setTiempoCarga(CargaTrabajo tiempoCarga) {
        this.tiempoCarga = tiempoCarga;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return name;
    }

    public void setNombre(String nombre) {
        this.name = nombre;
    }

    public int getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(int prioridad) {
        this.prioridad = prioridad;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return id + "(p" + prioridad + "," + cpuCores + "c," + memMB + "MB)";
    }

}
