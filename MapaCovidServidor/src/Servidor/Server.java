/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Servidor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;

/**
 *
 * @author pablo
 */
public class Server {
    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        ServerSocket servidor = new ServerSocket(9000);
        while(true){
            Socket cliente;
            cliente = servidor.accept();
            Hilo hilo = new Hilo(cliente);
            hilo.start();
        }
    }
    
}
