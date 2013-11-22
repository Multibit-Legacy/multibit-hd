package org.multibit.hd.ui.swing;

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
import javax.swing.*;
import java.awt.*;

class SplitPane  extends 	JFrame
{
  private		JSplitPane	splitPaneV;
  private		JSplitPane	splitPaneH;
  private		JPanel		panel1;
  private		JPanel		panel2;
  private		JPanel		panel3;


  public SplitPane()
  {
    setTitle( "Split Pane Application" );
    setBackground( Color.gray );

    JPanel topPanel = new JPanel();
    topPanel.setLayout( new BorderLayout() );
    getContentPane().add( topPanel );

    // Create the panels
    createPanel1();
    createPanel2();
    createPanel3();

    // Create a splitter pane
    splitPaneV = new JSplitPane( JSplitPane.VERTICAL_SPLIT );
    topPanel.add( splitPaneV, BorderLayout.CENTER );

    splitPaneH = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT );
    splitPaneH.setLeftComponent( panel1 );
    splitPaneH.setRightComponent( panel2 );

    splitPaneV.setLeftComponent( splitPaneH );
    splitPaneV.setRightComponent( panel3 );
  }

  public void createPanel1()
  {
    panel1 = new JPanel();
    panel1.setLayout( new BorderLayout() );

    // Add some buttons
    panel1.add( new JButton( "North" ), BorderLayout.NORTH );
    panel1.add( new JButton( "South" ), BorderLayout.SOUTH );
    panel1.add( new JButton( "East" ), BorderLayout.EAST );
    panel1.add( new JButton( "West" ), BorderLayout.WEST );
    panel1.add( new JButton( "Center" ), BorderLayout.CENTER );

  }

  public void createPanel2()
  {
    panel2 = new JPanel();
    panel2.setLayout( new FlowLayout() );

    panel2.add( new JButton( "Button 1" ) );
    panel2.add( new JButton( "Button 2" ) );
    panel2.add( new JButton( "Button 3" ) );
  }

  public void createPanel3()
  {
    panel3 = new JPanel();
    panel3.setLayout( new BorderLayout() );
    panel3.setPreferredSize( new Dimension( 400, 100 ) );
    panel3.setMinimumSize( new Dimension( 100, 50 ) );

    panel3.add( new JLabel( "Notes:" ), BorderLayout.NORTH );
    panel3.add( new JTextArea(), BorderLayout.CENTER );
  }

  public static void main( String args[] )
  {
    // Create an instance of the test application
    SplitPane mainFrame	= new SplitPane();
    mainFrame.pack();
    mainFrame.setVisible( true );
  }
}

