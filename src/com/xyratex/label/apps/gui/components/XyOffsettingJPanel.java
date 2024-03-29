package com.xyratex.label.apps.gui.components;

import com.xyratex.ui.XyOperationProgressListener;
import com.xyratex.label.output.print.offset.XyOffsetComponent;

/*
 * XyOffsettingJPanel.java
 *
 * Created on 17 November 2008, 15:39
 */



/**
 *
 * @author  rdavis
 */
public class XyOffsettingJPanel extends javax.swing.JPanel {


    
    private XyOffsetComponent offsetCapableLabel = null;
    
    private String sensitivity = "1px";
    
    /** Creates new form XyOffsettingJPanel */
    public XyOffsettingJPanel() {
        initComponents();
    }
    
    public void setLogic( XyOffsetComponent xyOffsetComponent )
    {
        offsetCapableLabel = xyOffsetComponent;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        XyOffsetUpJButton = new javax.swing.JButton();
        XyOffsetDownJButton = new javax.swing.JButton();
        XyOffsetLeftJButton = new javax.swing.JButton();
        XyOffsetRightJButton = new javax.swing.JButton();
        XyOffsetResetJButton = new javax.swing.JButton();
        XyOffsetLoadJButton = new javax.swing.JButton();
        XyOffsetSaveJButton = new javax.swing.JButton();
        XyOffsetLabel = new javax.swing.JLabel();

        XyOffsetUpJButton.setText("up");
        XyOffsetUpJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                XyOffsetUpJButtonActionPerformed(evt);
            }
        });

        XyOffsetDownJButton.setText("down");
        XyOffsetDownJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                XyOffsetDownJButtonActionPerformed(evt);
            }
        });

        XyOffsetLeftJButton.setText("left");
        XyOffsetLeftJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                XyOffsetLeftJButtonActionPerformed(evt);
            }
        });

        XyOffsetRightJButton.setText("right");
        XyOffsetRightJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                XyOffsetRightJButtonActionPerformed(evt);
            }
        });

        XyOffsetResetJButton.setText("reset");
        XyOffsetResetJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                XyOffsetResetJButtonActionPerformed(evt);
            }
        });

        XyOffsetLoadJButton.setText("load offsets");
        XyOffsetLoadJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                XyOffsetLoadJButtonActionPerformed(evt);
            }
        });

        XyOffsetSaveJButton.setText("save offsets");
        XyOffsetSaveJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                XyOffsetSaveJButtonActionPerformed(evt);
            }
        });

        XyOffsetLabel.setText("Offsetting");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(XyOffsetLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(XyOffsetUpJButton)
                .addComponent(XyOffsetDownJButton)
                .addComponent(XyOffsetLeftJButton)
                .addComponent(XyOffsetRightJButton)
                .addComponent(XyOffsetResetJButton)
                .addGap(0, 0, 0)
                .addComponent(XyOffsetLoadJButton)
                .addGap(0, 0, 0)
                .addComponent(XyOffsetSaveJButton))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(XyOffsetUpJButton, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(XyOffsetLabel))
            .addComponent(XyOffsetDownJButton, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(XyOffsetLeftJButton, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(XyOffsetRightJButton, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(XyOffsetResetJButton, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(XyOffsetLoadJButton, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(XyOffsetSaveJButton, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
    }// </editor-fold>//GEN-END:initComponents

// TODO add your handling code here:
// TODO add your handling code here:
private void XyOffsetUpJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_XyOffsetUpJButtonActionPerformed

  		  offsetCapableLabel.moveUp( sensitivity );

}//GEN-LAST:event_XyOffsetUpJButtonActionPerformed

// TODO add your handling code here:
private void XyOffsetDownJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_XyOffsetDownJButtonActionPerformed

    		offsetCapableLabel.moveDown( sensitivity );

}//GEN-LAST:event_XyOffsetDownJButtonActionPerformed

private void XyOffsetLeftJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_XyOffsetLeftJButtonActionPerformed

    		offsetCapableLabel.moveLeft( sensitivity ); 

}//GEN-LAST:event_XyOffsetLeftJButtonActionPerformed

private void XyOffsetRightJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_XyOffsetRightJButtonActionPerformed

    		offsetCapableLabel.moveRight( sensitivity );

}//GEN-LAST:event_XyOffsetRightJButtonActionPerformed

private void XyOffsetResetJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_XyOffsetResetJButtonActionPerformed
offsetCapableLabel.reset();
}//GEN-LAST:event_XyOffsetResetJButtonActionPerformed

private void XyOffsetLoadJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_XyOffsetLoadJButtonActionPerformed
  		offsetCapableLabel.load();
}//GEN-LAST:event_XyOffsetLoadJButtonActionPerformed

private void XyOffsetSaveJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_XyOffsetSaveJButtonActionPerformed
  		offsetCapableLabel.save(); 
}//GEN-LAST:event_XyOffsetSaveJButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton XyOffsetDownJButton;
    private javax.swing.JLabel XyOffsetLabel;
    private javax.swing.JButton XyOffsetLeftJButton;
    private javax.swing.JButton XyOffsetLoadJButton;
    private javax.swing.JButton XyOffsetResetJButton;
    private javax.swing.JButton XyOffsetRightJButton;
    private javax.swing.JButton XyOffsetSaveJButton;
    private javax.swing.JButton XyOffsetUpJButton;
    // End of variables declaration//GEN-END:variables

}
