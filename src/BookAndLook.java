
/*
 * TabDemo.java
 */
 
import java.awt.*;

import javax.swing.*;
 
public class BookAndLook {
    final static String BOOK_PANEL = "BOOK";
    final static String LOOK_PANEL = "LOOK";
    
    private JPanel card1;
    private JPanel card2;
 
    public BookAndLook(Storage storage) {
        card1 = new BookPanel(storage);      
        card2 = new LookPanel(storage);
    }
    
    public void addComponentToPane(Container pane) {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab(BOOK_PANEL, card1);
        tabbedPane.addTab(LOOK_PANEL, card2);
        pane.add(tabbedPane, BorderLayout.CENTER);
    }
 
    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event dispatch thread.
     */
    private static void createAndShowGUI(Storage storage) {
        //Create and set up the window.
        JFrame frame = new JFrame("Book and look");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
 
        //Create and set up the content pane.
        BookAndLook demo = new BookAndLook(storage);
        demo.addComponentToPane(frame.getContentPane());
 
        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }
 
    public static void main(String[] args) {
      /* Use an appropriate Look and Feel */
        try {
            //UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
        } catch (UnsupportedLookAndFeelException ex) {
            ex.printStackTrace();
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
        } catch (InstantiationException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        /* Turn off metal's use of bold fonts */
        UIManager.put("swing.boldMetal", Boolean.FALSE);
         
        //Schedule a job for the event dispatch thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI(new Storage("visitors"));
            }
        });
    }
}
