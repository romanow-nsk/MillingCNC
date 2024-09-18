/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package epos.slm3d.m3d;

import epos.slm3d.commands.Command;
import epos.slm3d.commands.USBCommandFactory;
import epos.slm3d.controller.USBBack;
import epos.slm3d.controller.USBCodes;
import epos.slm3d.controller.USBFace;
import epos.slm3d.controller.USBProtocol;
import epos.slm3d.utils.I_Notify;
import epos.slm3d.utils.UNIException;
import epos.slm3d.utils.Utils;
import epos.slm3d.utils.Values;

import java.util.ArrayList;

/**
 *
 * @author romanow
 */
public class M3DUSBTester extends javax.swing.JFrame {
    private I_Notify notify;
    private USBProtocol usb;
    private USBCommandFactory factory = new USBCommandFactory();
    /**
     * Creates new form M3DUSBTester
     */
    private USBBack back = new USBBack() {
        @Override
        public void onSuccess(int code, int[] data) {
            notify.info("Выполнено");
            if (code==USBCodes.GetAvailMemory)
                notify.info("Памяти команд "+data[1]+" слов");
            if (code==USBCodes.ReadMessages){
                int count = data[1];
                notify.info("Прочитано "+count+" строки");
                String ss[] = new String[0];
                try {
                    ss = Utils.IntArrayToStrings(data);
                    } catch (UNIException e) {
                        notify.notify(Values.fatal,e.toString());
                        return;
                        }
                for(String zz : ss){
                    notify.info(zz);
                    }
                }
        }
        @Override
        public void onError(int code, int[] data) {
            notify.notify(Values.error,"Ошибка принтера: "+code);
        }
        @Override
        public void onFatal(int code, String mes) {
            notify.notify(Values.fatal,"Ошибка интерфейса: "+code+" "+mes);
        }
    };
    public M3DUSBTester(I_Notify notify0, USBFace usb0) {
        initComponents();
        notify = notify0;
        usb = new USBProtocol(usb0);
        this.setBounds(150,150,400,350);
        setTitle("USB ручной тестер");
        ArrayList<String> ss = factory.commandList();
        for(int i=0;i<ss.size();i++)
            CmdList.addItem(ss.get(i));
        try {
            usb.init();
            } catch (UNIException e) { notify.notify(Values.error,e.toString()); }
        }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        CmdList = new javax.swing.JComboBox<>();
        jLabel1 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        getContentPane().setLayout(null);

        CmdList.setMinimumSize(new java.awt.Dimension(28, 30));
        CmdList.setPreferredSize(new java.awt.Dimension(76, 30));
        getContentPane().add(CmdList);
        CmdList.setBounds(40, 40, 230, 25);

        jLabel1.setText("Без параметров");
        getContentPane().add(jLabel1);
        jLabel1.setBounds(40, 20, 100, 14);

        jButton1.setText("Выполнить");
        jButton1.setMaximumSize(new java.awt.Dimension(89, 30));
        jButton1.setMinimumSize(new java.awt.Dimension(89, 30));
        jButton1.setPreferredSize(new java.awt.Dimension(89, 25));
        jButton1.setRequestFocusEnabled(false);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton1);
        jButton1.setBounds(280, 40, 89, 25);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        String cc = (String)CmdList.getSelectedItem();
        try {
            Command xx = factory.getCommand(cc);
            if (xx==null) {
                notify.log( "Не найдена команда:" + CmdList.getSelectedItem());
                return;
            }
            new Thread(()->{
                    int out[] = xx.toIntArray();
                    usb.oneCommand(out,back);
                }).start();
            } catch (UNIException e) { notify.log("Ошибка генерации команды: "+cc); }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        try {
            usb.close();
            } catch (UNIException e) { notify.notify(Values.error,e.toString()); }
    }//GEN-LAST:event_formWindowClosing


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> CmdList;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    // End of variables declaration//GEN-END:variables
}
