package metrics;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Main {

	private static Data classData;

	public static void main(String[] args) throws Exception {
		/*
		 * This is the main method, which provides the interface to let the user
		 * select the class they want to analyse. This also works if the user
		 * selects a directory, the program will scan through the directory and
		 * find all the .class files.
		 */

		/*
		 * This is the code for the file manager. First it creates a file and
		 * then assigns it to the one that the user has selected.
		 */
		File selected = null;
		JFileChooser select = new JFileChooser();
		select.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
				"Java File", "java");
		select.setFileFilter(filter);
		select.setCurrentDirectory(new java.io.File("."));
		select.changeToParentDirectory();
		select.setMultiSelectionEnabled(false);
		if (select.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {

			selected = select.getSelectedFile();
			classData = new Data(selected.getName());
			getDirContents(selected);
			classData.printResults();
		}
		else
		{
			System.out.println("Please select a file");
		}
	}

	public static void getDirContents(File dir) throws Exception {
		//This method searches the directory given for any java files.
		try {
			//if dir is a .java file, create a new Metric
			if (dir.getName().endsWith(".java")) {
				//Create a metric for the file
				File input = new File(dir.getCanonicalPath());
				Metric m = new Metric(input, classData);
			} else {
				//Else if there is none, then search the folder for any other .java files
				for (File file : dir.listFiles()) {
					if (file.getName().endsWith(".java")) {
						//Create a metric for the file
						File input = new File(file.getCanonicalPath());
						Metric m = new Metric(input, classData);
					} else if (file.isDirectory()) {
						//If it is a directory call recursively to search for .java files.
						getDirContents(file);
					}
				}
			}
		} catch (IOException e) {
			//Catch I/O exceptions
			e.printStackTrace();
		}

	}
}
