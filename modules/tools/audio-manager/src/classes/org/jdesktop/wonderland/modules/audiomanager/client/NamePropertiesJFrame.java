/*
 * NamePropertiesJFrame.java
 *
 * Created on May 28, 2009, 4:19 PM
 */

package org.jdesktop.wonderland.modules.audiomanager.client;

import org.jdesktop.wonderland.modules.presencemanager.common.PresenceInfo;

import org.jdesktop.wonderland.modules.avatarbase.client.jme.cellrenderer.AvatarNameEvent;

import org.jdesktop.wonderland.modules.avatarbase.client.jme.cellrenderer.NameTagNode;
import org.jdesktop.wonderland.modules.avatarbase.client.jme.cellrenderer.NameTagNode.EventType;

import org.jdesktop.wonderland.client.input.InputManager;

/**
 *
 * @author  jp
 */
public class NamePropertiesJFrame extends javax.swing.JFrame {

    PresenceInfo presenceInfo;

    private enum NameTagAttribute {
	HIDE,
	SMALL_FONT,
	REGULAR_FONT,
	LARGE_FONT
    };

    NameTagAttribute originalMyNameTagAttribute = NameTagAttribute.REGULAR_FONT;
    NameTagAttribute myNameTagAttribute = NameTagAttribute.REGULAR_FONT;

    NameTagAttribute originalOtherNameTagAttributes = NameTagAttribute.REGULAR_FONT;
    NameTagAttribute otherNameTagAttributes = NameTagAttribute.REGULAR_FONT;

    /** Creates new form NamePropertiesJFrame */
    public NamePropertiesJFrame() {
        initComponents();
    }

    public NamePropertiesJFrame(PresenceInfo presenceInfo) {
	this.presenceInfo = presenceInfo;

        initComponents();

	OKButton.setEnabled(true);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        buttonGroup2 = new javax.swing.ButtonGroup();
        jLabel1 = new javax.swing.JLabel();
        myNameHideRadioButton = new javax.swing.JRadioButton();
        myNameRegularRadioButton = new javax.swing.JRadioButton();
        myNameLargeRadioButton = new javax.swing.JRadioButton();
        jLabel2 = new javax.swing.JLabel();
        otherNamesHideRadioButton = new javax.swing.JRadioButton();
        otherNamesRegularRadioButton = new javax.swing.JRadioButton();
        otherNamesLargeRadioButton = new javax.swing.JRadioButton();
        OKButton = new javax.swing.JButton();
        otherNamesSmallRadioButton = new javax.swing.JRadioButton();
        myNameTagSmallRadioButton = new javax.swing.JRadioButton();
        jLabel3 = new javax.swing.JLabel();

        setTitle("User Properties");

        jLabel1.setText("My Name:");

        buttonGroup1.add(myNameHideRadioButton);
        myNameHideRadioButton.setText("Hide");
        myNameHideRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                myNameHideRadioButtonActionPerformed(evt);
            }
        });

        buttonGroup1.add(myNameRegularRadioButton);
        myNameRegularRadioButton.setSelected(true);
        myNameRegularRadioButton.setText("Regular");
        myNameRegularRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                myNameRegularRadioButtonActionPerformed(evt);
            }
        });

        buttonGroup1.add(myNameLargeRadioButton);
        myNameLargeRadioButton.setText("Large");
        myNameLargeRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                myNameLargeRadioButtonActionPerformed(evt);
            }
        });

        jLabel2.setText("Other Names:");

        buttonGroup2.add(otherNamesHideRadioButton);
        otherNamesHideRadioButton.setText("Hide");
        otherNamesHideRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                otherNamesHideRadioButtonActionPerformed(evt);
            }
        });

        buttonGroup2.add(otherNamesRegularRadioButton);
        otherNamesRegularRadioButton.setSelected(true);
        otherNamesRegularRadioButton.setText("Regular");
        otherNamesRegularRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                otherNamesRegularRadioButtonActionPerformed(evt);
            }
        });

        buttonGroup2.add(otherNamesLargeRadioButton);
        otherNamesLargeRadioButton.setText("Large");
        otherNamesLargeRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                otherNamesLargeRadioButtonActionPerformed(evt);
            }
        });

        OKButton.setText("OK");
        OKButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                OKButtonActionPerformed(evt);
            }
        });

        buttonGroup2.add(otherNamesSmallRadioButton);
        otherNamesSmallRadioButton.setText("small");
        otherNamesSmallRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                otherNamesSmallRadioButtonActionPerformed(evt);
            }
        });

        buttonGroup1.add(myNameTagSmallRadioButton);
        myNameTagSmallRadioButton.setText("small");
        myNameTagSmallRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                myNameTagSmallRadioButtonActionPerformed(evt);
            }
        });

        jLabel3.setText("Avatar Names");

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel3)
                    .add(layout.createSequentialGroup()
                        .add(40, 40, 40)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel2)
                            .add(jLabel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 74, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                        .add(otherNamesHideRadioButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(otherNamesSmallRadioButton))
                    .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                        .add(myNameHideRadioButton)
                        .add(18, 18, 18)
                        .add(myNameTagSmallRadioButton)))
                .add(18, 18, 18)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(otherNamesRegularRadioButton)
                    .add(myNameRegularRadioButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 79, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(otherNamesLargeRadioButton)
                    .add(myNameLargeRadioButton))
                .addContainerGap())
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap(366, Short.MAX_VALUE)
                .add(OKButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 68, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(31, 31, 31)
                .add(jLabel3)
                .add(10, 10, 10)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(myNameHideRadioButton)
                    .add(myNameTagSmallRadioButton)
                    .add(myNameLargeRadioButton)
                    .add(myNameRegularRadioButton))
                .add(18, 18, 18)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel2)
                    .add(otherNamesHideRadioButton)
                    .add(otherNamesLargeRadioButton)
                    .add(otherNamesSmallRadioButton)
                    .add(otherNamesRegularRadioButton))
                .add(34, 34, 34)
                .add(OKButton)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void OKButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OKButtonActionPerformed
    setVisible(false);
}//GEN-LAST:event_OKButtonActionPerformed

private void myNameHideRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_myNameHideRadioButtonActionPerformed
    myNameTagAttribute = NameTagAttribute.HIDE;

    applyChanges();
}//GEN-LAST:event_myNameHideRadioButtonActionPerformed

private void myNameTagSmallRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_myNameTagSmallRadioButtonActionPerformed
    myNameTagAttribute = NameTagAttribute.SMALL_FONT;
    applyChanges();
}//GEN-LAST:event_myNameTagSmallRadioButtonActionPerformed

private void myNameRegularRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_myNameRegularRadioButtonActionPerformed
    myNameTagAttribute = NameTagAttribute.REGULAR_FONT;
    applyChanges();
}//GEN-LAST:event_myNameRegularRadioButtonActionPerformed

private void myNameLargeRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_myNameLargeRadioButtonActionPerformed
    myNameTagAttribute = NameTagAttribute.LARGE_FONT;
    applyChanges();
}//GEN-LAST:event_myNameLargeRadioButtonActionPerformed

private void otherNamesHideRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_otherNamesHideRadioButtonActionPerformed
    otherNameTagAttributes = NameTagAttribute.HIDE;
    applyChanges();
}//GEN-LAST:event_otherNamesHideRadioButtonActionPerformed

private void otherNamesSmallRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_otherNamesSmallRadioButtonActionPerformed
    otherNameTagAttributes = NameTagAttribute.SMALL_FONT;
    applyChanges();
}//GEN-LAST:event_otherNamesSmallRadioButtonActionPerformed

private void otherNamesRegularRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_otherNamesRegularRadioButtonActionPerformed
    otherNameTagAttributes = NameTagAttribute.REGULAR_FONT;
    applyChanges();
}//GEN-LAST:event_otherNamesRegularRadioButtonActionPerformed

private void otherNamesLargeRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_otherNamesLargeRadioButtonActionPerformed
    otherNameTagAttributes = NameTagAttribute.LARGE_FONT;
    applyChanges();
}//GEN-LAST:event_otherNamesLargeRadioButtonActionPerformed

private void applyChanges() {
    AvatarNameEvent avatarNameEvent;

    if (myNameTagAttribute != originalMyNameTagAttribute) {
	originalMyNameTagAttribute = myNameTagAttribute;

        switch (myNameTagAttribute) {
        case HIDE:
	    NameTagNode.setMyNameTag(EventType.HIDE, 
                presenceInfo.userID.getUsername(), presenceInfo.usernameAlias);
	    break;

        case SMALL_FONT:
	    NameTagNode.setMyNameTag(EventType.SMALL_FONT,
                presenceInfo.userID.getUsername(), presenceInfo.usernameAlias);
	    break;

        case REGULAR_FONT:
	    NameTagNode.setMyNameTag(EventType.REGULAR_FONT,
                presenceInfo.userID.getUsername(), presenceInfo.usernameAlias);
	    break;

        case LARGE_FONT:
	    NameTagNode.setMyNameTag(EventType.LARGE_FONT,
                presenceInfo.userID.getUsername(), presenceInfo.usernameAlias);
	    break;
        }
    }

    if (otherNameTagAttributes == originalOtherNameTagAttributes) {
	return;
    }

    originalOtherNameTagAttributes = otherNameTagAttributes;

    switch (otherNameTagAttributes) {
    case HIDE:
	NameTagNode.setOtherNameTags(EventType.HIDE, 
            presenceInfo.userID.getUsername(), presenceInfo.usernameAlias);
	break;

    case SMALL_FONT:
	NameTagNode.setOtherNameTags(EventType.SMALL_FONT, 
            presenceInfo.userID.getUsername(), presenceInfo.usernameAlias);
	break;

    case REGULAR_FONT:
	NameTagNode.setOtherNameTags(EventType.REGULAR_FONT, 
            presenceInfo.userID.getUsername(), presenceInfo.usernameAlias);
	break;

    case LARGE_FONT:
	NameTagNode.setOtherNameTags(EventType.LARGE_FONT, 
            presenceInfo.userID.getUsername(), presenceInfo.usernameAlias);
	break;
    }
}

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new NamePropertiesJFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton OKButton;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JRadioButton myNameHideRadioButton;
    private javax.swing.JRadioButton myNameLargeRadioButton;
    private javax.swing.JRadioButton myNameRegularRadioButton;
    private javax.swing.JRadioButton myNameTagSmallRadioButton;
    private javax.swing.JRadioButton otherNamesHideRadioButton;
    private javax.swing.JRadioButton otherNamesLargeRadioButton;
    private javax.swing.JRadioButton otherNamesRegularRadioButton;
    private javax.swing.JRadioButton otherNamesSmallRadioButton;
    // End of variables declaration//GEN-END:variables

}