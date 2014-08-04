import javax.swing.*;

import java.awt.*;
import java.awt.event.*;

class MouseSpy implements MouseListener {
	public void mouseClicked(MouseEvent e) {
		System.out.println("ClickÃsuÃ(" + e.getX() + "," + e.getY() + ")");
	}

	public void mousePressed(MouseEvent e) {
		System.out.println("PremutoÃsuÃ(" + e.getX() + "," + e.getY() + ")");
	}

	public void mouseReleased(MouseEvent e) {
		System.out.println("RilasciatoÃsuÃ(" + e.getX() + "," + e.getY() + ")");
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}
}

//class AppFrame extends JFrame {
//	JPanel nordPnl = new JPanel();
//	JPanel centroPnl = new JPanel();
//	JPanel sudPnl = new JPanel();
//	//JLabel infoLbl = new Label("Selezionare:");
//	JCheckBox opz1Chk = new JCheckBox("Opz1");
//	JCheckBox opz2Chk = new JCheckBox("Opz2");
//	JButton okBtn = new JButton("OK");
//	JButton cancBtn = new JButton("Annulla");
//
//	public AppFrame() {
//		super("Esempio");
//		centroPnl.setLayout(new GridLayout(2, 1));
//		centroPnl.add(opz1Chk);
//		centroPnl.add(opz2Chk);
//		//nordPnl.add(infoLbl);
//		sudPnl.add(okBtn);
//		sudPnl.add(cancBtn);
//		getContentPane().add(nordPnl, BorderLayout.NORTH);
//		getContentPane().add(centroPnl, BorderLayout.CENTER);
//		getContentPane().add(sudPnl, BorderLayout.SOUTH);
//		pack();
//		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		//setLocation((dim.getWidth() - this.getWidth()) / 2,
		//		(dim.getHeight() - this.getHeight()) / 2);
//		this.addMouseListener(new MouseSpy());
//		setSize(200,200);
//		setVisible(true);
//	}
//}

//class AppFrame extends JFrame
//{
//	JLabel jl = new JLabel("Good lesson");
//	public void AppFrame1()
//	{
////		super("First window");
//		JButton one = new JButton("One");
//		JButton two = new JButton("Two");
//		JButton three = new JButton("Three");
//		Container c = this.getContentPane();
//		c.setLayout(new FlowLayout());
//		c.add(one);
//		c.add(two);
//		c.add(three);
//		this.setSize(200, 200);
//		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		this.setVisible(true);
//	}
//	
//	public AppFrame()
//	{
//		super("Grid Layout");
//		Container c = this.getContentPane();
//		c.setLayout(new GridLayout(4, 4));
//		for(int i = 0; i < 15; i++)
//			c.add(new JButton(String.valueOf(i)));
//		this.setSize(200, 200);
//		this.setVisible(true);
//	}
//}

public class Application {
	
	public static void main(String args[])
	{
		AppFrame frame = new AppFrame();
	}
	
//	public static void main(String args[])
//	{
//		JFrame win;
//			
//		/** 
//		// Example : Label
//		win = new JFrame("First window");
//		Container c = win.getContentPane();	
//		c.add(new JLabel("Good lesson"));
//		**/
//		
//		// Example: ComboBox
//		win = new JFrame("Example of ComboBox");
//		String list[] = new String[10];
//		for(int i = 0; i < list.length; i++)
//			list[i] = "Element number" + i;
//		JComboBox<String> cBox = new JComboBox<String>(list);
//		Container c = win.getContentPane();
//		c.add(cBox);
//		
//		/**
//		JTextField nameField = new JTextField(10);
//		c.add(nameField);
//		JLabel nameLabel = new JLabel("Name");
//		c.add(nameLabel);
//		JButton bookButton = new JButton("Book");
//		c.add(bookButton);
//		JCheckBox check1 = new JCheckBox("JCheck");
//		c.add(check1);	
//		JRadioButton radioButton1 = new JRadioButton("R1");
//		JRadioButton radioButton2 = new JRadioButton("R2");
//		JRadioButton radioButton3 = new JRadioButton("R2");
//		ButtonGroup group = new ButtonGroup();
//		group.add(radioButton1);
//		group.add(radioButton2);
//		group.add(radioButton3);
//		*/
//		// JButton bookButton = new JButton(new ImageIcon("hand.gif"));
//		//win.pack();
//		
//		
//		win.setSize(200, 200);
//		win.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		win.setVisible(true);
//	}
}
