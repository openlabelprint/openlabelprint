package com.xyratex.label.apps.gui.components;

/*
 * NewJPanel.java
 *
 * Created on 17 November 2008, 13:58
 */



/**
 *
 * @author  rdavis
 */
public class XyOLPLaunchSettingsJPanel extends javax.swing.JPanel {

    private XyLoadLabelTemplateLogic xyLoadLabelTemplateLogic;
    
    /** Creates new form NewJPanel */
    public XyOLPLaunchSettingsJPanel() {
        initComponents();
    }

    public void setLogic( XyLoadLabelTemplateLogic logic )
    {
        xyLoadLabelTemplateLogic = logic;
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        XyLoadLabelTemplateJButton = new javax.swing.JButton();
        XyOLPStatusRecallPrintedLabelJLabel = new javax.swing.JLabel();
        XyOLPRecallPrintedLabelJLabel = new javax.swing.JLabel();

        XyLoadLabelTemplateJButton.setText("Settings...");
        XyLoadLabelTemplateJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                XyLoadLabelTemplateJButtonActionPerformed(evt);
            }
        });

        XyOLPStatusRecallPrintedLabelJLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        XyOLPStatusRecallPrintedLabelJLabel.setText("status");

        XyOLPRecallPrintedLabelJLabel.setText("Configure Open Label Print");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(XyOLPRecallPrintedLabelJLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(XyLoadLabelTemplateJButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(XyOLPStatusRecallPrintedLabelJLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 291, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(XyOLPRecallPrintedLabelJLabel)
                .addComponent(XyLoadLabelTemplateJButton)
                .addComponent(XyOLPStatusRecallPrintedLabelJLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 23, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

private void XyLoadLabelTemplateJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_XyLoadLabelTemplateJButtonActionPerformed
xyLoadLabelTemplateLogic.loadTemplate();
}//GEN-LAST:event_XyLoadLabelTemplateJButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton XyLoadLabelTemplateJButton;
    private javax.swing.JLabel XyOLPRecallPrintedLabelJLabel;
    private javax.swing.JLabel XyOLPStatusRecallPrintedLabelJLabel;
    // End of variables declaration//GEN-END:variables

}
