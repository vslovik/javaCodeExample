package gui;
import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Robot;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;

import storage.Storage;
import storage.StorageException;
import storage.Visit;

// ToDo 
// 1. Focus to text field
// 2. Enter button input
// http://stackoverflow.com/questions/13731710/allowing-the-enter-key-to-press-the-submit-button-as-opposed-to-only-using-mo
public class BookPanel extends JPanel implements ActionListener, KeyListener {
	
	// Field labels: five steps
	private static final String LABEL_NAME = "Enter Name"; // 1.
	private static final String LABEL_DATE = "Enter Date (dd/mm/YYYY)"; // 2.
	private static final String LABEL_NUMBER = "Enter Number of visitors"; // 3.
	private static final String LABEL_GUIDE = "Do you need a guide?"; // 4.
	private static final String LABEL_REDUCTION = "Would you ask for reduction?"; // 5.
	private static final String LABEL_PRICE = "Price: "; // 6.
	private static final String LABEL_SUCCESS = "You booked your visit!"; // 7.
	private static final String LABEL_VISITOR = "Visitors names: "; // 5.

	
	String[] steps = {"NAME", "DATE", "NUMBER", "GUIDE", "REDUCTION", "PRICE", "SUCCESS"};
	int step = 0;
	
	JLabel errorLabel = new JLabel();
	
	// Label
	private JLabel label = new JLabel();
	
	// Text field
	private TextField textField = new TextField("", 20);
	
	// Yes/No radio buttons
    private JRadioButton yesRadio = new JRadioButton("Yes");
    private JRadioButton noRadio  = new JRadioButton("No");
	  
    // Next button
	private JButton nextButton = new JButton("Next");
	
	// Three panels to place elements
    private JPanel northPnl  = new JPanel();
    private JPanel centerPnl = new JPanel();
    private JPanel southPnl  = new JPanel();
	
    // Visit to save
    private Visit visit = new Visit();
    
    // Status lines
    Vector<String> statusLines = new Vector<String>();
       
    // Status list pane
    JPanel listPane = new JPanel();
    
    // List of visitors names
    Vector<String> visitors = new Vector<String>();
    
    // Storage
    private Storage storage;

    final static int extraWindowWidth = 100;
    
	private static final long serialVersionUID = 1L;

	//Make the panel wider than it really needs, so
    //the window's wide enough for the tabs to stay
    //in one row.
    public Dimension getPreferredSize() {
        Dimension size = super.getPreferredSize();
        size.width += extraWindowWidth;
        return size;
    }
	public BookPanel(Storage storage) {
		this.storage = storage;
		makeLayout();
		showStep("NAME");
	}
	
	public void actionPerformed(ActionEvent e) {
		execute(nextButton.getActionCommand());
	}	
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode()==KeyEvent.VK_ENTER){
            execute(nextButton.getActionCommand());
        }
    }
    public void keyReleased(KeyEvent arg0) {}
    public void keyTyped(KeyEvent arg0) {}
    
    private void execute(String action)
    {
		switch(action) {
		case "NEW":
			renewStatusPanel();
			step = 0;
			visitors = new Vector<String>();
			showStep(steps[step]);
			break;
		case "NAME": 
			acceptName();			
			break;
		case "DATE":
			acceptDate();
			break;
		case "NUMBER":
			acceptNumber();				
			break;
		case "GUIDE":
			acceptGuide();				
			break;
		case "REDUCTION":
			acceptReduction();
			break;
		case "VISITOR":
			acceptVisitor();
			break;
		case "PRICE":
			doSave();				
			break;
		}
    }

	private void acceptName() {
		if (textField.getText().length() == 0) {
			showError("Name can not be empty!");			
		} else {
			visit.setName(textField.getText());
			showStatus("Name: " + textField.getText());
			textField.setText("");
			textField.repaint();
			showStep(steps[++step]);
		}
	}
	
	private void acceptDate() {
		if (textField.getText().length() == 0) {
			showError("Date can not be empty!");
		} else {
			String text = textField.getText();
			try {
				SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
				Date date = dateFormat.parse(text);
				if (date.compareTo(new Date()) > 0) {
					visit.setDate(date);
					showStatus("Date: " + text);
					textField.setText("");
					textField.repaint();
					showStep(steps[++step]);
				} else {
					showError("You should choose the date in the future");
				}
			} catch (ParseException e) {
				showError("Wrong data format! Should be dd/mm/yy");
			}
		}
	}
	
	private void acceptNumber() {
		try {
			int number = Integer.parseInt(textField.getText());
			if (number == 0) {
				showError("Enter a valid number");
			}
			visit.setVisitorNumber(number);
			showStatus("Number of visitors: " + textField.getText());
			textField.setText("");
			textField.repaint();
			showStep(steps[++step]);
		} catch (NumberFormatException e) {
			showError("Enter a valid number");
		}
	}
	
	private void acceptGuide()
	{
		if (yesRadio.isSelected()) {
			showStatus("Guide needed: " + yesRadio.getText());
			yesRadio.setSelected(false);
			showStep("VISITOR");
		} else if (noRadio.isSelected()) {
			showStatus("Guide needed: " + noRadio.getText());
			noRadio.setSelected(false);
			showStep(steps[++step]);
		} else {
			showError("Tell if guide is needed");
		}
	}
	
	private void acceptReduction()
	{
		if (yesRadio.isSelected()) {
			showStatus("Reduction requested: " + yesRadio.getText());
			yesRadio.setSelected(false);
			if(visit.getVisitorNames().size() == 0) {
				showStep("VISITOR");
			} else {
				showStep("PRICE");
			}
		} else if (noRadio.isSelected()) {
			showStatus("Reduction requested: " + noRadio.getText());
			noRadio.setSelected(false);
			showStep("PRICE");
		} else {
			showError("Tell if reduction is requested");
		}
	}
	
	private boolean acceptVisitor()
	{
		if (textField.getText().length() == 0) {
			showError("Name can not be empty!");
		} else {
			visitors.add(textField.getText());
			showStatus("Visitor " + Integer.toString(visitors.size()) + ": " + textField.getText());
			textField.setText("");
			textField.repaint();
			if(visit.getVisitorNumber() == visitors.size()) {
				visit.setVisitorNames(visitors);
				if(steps[step] == "GUIDE") {
					visit.setGuide();
				} else {
					visit.setReduction();
				}
				showStep(steps[++step]);
			} else {
				showStep("VISITOR");
			}
		}
		
		return true;
	}
	
	private void doSave()
	{
		try {
			storage.put(visit);
		} catch (StorageException e) {
			showError("System error!");
		}
		if(!storage.save()) {
			showError("System error!");
		} else {
			showStep("SUCCESS");
		}
	}
	
	/**
	 * 
	 * @param step
	 */
	private void showStep(String stepName) {
		switch (stepName) {
		case "NAME":			
			label.setText(LABEL_NAME);
			textField.setVisible(true);
			textField.requestFocusInWindow();
			nextButton.setActionCommand(stepName);
			break;
		case "DATE":
			label.setText(LABEL_DATE);
			textField.requestFocusInWindow();
			nextButton.setActionCommand(stepName);
			break;
		case "NUMBER":
			label.setText(LABEL_NUMBER);
			textField.requestFocusInWindow();
			nextButton.setActionCommand(stepName);
			break;
		case "GUIDE":
			label.setText(LABEL_GUIDE);
			textField.setVisible(false);
			yesRadio.setVisible(true);
			noRadio.setVisible(true);
			this.repaint();
			nextButton.setActionCommand(stepName);
			break;
		case "REDUCTION":
			if(visit.getVisitorNumber() < Visit.applyReductionTreshold) {
				System.out.println(visit.getVisitorNumber());
				System.out.println(Visit.applyReductionTreshold);
				showStep(steps[++step]);
			} else {
				label.setText(LABEL_REDUCTION);
				textField.setVisible(false);
				yesRadio.setVisible(true);
				noRadio.setVisible(true);
				nextButton.setActionCommand(stepName);
			}
			break;	
		case "VISITOR":
			label.setText(LABEL_VISITOR + " " + Integer.toString(visitors.size() + 1) + ": ");
			textField.setVisible(true);
			textField.requestFocusInWindow();
			yesRadio.setVisible(false);
			noRadio.setVisible(false);
			nextButton.setActionCommand(stepName);
			break;
		case "PRICE":
			label.setText(LABEL_PRICE + visit.getPrice() + " euro");
			textField.setVisible(false);
			yesRadio.setVisible(false);
			noRadio.setVisible(false);
			nextButton.setText("Save");
			nextButton.setActionCommand(stepName);
			break;	
		case "SUCCESS":
			showStatus(LABEL_PRICE + visit.getPrice() + " euro");
			label.setText(LABEL_SUCCESS);
			nextButton.setText("New Booking");
			nextButton.setActionCommand("NEW");
			break;
		}
	}
	
	/**
	 * Make general layout
	 */
	private void makeLayout()
	{
		this.setLayout(new BorderLayout());		
		
		// North panel
		initStatusPanel();
		
		// Center panel
		initInputPanel();
				
		// South panel
		initNextButtonPanel();
	}
	
//	private void showStatus() {
//		
//		if (statusLines.size() == 0)
//			return;
//
//		
//		list.setFont(new Font("Verdana", Font.BOLD, 6));
//		list.setForeground(new Color(66, 163, 14));
//		list.setBorder(new EmptyBorder(10, 10, 10, 10));
//		
//		list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
//		JScrollPane listScroller = new JScrollPane(list);
//		listScroller.setAlignmentX(LEFT_ALIGNMENT);
//
//		listPane.setLayout(new BoxLayout(listPane, BoxLayout.PAGE_AXIS));
//		listPane.setPreferredSize(new Dimension(300, 300));
//		JLabel label = new JLabel("Your booking: ");
//			
//		label.setLabelFor(list);
//		listPane.add(label);
//		listPane.add(Box.createRigidArea(new Dimension(0, 5)));
//		listPane.add(listScroller);
//		listPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
//		listPane.setVisible(true);
//
//		for (String s : statusLines) {
//			System.out.println(s);
//		}
//	}
	
	private void renewStatusPanel()
	{
		cleanStatusPanel();
		statusLines = new Vector<String>();
		statusLines.add("Your visit: ");
	}
	
	private void cleanStatusPanel()
	{
		listPane.removeAll();
		errorLabel.setVisible(false);
	}
	
	
	private void showStatus(String line)
	{
		cleanStatusPanel();
		
		statusLines.add(line);
		
		JList<String> list = new JList<String>(statusLines);
		list = new JList<String>(statusLines);
		list.setFont(new Font("Verdana", Font.BOLD, 12));
		list.setForeground(new Color(66, 163, 14));
		list.setBorder(new EmptyBorder(10, 10, 10, 10));
		
		listPane.add(list);
	}
	
	private void showError(String message) {
		cleanStatusPanel();

		errorLabel.setText(message);
		errorLabel.setFont(new Font("Verdana", Font.BOLD, 12));
		errorLabel.setForeground(new Color(230, 36, 36));
		errorLabel.setBorder(new EmptyBorder(10, 10, 10, 10));
		errorLabel.setVisible(true);

	}
	
	
	private void initStatusPanel()
	{
		statusLines.add("Your visit: ");
		northPnl.add(listPane);
		northPnl.add(errorLabel);
		
		add(northPnl, BorderLayout.NORTH);
	}
	
	private void initInputPanel()
	{
		add(centerPnl, BorderLayout.CENTER);
		centerPnl.setLayout(new GridBagLayout());
		centerPnl.setBorder(new EmptyBorder(10, 10, 10, 10));

		label.setBorder(new EmptyBorder(10, 10, 10, 10));
		centerPnl.add(label);
		centerPnl.add(textField);
		textField.addActionListener(this);
		textField.addKeyListener(this);
		
		
		//http://stackoverflow.com/questions/13563042/programmatically-trigger-a-key-events-in-a-jtextfield
//		textField.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent ae) {
//                System.out.println("Here..");
//            }
//        });
//		textField.requestFocusInWindow();
//        try {
//            Robot robot = new Robot();
//            robot.keyPress(KeyEvent.VK_ENTER);
//        } catch (AWTException e) {
//            e.printStackTrace();
//        }
		
		ButtonGroup group = new ButtonGroup();
		group.add(yesRadio);
		group.add(noRadio);
             
		centerPnl.add(yesRadio);
		centerPnl.add(noRadio);
	
		yesRadio.setVisible(false);
		noRadio.setVisible(false);
	}	
	
	private void initNextButtonPanel()
	{
		add(southPnl, BorderLayout.SOUTH);
		southPnl.setLayout(new GridBagLayout());
		southPnl.setBorder(new EmptyBorder(10, 10, 10, 10) );
		//southPnl.setBorder(BorderFactory.createCompoundBorder(new EmptyBorder(10, 10, 10, 10),  new EtchedBorder()));
		
		// NextButton
		southPnl.add(nextButton);		
		nextButton.addKeyListener(this);
		nextButton.addActionListener(this);
		nextButton.setMnemonic(KeyEvent.VK_ENTER);
		
	}
}
