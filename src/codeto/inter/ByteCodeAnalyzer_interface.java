package codeto.inter;
/**
 * @author Christopher Forbes, Heather Graham 
 */
import org.objectweb.asm.*;
import org.objectweb.asm.tree.InsnList;

import edu.uci.ics.jung.graph.Graph;


import java.io.File;

@SuppressWarnings("unused")
public interface ByteCodeAnalyzer_interface {

	/**
	 * @param File jarLocation
	 * @return byteCodeTextFileLocation 
	 * @desc Call this to extract the bytecode, then call codeAnalyzer to retrieve the list as a tree.
	 */
	public Graph<String, String> ByteCodeExtractor(File jarLocation); 
	
	/**
	 * @param File ByteCodeTextFileLocation
	 * @return InsnList 
	 * @desc Returns the implementation of the InsnList from the Asm framework. 
	 */
	public InsnList codeAnalyzer(File ByteCodeTextFileLocation);
}
