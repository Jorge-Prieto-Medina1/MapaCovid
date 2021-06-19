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
public class UsuarioRegistro implements Serializable{
        public String email;
        public String nombre;
        public String contraseña;
        public int rol;
    
    
    public UsuarioRegistro(String email, String nombre, String contraseña, String rol) {
        this.email = email;
        this.nombre = nombre;
        this.contraseña = contraseña;
        if (rol.equals("Administrador")){
            this.rol= 0;
        }else{
            this.rol = 1;
        }
    }

    public UsuarioRegistro(String email, String contraseña) {
        this.email = email;
        this.contraseña = contraseña;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getContraseña() {
        return contraseña;
    }

    public void setContraseña(String contraseña) {
        this.contraseña = contraseña;
    }

    public int getRol() {
        return rol;
    }

    public void setRol(int rol) {
        this.rol = rol;
    }

 
    
}
