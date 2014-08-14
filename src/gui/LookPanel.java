package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.text.ParseException;
import java.util.Collections;
import java.util.Date;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;

import storage.Storage;
import storage.StorageException;
import storage.Visit;

/** 
 * The {@code LookPanel} provides GUI to search and cancel visits.
 * 
 * Extends {@link JPanel}, instantiates inputs fields and button
 * like class variables to access from any method,
 * implements  {@link ActionListener} and {@link KeyListener}
 * interfaces to attach listeners to fields and button and 
 * process related events.
 * 
 * All language related information is grouped in 
 * language constants sets that might ease localization 
 * of the program. 
 * 
 * @author  Valeriya Slovikovskaya
*/
public class LookPanel extends JPanel implements ActionListener, KeyListener {
	
	private static final long serialVersionUID = -7521824039604344258L;
	
	/** 
	 * Language related constants
	 */
	
	/**
	 * Text field labels for booking steps
	 */
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
	private static final String LABEL_BACK     = "Back";
	private static final String LABEL_VISITORS = "choose a line and press the buton to cancel visit or list visitors";
	private static final String LABEL_CANCEL   = "cancel";
	private static final String LABEL_LIST     = "list";
	
	/**
	 * Status messages
	 */
	private static final String STATUS_SUCCESS   = "Success!";
	private static final String STATUS_NO_VISITS = "No visits found";
	
	/**
	 * Error messages
	 */
	private static final String ERROR_CHOOSE     = "Choose one option";
	private static final String ERROR_NAME       = "Name can not be empty";
	private static final String ERROR_EMPTY_DATE = "Date can not be empty";
	private static final String ERROR_DATE       = "Invalid date";
	private static final String ERROR_SYSTEM     = "System error";
	private static final String ERROR_VISITORS   = "Visitors list available only for guided visits";
	
	/**
	 * Extra window width to display list control
	 */
    final static int extraWindowWidth = 320;
	
    /**
     * Reusable GUI elements:
     */
    
	/**
	 *  Error label {@link JLabel}
	 */
	private JLabel errorLabel;
	
	/**
	 *  Status label {@link JLabel}
	 */
	private JLabel statusLabel;
	
	/**
	 *  Text field label {@link JLabel}
	 */
	private JLabel label;
	
	/**
	 *  Search/Show all radio buttons 
	 *  {@link JRadioButton}
	 */
    private JRadioButton chooseSearchRadio;
    private JRadioButton chooseShowRadio;
    
	/**
	 *  Search by Name/Date radio buttons
	 *  {@link JRadioButton}
	 */
    private JRadioButton searchByNameRadio;
    private JRadioButton searchByDateRadio;
	  
	/**
	 *  Text field {@link TextField}
	 */
	private TextField textField;
    
    /**
     *  Next-step button
     */
	private JButton nextButton;
	
	/**
	 *  Cancel button {@link JButton}
	 */
	private JButton cancelButton;
	
	/**
	 *  Visitors button to show visitors list for guided visits 
	 */
	private JButton visitorsButton;
	
    /**
     *  {@link Storage} instance
     */
    private Storage storage;
    
    /**
     * {@link Vector} collection of found visits
     */
    private Vector<Visit> visits;
    
    /**
     *  Visitor list pane {@link JPanel}
     */
    private JPanel listPane;
    
    /**
     *  List control elements
     */
    private JList<Visit> list;
    private JList<String> visitorsList;
	private DefaultListModel<Visit> visitsModel;
	private DefaultListModel<String> visitorsModel;
	private JScrollPane listScroller;
	private JScrollPane visitorsScroller;
	private JPanel buttonPane;
	private JLabel buttonsLabel;
	
	/**
	 * Makes the panel wider than it really needs, 
	 * so the window's wide enough
	 * for the tabs to stay in one row.
	 */
    public Dimension getPreferredSize() 
    {
        Dimension size = super.getPreferredSize();     
        size.width  += extraWindowWidth;
        size.height += 300;
        return size;
    }
    
	public LookPanel(Storage storage) 
	{
		visits = new Vector<Visit>();
		this.storage = storage;
		makeLayout();
		showStep("CHOICE");
	}
	
    /**
     *  Listeners interface methods
     */
	
	/**
	 * Action performed
	 * 
	 * Reads and executes command in response 
	 * on {@link ActionEvent}
	 * 
	 * @see #execute(String)
	 */
	public void actionPerformed(ActionEvent e) 
	{
		if(e.getSource() == textField) {
			execute(nextButton.getActionCommand());
		} else {
			execute(e.getActionCommand());
		}		
	}	
	
	/**
	 * Key pressed
	 * 
	 * Reads and executes command in response 
	 * on {@link KeyEvent}
	 */
    public void keyPressed(KeyEvent e) 
    {
        if (e.getKeyCode() == KeyEvent.VK_ENTER){
            execute(nextButton.getActionCommand());
        }
    }
    
    public void keyReleased(KeyEvent arg0) {}
    public void keyTyped(KeyEvent arg0) {}
	
    /**
     * Executes event actions fired by 
     * user interaction with GUI
     * 
     * @see #acceptChoice()
     * @see #acceptSearchOption()
     * @see #acceptName()
     * @see #acceptDate()
     * @see #showVisitors()
     * 
     * @param action
     */
	public void execute(String command)
	{
		errorLabel.setVisible(false);
		switch(command) {
		case "NEW":
			visitsModel.removeAllElements();
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
		case "VISITORS":
			showVisitors();
			break;		
		case "BACK":
			backToVisits();
			break;
		}
	}
	
	/**
	 * Takes user choice: search for visits or 
	 * just list them - from input
	 */
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
	
	/**
	 * Reads chosen search option: 
	 * search by name or date
	 * from the input
	 */
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
	
	/**
	 * Takes visit's name from input
	 */
	private void acceptName() 
	{
		if (textField.getText().length() == 0) {
			errorLabel.setText(ERROR_NAME);
		} else {
			visits = storage.get(textField.getText());
			textField.setText("");
			textField.repaint();
			showStep("SHOW");
		}
	}
	
	/**
	 * Takes visit's date from input
	 */
	private void acceptDate() 
	{
		if (textField.getText().length() == 0) {
			errorLabel.setText(ERROR_EMPTY_DATE);
		} else {
			String text = textField.getText();
			textField.setText("");
			textField.repaint();
			try {
				Date date = Visit.dateFormat.parse(text);
				visits = storage.get(date);
				showStep("SHOW");
			} catch (ParseException e) {
				errorLabel.setText(ERROR_DATE);
			}
		}
	}
	
	/**
	 * Takes visit's index from the input,
	 * removes visit from the visits list {@link #visitsModel}
	 * and from the {@link Storage}
	 */
	private void cancelVisit()
	{
		int index = list.getSelectedIndex();
		if(index < 0)
			return;	
		try {
			storage.delete(visitsModel.getElementAt(index));
		} catch (StorageException e) {
			showError(ERROR_SYSTEM);
		}
		visitsModel.removeElementAt(index);

	    nextButton.setText(LABEL_SAVE);
	    nextButton.setActionCommand("SAVE");
	}
	
	/**
	 * Displays visitors names for guided visit
	 */
	private void showVisitors()
	{
		visitorsModel.removeAllElements();
		
		int index = list.getSelectedIndex();
		if(index < 0)
			return;
		Visit visit = visitsModel.getElementAt(index);
		if(!visit.hasGuide()) {
			showError(ERROR_VISITORS);
			return;
		}
		
		statusLabel.setText(visit.toString());
		statusLabel.setVisible(true);
		
		Vector<String> visitors = visit.getVisitorNames();
		Collections.sort(visitors);
		for(String name: visitors){
			visitorsModel.addElement(" " + String.format("%-40s", name.trim()));
		}	
		
		buttonsLabel.setVisible(false);
		buttonPane.setVisible(false);
		listScroller.setVisible(false);
        visitorsScroller.setVisible(true);	
	    nextButton.setText(LABEL_BACK);
	    nextButton.setActionCommand("BACK");
	}
	
	/**
	 * Navigates from visitors list to visits list
	 */
	private void backToVisits()
	{
		buttonsLabel.setVisible(true);
		buttonPane.setVisible(true);
		listScroller.setVisible(true);
        visitorsScroller.setVisible(false);		
	    nextButton.setText(LABEL_LOOK_UP);
	    nextButton.setActionCommand("NEW");
	}
		
	/**
	 * Displays visits found
	 */
	private void showVisits() 
	{
		if (visits.size() == 0) {
			showSuccess(STATUS_NO_VISITS);
			return;
		}

		visitsModel.removeAllElements();
        for (Visit v : visits) {
        	visitsModel.addElement(v);
        }
        
        listScroller.setVisible(true);
        visitorsScroller.setVisible(false);
        listPane.setVisible(true);
	}
	
	/**
	 * Save changes in the {@link Storage}
	 */
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

	/**
	 * Shows error message
	 * 
	 * @param message Error message
	 */
	private void showError(String message)
	{
		errorLabel.setText(message);
		errorLabel.setVisible(true);
	}
	
	/**
	 * Shows success message
	 * 
	 * @param message Success message
	 */
	private void showSuccess(String message)
	{
		statusLabel.setText(message);
		statusLabel.setVisible(true);
	}
	
	/**
	 * Shows current step view
	 * 
	 * @param step Step name
	 */
	private void showStep(String step)
	{
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

	/**
	 * Creates GUI layout,
	 * that consists of three parts:
	 * Error, Input and Next-button Panels
	 * 
	 * Error panel shows error messages
	 * Input panel contains text field and radio buttons
	 * to input data, and List control to display and 
	 * manage visits.
	 * 
	 * Next-button panel contains the Next-button 
	 * to fire next step action event
	 *
	 * @see #initErrorPanel()
	 * @see #initListPanel()
	 * @see #initInputPanel()
	 * @see #initNextButtonPanel()
	 */
	private void makeLayout()
	{
		this.setLayout(new BorderLayout());		
		
		// North panel
		initErrorPanel();
		
		// List panel (belongs to Center panel)
		initListPanel();
		
		// Center panel
		initInputPanel();
				
		// South panel
		initNextButtonPanel();
	}
	
	/**
	 * Initiates List panel 
	 */
	private void initListPanel() 
	{
		listPane = new JPanel();

		listPane.setPreferredSize(new Dimension(520, 300));
		listPane.add(Box.createRigidArea(new Dimension(0, 5)));
		listPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		listPane.setVisible(false);
		
		addScrollersToListPane();
		addButtonForListsManagement();
	}
	
	/**
	 * Places visitors list into scroll pane {@link JScrollPane}
	 */
	private void addScrollersToListPane()
	{
		list = new JList<Visit>();
		visitsModel = new DefaultListModel<Visit>();
		list.setModel(visitsModel);
		
		list.setFont(new Font("Courier", Font.BOLD, 12));
		list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		listScroller = new JScrollPane(list);
		listScroller.setAlignmentX(LEFT_ALIGNMENT);
		listPane.add(listScroller);
		
		visitorsList  = new JList<String>();
		visitorsModel = new DefaultListModel<String>();
		visitorsList.setModel(visitorsModel);
		
		visitorsList.setFont(new Font("Courier", Font.BOLD, 12));
		visitorsList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		visitorsScroller = new JScrollPane(visitorsList);
		visitorsScroller.setAlignmentX(LEFT_ALIGNMENT);
		visitorsScroller.setVisible(false);
		listPane.add(visitorsScroller);
	}
	
	/**
	 * Adds "cancel" and "list" buttons to {@link #listPane}
	 * to cancel selected visits and 
	 * to list visitors of selected guided visit  
	 */
	private void addButtonForListsManagement()
	{
		buttonPane = new JPanel();
		
		cancelButton = new JButton(String.format("%-8s", LABEL_CANCEL));
		cancelButton.addActionListener(this);
		cancelButton.addKeyListener(this);
		cancelButton.setActionCommand("CANCEL");
		buttonPane.add(cancelButton);
		
		visitorsButton = new JButton(String.format("%-8s", LABEL_LIST));
		visitorsButton.addActionListener(this);
		visitorsButton.addKeyListener(this);
		visitorsButton.setActionCommand("VISITORS");
		buttonPane.add(visitorsButton);
		
		buttonsLabel = new JLabel(LABEL_VISITORS);
		listPane.add(buttonsLabel);
		listPane.add(buttonPane);
	}
	
	/**
	 * Initiates Error panel elements
	 */
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
	
	/**
	 * Initiates Input panel elements
	 */
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
	
	/**
	 * Initiates Next-button-panel elements
	 */
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