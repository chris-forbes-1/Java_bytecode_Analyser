package analysis.bytecode;

import java.awt.Color;
import java.awt.Dimension;
import java.io.File;
import java.util.Scanner;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import metrics.Data;

import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.FRLayout2;
import edu.uci.ics.jung.algorithms.layout.ISOMLayout;
import edu.uci.ics.jung.algorithms.layout.KKLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.SpringLayout2;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse.Mode;
import edu.uci.ics.jung.visualization.decorators.PickableVertexPaintTransformer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;


public class Visualize {
	static VisualizationViewer<String, String> vv;
	static Layout<String, String> layout;
	static Layout<String, String> layout2;
	static Layout<String, String> layout3;
	static Layout<String, String> layout4;
	static Layout<String, String> layout5;
	
	public Visualize(){
		

	}
	
	public void formGraph(){
		
		ByteCodeAnalyzer_Code graph = new ByteCodeAnalyzer_Code();
		
		//Find the graph
		JFileChooser JFC = new JFileChooser();
		File selected = null;
		JFC.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		JFC.setAcceptAllFileFilterUsed(false);
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
				"Java class File", "class");
		if (JFC.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {

			selected = JFC.getSelectedFile();
			
		}
		else
		{
			System.out.println("Please select a file");
		}
		
		Graph<String, String> graphGetter = graph.ByteCodeExtractor(selected);
	
		
		//Set the layout of the graph
				layout = new CircleLayout<String, String>(graph.Multigraph);
				layout.setSize(new Dimension(400,400));
				layout2 = new KKLayout<String, String>(graph.Multigraph);
				layout2.setSize(new Dimension(400,400));
				layout3 = new FRLayout2<String, String>(graph.Multigraph);
				layout3.setSize(new Dimension(400,400));
				layout4 = new SpringLayout2<String, String>(graph.Multigraph);
				layout4.setSize(new Dimension(400,400));
				layout5 = new ISOMLayout<String, String>(graph.Multigraph);
				layout5.setSize(new Dimension(400,400));
				
		//Visualization of the Graph- Set the size
			GUI gui = new GUI("Byte Code Analyser, Metrics Calculator and Control Flow Graph");
			if(gui.newSelect == "Circle"){
			vv = new VisualizationViewer<String, String>(layout);
		}
		else if(gui.newSelect == "Kamada-Kawai"){
			vv = new VisualizationViewer<String, String>(layout2);
		}
		else if(gui.newSelect == "Fruchterman-Rheingold"){
			vv = new VisualizationViewer<String, String>(layout3);
		}
		else if(gui.newSelect == "Spring"){
			vv = new VisualizationViewer<String, String>(layout4);
		}
		else {
			vv = new VisualizationViewer<String, String>(layout5);
		}
		vv.setPreferredSize(new Dimension(500, 500));
		
		//Set colour of nodes, and labels
		vv.getRenderContext().setVertexFillPaintTransformer(new PickableVertexPaintTransformer<String>(vv.getPickedVertexState(), Color.CYAN, Color.BLACK));
		vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
		vv.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller());
				
		//Zoom, Pan, Rotate, Shear
		DefaultModalGraphMouse mouse = new DefaultModalGraphMouse();
		mouse.setMode(Mode.TRANSFORMING);
		vv.setGraphMouse(mouse);
		
		JFrame frame = new JFrame("Test Graph");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(vv);
		frame.pack();
		frame.setVisible(true);
		
		
				
	
	}

	


	
}
