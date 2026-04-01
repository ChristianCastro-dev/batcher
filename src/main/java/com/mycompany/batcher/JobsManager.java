/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.batcher;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Christian
 */
public class JobsManager {
    List<Job> listaReady = new ArrayList<>();
    List<Job> listaWaiting = new ArrayList<>();
    List<Job> listaRunning = new ArrayList<>();



    public List<Job> getListaReady() {
        return listaReady;
    }

    public void setListaReady(List<Job> listaReady) {
        this.listaReady = listaReady;
    }

    public List<Job> getListaWaiting() {
        return listaWaiting;
    }

    public void añadirJobReady(Job job){
        listaReady.add(job);
    }
    public void añadirJobWaiting(Job job){
        listaWaiting.add(job);
    }
    public void añadirJobRunning(Job job){
        listaRunning.add(job);
    }
    
    public static void main(String[] args){
        try {
            Thread.sleep(Long.parseLong(args[0]));
        } catch (InterruptedException ex) {
            System.getLogger(JobsManager.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }
}
