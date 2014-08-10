package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Rectangle;
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

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneLayout;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;

import storage.Storage;
import storage.StorageException;
import storage.Visit;

public class LookPanel extends JPanel implements ActionListener, KeyListener {
	
	private static final long serialVersionUID = -7521824039604344258L;
	
	// Field labels: five steps
	private static final String LABEL_CHOOSE   = "Choose"; 
	private static final String LABEL_NAME     = "Name"; 
	private static final String LABEL_DATE     = "Date (dd/mm/YYYY)"; 
	private static final String LABEL_SEARCH   = "Search";
	private static final String LABEL_SHOW_ALL = "Show all";
	private static final String LABEL_BY_NAME  = "by Name";
	private static final String LABEL_BY_DATE  = "by Date";
	private static final String LABEL_NEXT     = "Next";
	private static final String LABEL_SAVE     = "Save";
	private static final String LABEL_LOOK_UP  = "New Look Up";
	private static final String LABEL_VISITS   = "Visits: ";
	
	private static final String STATUS_SUCCESS   = "Success!";
	private static final String STATUS_NO_VISITS = "No visits found";
	
	private static final String ERROR_CHOOSE     = "Choose one option";
	private static final String ERROR_NAME       = "Name can not be empty";
	private static final String ERROR_EMPTY_DATE = "Date can not be empty";
	private static final String ERROR_DATE       = "Invalid date";
	private static final String ERROR_SYSTEM     = "System error";
	
    final static int extraWindowWidth = 100;
	
    // Reusable GUI elements:
    
	// Error label
	private JLabel errorLabel;
	
	// Status label
	private JLabel statusLabel;
	
	// Label
	private JLabel label;
	
	// Search/Show all radio buttons
    private JRadioButton chooseSearchRadio;
    private JRadioButton chooseShowRadio;
    
	// Search by Name/Date radio buttons
    private JRadioButton searchByNameRadio;
    private JRadioButton searchByDateRadio;
	  
	// Text field
	private TextField textField;
    
    // Next button
	private JButton nextButton;
	
	// Cancel button
	private JButton cancelButton;
	
    // Storage
    private Storage storage;
    
    // Visits
    private Vector<Visit> visits;
    
    // Visitor list pane
    private JPanel listPane = new JPanel();
    // List of visits to show
    private JList<Visit> list;
	private DefaultListModel<Visit> listModel;
	

	//Make the panel wider than it really needs, so
    //the window's wide enough for the tabs to stay
    //in one row.
    public Dimension getPreferredSize() {
        Dimension size = super.getPreferredSize();     
        size.width  += extraWindowWidth;
        size.height += 300;
        return size;
    }
    
	public LookPanel(Storage storage) {
		visits = new Vector<Visit>();
		this.storage = storage;
		makeLayout();
		showStep("CHOICE");
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
	
	public void execute(String command) {
		switch(command) {
		case "NEW":
			listPane.removeAll();
			listPane.setVisible(false);
			label.setVisible(true);
			chooseSearchRadio.setVisible(true);
			chooseShowRadio.setVisible(true);
			nextButton.setText(LABEL_NEXT);
			showStep("CHOICE");
			break;
		case "CHOICE": 
			acceptChoice();
			break;
		case "SEARCH":
			acceptSearchOption();
			break;
		case "NAME":
			acceptName();
			break;
		case "DATE":
			acceptDate();
			break;
		case "CANCEL":
			cancelVisit();
			break;
		case "SAVE":
			saveVisits();
			break;
		}
	}
	
	private void acceptChoice()
	{
		if (chooseSearchRadio.isSelected()) {
			chooseSearchRadio.setSelected(false);
			showStep("SEARCH");
		} else if (chooseShowRadio.isSelected()) {
			chooseShowRadio.setSelected(false);
			visits = storage.get();
			showStep("SHOW");
		} else {
			errorLabel.setText(ERROR_CHOOSE);
		}
	}
	
	private void acceptSearchOption()
	{
		if (searchByNameRadio.isSelected()) {
			searchByNameRadio.setSelected(false);
			showStep("NAME");
		} else if (searchByDateRadio.isSelected()) {
			searchByDateRadio.setSelected(false);
			showStep("DATE");
		} else {
			errorLabel.setText(ERROR_CHOOSE);
		}
	}
	
	private void acceptName() {
		if (textField.getText().length() == 0) {
			errorLabel.setText(ERROR_NAME);
		} else {
			visits = storage.get(textField.getText());
			showStep("SHOW");
		}
	}
	
	private void acceptDate() {
		if (textField.getText().length() == 0) {
			errorLabel.setText(ERROR_EMPTY_DATE);
		} else {
			String text = textField.getText();
			try {
				Date date = Visit.dateFormat.parse(text);
				visits = storage.get(date);
				showStep("SHOW");
			} catch (ParseException e) {
				errorLabel.setText(ERROR_DATE);
			}
		}
	}
	
	private void cancelVisit()
	{
		int index = list.getSelectedIndex();
		if(index < 0)
			return;
		System.out.println(index);
		
		try {
			storage.delete(listModel.getElementAt(index));
		} catch (StorageException e) {
			showError(ERROR_SYSTEM);
		}
		listModel.removeElementAt(index);

	    nextButton.setText(LABEL_SAVE);
	    nextButton.setActionCommand("SAVE");
	}
	
	private void saveVisits()
	{
		if(!storage.save()) {
			showError(ERROR_SYSTEM);
		} else {
			showSuccess(STATUS_SUCCESS);
		}
	    nextButton.setText(LABEL_LOOK_UP);
	    nextButton.setActionCommand("NEW");
	}

	private void showError(String message)
	{
		errorLabel.setText(message);
		errorLabel.setVisible(true);
	}
	
	private void showSuccess(String message)
	{
		statusLabel.setText(message);
		statusLabel.setVisible(true);
	}
	
	/**
	 * Show current step
	 * 
	 * @param step
	 */
	private void showStep(String step) {
		errorLabel.setVisible(false);
		statusLabel.setVisible(false);
		switch (step) {
		case "CHOICE":
			label.setText(LABEL_CHOOSE);
			
			textField.setVisible(false);
			searchByNameRadio.setVisible(false);
			searchByDateRadio.setVisible(false);
			
			nextButton.setActionCommand("CHOICE");
			break;
		case "SEARCH":
			label.setText(LABEL_SEARCH);
			
			chooseSearchRadio.setVisible(false);
			chooseShowRadio.setVisible(false);
			searchByNameRadio.setVisible(true);
			searchByDateRadio.setVisible(true);			
		
			nextButton.setActionCommand("SEARCH");
			break;
		case "NAME":
			label.setText(LABEL_NAME);
			
			searchByNameRadio.setVisible(false);
			searchByDateRadio.setVisible(false);			
			textField.setVisible(true);
			
			nextButton.setActionCommand("NAME");
			break;
		case "DATE":
			label.setText(LABEL_DATE);
			
			searchByNameRadio.setVisible(false);
			searchByDateRadio.setVisible(false);			
			textField.setVisible(true);
			
			nextButton.setActionCommand("DATE");
			break;
		case "SHOW":
			label.setVisible(false);
			chooseSearchRadio.setVisible(false);
			chooseShowRadio.setVisible(false);
			searchByNameRadio.setVisible(false);
			searchByDateRadio.setVisible(false);	
			textField.setVisible(false);
			
			
			showVisits();
			
			nextButton.setText(LABEL_LOOK_UP);
			nextButton.setActionCommand("NEW");
			break;	
		}
	}

	private void showVisits() {
		if (visits.size() == 0) {
			showSuccess(STATUS_NO_VISITS);
			return;
		}

        for (Visit v : visits) {
        	listModel.addElement(v);
        }
 
        
        listPane.removeAll();
        listPane.setLayout(new BoxLayout(listPane, BoxLayout.PAGE_AXIS));
        listPane.setPreferredSize(new Dimension(300, 300));
        listPane.add(Box.createRigidArea(new Dimension(0,5)));      
        listPane.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        listPane.setVisible(true);
        
		list = new JList<Visit>();
        listModel = new DefaultListModel<Visit>();
        list.setModel(listModel);
        list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);    
        JScrollPane listScroller = new JScrollPane(list);
        listScroller.setAlignmentX(LEFT_ALIGNMENT);
        listPane.add(listScroller);
        
        JLabel label = new JLabel(LABEL_VISITS);
        label.setLabelFor(list);
        listPane.add(label);
        
        cancelButton = new JButton("-");
		cancelButton.addActionListener(this);
		cancelButton.addKeyListener(this);	
		cancelButton.setActionCommand("CANCEL");
        listPane.add(cancelButton);
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
	
	private void initErrorPanel()
	{
	    JPanel northPnl  = new JPanel();
	    
		errorLabel = new JLabel();
		errorLabel.setFont(new Font("Verdana", Font.BOLD, 12));
		errorLabel.setForeground(new Color(230, 36, 36));
		errorLabel.setBorder(new EmptyBorder(10, 10, 10, 10));
		northPnl.add(errorLabel);
		
		statusLabel = new JLabel();
		statusLabel.setFont(new Font("Verdana", Font.BOLD, 12));
		statusLabel.setForeground(new Color(66, 163, 14));
		statusLabel.setBorder(new EmptyBorder(10, 10, 10, 10));
		northPnl.add(statusLabel);
		
		add(northPnl, BorderLayout.NORTH);
	}
	

	private void initInputPanel()
	{
		label = new JLabel();
		label.setBorder(new EmptyBorder(10, 10, 10, 10));
		
		textField = new TextField("", 20);
		textField.addActionListener(this);
		textField.addKeyListener(this);	
		textField.requestFocusInWindow();
		
	    chooseSearchRadio = new JRadioButton(LABEL_SEARCH);
	    chooseShowRadio   = new JRadioButton(LABEL_SHOW_ALL);
	    
	    searchByNameRadio  = new JRadioButton(LABEL_BY_NAME);
	    searchByDateRadio  = new JRadioButton(LABEL_BY_DATE);
		
		ButtonGroup chooseGroup = new ButtonGroup();
		chooseGroup.add(chooseSearchRadio);
		chooseGroup.add(chooseShowRadio);
		
		ButtonGroup searchGroup = new ButtonGroup();
		searchGroup.add(searchByNameRadio);
		searchGroup.add(searchByDateRadio);

	    JPanel centerPnl = new JPanel();
		centerPnl.add(label);
		centerPnl.add(textField);
		centerPnl.add(chooseSearchRadio);
		centerPnl.add(chooseShowRadio);
		centerPnl.add(searchByNameRadio);
		centerPnl.add(searchByDateRadio);
		
		centerPnl.add(listPane);
		
		add(centerPnl, BorderLayout.CENTER);
		centerPnl.setLayout(new GridBagLayout());
		centerPnl.setBorder(new EmptyBorder(10, 10, 10, 10));	
	}	
	
	private void initNextButtonPanel()
	{
		nextButton = new JButton(LABEL_NEXT);
		nextButton.addKeyListener(this);
		nextButton.addActionListener(this);
		nextButton.setMnemonic(KeyEvent.VK_ENTER);
	    JPanel southPnl  = new JPanel();
		southPnl.setLayout(new GridBagLayout());
		southPnl.setBorder(new EmptyBorder(10, 10, 10, 10));
		southPnl.add(nextButton);	
		add(southPnl, BorderLayout.SOUTH);	
	}
}