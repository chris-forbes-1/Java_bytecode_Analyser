package analysis.bytecode;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import metrics.Main;

import analysis.bytecode.GUI.OpenCommand;

public class frontScreen extends JFrame implements ActionListener {
	
	
	JButton metrics = new JButton("Metrics Calulation");
	JButton cfg = new JButton("Byte Code Control Flow Graph");
	BorderLayout layout = new BorderLayout();
	
	public void addComponents(final Container pane){
		final JPanel components = new JPanel();
		components.setLayout(layout);
		components.add(metrics, BorderLayout.WEST);
		metrics.addActionListener(this.metricCal());
		components.add(cfg, BorderLayout.EAST);
		cfg.addActionListener(this.controlFlow());
		pane.add(components); 
		
	}
	
	private ActionListener controlFlow() {
		return new flowGraph();
	}

	private ActionListener metricCal() {
		return new metrics();
	}

	public frontScreen(String name) {
        super(name);
        setResizable(false);
    }
	private static void createAndShowGUI2() {
        //Create and set up the window.
        frontScreen frame = new frontScreen("Bytecode Analyser, Control Flow Diagrams and Metric Calculator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //Set up the content pane.
        frame.addComponents(frame.getContentPane());
        //Display the window.
 
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
                createAndShowGUI2();
            }
        });
        
      
	
	
    }
	

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	class metrics implements ActionListener{

		
		public void actionPerformed(ActionEvent arg0) {
			Main met = new Main();
			try {
				Main.main(null);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}
	}
	
			
	class flowGraph implements ActionListener{

		
		public void actionPerformed(ActionEvent arg0) {
			GUI guu = new GUI("Byte Code Analyser, Control Flow Graph and Metrics Calculation");
			guu.main(null);
			
		}
		
	
	}
	

}
