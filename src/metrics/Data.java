package metrics;

import japa.parser.ast.stmt.Statement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JFrame;

public class Data {
	// Data stores all the data about the metrics
	
	private String projectName;
	private ArrayList<String> classNames = new ArrayList<String>();
	private ArrayList<ArrayList<String>> parameters = new ArrayList<ArrayList<String>>();
	private ArrayList<String> packageName = new ArrayList<String>();
	private ArrayList<ArrayList<String>> imports = new ArrayList<ArrayList<String>>();
	private ArrayList<ArrayList<String>> firstParameters = new ArrayList<ArrayList<String>>();
	private ArrayList<ArrayList<String>> methodNames = new ArrayList<ArrayList<String>>();
	private HashMap<String, String> tree = new HashMap<String, String>();
	private HashMap<String, String> cImplements = new HashMap<String, String>();
	private ArrayList<Integer> publicMethods = new ArrayList<Integer>();
	private ArrayList<Integer> spublicMethods = new ArrayList<Integer>();
	private int totalMethods = 0;
	private ArrayList<List<Statement>> statements = new ArrayList<List<Statement>>();
	private TextBox print = new TextBox();
	// private int methodNumber = 0;

	public Data(String name) {
		projectName = name;
	}

	public void setProjectName(String name) {
		projectName = name;
	}

	public void addClass(String name) {
		classNames.add(name);
	}

	public void addPackageName(String name) {
		packageName.add(name);
	}

	public void addImports(ArrayList<String> name) {
		imports.add(name);
	}

	public void addParameters(ArrayList<String> para) {
		parameters.add(para);
	}

	public void addAllParamerters(ArrayList<String> para) {
		firstParameters.add(para);
	}

	public void addMethods(ArrayList<String> names) {
		if (names != null)
			methodNames.add(names);
		else {
			ArrayList<String> a = new ArrayList<String>();
			a.add("None");
			methodNames.add(a);
		}
	}

	public void addMethod() {
		totalMethods = totalMethods + 1;
	}

	public void addTree(String a, String b) {
		tree.put(a, b);
	}

	public void addImplements(String a, String b) {
		cImplements.put(a, b);
	}

	public void addPublicMethod(int a) {
		publicMethods.add(a);
	}

	public void addSPublicMethod(int a) {
		spublicMethods.add(a);
	}

	public void addStatements(List<Statement> listStatements) {
		statements.add(listStatements);
	}

	public int calculateWMC(int classNo) {
		/*
		 * To calculate WMV, this program will assign the complexity of 1 to
		 * each method and so the WMV will be the total number of methods in the
		 * class
		 */
		return methodNames.get(classNo).size();
	}

	public int calculateDIT(int classNo) {
		/*
		 * To calculate DIT, store in Hashmap what classes and superclasses have
		 * been visited. Superclass can be key and classes can be value. Use
		 * ClassOrInterfaceDeclaration calling super after visiting.
		 */

		String className = classNames.get(classNo);
		return 0;
	}

	public int calculateNOC(int classNo) {
		/*
		 * NOC: Number of Children A class's number of children (NOC) metric
		 * simply measures the number of immediate descendants of the class.
		 * Could use a similar technique to DIT, visit all the methods and add
		 * classes to a HashMap
		 */

		String className = classNames.get(classNo);
		// System.out.println(tree.get(className));
		return 0;
	}

	public int calculateNPM(int classNo) {
		if (publicMethods.get(classNo) != null)
			return publicMethods.get(classNo);
		else
			return 0;
	}

	public int calculateLines(int classNo) {
		List<Statement> a = statements.get(classNo);
		return a.size();
	}

	public void godClass() {
		print.addText("Total Methods: " + totalMethods  + "\n");
		double average = (1.0) * totalMethods / classNames.size();
		print.addText("Average Method Size: " + average  + "\n");
		print.addText("The following classes may be God classes as they have more methods than average:"  + "\n");
		for (int i = 0; i < methodNames.size(); i++) {
			if (methodNames.get(i).size() > average) {

				print.addText(classNames.get(i)  + "\n");
			}
		}
	}

	public void printResults() {
		JFrame frame = new JFrame(projectName + " analysis");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		print.addText("Project: " + projectName + "\n");   
		print.addText("There are " + classNames.size() + " classes." + "\n");
		for (int i = 0; i < classNames.size(); i++) {
			print.addText("Class: " + classNames.get(i)+ "\n");
			print.addText("Package: " + packageName.get(i)+ "\n");
			if (imports.get(i) != null) {
				print.addText("Imports: [");
				for (int j = 0; j < imports.get(i).size(); j++) {
					if (j == imports.get(i).size() - 1)
						print.addText(imports.get(i).get(j) + "]" + "\n");
					else
						print.addText(imports.get(i).get(j) + ",");
				}
			} else
				print.addText("Imports: none" + "\n");
			print.addText("WMC: " + calculateWMC(i) + "\n");
			print.addText("NPM: " + calculateNPM(i) + " plus "
					+ spublicMethods.get(i) + " static public methods." + "\n");
			if (firstParameters.get(i) != null) {
				print.addText("Parameters: [");
				for (int j = 0; j < firstParameters.get(i).size(); j++) {
					if (j == firstParameters.get(i).size() - 1)
						print.addText(firstParameters.get(i).get(j) + "]" + "\n");
					else
						print.addText(firstParameters.get(i).get(j) + ",");
				}
			} else
				print.addText("Parameters: none" + "\n");
			print.addText("\n");
		}
//		System.out.println("Project: " + projectName);
//		System.out.println("There are " + classNames.size() + " classes.");
//		System.out.println();
//		for (int i = 0; i < classNames.size(); i++) {
//			System.out.println("Class: " + classNames.get(i));
//			System.out.println("Package: " + packageName.get(i));
//			if (imports.get(i) != null) {
//				System.out.print("Imports: [");
//				for (int j = 0; j < imports.get(i).size(); j++) {
//					if (j == imports.get(i).size() - 1)
//						System.out.println(imports.get(i).get(j) + "]");
//					else
//						System.out.print(imports.get(i).get(j) + ",");
//				}
//			} else
//				System.out.println("Imports: none");
//			System.out.println("WMC: " + calculateWMC(i));
//			System.out.println("NPM: " + calculateNPM(i) + " plus "
//					+ spublicMethods.get(i) + " static public methods.");
			// System.out.println("Number of lines in method: " +
			// calculateLines(i));
			// System.out.println("NOC: " + calculateNOC(i));
			// System.out.println("DIT: " + calculateDIT(i));
//			if (firstParameters.get(i) != null) {
//				System.out.print("Parameters: [");
//				for (int j = 0; j < firstParameters.get(i).size(); j++) {
//					if (j == firstParameters.get(i).size() - 1)
//						System.out.println(firstParameters.get(i).get(j) + "]");
//					else
//						System.out.print(firstParameters.get(i).get(j) + ",");
//				}
//			} else
//				System.out.println("Parameters: none");
			
//			System.out.println();
//		}
//		System.out.println("Parameter Sizes:");
		//System.out.println(parameters);
		godClass();
		
		frame.add(print);
		frame.pack();
        frame.setVisible(true);
	}
	
	
}
