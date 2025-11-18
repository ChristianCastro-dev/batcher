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
class CargaTrabajo {
    @JsonProperty("duration_ms")
    private int duracion_ms;

    

    public int getDuracion_ms() {
        return duracion_ms;
    }

    public void setDuracion_ms(int duracion_ms) {
        this.duracion_ms = duracion_ms;
    }
    
    
}
