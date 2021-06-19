/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Objetos;

import java.io.Serializable;

/**
 *
 * @author pablo
 */
public class DatosSemana implements Serializable{
    
    public String region;
    public int semana;
    public int muertes;
    public int infectados;
    public int altas;
    

    public DatosSemana(String region, int semana, int muertes, int infectados, int altas) {
        this.region = region;
        this.semana = semana;
        this.muertes = muertes;
        this.infectados = infectados;
        this.altas = altas;
    }

    public DatosSemana() {
    }

    public DatosSemana(String region, int semana) {
        this.semana = semana;
        this.region = region;
    }   
    
    
}
