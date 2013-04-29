package analysis.bytecode;
 
import analysis.bytecode.*;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;



public class GUI extends JFrame implements ActionListener{
	
	String newSelect;
	String[] alg = {"Circle", "Kamada-Kawai", "Fruchterman-Rheingold", "Spring", "Meyer's Self-Organising Graph (ISOM)"};
	static JMenuBar menu = new JMenuBar();
	JMenu file = new JMenu("File");
	JMenuItem open = new JMenuItem("Open");
	JLabel select = new JLabel("Select a Layout");
	JComboBox algorithms = new JComboBox(alg);
	JButton go = new JButton("Run");
	BorderLayout layoutGUI = new BorderLayout();
	Visualize viz = new Visualize();
	
	public void addComponents(final Container pane){
		final JPanel components = new JPanel();
		components.setLayout(layoutGUI);
		components.add(menu);
		menu.add(file);
		file.add(open);
		open.addActionListener(this.openListener());
		components.add(select, BorderLayout.NORTH);
		select.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		components.add(algorithms, BorderLayout.CENTER);
		algorithms.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		algorithms.addActionListener(this);
		components.add(go, BorderLayout.SOUTH);
		go.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		go.addActionListener(this.buttonListener());
		pane.add(components);
		
	}
	
	private ActionListener openListener(){
		return new OpenCommand();
	}
	
	private ActionListener buttonListener() {
		return new ButtonCommand();
	}

	public GUI(String name) {
        super(name);
        setResizable(false);
    }
	private static void createAndShowGUI() {
        //Create and set up the window.
        GUI frame = new GUI("Bytecode Analyser, Control Flow Diagrams and Metric Calculator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //Set up the content pane.
        frame.addComponents(frame.getContentPane());
        //Display the window.
        frame.setJMenuBar(menu);
        frame.pack();
        frame.setVisible(true);
    }
     
    public static void main(String[] args) {

        try {
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

        UIManager.put("swing.boldMetal", Boolean.FALSE);
    
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
	
}

	
	public void actionPerformed(ActionEvent arg0) {
		JComboBox cb = (JComboBox)arg0.getSource();
		newSelect = (String)cb.getSelectedItem();
		System.out.println(newSelect.toString());
	}
	
	class ButtonCommand implements ActionListener{

	
		public void actionPerformed(ActionEvent arg0) {
			Visualize vx = new Visualize();
			vx.formGraph();
			
		}
		
	}
	
	class OpenCommand implements ActionListener{
		public void actionPerformed(ActionEvent arg0){
			
		}
	}

}