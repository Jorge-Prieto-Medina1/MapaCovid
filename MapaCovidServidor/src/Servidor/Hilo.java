/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Servidor;


import BaseDeDatos.ConexionBaseDeDatos;
import Objetos.DatosSemana;
import Objetos.UsuarioRegistro;
import Utiles.Constantes;
import static Utiles.Utiles.enviarObjeto;
import static Utiles.Utiles.recibirObjeto;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.sql.SQLException;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
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
    public PublicKey claveCifrar;
    public PublicKey clavePublica;
    public PrivateKey clavePrivada;

    public Hilo(Socket cliente) throws NoSuchAlgorithmException {
        this.cliente = cliente;
        this.comunicacion = true;
        this.conexion = new ConexionBaseDeDatos();
        KeyPairGenerator generadorDeClave = KeyPairGenerator.getInstance("RSA");
        generadorDeClave.initialize(1024);
        KeyPair Par = generadorDeClave.generateKeyPair();
        this.clavePrivada = Par.getPrivate();
        this.clavePublica = Par.getPublic();
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
        
        while (comunicacion){
            try {
                int num = (int) Utiles.Utiles.recibirObjeto(cliente);
                switch (num) {
                    case 0:
                        verDatos();
                        break;
                        
                    case 1:
                        regiones();
                        break;
                        
                        
                    case 2:
                        modificarDatos();
                        break;
                        
                    case 3:
                        modificarUsuarios();
                        break;
                        
                    case 4:
                        comunicacion = false;
                        break;
                       
                }
            } catch (IOException ex) {
                Logger.getLogger(Hilo.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(Hilo.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public void regiones() throws IOException, ClassNotFoundException{
        this.claveCifrar = (PublicKey) Utiles.Utiles.recibirObjeto(cliente);
        System.out.println("Clave publica"+this.clavePublica);
        Utiles.Utiles.enviarObjeto(cliente, this.clavePublica);
        conexion.establecerConexion();
        conexion.enviarRegiones(cliente);
        boolean SeguirViendo = true;
        while (SeguirViendo){
            try {
                int num = (int) Utiles.Utiles.recibirObjeto(cliente);
                switch (num) {
                    case 0:
                        añadirRegion();
                        break;
                        
                    case 1:
                        eliminarRegion();
                        break;
                        
                    case 2:
                        conexion.cerrarConexion();
                        SeguirViendo = false;
                        break;
                }   } catch (IOException ex) {
                Logger.getLogger(Hilo.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(Hilo.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public void eliminarRegion(){
        try {
            SealedObject regionSealed = (SealedObject) Utiles.Utiles.recibirObjeto(cliente);
            String regionEliminar = (String) Utiles.Utiles.descifrar(clavePrivada, regionSealed);
            if(conexion.existeRegion(regionEliminar)){
                conexion.eliminarRegion(regionEliminar);
                Utiles.Utiles.enviarObjeto(cliente, true);
                conexion.enviarRegiones(cliente);
            }else{
                Utiles.Utiles.enviarObjeto(cliente, false);
            }
            } catch (IOException ex) {
            Logger.getLogger(Hilo.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Hilo.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(Hilo.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchPaddingException ex) {
            Logger.getLogger(Hilo.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidKeyException ex) {
            Logger.getLogger(Hilo.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalBlockSizeException ex) {
            Logger.getLogger(Hilo.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BadPaddingException ex) {
            Logger.getLogger(Hilo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
            
    
    public void añadirRegion(){
        try {
            SealedObject regionSealed = (SealedObject) Utiles.Utiles.recibirObjeto(cliente);
            String regionNueva = (String) Utiles.Utiles.descifrar(clavePrivada, regionSealed);
            if(!conexion.existeRegion(regionNueva)){
                conexion.añadirRegion(regionNueva);
                Utiles.Utiles.enviarObjeto(cliente, true);
                conexion.enviarRegiones(cliente);
            }else{
                Utiles.Utiles.enviarObjeto(cliente, false);
            }
            
            
            
        } catch (IOException ex) {
            Logger.getLogger(Hilo.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Hilo.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(Hilo.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchPaddingException ex) {
            Logger.getLogger(Hilo.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidKeyException ex) {
            Logger.getLogger(Hilo.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalBlockSizeException ex) {
            Logger.getLogger(Hilo.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BadPaddingException ex) {
            Logger.getLogger(Hilo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    
    public void verDatos(){
        boolean SeguirViendo = true;
        conexion.establecerConexion();
        conexion.enviarRegiones(cliente);
        while (SeguirViendo){
            try {
                int num = (int) Utiles.Utiles.recibirObjeto(cliente);
                switch (num) {
                    case 0:
                        consultarTodo();
                        break;
                        
                    case 1:
                        consultarPorDatos();
                        break;
                        
                        
                    case 3:
                        SeguirViendo = false;
                        conexion.cerrarConexion();
                        break;
                        
                }   } catch (IOException ex) {
                Logger.getLogger(Hilo.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(Hilo.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
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

    private void modificarDatos() {
        try {
            this.claveCifrar = (PublicKey) Utiles.Utiles.recibirObjeto(cliente);
            System.out.println("Clave publica"+this.clavePublica);
            Utiles.Utiles.enviarObjeto(cliente, this.clavePublica);
            conexion.establecerConexion();
            conexion.enviarRegiones(cliente);
            boolean SeguirViendo = true;
            while (SeguirViendo){
                try {
                    int num = (int) Utiles.Utiles.recibirObjeto(cliente);
                    switch (num) {
                        case 0:
                            añadirDatos();
                            break;
                            
                        case 1:
                            eliminarDatos();
                            break;
                            
                        case 2:
                            conexion.cerrarConexion();
                            SeguirViendo = false;
                            break;
                            
                            
                    }   } catch (IOException ex) {
                        Logger.getLogger(Hilo.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (ClassNotFoundException ex) {
                        Logger.getLogger(Hilo.class.getName()).log(Level.SEVERE, null, ex);
                    }
            }
        } catch (IOException ex) {
            Logger.getLogger(Hilo.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Hilo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void añadirDatos() {
        try {
            DatosSemana datosAñadir = (DatosSemana) Utiles.Utiles.recibirObjeto(cliente);
            if(!conexion.existenDatos(datosAñadir)){
                conexion.añadirDatos(datosAñadir);
                Utiles.Utiles.enviarObjeto(cliente, true);
            }else{
                Utiles.Utiles.enviarObjeto(cliente, false);
            }
            } catch (IOException ex) {
            Logger.getLogger(Hilo.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Hilo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void eliminarDatos() {
        try {
            DatosSemana datosEliminar = (DatosSemana) Utiles.Utiles.recibirObjeto(cliente);
            
            if(conexion.existenDatos(datosEliminar)){
                conexion.eliminarDatos(datosEliminar);
                Utiles.Utiles.enviarObjeto(cliente, true);
            }else{
                Utiles.Utiles.enviarObjeto(cliente, false);
            }
            } catch (IOException ex) {
            Logger.getLogger(Hilo.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Hilo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void consultarTodo() {
        try {
            conexion.DevolverTodosLosDatos(cliente);
        } catch (SQLException ex) {
            Logger.getLogger(Hilo.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Hilo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void consultarPorDatos() {
        try {
            DatosSemana datosAñadir = (DatosSemana) Utiles.Utiles.recibirObjeto(cliente);
            conexion.DevolverDatosFiltrados(datosAñadir, cliente);
            
        } catch (IOException ex) {
            Logger.getLogger(Hilo.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Hilo.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }

    private void modificarUsuarios() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
}
