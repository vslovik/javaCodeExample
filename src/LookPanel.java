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
import javax.swing.ScrollPaneLayout;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;

// ToDo 
// 1. Focus to text field
// 2. Enter button input
public class LookPanel extends JPanel implements ActionListener {
	
	// Field labels: five steps
	private static final String LABEL_CHOOSE = "Choose"; 
	private static final String LABEL_SEARCH = "Search by"; 	
	private static final String LABEL_NAME = "Enter Name"; 
	private static final String LABEL_DATE = "Enter Date (dd/mm/YYYY)"; 
	
	// Error label
	private JLabel errorLabel = new JLabel();
	
	// Label
	private JLabel label = new JLabel();
	
	// Search/Show all radio buttons
    private JRadioButton chooseSearchRadio = new JRadioButton("Search");
    private JRadioButton chooseShowRadio   = new JRadioButton("Show all");
    
	// Search by Name/Date radio buttons
    private JRadioButton searchByNameRadio  = new JRadioButton("by Name");
    private JRadioButton searchByDateRadio  = new JRadioButton("by Date");
	  
	// Text field
	private TextField textField = new TextField("", 20);
    
    // Next button
	private JButton nextButton = new JButton("Next");
	
	// Three panels to place elements
    private JPanel northPnl  = new JPanel();
    private JPanel centerPnl = new JPanel();
    private JPanel southPnl  = new JPanel();
	
    // Storage
    private Storage storage;
    
    // Visits
    Vector<Visit> visits = new Vector<Visit>();
    
    // Visitor list pane
    JPanel listPane = new JPanel();
    
    // Current Page
    int currentPage = 1;

    final static int extraWindowWidth = 100;
    
	private static final long serialVersionUID = 1L;

	//Make the panel wider than it really needs, so
    //the window's wide enough for the tabs to stay
    //in one row.
    public Dimension getPreferredSize() {
        Dimension size = super.getPreferredSize();
        size.width += extraWindowWidth;
        size.height += 300;
        return size;
    }
	public LookPanel(Storage storage) {
		this.storage = storage;
		makeLayout();
		showStep("CHOICE");
	}
	
	public void actionPerformed(ActionEvent e) {
		switch(e.getActionCommand()) {
		case "NEW":
			listPane.removeAll();
			listPane.setVisible(false);
			label.setVisible(true);
			chooseSearchRadio.setVisible(true);
			chooseShowRadio.setVisible(true);
			nextButton.setText("Next");
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
			errorLabel.setText("Choose one option!");
			errorLabel.repaint();
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
			errorLabel.setText("Tell if reduction is requested");
			errorLabel.repaint();
		}
	}
	
	private void acceptName() {
		if (textField.getText().length() == 0) {
			errorLabel.setText("Name can not be empty!");
		} else {
			visits = storage.get(textField.getText());
			showStep("SHOW");
		}
	}
	
	private void acceptDate() {
		if (textField.getText().length() == 0) {
			errorLabel.setText("Date can not be empty!");
		} else {
			String text = textField.getText();
			try {
				SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
				Date date = dateFormat.parse(text);
				if (date.compareTo(new Date()) > 0) {
					visits = storage.get(date);
					showStep("SHOW");
				} else {
					errorLabel.setText("You should choose the date in the future");
					errorLabel.repaint();
				}
			} catch (ParseException e) {
				errorLabel.setText("Wrong data format! Should be dd/mm/yy");
				errorLabel.repaint();
			}
		}
	}

	/**
	 * Show current step
	 * 
	 * @param step
	 */
	private void showStep(String step) {
		errorLabel.setVisible(false);
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
			
			nextButton.setText("New Look Up");
			nextButton.setActionCommand("NEW");
			break;	
		}
	}
	

	
	private void showVisits() {
		if (visits.size() == 0)
			return;

		@SuppressWarnings("serial")
		JList<Visit> list = new JList<Visit>(visits) {
            //Subclass JList to workaround bug 4832765, which can cause the
            //scroll pane to not let the user easily scroll up to the beginning
            //of the list.  An alternative would be to set the unitIncrement
            //of the JScrollBar to a fixed value. You wouldn't get the nice
            //aligned scrolling, but it should work.
//            public int getScrollableUnitIncrement(Rectangle visibleRect,
//                                                  int orientation,
//                                                  int direction) {
//                int row;
//                if (orientation == SwingConstants.VERTICAL &&
//                      direction < 0 && (row = getFirstVisibleIndex()) != -1) {
//                    Rectangle r = getCellBounds(row, row);
//                    if ((r.y == visibleRect.y) && (row != 0))  {
//                        Point loc = r.getLocation();
//                        loc.y--;
//                        int prevIndex = locationToIndex(loc);
//                        Rectangle prevR = getCellBounds(prevIndex, prevIndex);
// 
//                        if (prevR == null || prevR.y >= r.y) {
//                            return 0;
//                        }
//                        return prevR.height;
//                    }
//                }
//                return super.getScrollableUnitIncrement(
//                                visibleRect, orientation, direction);
//            }
        };
 
        list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);

//        list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
//        list.setVisibleRowCount(-1);

        JScrollPane listScroller = new JScrollPane(list);
//        listScroller.setPreferredSize(new Dimension(300, 300));
        listScroller.setAlignmentX(LEFT_ALIGNMENT);
 

        listPane.setLayout(new BoxLayout(listPane, BoxLayout.PAGE_AXIS));
        listPane.setPreferredSize(new Dimension(300, 300));
        JLabel label = new JLabel("Visits:");
        label.setLabelFor(list);
        listPane.add(label);
        listPane.add(Box.createRigidArea(new Dimension(0,5)));
        listPane.add(listScroller);
        listPane.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        listPane.setVisible(true);

		

		for (Visit v : visits) {
			System.out.println(v);
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
		centerPnl.add(chooseSearchRadio);
		centerPnl.add(chooseShowRadio);
		centerPnl.add(searchByNameRadio);
		centerPnl.add(searchByDateRadio);
		centerPnl.add(listPane);
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