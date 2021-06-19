/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BaseDeDatos;

import Objetos.DatosSemana;
import Objetos.Region;
import java.io.IOException;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author pablo
 */
public class ConexionBaseDeDatos {

     private java.sql.Connection Conexion;
     private java.sql.Statement Sentencia;
    
    public ConexionBaseDeDatos() {
    }
    
    public void establecerConexion() {
        try {
            String controladorBaseDeDatos = "com.mysql.cj.jdbc.Driver";
            Class.forName(controladorBaseDeDatos).newInstance();
            String URL_BD = "jdbc:mysql://localhost:3306/" + "mapacovid";
            Conexion = java.sql.DriverManager.getConnection(URL_BD, "root", "");
            Sentencia = Conexion.createStatement();
            System.out.println("Conexión realizada con éxito");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void cerrarConexion() {
        try {
            this.Conexion.close();
            System.out.println("Desconectado de la Base de Datos"); // Opcional para seguridad
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(), "Error de Desconexion", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public Boolean hayUsuario (String where) throws SQLException {
        String sentencia = "SELECT * from usuario WHERE " + where;        
        ResultSet usuarios = Sentencia.executeQuery(sentencia);
        if (usuarios.next()) {
            return true;
        } else {
            return false;
        }
    }
    
    
     public Boolean hayUsuarioLogin (String where, Socket cliente) throws SQLException {
        String sentencia = "SELECT * from usuario WHERE " + where;        
        ResultSet usuarios = Sentencia.executeQuery(sentencia);
        if (usuarios.next()) {
            try {
                int rol = usuarios.getInt("rol");
                String clavePublica = usuarios.getNString("clave_publica");
                String clavePrivada = usuarios.getNString("clave_privada");
                Utiles.Utiles.enviarObjeto(cliente, true);
                Utiles.Utiles.enviarObjeto(cliente, rol);
                Utiles.Utiles.enviarObjeto(cliente, clavePublica);
                Utiles.Utiles.enviarObjeto(cliente, clavePrivada);
                return true;
            } catch (IOException ex) {
                Logger.getLogger(ConexionBaseDeDatos.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            try {
                Utiles.Utiles.enviarObjeto(cliente, false);
                return false;
            } catch (IOException ex) {
                Logger.getLogger(ConexionBaseDeDatos.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
         return false;
    }
    
   public void insertarUsuario(String email, String nombre, String contraseña, int rol, String clavePublica, String clavePrivada) {
         try {
             String Sentencia = "INSERT INTO usuario VALUES ('" + email + "'," + "'" + nombre + "'," + "'" + contraseña + "'," + "" + rol + ", 0, " + "'" + clavePublica + "'," + "'" + clavePrivada + "')";
             this.Sentencia.executeUpdate(Sentencia);
         } catch (SQLException ex) {
             Logger.getLogger(ConexionBaseDeDatos.class.getName()).log(Level.SEVERE, null, ex);
         }
    }

    public void enviarRegiones(Socket cliente) {
         try {
            ArrayList <Region> regiones = new ArrayList <Region>();
            String sentencia = "SELECT * from  region";
            ResultSet RegionesSentencia = Sentencia.executeQuery(sentencia);
            while(RegionesSentencia.next()) {
                Region region  = new Region (RegionesSentencia.getNString("nombre_region"));
                regiones.add(region);
            }
            Utiles.Utiles.enviarObjeto(cliente, regiones);
         
            } catch (SQLException ex) {
             Logger.getLogger(ConexionBaseDeDatos.class.getName()).log(Level.SEVERE, null, ex);
         } catch (IOException ex) {
             Logger.getLogger(ConexionBaseDeDatos.class.getName()).log(Level.SEVERE, null, ex);
         }
    }

    public boolean existeRegion(String regionNueva) {
         try {
             String sentencia = "SELECT * from region WHERE nombre_region = '"+ regionNueva+"'";
             ResultSet usuarios = Sentencia.executeQuery(sentencia);
             if (usuarios.next()) {
                 return true;
             } else {
                 return false;
             }} catch (SQLException ex) {
             Logger.getLogger(ConexionBaseDeDatos.class.getName()).log(Level.SEVERE, null, ex);
         }
         return false;
    }

    public void añadirRegion(String regionNueva) {
         try {
             String Sentencia = "INSERT INTO region VALUES ('"+regionNueva+"')";
             this.Sentencia.executeUpdate(Sentencia);
         } catch (SQLException ex) {
             Logger.getLogger(ConexionBaseDeDatos.class.getName()).log(Level.SEVERE, null, ex);
         }
    }

    public void eliminarRegion(String regionNueva) {
        try {
             String Sentencia = "DELETE FROM region WHERE nombre_region = '"+regionNueva+"'";
             this.Sentencia.executeUpdate(Sentencia);
             
             Sentencia = "DELETE FROM datos_semanales_region WHERE id_region= '"+regionNueva+"'";
             this.Sentencia.executeUpdate(Sentencia);
             
         } catch (SQLException ex) {
             Logger.getLogger(ConexionBaseDeDatos.class.getName()).log(Level.SEVERE, null, ex);
         }
    }

    public boolean existenDatos(DatosSemana datosEliminar) {
       try {
            String idRegion = datosEliminar.region;
            int semana = datosEliminar.semana;
            String id = idRegion.concat(Integer.toString(semana));
            String sentencia = "SELECT * from datos_semanales_region WHERE id_region_num_semana = '"+ id+"'";
            ResultSet usuarios = Sentencia.executeQuery(sentencia);
            if (usuarios.next()) {
                return true;
            } else {
                return false;
            }} catch (SQLException ex) {
            Logger.getLogger(ConexionBaseDeDatos.class.getName()).log(Level.SEVERE, null, ex);
         }
         return false;
    }

    public void eliminarDatos(DatosSemana datosEliminar) {
        try {
            String idRegion = datosEliminar.region;
            int semana = datosEliminar.semana;
            String id = idRegion.concat(Integer.toString(semana));
            String Sentencia = "DELETE FROM datos_semanales_region WHERE id_region_num_semana = '"+id+"'";
            this.Sentencia.executeUpdate(Sentencia);
         } catch (SQLException ex) {
             Logger.getLogger(ConexionBaseDeDatos.class.getName()).log(Level.SEVERE, null, ex);
         }
    }

    public void añadirDatos(DatosSemana datosEliminar) {
        try {
            
            String idRegion = datosEliminar.region;
            int semana = datosEliminar.semana;
            int muertos = datosEliminar.muertes;
            int infectados = datosEliminar.infectados;
            int altas = datosEliminar.altas;
            String id = idRegion.concat(Integer.toString(semana));
            
            String Sentencia = "INSERT INTO datos_semanales_region VALUES ('"+ id +"'," + "'" + idRegion + "'," + "'" + semana + "'," + "'" + muertos + "', " + "'" + infectados + "'," + "'" + altas + "')";
            this.Sentencia.executeUpdate(Sentencia);
            System.out.println("Ejecutada");
        } catch (SQLException ex) {
            Logger.getLogger(ConexionBaseDeDatos.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void DevolverTodosLosDatos(Socket cliente) throws SQLException, IOException {
            ArrayList <DatosSemana> datos = new ArrayList <DatosSemana>();
            String sentencia = "SELECT * from  datos_semanales_region";
            ResultSet DatosSentencia = Sentencia.executeQuery(sentencia);
            while(DatosSentencia.next()) {
                DatosSemana datoNuevo  = new DatosSemana (DatosSentencia.getNString("id_region"), DatosSentencia.getInt("numero_semana"), DatosSentencia.getInt("muertos"), DatosSentencia.getInt("infectados"), DatosSentencia.getInt("alta"));
                datos.add(datoNuevo);
            }
            Utiles.Utiles.enviarObjeto(cliente, datos);
         
    }

    public void DevolverDatosFiltrados(DatosSemana datosAñadir, Socket cliente) {
         try {
             String id = datosAñadir.region;
             int num = datosAñadir.semana;
             ArrayList <DatosSemana> datos = new ArrayList <DatosSemana>();
             String sentencia = "SELECT * from  datos_semanales_region WHERE id_region = '"+id+"' and numero_semana="+num+"";
             ResultSet DatosSentencia = Sentencia.executeQuery(sentencia);
             while(DatosSentencia.next()) {
                 DatosSemana datoNuevo  = new DatosSemana (DatosSentencia.getNString("id_region"), DatosSentencia.getInt("numero_semana"), DatosSentencia.getInt("muertos"), DatosSentencia.getInt("infectados"), DatosSentencia.getInt("alta"));
                 datos.add(datoNuevo);
             }
             Utiles.Utiles.enviarObjeto(cliente, datos);
         } catch (SQLException ex) {
             Logger.getLogger(ConexionBaseDeDatos.class.getName()).log(Level.SEVERE, null, ex);
         } catch (IOException ex) {
             Logger.getLogger(ConexionBaseDeDatos.class.getName()).log(Level.SEVERE, null, ex);
         }
    }
}
        
    

