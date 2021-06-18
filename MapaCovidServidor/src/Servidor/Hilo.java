/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Servidor;


import BaseDeDatos.ConexionBaseDeDatos;
import Objetos.UsuarioRegistro;
import Utiles.Constantes;
import static Utiles.Utiles.enviarObjeto;
import static Utiles.Utiles.recibirObjeto;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.SealedObject;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author pablo
 */
public class Hilo extends Thread{
    public Socket cliente;
    public boolean comunicacion;
    public ConexionBaseDeDatos conexion;

    public Hilo(Socket cliente) {
        this.cliente = cliente;
        this.comunicacion = true;
        this.conexion = new ConexionBaseDeDatos();
    }

    public Socket getCliente() {
        return cliente;
    }

    public void setCliente(Socket cliente) {
        this.cliente = cliente;
    }
    
    
    
    
    @Override
    public void run(){
        try {
            enviarObjeto(cliente, Constantes.claveSimetrica);
            while (comunicacion){
                boolean login = (boolean) recibirObjeto(this.cliente);
                    if(login){
                        boolean tipoDeCuenta = (boolean) recibirObjeto(this.cliente);
                        if (tipoDeCuenta){
                            if (realizarlogin()){
                                lista();
                            }
                            
                        }else{
                                lista();
                        }
                    } else{
                        registro();
                    }
                System.out.println("Fin");
            }
         } catch (IOException ex) {
            Logger.getLogger(Hilo.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Hilo.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(Hilo.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(Hilo.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }
    
    
    public boolean realizarlogin() throws IOException, ClassNotFoundException, SQLException, Exception{
        boolean existeUsuario = false;
        SealedObject usuarioEncrptado=  (SealedObject) recibirObjeto(this.cliente);
        String claveCodificada = Utiles.Constantes.claveSimetrica;
        byte[] decodedKey = Base64.getDecoder().decode(claveCodificada);
        SecretKey claveSecreta = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES"); 
        UsuarioRegistro usuarioLogin = (UsuarioRegistro) Utiles.Utiles.desencriptarSimetrico(usuarioEncrptado, claveSecreta);
        this.conexion.establecerConexion();
        boolean Existe = comprobarLogin(usuarioLogin);
        if (Existe){
            this.conexion.cerrarConexion();
            existeUsuario = true;
        } else{
            this.conexion.cerrarConexion();
        }
        return existeUsuario;
    }
    
    
    public void lista(){
        
    }
    
    
    public void registro() throws IOException, ClassNotFoundException, SQLException{
        UsuarioRegistro usuarioNuevo = (UsuarioRegistro) recibirObjeto(this.cliente);
        this.conexion.establecerConexion();
        boolean Existe = comprobarUsuario(usuarioNuevo);
        if (Existe){
            this.conexion.cerrarConexion();
            enviarObjeto(cliente, false);
        } else{
            String[] claves = Utiles.Utiles.generarClaves();
            String email = usuarioNuevo.email;
            String nombre = usuarioNuevo.nombre;
            String contraseña = usuarioNuevo.contraseña;
            int rol = usuarioNuevo.rol;
            String clavePublica = claves[0];
            String clavePrivada = claves[1];
            this.conexion.insertarUsuario(email, nombre, contraseña, rol, clavePublica, clavePrivada);
            this.conexion.cerrarConexion();
            enviarObjeto(cliente, true);
        }
    }
    
    private boolean comprobarLogin (UsuarioRegistro usuarioNuevo) throws SQLException, IOException {
       boolean existe = false;
       String email = usuarioNuevo.getEmail();
       String contraseña = usuarioNuevo.getContraseña();
       String where = "email = '"+email+"' AND contraseña ='"+contraseña+"' AND activo = 1"; 
     
       if(conexion.hayUsuarioLogin(where, cliente)){
           existe = true;
       }
       return existe; 
    }
    

    private boolean comprobarUsuario(UsuarioRegistro usuarioNuevo) throws SQLException {
       boolean existe = false;
       String email = usuarioNuevo.getEmail();
       String nombre = usuarioNuevo.getNombre();
       String where = "email = '"+email+"'";
       if(conexion.hayUsuario(where)){
           existe = true;
       } else {
           where = "nombre_usuario = '"+nombre+"'";
           if(conexion.hayUsuario(where)){
               existe = true;
           }
       }
       return existe; 
    }
    
}
