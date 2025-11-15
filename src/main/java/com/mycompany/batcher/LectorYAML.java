/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.batcher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
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
        if(archivos == null){
            return listaJobs;
        }
        
        for (File f : archivos) {
            if(f.toString().endsWith(".yaml")|| f.toString().endsWith(".yml")){
                try{
                    // Configurar maper de yaml
                    
                    ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
                    Job jobLeido = mapper.readValue(f, Job.class);
                    
                    
                    // Pendiente comprobar si os traballos son validos
                    
                    if(jobLeido.getId() == null || jobLeido.getId().isBlank()){
                        System.out.println("Id vacio");
                    }
                    if(jobLeido.getNombre() == null || jobLeido.getNombre().isBlank()){
                        System.out.println("Nombre vacio");
                    }
                    if(jobLeido.getPrioridad() <0 || jobLeido.getPrioridad()>4){
                        System.out.println("Prioridad fuera de rango, se establece 0 por ir de listo");
                        jobLeido.setPrioridad(0);
                    }
                    if(jobLeido.getRecursos().getCpu_cores()<1){
                        jobLeido.setCpuCores(1);
                    }
                    if(jobLeido.getTiempoCarga().getDuracion_ms()<=0){
                        System.out.println("job con tiempo de carga menor que 0");
                    }
                    jobLeido.normalizar();
                    jobLeido.setEstado(Job.Estado.NEW);
                    listaJobs.add(jobLeido);
                    
                    System.out.println("Jobs cargados de " + f.getName());
                    
                }catch(Exception e){
                    System.out.println("Error" + e.getMessage());
                }
            }
        }

        return listaJobs;
    }
}
