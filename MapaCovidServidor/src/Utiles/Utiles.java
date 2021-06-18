/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utiles;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SealedObject;
import javax.crypto.SecretKey;

/**
 *
 * @author pablo
 */
public class Utiles {
    public static void enviarObjeto(Socket cliente, Object objeto) throws IOException{
        ObjectOutputStream oos = new ObjectOutputStream(cliente.getOutputStream());
        oos.writeObject(objeto);
    }
    
    public static Object recibirObjeto(Socket cliente) throws IOException, ClassNotFoundException{
        ObjectInputStream ois = new ObjectInputStream(cliente.getInputStream());
        return ois.readObject();
    }
    
    public static SealedObject cifrar(PublicKey claveAjena, Object o) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IOException, IllegalBlockSizeException {
        Cipher c = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        c.init(Cipher.ENCRYPT_MODE, claveAjena);
        SealedObject objetoCifrado = new SealedObject((Serializable) o, c);
        return objetoCifrado;
    }

    public static Object descifrar(PrivateKey clavePropia, SealedObject so) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IOException, ClassNotFoundException, IllegalBlockSizeException, BadPaddingException {
        Cipher c = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        c.init(Cipher.DECRYPT_MODE, clavePropia);
        Object o = so.getObject(c);
        return o;
    }
    
    public static SealedObject cifrarSimetrico(Object o, SecretKey clave) throws Exception {
        Cipher c = Cipher.getInstance("AES/ECB/PKCS5Padding");
        c.init(Cipher.ENCRYPT_MODE, clave);
        SealedObject objetoCifrado = new SealedObject((Serializable) o, c);
        return objetoCifrado;
    }

    public static Object desencriptarSimetrico(SealedObject so, SecretKey clave) throws Exception {
        Cipher c = Cipher.getInstance("AES/ECB/PKCS5Padding");
        c.init(Cipher.DECRYPT_MODE, clave);
        Object o = so.getObject(c);
        return o;
    }
    
    public static String [] generarClaves(){
        try {
            String claves[] = new String[2];
            KeyPairGenerator generadorDeClave = KeyPairGenerator.getInstance("RSA");
            generadorDeClave.initialize(1024);
            KeyPair Par = generadorDeClave.generateKeyPair();
            PrivateKey clavePrivada = Par.getPrivate();
            PublicKey clavePublica = Par.getPublic();
            claves[0] = Base64.getEncoder().encodeToString(clavePublica.getEncoded());
            claves[1] = Base64.getEncoder().encodeToString(clavePrivada.getEncoded());
            
            return claves;
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(Utiles.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
