/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BaseDeDatos;

import java.io.IOException;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;
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
             int cod = 0;
             this.Sentencia.executeUpdate(Sentencia);
         } catch (SQLException ex) {
             Logger.getLogger(ConexionBaseDeDatos.class.getName()).log(Level.SEVERE, null, ex);
         }
    }
}
