/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package romanow.cnc.settingsView;

import romanow.cnc.view.BaseFrame;
import romanow.cnc.utils.Events;
import romanow.cnc.m3d.OK;
import romanow.cnc.settings.UserProfile;
import romanow.cnc.settings.WorkSpace;
import romanow.cnc.utils.StringCrypter;
import romanow.cnc.utils.UNIException;
import romanow.cnc.Values;

import java.awt.FileDialog;
import java.util.ArrayList;

/**
 *
 * @author romanow
 */
public class UserListEditor extends BaseFrame {
    private boolean changed=false;
    private ArrayList<UserProfile> users;
    private UserProfile selected;
    /**
     * Creates new form Login
     */
    private void setSize(boolean full){
        setBounds(350,250,300,full ? 370 :120);
        }
    private void init(){
        WorkSpace ws = WorkSpace.ws();
        users = ws.global().userList;
        UserList.removeAll();
        for(UserProfile uu : users){
            UserList.add(uu.name);}
        setSize(false);
        UserType.removeAll();
        UserType.add("Посторонний");
        UserType.add("Оператор");
        UserType.add("Технолог");
        UserType.add("Администратор");
        }
    
    public UserListEditor() {
        if (!tryToStart()) return;        
        initComponents();
        setTitle("Пользователи");
        WorkSpace ws = WorkSpace.ws();
        try {
            ws.loadGlobalSettings();
            } catch (UNIException e) {
                ws().sendEvent(null,Events.Notify, 1, Values.important, "Настойки не прочитаны - умолчание",null);
                ws.saveSettings();
            }
        init();
    }
    
    @Override
    public void onEvent(int code,boolean on, int value, String name) {
        if (code == Events.Close){
            onClose();
            }
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
        Add = new javax.swing.JButton();
        Insert = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        UserType = new java.awt.Choice();
        UserName = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        Path = new javax.swing.JTextField();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        Password = new javax.swing.JPasswordField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        getContentPane().setLayout(null);
        getContentPane().add(UserList);
        UserList.setBounds(120, 10, 140, 25);

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel1.setText("Логин");
        getContentPane().add(jLabel1);
        jLabel1.setBounds(30, 80, 60, 14);

        Add.setText("Добавить");
        Add.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AddActionPerformed(evt);
            }
        });
        getContentPane().add(Add);
        Add.setBounds(20, 40, 90, 23);

        Insert.setText("Изменить");
        Insert.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                InsertActionPerformed(evt);
            }
        });
        getContentPane().add(Insert);
        Insert.setBounds(170, 40, 90, 23);

        jButton3.setText("Удалить");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton3);
        jButton3.setBounds(20, 10, 90, 23);
        getContentPane().add(UserType);
        UserType.setBounds(100, 160, 160, 25);
        getContentPane().add(UserName);
        UserName.setBounds(100, 80, 160, 25);

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel2.setText("Пароль");
        getContentPane().add(jLabel2);
        jLabel2.setBounds(30, 120, 60, 14);
        getContentPane().add(jSeparator1);
        jSeparator1.setBounds(20, 70, 250, 2);
        getContentPane().add(Path);
        Path.setBounds(10, 200, 250, 25);

        jButton4.setText("Рабочая область");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton4);
        jButton4.setBounds(130, 240, 130, 23);

        jButton5.setText("Сохранить");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton5);
        jButton5.setBounds(130, 270, 130, 23);

        Password.setText("1234");
        getContentPane().add(Password);
        Password.setBounds(100, 120, 160, 25);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void load(){
        UserName.setText(selected.name);
        Password.setText(StringCrypter.decrypt(selected.password));
        UserType.select(selected.accessMode);
        Path.setText(selected.workSpaceDir);
        }
    
    private void save(){
        selected.name = UserName.getText();
        selected.password = StringCrypter.encrypt(Password.getText());
        selected.accessMode = UserType.getSelectedIndex();
        selected.workSpaceDir = Path.getText();
        }
    
    private void AddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AddActionPerformed
        setSize(true);
        changed=false;
        selected = new UserProfile("","",0,"");
        load();
    }//GEN-LAST:event_AddActionPerformed

    private void InsertActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_InsertActionPerformed
        //if (UserList.getSelectedIndex()<2){
        //    ws().notifyEvent(Values.info, "Посторонний и админ не редактируются");
        //    return;
        //    }
        setSize(true);
        changed=true;
        selected = users.get(UserList.getSelectedIndex());
        load();
    }//GEN-LAST:event_InsertActionPerformed

    
    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        if (UserList.getSelectedIndex()<2){
            ws().notify(Values.info, "Посторонний и админ не удаляются");
            return;
            }
        new OK(getBounds(),"Удалить "+UserList.getSelectedItem(),()->{
            int idx = UserList.getSelectedIndex();
            users.remove(idx);
            init();
            }).setVisible(true);
    }//GEN-LAST:event_jButton3ActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        onClose();
    }//GEN-LAST:event_formWindowClosing

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        FileDialog dlg=new FileDialog(this,"Рабочий каталог",FileDialog.SAVE);
        dlg.setFile("aaa.aaa");
        dlg.show();
        String fname=dlg.getDirectory();
        Path.setText(fname==null ? "" : fname); 
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        if (UserName.getText().length()==0){
            ws().notify(Values.info, "Отсутствует логин");
            return;
            }
        if (Password.getText().length()==0){
            ws().notify(Values.info, "Отсутствует пароль");
            return;
            }
        if (Path.getText().length()==0 && UserType.getSelectedIndex()!=Values.userAdmin){
            ws().notify(Values.info, "Отсутствует рабочий каталог");
            return;
            }
        save();
        if (!changed)
            users.add(selected);
        ws().saveSettings();
        init();
    }//GEN-LAST:event_jButton5ActionPerformed

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
            java.util.logging.Logger.getLogger(UserListEditor.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(UserListEditor.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(UserListEditor.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(UserListEditor.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new UserListEditor().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Add;
    private javax.swing.JButton Insert;
    private javax.swing.JPasswordField Password;
    private javax.swing.JTextField Path;
    private java.awt.Choice UserList;
    private javax.swing.JTextField UserName;
    private java.awt.Choice UserType;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JSeparator jSeparator1;

    @Override
    public void refresh() {

    }

    @Override
    public void shutDown() {

    }
    // End of variables declaration//GEN-END:variables
}