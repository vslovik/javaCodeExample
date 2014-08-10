package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.text.ParseException;
import java.util.Date;
import java.util.Vector;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.EmptyBorder;
import storage.Storage;
import storage.StorageException;
import storage.Visit;

public class BookPanel extends JPanel implements ActionListener, KeyListener {
	
	private static final long serialVersionUID = 3824198048480501335L;
	
	// Language related constant
	
	// Field labels for booking steps
	private static final String LABEL_NAME      = "Name"; 
	private static final String LABEL_DATE      = "Date (dd/mm/YYYY)"; 
	private static final String LABEL_NUMBER    = "Visitors number"; 
	private static final String LABEL_GUIDE     = "Do you need a guide?"; 
	private static final String LABEL_REDUCTION = "Would you ask for reduction?"; 
	private static final String LABEL_PRICE     = "Price: "; 
	private static final String LABEL_SUCCESS   = "You booked your visit!"; 
	private static final String LABEL_VISITOR   = "Visitors names: "; 
	
	private static final String NEXT_STEP       = "Next"; 
	private static final String SAVE            = "Save";
	private static final String NEXT_BOOKING    = "Next booking"; 

	private static final String ERROR_EMPTY_NAME      = "Name can not be empty";
	private static final String ERROR_EMPTY_DATE      = "Date can not be empty";
	private static final String ERROR_DATE_IN_PAST    = "Choose a date in the future";
	private static final String ERROR_INVALID_DATE    = "Invalid date";
	private static final String ERROR_INVALID_NUMBER  = "Invalid number";
	private static final String ERROR_GUIDE           = "Tell if guide is needed";
	private static final String ERROR_REDUCTION       = "Tell if you ask for reduction";
	private static final String ERROR_SYSTEM          = "System error";
	
	private static final String STATUS_TITLE          = "Your visit: ";
	private static final String STATUS_NAME           = "Name: ";
	private static final String STATUS_DATE           = "Date: ";
	private static final String STATUS_NUMBER         = "Visitors number: ";
	private static final String STATUS_GUIDE          = "Guide: ";
	private static final String STATUS_REDUCTION      = "Reduction requested: ";
	private static final String STATUS_VISITOR        = "Visitor ";
	
	private static final String YES = "Yes";
	private static final String NO  = "No";
	
	private static final String CURRENCY = "euro";

	final static String[] steps = {"NAME", "DATE", "NUMBER", "GUIDE", "REDUCTION", "PRICE", "SUCCESS"};
	
	// Current step
	int step = 0;
	
	// Reusable GUI elements:
	
	// Error label
	private JLabel errorLabel;
	
    // Status 
    private DefaultListModel<String> statusList;
	
	// Current field label
	private JLabel label;
	
	// Text field
	private TextField textField;
	
	// Yes/No radio buttons
    private JRadioButton yesRadio;
    private JRadioButton noRadio;
	  
    // Next button
	private JButton nextButton;
	
    // Visit to book
    private Visit visit;
    
    // List of visitors names
    private Vector<String> visitors;
    
    // Storage to save visit
    private Storage storage;
    
	public BookPanel(Storage storage) {
		visit = new Visit();
		visitors = new Vector<String>();
		this.storage = storage;
		makeLayout();
		showStep("NAME");
	}

    // Listeners interface methods
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
			visit = new Visit();
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
			showError(ERROR_EMPTY_NAME);			
		} else {
			visit.setName(textField.getText());
			showStatus(STATUS_NAME + " " + textField.getText());
			textField.setText("");
			textField.repaint();
			showStep(steps[++step]);
		}
	}
	
	private void acceptDate() {
		if (textField.getText().length() == 0) {
			showError(ERROR_EMPTY_DATE);
		} else {
			String text = textField.getText();
			try {
				Date date = Visit.dateFormat.parse(text);
				if (date.compareTo(new Date()) > 0) {
					visit.setDate(date);
					showStatus(STATUS_DATE + " " + text);
					textField.setText("");
					textField.repaint();
					showStep(steps[++step]);
				} else {
					showError(ERROR_DATE_IN_PAST);
				}
			} catch (ParseException e) {
				showError(ERROR_INVALID_DATE);
			}
		}
	}
	
	private void acceptNumber() {
		try {
			int number = Integer.parseInt(textField.getText());
			if (number == 0) {
				showError(ERROR_INVALID_NUMBER);
			}
			visit.setVisitorNumber(number);
			showStatus(STATUS_NUMBER + " " + textField.getText());
			textField.setText("");
			textField.repaint();
			showStep(steps[++step]);
		} catch (NumberFormatException e) {
			showError(ERROR_INVALID_NUMBER);
		}
	}
	
	private void acceptGuide()
	{
		if (yesRadio.isSelected()) {
			showStatus(STATUS_GUIDE + " " +yesRadio.getText());
			yesRadio.setSelected(false);
			showStep("VISITOR");
		} else if (noRadio.isSelected()) {
			showStatus(STATUS_GUIDE + " " + noRadio.getText());
			noRadio.setSelected(false);
			showStep(steps[++step]);
		} else {
			showError(ERROR_GUIDE);
		}
	}
	
	private void acceptReduction()
	{
		if (yesRadio.isSelected()) {
			showStatus(STATUS_REDUCTION + " " + yesRadio.getText());
			yesRadio.setSelected(false);
			if(visit.getVisitorNames().size() == 0) {
				showStep("VISITOR");
			} else {
				showStep("PRICE");
			}
		} else if (noRadio.isSelected()) {
			showStatus(STATUS_REDUCTION + " " + noRadio.getText());
			noRadio.setSelected(false);
			showStep("PRICE");
		} else {
			showError(ERROR_REDUCTION);
		}
	}
	
	private boolean acceptVisitor()
	{
		if (textField.getText().length() == 0) {
			showError(ERROR_EMPTY_NAME);
		} else {
			visitors.add(textField.getText());
			showStatus(STATUS_VISITOR + " " + Integer.toString(visitors.size()) + ": " + textField.getText());
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
			showError(ERROR_SYSTEM);
		}
		if(!storage.save()) {
			showError(ERROR_SYSTEM);
		} else {
			showStep("SUCCESS");
		}
	}
	
	private void showStep(String stepName) {
		switch (stepName) {
		case "NAME":			
			label.setText(LABEL_NAME);
			textField.setVisible(true);
			nextButton.setActionCommand(stepName);
			break;
		case "DATE":
			label.setText(LABEL_DATE);
			nextButton.setActionCommand(stepName);
			break;
		case "NUMBER":
			label.setText(LABEL_NUMBER);
			nextButton.setActionCommand(stepName);
			break;
		case "GUIDE":
			label.setText(LABEL_GUIDE);
			textField.setVisible(false);
			yesRadio.setVisible(true);
			noRadio.setVisible(true);
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
			yesRadio.setVisible(false);
			noRadio.setVisible(false);
			nextButton.setActionCommand(stepName);
			break;
		case "PRICE":
			label.setText(LABEL_PRICE + visit.getPrice() + " " + CURRENCY);
			textField.setVisible(false);
			yesRadio.setVisible(false);
			noRadio.setVisible(false);
			nextButton.setText(SAVE);
			nextButton.setActionCommand(stepName);
			break;	
		case "SUCCESS":
			showStatus(LABEL_PRICE + visit.getPrice() + " " + CURRENCY);
			label.setText(LABEL_SUCCESS);
			nextButton.setText(NEXT_BOOKING);
			nextButton.setActionCommand("NEW");
			break;
		}
	}
	
	private void renewStatusPanel()
	{
		statusList.removeAllElements();
		errorLabel.setVisible(false);
	}
	
	private void showStatus(String line)
	{
		errorLabel.setVisible(false);
		statusList.addElement(line);
	}
	
	private void showError(String message) {
		errorLabel.setText(message);
		errorLabel.setVisible(true);
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
	
	private void initStatusPanel()
	{
		// Error
		errorLabel = new JLabel();
		errorLabel.setFont(new Font("Verdana", Font.BOLD, 12));
		errorLabel.setForeground(new Color(230, 36, 36));
		errorLabel.setBorder(new EmptyBorder(10, 10, 10, 10));
		
		// Status
		statusList = new DefaultListModel<String>();
		statusList.addElement(STATUS_TITLE);
		
		JList<String> list = new JList<String>();
		list.setModel(statusList);
		list.setFont(new Font("Verdana", Font.BOLD, 12));
		list.setForeground(new Color(66, 163, 14));
		list.setBorder(new EmptyBorder(10, 10, 10, 10));
		
	    JPanel listPane = new JPanel();
		listPane.add(list);
		
		JPanel northPnl  = new JPanel();
		northPnl.add(errorLabel);
		northPnl.add(listPane);
			
		northPnl.setLayout(new BoxLayout(northPnl, BoxLayout.Y_AXIS));
		northPnl.setBorder(new EmptyBorder(10, 10, 10, 10));
		add(northPnl, BorderLayout.NORTH);
	}
	
	private void initInputPanel()
	{
		JPanel centerPnl = new JPanel();
		
		label = new JLabel();
		label.setBorder(new EmptyBorder(10, 10, 10, 10));
		centerPnl.add(label);
		
		textField = new TextField("", 20);
		textField.addActionListener(this);
		textField.addKeyListener(this);	
		textField.requestFocusInWindow();
		centerPnl.add(textField);

		yesRadio = new JRadioButton(YES);
	    noRadio  = new JRadioButton(NO);
		// Group radio buttons
		ButtonGroup group = new ButtonGroup();
		group.add(yesRadio);
		group.add(noRadio);   
		// Hide radio buttons
		yesRadio.setVisible(false);
		noRadio.setVisible(false);
		// Add them
		centerPnl.add(yesRadio);
		centerPnl.add(noRadio);
			
		centerPnl.setLayout(new GridBagLayout());
		centerPnl.setBorder(new EmptyBorder(10, 10, 10, 10));
		add(centerPnl, BorderLayout.CENTER);	
	}	
	
	private void initNextButtonPanel()
	{	
		nextButton = new JButton(NEXT_STEP);
		nextButton.addKeyListener(this);
		nextButton.addActionListener(this);
		nextButton.setMnemonic(KeyEvent.VK_ENTER);
		
		JPanel southPnl  = new JPanel();
		southPnl.add(nextButton);			
		southPnl.setLayout(new GridBagLayout());
		southPnl.setBorder(new EmptyBorder(10, 10, 10, 10));
		
		add(southPnl, BorderLayout.SOUTH);	
	}
}
