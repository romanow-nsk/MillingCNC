/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package romanow.cnc.view;

import romanow.cnc.Values;
import romanow.cnc.settings.UserProfile;
import romanow.cnc.settings.WorkSpace;
import romanow.cnc.utils.StringCrypter;
import romanow.cnc.utils.UNIException;

import java.awt.*;
import java.util.ArrayList;

/**
 *
 * @author Admin
 */
public class LoginPanel extends BasePanel {

    /**
     * Creates new form LoginPanel
     */
    private void init(){
        ArrayList<UserProfile> users = WorkSpace.ws().global().userList;
        UserList.removeAll();
        for(UserProfile uu : users){
            UserList.add(uu.name);
            }
        // Password.setText("");
        //-------------- Отладка ------------------------
        Password.setText("admin");
        UserList.select(1);
        }

    private void login(){
        WorkSpace ws = WorkSpace.ws();
        ArrayList<UserProfile> users = ws.global().userList;
        UserProfile user = users.get(UserList.getSelectedIndex());
        if (user.password.equals(StringCrypter.encrypt(Password.getText()))){
            ws.currentUser(user);
            getBaseFrame().setViewPanel(Values.PanelMain | Values.PanelGlobalSettings | Values.PanelCommonView);
            getBaseFrame().refreshPanels();
            }
        else
            MES.setText("Неверный пароль");
        }



    @Override
    public String getName() {
        return "Авторизация";
        }

    @Override
    public int modeMask() {
        return Values.PanelLogin;
        }

    @Override
    public boolean modeEnabled() {
        return true;
    }

    @Override
    public void onActivate() {
        initView();
        }

    @Override
    public void onDeactivate() {
        }

    @Override
    public void onClose() {
        }

    public LoginPanel(BaseFrame baseFrame,Dimension dim) {
        super(baseFrame,dim);
        initComponents();
        }

    public void initView() {
        WorkSpace ws = WorkSpace.ws();
        try {
            ws.loadGlobalSettings();
            } catch (UNIException e) {
                MES.setText("Настойки не прочитаны - умолчание");
                ws.saveSettings();
            }
        init();
        }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        UserList = new java.awt.Choice();
        jLabel1 = new javax.swing.JLabel();
        Password = new javax.swing.JPasswordField();
        jButton1 = new javax.swing.JButton();
        MES = new javax.swing.JTextField();

        setLayout(null);
        add(UserList);
        UserList.setBounds(100, 10, 150, 25);

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel1.setText("Пароль");
        add(jLabel1);
        jLabel1.setBounds(40, 50, 60, 14);

        Password.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                PasswordKeyPressed(evt);
            }
        });
        add(Password);
        Password.setBounds(100, 50, 150, 25);

        jButton1.setText("Войти");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        add(jButton1);
        jButton1.setBounds(180, 80, 73, 30);
        add(MES);
        MES.setBounds(40, 120, 220, 25);
    }// </editor-fold>//GEN-END:initComponents

    private void PasswordKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_PasswordKeyPressed
        if(evt.getKeyCode()==10){//Enter key
            login();
        }
    }//GEN-LAST:event_PasswordKeyPressed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        login();
    }//GEN-LAST:event_jButton1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField MES;
    private javax.swing.JPasswordField Password;
    private java.awt.Choice UserList;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;

    @Override
    public void refresh() {

    }

    @Override
    public void onEvent(int code, int par1, long par2, String par3, Object oo) {
        }

    @Override
    public void shutDown() {

    }
    // End of variables declaration//GEN-END:variables
}
