/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.batcher;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author Christian
 */
class Recursos {
    
    
    private int cpu_cores;
    @JsonProperty("memory")
    private String memoria;


    

    public int getCpu_cores() {
        return cpu_cores;
    }

    public void setCpu_cores(int cpu_cores) {
        this.cpu_cores = cpu_cores;
    }

    public String getMemoria() {
        return memoria;
    }

    public void setMemoria(String memoria) {
        this.memoria = memoria;
    }
    
}
