/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.batcher;

import java.security.Timestamp;

/**
 *
 * @author Christian
 */
public class Job {

    private int id;
    private String nombre;
    private int prioridad;
    private int cpuCores;
    private int memMB;
    private int duracionMs;

    private enum Estado {
        NEW, READY, WAITING, RUNNING, DONE, FAILED
    };
    private Estado estado;
    private Timestamp horaLectura;

    private Timestamp horaInicio;
    private Timestamp horaFin;

    public Job(int id, String nombre, int prioridad, int cpuCores, int memMB, int duracionMs, Timestamp horaLectura, Timestamp horaInicio) {
        this.id = id;
        this.nombre = nombre;
        this.prioridad = prioridad;
        this.cpuCores = cpuCores;
        this.memMB = memMB;
        this.duracionMs = duracionMs;
        this.horaLectura = horaLectura;
        this.horaInicio = horaInicio;
        this.estado = Estado.NEW;
        this.horaInicio = null;
        this.horaFin = null;
    }
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(int prioridad) {
        this.prioridad = prioridad;
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

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public Timestamp getHoraLectura() {
        return horaLectura;
    }

    public void setHoraLectura(Timestamp horaLectura) {
        this.horaLectura = horaLectura;
    }

    public Timestamp getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(Timestamp startTime) {
        this.horaInicio = startTime;
    }

    public Timestamp getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(Timestamp endTime) {
        this.horaFin = endTime;
    }
    


    
}
