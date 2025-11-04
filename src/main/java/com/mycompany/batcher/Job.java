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
    private Recursos recursos= new Recursos();
    private CargaTrabajo tiempoCarga = new CargaTrabajo();

    private enum Estado {
        NEW, READY, WAITING, RUNNING, DONE, FAILED
    };
    private Estado estado;


    public Job() {
    this.id = 0;
    this.nombre = "";
    this.prioridad = 0;
    recursos.getCpu_cores();
    recursos.getMemoria();
    tiempoCarga.getDuracion_ms();

    this.estado = Estado.NEW;

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

    
    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    


    
}
