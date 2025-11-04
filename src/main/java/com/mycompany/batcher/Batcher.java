/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.batcher;

import java.util.List;

/**
 *
 * @author Christian
 */
public class Batcher {

    public static void main(String[] args) {
        List<Job> listaJobs = LectorYAML.cargarJobs("jobs");
        
        for (Job job : listaJobs) {
            System.out.println("Job cargado: " + job);
        }
    }
}
