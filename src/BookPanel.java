import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;

// ToDo 
// 1. Focus to text field
// 2. Enter button input
@SuppressWarnings("serial")
public class BookPanel extends JPanel implements ActionListener {
	
	// Field labels: five steps
	private static final String LABEL_NAME = "Enter Name"; // 1.
	private static final String LABEL_DATE = "Enter Date (dd/mm/YYYY)"; // 2.
	private static final String LABEL_NUMBER = "Enter Number of visitors"; // 3.
	private static final String LABEL_GUIDE = "Do you need a guide?"; // 4.
	private static final String LABEL_REDUCTION = "Would you ask for reduction?"; // 5.
	private static final String LABEL_PRICE = "Price: "; // 6.
	private static final String LABEL_SUCCESS = "You booked your visit!"; // 7.
	
	// Error label
	private JLabel errorLabel = new JLabel();
	
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
		switch(e.getActionCommand()) {
		case "NEW":
			showStep("NAME");
			break;
		case "NAME": 
			if(acceptName()) {
				showStep("DATE");
			} 
			break;
		case "DATE":
			if(acceptDate()) {
				showStep("NUMBER");
			} 
			break;
		case "NUMBER":
			if(acceptNumber()) {
				showStep("GUIDE");
			} 
			break;
		case "GUIDE":
			if(acceptGuide()) {
				showStep("REDUCTION");
			} 
			break;
		case "REDUCTION":
			if(acceptReduction()) {
				showStep("PRICE");
			} 
			break;
		case "SUCCESS":
			if(doSave()) {
				showStep("SUCCESS");
			}
			break;
		}
	}

	/**
	 * Validate user input
	 * 
	 * @param step
	 * 
	 * @return
	 */
	private boolean acceptName() {
		// System.out.println(textField.getText());
		if (textField.getText().length() == 0) {
			errorLabel.setText("Name can not be empty!");
			return false;
		} else {
			visit.setName(textField.getText());
//			statusLabel.setText(statusLabel.getText() + "Name: " + textField.getText());
			statusLines.add("Name: " + textField.getText());
			textField.setText("");
			textField.repaint();
			errorLabel.setText("");
			errorLabel.repaint();		
			return true;
		}
	}
	
	private boolean acceptDate() {
		if (textField.getText().length() == 0) {
			errorLabel.setText("Date can not be empty!");
			return false;
		} else {
			String text = textField.getText();
			try {
				SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
				Date date = dateFormat.parse(text);
				if (date.compareTo(new Date()) > 0) {
					visit.setDate(date);
//					statusLabel.setText(statusLabel.getText() + "<br>Date: " + text);
//					statusLabel.setVisible(true);
					statusLines.add("Date: " + text);
					textField.setText("");
					textField.repaint();
					errorLabel.setText("");
					errorLabel.repaint();
					return true;
				} else {
//					statusLabel.setVisible(false);
					errorLabel.setText("You should choose the date in the future");
					errorLabel.repaint();
					return false;
				}
			} catch (ParseException e) {
//				statusLabel.setVisible(false);
				errorLabel.setText("Wrong data format! Should be dd/mm/yy");
				errorLabel.repaint();
				return false;
			}
		}
	}
	
	private boolean acceptNumber() {
		try {
			int number = Integer.parseInt(textField.getText());
			if (number == 0) {
//				statusLabel.setVisible(false);
				errorLabel.setText("Enter a valid number");
				errorLabel.repaint();
				return false;
			}
			visit.setVisitorNumber(number);
			statusLines.add("Number of visitors: " + textField.getText());
			showStatus();
			textField.setText("");
			textField.repaint();
			errorLabel.setText("");
			errorLabel.repaint();
			return true;

		} catch (NumberFormatException e) {
			errorLabel.setText("Enter a valid number");
			errorLabel.repaint();
			return false;
		}
	}
	
	private boolean acceptGuide()
	{
		if (yesRadio.isSelected()) {
			visit.setGuide();
			statusLines.add(" Guide needed: " + yesRadio.getText());
			showStatus();
			yesRadio.setSelected(false);
			return true;
		} else if (noRadio.isSelected()) {
			statusLines.add("Guide needed: " + noRadio.getText());
			showStatus();
			noRadio.setSelected(false);
			return true;
		} else {
			errorLabel.setText("Tell if guide is needed");
			errorLabel.repaint();
			return false;
		}
	}
	
	private boolean acceptReduction()
	{
		if (yesRadio.isSelected()) {
			visit.setReduction(); 
			statusLines.add("Reduction requested: " + yesRadio.getText());
			showStatus();
			yesRadio.setSelected(false);
			return true;
		} else if (noRadio.isSelected()) {
			statusLines.add("Reduction requested: " + noRadio.getText());
			showStatus();
			noRadio.setSelected(false);
			return true;
		} else {
			errorLabel.setText("Tell if reduction is requested");
			errorLabel.repaint();
			return false;
		}
	}
	
	
	private boolean acceptNames()
	{
		return true;
	}
	
	private boolean doSave()
	{
		storage.put(visit);
		if(!storage.save()) {
			errorLabel.setText("System error!");
			errorLabel.repaint();
			return false; 
		} else {
			statusLines.add("Price: " + visit.getPrice());
			showStatus();
			return true;
		}
	}
	
	/**
	 * Show current step
	 * 
	 * @param step
	 */
	private void showStep(String step) {
		switch (step) {
		case "NAME":
			label.setText(LABEL_NAME);
			nextButton.setActionCommand("NAME");
			break;
		case "DATE":
			label.setText(LABEL_DATE);
			nextButton.setActionCommand("DATE");
			break;
		case "NUMBER":
			label.setText(LABEL_NUMBER);
			nextButton.setActionCommand("NUMBER");
			break;
		case "GUIDE":
			label.setText(LABEL_GUIDE);
			textField.setVisible(false);
			yesRadio.setVisible(true);
			noRadio.setVisible(true);
			this.repaint();
			nextButton.setActionCommand("GUIDE");
			break;
		case "REDUCTION":
			label.setText(LABEL_REDUCTION);
			nextButton.setActionCommand("REDUCTION");
			break;	
		case "PRICE":
			label.setText(LABEL_PRICE + visit.getPrice() + " euro");
			yesRadio.setVisible(false);
			noRadio.setVisible(false);
			nextButton.setText("Save");
			nextButton.setActionCommand("SUCCESS");
			break;	
		case "SUCCESS":
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
		initErrorPanel();
		
		// Center panel
		initInputPanel();
				
		// South panel
		initNextButtonPanel();
	}
	
	private void showStatus() {
		if (statusLines.size() == 0)
			return;

		JList<String> list = new JList<String>(statusLines);
		
		list.setFont(new Font("Verdana", Font.BOLD, 6));
		list.setForeground(new Color(66, 163, 14));
		list.setBorder(new EmptyBorder(10, 10, 10, 10));
		
		list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		JScrollPane listScroller = new JScrollPane(list);
		listScroller.setAlignmentX(LEFT_ALIGNMENT);

		listPane.setLayout(new BoxLayout(listPane, BoxLayout.PAGE_AXIS));
		listPane.setPreferredSize(new Dimension(300, 300));
		JLabel label = new JLabel("Your booking: ");
			
		label.setLabelFor(list);s
		listPane.add(label);
		listPane.add(Box.createRigidArea(new Dimension(0, 5)));
		listPane.add(listScroller);
		listPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		listPane.setVisible(true);

		for (String s : statusLines) {
			System.out.println(s);
		}
	}
	
	private void initErrorPanel()
	{
		add(northPnl, BorderLayout.NORTH);
		
		errorLabel.setFont(new Font("Verdana", Font.BOLD, 12));
		errorLabel.setForeground(new Color(230, 36, 36));
		errorLabel.setBorder(new EmptyBorder(10, 10, 10, 10));
		northPnl.add(errorLabel);

	}
	
	private void initInputPanel()
	{
		add(centerPnl, BorderLayout.CENTER);
		centerPnl.setLayout(new GridBagLayout());
		centerPnl.setBorder(new EmptyBorder(10, 10, 10, 10));

		label.setBorder(new EmptyBorder(10, 10, 10, 10));
		centerPnl.add(label);
		centerPnl.add(textField);
		centerPnl.add(yesRadio);
		centerPnl.add(noRadio);
		centerPnl.add(listPane);
		
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
		nextButton.addActionListener(this);
	}
}