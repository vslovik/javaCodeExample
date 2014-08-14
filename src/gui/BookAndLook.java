package gui;

import java.awt.*;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import storage.Storage;

/** 
 * The {@code BookAndLook} GUI to access visits storage 
 * with options to list, book, cancel and find visits.
 * 
 * Contain two tabs to access:
 * {@link BookPanel} to book visits and
 * {@LookPanel} to search and cancel them
 * 
 * Uses jaws swing library
 * 
 * @see {@link JFrame}
 * @see {@link JTabbedPane}
 * @see {@link UIManager}
 * 
 * @author  Valeriya Slovikovskaya
*/
public class BookAndLook {
	
	/**
	 * Window title
	 */
	final static String FRAME_TITLE = "Book and look";
	
	/**
	 * Book panel title
	 */
    final static String BOOK_PANEL  = "BOOK";
    
    /**
     * Look panel title
     */
    final static String LOOK_PANEL  = "LOOK";
    
    /**
     * Instance of {@link BookPanel}
     */
    private BookPanel card1;
    
    /**
     * Instance of {@link LookPanel}
     */
    private LookPanel card2;
 
    /**
     * Constructor. 
     * Instantiates both {@link BookPanel} 
     * and {@link LookPanel} panels
     * 
     * @param storage
     */
    public BookAndLook(Storage storage) {
        card1 = new BookPanel(storage);      
        card2 = new LookPanel(storage);
    }
    
    /**
     * Adds two panel to the GUI's pane
     * 
     * @param pane
     */
    public void addComponentToPane(Container pane) {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab(BOOK_PANEL, card1);
        tabbedPane.addTab(LOOK_PANEL, card2);
        pane.add(tabbedPane, BorderLayout.CENTER);
    }
 
    /**
     * Creates the GUI and show it. For thread safety,
     * this method should be invoked from the
     * event dispatch thread.
     */
    private static void createAndShowGUI(Storage storage) {
        //Creates and set up the window
        JFrame frame = new JFrame(FRAME_TITLE);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
 
        //Creates and set up the content pane
        BookAndLook demo = new BookAndLook(storage);
        demo.addComponentToPane(frame.getContentPane());
 
        //Displays the window
        frame.pack();
        frame.setVisible(true);
    }
 
    /**
     * Main method. 
     * Sets look and feel, schedules 
     * a job for GUI creation
     * 
     * @param args
     */
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
        } catch (UnsupportedLookAndFeelException e) {
        	System.out.println(e);
        } catch (IllegalAccessException e) {
        	System.out.println(e);
        } catch (InstantiationException e) {
        	System.out.println(e);
        } catch (ClassNotFoundException e) {
        	System.out.println(e);
        }

        UIManager.put("swing.boldMetal", Boolean.FALSE);
         
        //Schedule a job for the event dispatch thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI(new Storage());
            }
        });
    }
}
