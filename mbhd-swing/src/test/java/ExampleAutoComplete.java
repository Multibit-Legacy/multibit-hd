/**
 * <p>[Pattern] to provide the following to {@link Object}:</p>
 * <ul>
 * <li></li>
 * </ul>
 * <p>Example:</p>
 * <pre>
 * </pre>
 *
 * @since 0.0.1
 *  
 */

import com.google.common.collect.Lists;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

/**
 * @author John
 */
public class ExampleAutoComplete extends javax.swing.JFrame {

  /**
   * Creates new form test2
   */
  public ExampleAutoComplete() {
    initComponents();

    final JTextField textfield = (JTextField) jComboBox1.getEditor().getEditorComponent();
    textfield.addKeyListener(new KeyAdapter() {
      public void keyReleased(KeyEvent ke) {

          SwingUtilities.invokeLater(new Runnable() {
            public void run() {
              comboFilter(textfield.getText());
            }
          });
      }
    });

    textfield.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        System.out.println("Selected: "+ textfield.getText());
        jComboBox1.hidePopup();
      }
    });
  }


  public void comboFilter(String enteredText) {


    List<String> filterArray = Lists.newArrayList("bob", "bobella");

    String str1 = "";


    filterArray.add(str1);

    if (filterArray.size() > 0) {
      jComboBox1.setModel(new DefaultComboBoxModel(filterArray.toArray()));
      jComboBox1.setSelectedItem(enteredText);
      jComboBox1.showPopup();
    } else {
      jComboBox1.hidePopup();
    }
  }

  @SuppressWarnings("unchecked")
  private void initComponents() {

    jComboBox1 = new javax.swing.JComboBox();

    setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

    jComboBox1.setEditable(true);

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(layout.createSequentialGroup()
          .addGap(87, 87, 87)
          .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addContainerGap(98, Short.MAX_VALUE))
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(layout.createSequentialGroup()
          .addGap(108, 108, 108)
          .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addContainerGap(172, Short.MAX_VALUE))
    );

    pack();
  }

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
    } catch (InstantiationException ex) {
    } catch (IllegalAccessException ex) {
    } catch (javax.swing.UnsupportedLookAndFeelException ex) {
    }
    //</editor-fold>

    /* Create and display the form */
    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        new ExampleAutoComplete().setVisible(true);
      }
    });
  }

  // Variables declaration - do not modify
  private javax.swing.JComboBox jComboBox1;
// End of variables declaration
}
