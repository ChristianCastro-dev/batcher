/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.batcher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Christian
 */
public class LectorYAML {

    public static List<Job> cargarJobs(String carpetaRaiz) {
        List<Job> listaJobs = new ArrayList<Job>();
        File carpeta = new File(carpetaRaiz);

        if (!carpeta.exists() || !carpeta.isDirectory()) {
            System.out.println("Carpeta no valida");
            return listaJobs;
        }

        File[] archivos = carpeta.listFiles();
        
        for (File f : archivos) {
            if(f.toString().endsWith(".yaml")){
                try{
                    // Configurar maper de yaml
                    
                    ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
                    
                    Job jobLeidos = mapper.readValue(f, Job.class);
                    
                    
                    // Pendiente comprobar si os traballos son validos
                    listaJobs.add(jobLeidos);
                    
                    System.out.println("Jobs cargados de " + f.getName());
                    
                }catch(Exception e){
                    System.out.println("Error" + e.getMessage());
                }
            }
        }

        return listaJobs;
    }
}
