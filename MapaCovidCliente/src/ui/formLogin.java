/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ui;

import Objetos.UsuarioRegistro;
import static Utiles.Utiles.recibirObjeto;
import java.awt.RenderingHints.Key;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.SealedObject;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.JOptionPane;
import org.bouncycastle.jce.provider.BouncyCastleProvider;


/**
 *
 * @author pablo
 */
public class formLogin extends javax.swing.JFrame {

    public Socket servidor;
    public SecretKey  claveSecreta;
    
    public formLogin() throws UnknownHostException, IOException, ClassNotFoundException {
        initComponents();
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        InetAddress dir = InetAddress.getLocalHost();
        this.servidor = new Socket(dir, 9000);
        String claveCodificada =(String) recibirObjeto(servidor);
        byte[] decodedKey = Base64.getDecoder().decode(claveCodificada);
        this.claveSecreta = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES"); 
        System.out.println("Conseguida la conexion");
        
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnLogin = new javax.swing.JButton();
        btnRegistro = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtEmail = new javax.swing.JTextField();
        txtContrase??a = new javax.swing.JPasswordField();
        btnInvitado = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        btnLogin.setText("Log In");
        btnLogin.setToolTipText("");
        btnLogin.setName("btnLogin"); // NOI18N
        btnLogin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLoginActionPerformed(evt);
            }
        });

        btnRegistro.setText("Registro");
        btnRegistro.setToolTipText("");
        btnRegistro.setName("btnRegistro"); // NOI18N
        btnRegistro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRegistroActionPerformed(evt);
            }
        });

        jLabel1.setText("Email:");

        jLabel2.setText("Contrase??a:");

        btnInvitado.setText("Invitado");
        btnInvitado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInvitadoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(83, 83, 83)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel1))
                        .addGap(34, 34, 34)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtEmail)
                            .addComponent(txtContrase??a, javax.swing.GroupLayout.DEFAULT_SIZE, 134, Short.MAX_VALUE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(158, 158, 158)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnLogin, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnInvitado)
                            .addComponent(btnRegistro))))
                .addContainerGap(89, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(33, 33, 33)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(40, 40, 40)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtContrase??a, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(27, 27, 27)
                .addComponent(btnLogin)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnRegistro)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnInvitado)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnLoginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLoginActionPerformed
        if(!this.txtEmail.equals("") && !this.txtContrase??a.getText().equals("")){
             try {
               Utiles.Utiles.enviarObjeto(servidor, true);
               Utiles.Utiles.enviarObjeto(servidor, true);
               UsuarioRegistro usuario= new UsuarioRegistro(this.txtEmail.getText(), Utiles.Utiles.cifrarContrase??a(this.txtContrase??a.getText()));
               SealedObject usuariocifrado = Utiles.Utiles.cifrarSimetrico(usuario, claveSecreta);
               Utiles.Utiles.enviarObjeto(servidor, usuariocifrado);
               boolean existe = (boolean) Utiles.Utiles.recibirObjeto(servidor);
               if (existe){
                    int rol = (int) Utiles.Utiles.recibirObjeto(servidor);
                    String clavePublicaString = (String) Utiles.Utiles.recibirObjeto(servidor);
                    String clavePrivadaString = (String) Utiles.Utiles.recibirObjeto(servidor);
                    
                    KeyPairGenerator generadorDeClave = KeyPairGenerator.getInstance("RSA");
                    generadorDeClave.initialize(1024);
                    KeyPair Par = generadorDeClave.generateKeyPair();
                    PrivateKey clavePrivada = Par.getPrivate();
                    PublicKey clavePublica = Par.getPublic();
                    formSeleccion formulario = new formSeleccion(rol, servidor, clavePublica, clavePrivada);
                    formulario.setVisible(true);
                    this.dispose();
               }else{
                   JOptionPane.showMessageDialog(null, "Error contrase??a o email incorrectos o cuenta no activada");
               }
               
           } catch (IOException ex) {
               Logger.getLogger(formRegistro.class.getName()).log(Level.SEVERE, null, ex);
           } catch (Exception ex) {
               Logger.getLogger(formRegistro.class.getName()).log(Level.SEVERE, null, ex);
           }
            
            
        }else{
             JOptionPane.showMessageDialog(null, "Rellene bien los campos");
        }
    }//GEN-LAST:event_btnLoginActionPerformed

    private void btnRegistroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRegistroActionPerformed
        formRegistro reg = new formRegistro(servidor, this);
        this.setVisible(false);
        reg.setVisible(true);
    }//GEN-LAST:event_btnRegistroActionPerformed

    private void btnInvitadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInvitadoActionPerformed
        try {
            Utiles.Utiles.enviarObjeto(servidor, true);
            Utiles.Utiles.enviarObjeto(servidor, false);
            formSeleccion formulario = new formSeleccion(servidor);
            formulario.setVisible(true);
            this.dispose();
            
        } catch (IOException ex) {
            Logger.getLogger(formLogin.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnInvitadoActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(formLogin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(formLogin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(formLogin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(formLogin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    new formLogin().setVisible(true);
                } catch (IOException ex) {
                    Logger.getLogger(formLogin.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(formLogin.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnInvitado;
    private javax.swing.JButton btnLogin;
    private javax.swing.JButton btnRegistro;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPasswordField txtContrase??a;
    private javax.swing.JTextField txtEmail;
    // End of variables declaration//GEN-END:variables
}
