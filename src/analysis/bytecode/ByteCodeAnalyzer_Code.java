package analysis.bytecode;

/**
 * @author christopher Forbnes
 * @desc create a memory based tree structure of the java byte code for .class files
 * Abandon all hope ye who enter here.
 */
//Java API imports
import java.util.List;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.lang.Class;

//ASM framework imports
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.MethodInsnNode;
//Jung framework imports
import edu.uci.ics.jung.algorithms.filters.KNeighborhoodFilter.EdgeType;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.graph.Forest;
//bespoke imports
import error.noU.ByteCodeAnalyzerError;
import codeto.inter.ByteCodeAnalyzer_interface;

@SuppressWarnings("unused")
public class ByteCodeAnalyzer_Code implements ByteCodeAnalyzer_interface {
	private static int nameMethod = 34000;
	ClassReader class_reader = null;
	ClassNode clno = null;
	Graph<String, String> Multigraph = new SparseMultigraph<String, String>();

	public ByteCodeAnalyzer_Code() {

	}

	@Override
	/**
	 * @see codeto.inter.ByteCodeAnalyzer_interface.java
	 */
	public Graph<String, String> ByteCodeExtractor(File jarLocation)
			throws ByteCodeAnalyzerError {
		try {
			class_reader = new ClassReader(new FileInputStream(jarLocation));
			// class_reader = new ClassReader(new
			// FileInputStream("bin/example/code/TestMe.class"));
			clno = new ClassNode();
			class_reader.accept(clno, 0);
			disassembleClass(clno);

		} catch (FileNotFoundException e) {
			throw new ByteCodeAnalyzerError("File not found exception");
		} catch (IOException e) {
			throw new ByteCodeAnalyzerError("IOException");
		}

		return Multigraph;
	}

	private void disassembleClass(ClassNode cN) {
		System.out.println("Class: " + cN.name);
		Multigraph.addVertex("END");
		List<MethodNode> mnl = cN.methods;

		for (int i = 0; i < mnl.size(); i++) {
			MethodNode method = mnl.get(i);
			try {
				disassembleMethod(method, cN);
			} catch (ByteCodeAnalyzerError r) {
				throw new ByteCodeAnalyzerError(r.toString());
			}
		}

	}

	private void disassembleMethod(MethodNode method, ClassNode Class)
			throws ByteCodeAnalyzerError {
		System.out.println("Method:" + method.name);
		InsnList instructions = method.instructions;
		
		for (int i = 0; i < instructions.size(); i++) {
			try {
				AbstractInsnNode Working_instruction = instructions.get(i);
				disassembleInstruction(Working_instruction, i, Class,
						instructions);
			} catch (ByteCodeAnalyzerError e) {
				throw new ByteCodeAnalyzerError(e.toString());
			}
		}
		Multigraph.addEdge(
				instructions.get(instructions.size() - 1).toString(),
				instructions.get(instructions.size() - 1).toString(), "END",
				edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
	}

	private void disassembleInstruction(AbstractInsnNode AIN, int index,
			ClassNode Class, InsnList Instruction) throws ByteCodeAnalyzerError {
		// System.out.println(Instruction.get(index).getOpcode());
		switch (AIN.getOpcode()) {
		case Opcodes.NEW: {
			// System.out.println(Instruction.get(index));
			// System.out.println(AIN.getNext().getClass().toString());
			// System.out.println(AIN.getNext().getClass().toString());
			// Multigraph.addVertex(Instruction.get(index).toString());
			// Multigraph.addVertex(AIN.getNext().getClass().getCanonicalName());
			if (Multigraph.containsVertex(Instruction.get(index).toString())
					&& Multigraph.containsVertex(AIN.getNext().toString())) {
				// add edge
				Multigraph.addEdge(Integer.toString(index),
						Instruction.get(index).toString(), AIN.getNext()
								.toString(),
						edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
			} else if (Multigraph.containsVertex(Instruction.get(index)
					.toString())
					|| Multigraph.containsVertex(AIN.getNext().toString())) {
				// check which does not exist

				if (Multigraph
						.containsVertex(Instruction.get(index).toString())) {
					Multigraph.addVertex(AIN.getNext().toString());
					Multigraph.addEdge(Integer.toString(index), Instruction
							.get(index).toString(), AIN.getNext().toString(),
							edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
				} else if (Multigraph.containsVertex(AIN.getNext().toString())) {
					Multigraph.addVertex(Instruction.get(index).toString());
					Multigraph.addEdge(Integer.toString(index), Instruction
							.get(index).toString(), AIN.getNext().toString(),
							edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
				}
				// add new vertex
				// add edge between existing
			} else {
				// add both vertex's
				Multigraph.addVertex(Instruction.get(index).toString());
				Multigraph.addVertex(AIN.getNext().toString());
				Multigraph.addEdge(Integer.toString(index),
						Instruction.get(index).toString(), AIN.getNext()
								.toString(),
						edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
			}
			Multigraph.addEdge(Integer.toString(nameMethod), AIN.getClass().toString(), Instruction.get(index--).toString(), edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
			break;
		}
		// graphnotation <name, from , to , direction>
		// remember to operate on the instruction not the class T_T
		case Opcodes.GETSTATIC: {
			if (Multigraph.containsVertex(Instruction.get(index).toString())
					&& Multigraph.containsVertex(AIN.getNext().toString())) {
				// add edge
				Multigraph.addEdge(Integer.toString(index),
						Instruction.get(index).toString(), AIN.getNext()
								.toString(),
						edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
			} else if (Multigraph.containsVertex(Instruction.get(index)
					.toString())
					|| Multigraph.containsVertex(AIN.getNext().toString())) {
				// check which does not exist

				if (Multigraph
						.containsVertex(Instruction.get(index).toString())) {
					Multigraph.addVertex(AIN.getNext().toString());
					Multigraph.addEdge(Integer.toString(index), Instruction
							.get(index).toString(), AIN.getNext().toString(),
							edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
				} else if (Multigraph.containsVertex(AIN.getNext().toString())) {
					Multigraph.addVertex(Instruction.get(index).toString());
					Multigraph.addEdge(Integer.toString(index), Instruction
							.get(index).toString(), AIN.getNext().toString(),
							edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
				}
				// add new vertex
				// add edge between existing
			} else {
				// add both vertex's
				Multigraph.addVertex(Instruction.get(index).toString());
				Multigraph.addVertex(AIN.getNext().toString());
				Multigraph.addEdge(Integer.toString(index),
						Instruction.get(index).toString(), AIN.getNext()
								.toString(),
						edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
				// add edge between them
			}
			Multigraph.addEdge(Integer.toString(nameMethod), AIN.getClass().toString(), Instruction.get(index--).toString(), edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
			break;
		}
		case Opcodes.GOTO: {
			// if A & B & C exist create edges from instruction to abstract list
			if (Multigraph.containsVertex(Instruction.get(index).toString()) == true
					&& Multigraph.containsVertex(AIN.getNext().toString()) == true
					&& Multigraph.containsVertex(AIN.getNext().getNext()
							.toString()) == true) {
				Multigraph.addEdge(Integer.toString(index),
						Instruction.get(index).toString(), AIN.getNext()
								.toString(),
						edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
				Multigraph.addEdge(Integer.toString(index),
						Instruction.get(index).toString(), AIN.getNext()
								.getNext().toString(),
						edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
			}
			// ifA or B or C exist find out which two exist add the other node
			// and create edges between them
			else if (Multigraph.containsVertex(Instruction.get(index)
					.toString()) == false
					|| Multigraph.containsVertex(AIN.getNext().toString()) == false
					|| Multigraph.containsVertex(AIN.getNext().getNext()
							.toString()) == false) {
				if (Multigraph
						.containsVertex(Instruction.get(index).toString()) == true) {
					if (Multigraph.containsVertex(AIN.getNext().toString()) == true) {
						Multigraph
								.addVertex(AIN.getNext().getNext().toString());
						Multigraph.addEdge(Integer.toString(index), Instruction
								.get(index).toString(), AIN.getNext()
								.toString(),
								edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
						Multigraph.addEdge(Integer.toString(index), Instruction
								.get(index).toString(), AIN.getNext().getNext()
								.toString(),
								edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
					} else if (Multigraph.containsVertex(AIN.getNext()
							.getNext().toString()) == true && AIN.getNext().getNext() != null) {
						Multigraph.addVertex(AIN.getNext().toString());
						Multigraph.addEdge(Integer.toString(index), Instruction
								.get(index).toString(), AIN.getNext()
								.toString(),
								edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
						Multigraph.addEdge(Integer.toString(index), Instruction
								.get(index).toString(), AIN.getNext().getNext()
								.toString(),
								edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
					}
				} else if (Multigraph.containsVertex(AIN.getNext().toString())) {
					if (Multigraph.containsVertex(Instruction.get(index)
							.toString())) {
						Multigraph
								.addVertex(AIN.getNext().getNext().toString());
						Multigraph.addEdge(Integer.toString(index), Instruction
								.get(index).toString(), AIN.getNext()
								.toString(),
								edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
						Multigraph.addEdge(Integer.toString(index), Instruction
								.get(index).toString(), AIN.getNext().getNext()
								.toString(),
								edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
					} else if (Multigraph.containsVertex(AIN.getNext()
							.getNext().toString())) {
						Multigraph.addVertex(Instruction.get(index).toString());
						Multigraph.addEdge(Integer.toString(index), Instruction
								.get(index).toString(), AIN.getNext()
								.toString(),
								edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
						Multigraph.addEdge(Integer.toString(index), Instruction
								.get(index).toString(), AIN.getNext().getNext()
								.toString(),
								edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
					}
				}
			} else {// **THIS IS CAUSING CRASHES**
					if(Multigraph.containsVertex(Instruction.get(index).toString()) == false){
						if(Multigraph.containsVertex(AIN.getNext().getNext().toString()) == false){
							if(Multigraph.containsVertex(AIN.getNext().getNext().toString())== false && AIN.getNext().getNext() != null){
								try{
									Multigraph.addVertex(Instruction.get(index).toString());
									Multigraph.addVertex(AIN.getNext().toString());
									Multigraph.addVertex(AIN.getNext().getNext().toString());
									
									Multigraph.addEdge(Integer.toString(index), Instruction.get(index).toString(),AIN.getNext().toString(),edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
								}catch (Exception e){
									System.out.println("TODO end of broken bit");
								}
							}else{
								Multigraph.addVertex(Instruction.get(index).toString());
								Multigraph.addVertex(AIN.getNext().toString());
							}
							
						}
					}
				// TODO end of broken bit

			}
			Multigraph.addEdge(Integer.toString(nameMethod), AIN.getClass().toString(), Instruction.get(index--).toString(), edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
			break;
		}
		case Opcodes.IF_ACMPEQ: {
			if (Multigraph.containsVertex(Instruction.get(index).toString())
					&& Multigraph.containsVertex(AIN.getNext().toString())
					&& Multigraph.containsVertex(AIN.getNext().getNext()
							.toString())) {
				Multigraph.addEdge(Integer.toString(index),
						Instruction.get(index).toString(), AIN.getNext()
								.toString(),
						edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
				Multigraph.addEdge(Integer.toString(index),
						Instruction.get(index).toString(), AIN.getNext()
								.getNext().toString(),
						edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
			}
			// ifA or B or C exist find out which two exist add the other node
			// and create edges between them
			else if (Multigraph.containsVertex(Instruction.get(index)
					.toString())
					|| Multigraph.containsVertex(AIN.getNext().toString())
					|| Multigraph.containsVertex(AIN.getNext().getNext()
							.toString())) {
				if (Multigraph
						.containsVertex(Instruction.get(index).toString()) == true) {
					if (Multigraph.containsVertex(AIN.getNext().toString())) {
						Multigraph
								.addVertex(AIN.getNext().getNext().toString());
						Multigraph.addEdge(Integer.toString(index), Instruction
								.get(index).toString(), AIN.getNext()
								.toString(),
								edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
						Multigraph.addEdge(Integer.toString(index), Instruction
								.get(index).toString(), AIN.getNext().getNext()
								.toString(),
								edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
					} else if (Multigraph.containsVertex(AIN.getNext()
							.getNext().toString())) {
						Multigraph.addVertex(AIN.getNext().toString());
						Multigraph.addEdge(Integer.toString(index), Instruction
								.get(index).toString(), AIN.getNext()
								.toString(),
								edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
						Multigraph.addEdge(Integer.toString(index), Instruction
								.get(index).toString(), AIN.getNext().getNext()
								.toString(),
								edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
					}
				} else if (Multigraph.containsVertex(AIN.getNext().toString())) {
					if (Multigraph.containsVertex(Instruction.get(index)
							.toString())) {
						Multigraph
								.addVertex(AIN.getNext().getNext().toString());
						Multigraph.addEdge(Integer.toString(index), Instruction
								.get(index).toString(), AIN.getNext()
								.toString(),
								edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
						Multigraph.addEdge(Integer.toString(index), Instruction
								.get(index).toString(), AIN.getNext().getNext()
								.toString(),
								edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
					} else if (Multigraph.containsVertex(AIN.getNext()
							.getNext().toString())) {
						Multigraph.addVertex(Instruction.get(index).toString());
						Multigraph.addEdge(Integer.toString(index), Instruction
								.get(index).toString(), AIN.getNext()
								.toString(),
								edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
						Multigraph.addEdge(Integer.toString(index), Instruction
								.get(index).toString(), AIN.getNext().getNext()
								.toString(),
								edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
					}
				}
			}
			Multigraph.addEdge(Integer.toString(nameMethod), AIN.getClass().toString(), Instruction.get(index--).toString(), edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
			break;
		}
		case Opcodes.IF_ACMPNE: {
			if (Multigraph.containsVertex(Instruction.get(index).toString())
					&& Multigraph.containsVertex(AIN.getNext().toString())
					&& Multigraph.containsVertex(AIN.getNext().getNext()
							.toString())) {
				Multigraph.addEdge(Integer.toString(index),
						Instruction.get(index).toString(), AIN.getNext()
								.toString(),
						edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
				Multigraph.addEdge(Integer.toString(index),
						Instruction.get(index).toString(), AIN.getNext()
								.getNext().toString(),
						edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
			}
			// ifA or B or C exist find out which two exist add the other node
			// and create edges between them
			else if (Multigraph.containsVertex(Instruction.get(index)
					.toString())
					|| Multigraph.containsVertex(AIN.getNext().toString())
					|| Multigraph.containsVertex(AIN.getNext().getNext()
							.toString())) {
				if (Multigraph
						.containsVertex(Instruction.get(index).toString()) == true) {
					if (Multigraph.containsVertex(AIN.getNext().toString())) {
						Multigraph
								.addVertex(AIN.getNext().getNext().toString());
						Multigraph.addEdge(Integer.toString(index), Instruction
								.get(index).toString(), AIN.getNext()
								.toString(),
								edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
						Multigraph.addEdge(Integer.toString(index), Instruction
								.get(index).toString(), AIN.getNext().getNext()
								.toString(),
								edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
					} else if (Multigraph.containsVertex(AIN.getNext()
							.getNext().toString())) {
						Multigraph.addVertex(AIN.getNext().toString());
						Multigraph.addEdge(Integer.toString(index), Instruction
								.get(index).toString(), AIN.getNext()
								.toString(),
								edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
						Multigraph.addEdge(Integer.toString(index), Instruction
								.get(index).toString(), AIN.getNext().getNext()
								.toString(),
								edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
					}
				} else if (Multigraph.containsVertex(AIN.getNext().toString())) {
					if (Multigraph.containsVertex(Instruction.get(index)
							.toString())) {
						Multigraph
								.addVertex(AIN.getNext().getNext().toString());
						Multigraph.addEdge(Integer.toString(index), Instruction
								.get(index).toString(), AIN.getNext()
								.toString(),
								edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
						Multigraph.addEdge(Integer.toString(index), Instruction
								.get(index).toString(), AIN.getNext().getNext()
								.toString(),
								edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
					} else if (Multigraph.containsVertex(AIN.getNext()
							.getNext().toString())) {
						Multigraph.addVertex(Instruction.get(index).toString());
						Multigraph.addEdge(Integer.toString(index), Instruction
								.get(index).toString(), AIN.getNext()
								.toString(),
								edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
						Multigraph.addEdge(Integer.toString(index), Instruction
								.get(index).toString(), AIN.getNext().getNext()
								.toString(),
								edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
					}
				}
			}
			Multigraph.addEdge(Integer.toString(nameMethod), AIN.getClass().toString(), Instruction.get(index--).toString(), edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
			break;
		}
		case Opcodes.IF_ICMPEQ: {
			if (Multigraph.containsVertex(Instruction.get(index).toString())
					&& Multigraph.containsVertex(AIN.getNext().toString())
					&& Multigraph.containsVertex(AIN.getNext().getNext()
							.toString())) {
				Multigraph.addEdge(Integer.toString(index),
						Instruction.get(index).toString(), AIN.getNext()
								.toString(),
						edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
				Multigraph.addEdge(Integer.toString(index),
						Instruction.get(index).toString(), AIN.getNext()
								.getNext().toString(),
						edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
			}
			// ifA or B or C exist find out which two exist add the other node
			// and create edges between them
			else if (Multigraph.containsVertex(Instruction.get(index)
					.toString())
					|| Multigraph.containsVertex(AIN.getNext().toString())
					|| Multigraph.containsVertex(AIN.getNext().getNext()
							.toString())) {
				if (Multigraph
						.containsVertex(Instruction.get(index).toString()) == true) {
					if (Multigraph.containsVertex(AIN.getNext().toString())) {
						Multigraph
								.addVertex(AIN.getNext().getNext().toString());
						Multigraph.addEdge(Integer.toString(index), Instruction
								.get(index).toString(), AIN.getNext()
								.toString(),
								edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
						Multigraph.addEdge(Integer.toString(index), Instruction
								.get(index).toString(), AIN.getNext().getNext()
								.toString(),
								edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
					} else if (Multigraph.containsVertex(AIN.getNext()
							.getNext().toString())) {
						Multigraph.addVertex(AIN.getNext().toString());
						Multigraph.addEdge(Integer.toString(index), Instruction
								.get(index).toString(), AIN.getNext()
								.toString(),
								edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
						Multigraph.addEdge(Integer.toString(index), Instruction
								.get(index).toString(), AIN.getNext().getNext()
								.toString(),
								edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
					}
				} else if (Multigraph.containsVertex(AIN.getNext().toString())) {
					if (Multigraph.containsVertex(Instruction.get(index)
							.toString())) {
						Multigraph
								.addVertex(AIN.getNext().getNext().toString());
						Multigraph.addEdge(Integer.toString(index), Instruction
								.get(index).toString(), AIN.getNext()
								.toString(),
								edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
						Multigraph.addEdge(Integer.toString(index), Instruction
								.get(index).toString(), AIN.getNext().getNext()
								.toString(),
								edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
					} else if (Multigraph.containsVertex(AIN.getNext()
							.getNext().toString())) {
						Multigraph.addVertex(Instruction.get(index).toString());
						Multigraph.addEdge(Integer.toString(index), Instruction
								.get(index).toString(), AIN.getNext()
								.toString(),
								edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
						Multigraph.addEdge(Integer.toString(index), Instruction
								.get(index).toString(), AIN.getNext().getNext()
								.toString(),
								edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
					}
				}
			}
			Multigraph.addEdge(Integer.toString(nameMethod), AIN.getClass().toString(), Instruction.get(index--).toString(), edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
			break;
		}
		case Opcodes.IF_ICMPGE: {
			if (Multigraph.containsVertex(Instruction.get(index).toString())
					&& Multigraph.containsVertex(AIN.getNext().toString())
					&& Multigraph.containsVertex(AIN.getNext().getNext()
							.toString())) {
				Multigraph.addEdge(Integer.toString(index),
						Instruction.get(index).toString(), AIN.getNext()
								.toString(),
						edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
				Multigraph.addEdge(Integer.toString(index),
						Instruction.get(index).toString(), AIN.getNext()
								.getNext().toString(),
						edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
			}
			// ifA or B or C exist find out which two exist add the other node
			// and create edges between them
			else if (Multigraph.containsVertex(Instruction.get(index)
					.toString())
					|| Multigraph.containsVertex(AIN.getNext().toString())
					|| Multigraph.containsVertex(AIN.getNext().getNext()
							.toString())) {
				if (Multigraph
						.containsVertex(Instruction.get(index).toString()) == true) {
					if (Multigraph.containsVertex(AIN.getNext().toString())) {
						Multigraph
								.addVertex(AIN.getNext().getNext().toString());
						Multigraph.addEdge(Integer.toString(index), Instruction
								.get(index).toString(), AIN.getNext()
								.toString(),
								edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
						Multigraph.addEdge(Integer.toString(index), Instruction
								.get(index).toString(), AIN.getNext().getNext()
								.toString(),
								edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
					} else if (Multigraph.containsVertex(AIN.getNext()
							.getNext().toString())) {
						Multigraph.addVertex(AIN.getNext().toString());
						Multigraph.addEdge(Integer.toString(index), Instruction
								.get(index).toString(), AIN.getNext()
								.toString(),
								edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
						Multigraph.addEdge(Integer.toString(index), Instruction
								.get(index).toString(), AIN.getNext().getNext()
								.toString(),
								edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
					}
				} else if (Multigraph.containsVertex(AIN.getNext().toString())) {
					if (Multigraph.containsVertex(Instruction.get(index)
							.toString())) {
						Multigraph
								.addVertex(AIN.getNext().getNext().toString());
						Multigraph.addEdge(Integer.toString(index), Instruction
								.get(index).toString(), AIN.getNext()
								.toString(),
								edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
						Multigraph.addEdge(Integer.toString(index), Instruction
								.get(index).toString(), AIN.getNext().getNext()
								.toString(),
								edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
					} else if (Multigraph.containsVertex(AIN.getNext()
							.getNext().toString())) {
						Multigraph.addVertex(Instruction.get(index).toString());
						Multigraph.addEdge(Integer.toString(index), Instruction
								.get(index).toString(), AIN.getNext()
								.toString(),
								edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
						Multigraph.addEdge(Integer.toString(index), Instruction
								.get(index).toString(), AIN.getNext().getNext()
								.toString(),
								edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
					}
				}
			}
			Multigraph.addEdge(Integer.toString(nameMethod), AIN.getClass().toString(), Instruction.get(index--).toString(), edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
			break;
		}
		case Opcodes.IF_ICMPGT: {
			if (Multigraph.containsVertex(Instruction.get(index).toString())
					&& Multigraph.containsVertex(AIN.getNext().toString())
					&& Multigraph.containsVertex(AIN.getNext().getNext()
							.toString())) {
				Multigraph.addEdge(Integer.toString(index),
						Instruction.get(index).toString(), AIN.getNext()
								.toString(),
						edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
				Multigraph.addEdge(Integer.toString(index),
						Instruction.get(index).toString(), AIN.getNext()
								.getNext().toString(),
						edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
			}
			// ifA or B or C exist find out which two exist add the other node
			// and create edges between them
			else if (Multigraph.containsVertex(Instruction.get(index)
					.toString())
					|| Multigraph.containsVertex(AIN.getNext().toString())
					|| Multigraph.containsVertex(AIN.getNext().getNext()
							.toString())) {
				if (Multigraph
						.containsVertex(Instruction.get(index).toString()) == true) {
					if (Multigraph.containsVertex(AIN.getNext().toString())) {
						Multigraph
								.addVertex(AIN.getNext().getNext().toString());
						Multigraph.addEdge(Integer.toString(index), Instruction
								.get(index).toString(), AIN.getNext()
								.toString(),
								edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
						Multigraph.addEdge(Integer.toString(index), Instruction
								.get(index).toString(), AIN.getNext().getNext()
								.toString(),
								edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
					} else if (Multigraph.containsVertex(AIN.getNext()
							.getNext().toString())) {
						Multigraph.addVertex(AIN.getNext().toString());
						Multigraph.addEdge(Integer.toString(index), Instruction
								.get(index).toString(), AIN.getNext()
								.toString(),
								edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
						Multigraph.addEdge(Integer.toString(index), Instruction
								.get(index).toString(), AIN.getNext().getNext()
								.toString(),
								edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
					}
				} else if (Multigraph.containsVertex(AIN.getNext().toString())) {
					if (Multigraph.containsVertex(Instruction.get(index)
							.toString())) {
						Multigraph
								.addVertex(AIN.getNext().getNext().toString());
						Multigraph.addEdge(Integer.toString(index), Instruction
								.get(index).toString(), AIN.getNext()
								.toString(),
								edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
						Multigraph.addEdge(Integer.toString(index), Instruction
								.get(index).toString(), AIN.getNext().getNext()
								.toString(),
								edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
					} else if (Multigraph.containsVertex(AIN.getNext()
							.getNext().toString())) {
						Multigraph.addVertex(Instruction.get(index).toString());
						Multigraph.addEdge(Integer.toString(index), Instruction
								.get(index).toString(), AIN.getNext()
								.toString(),
								edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
						Multigraph.addEdge(Integer.toString(index), Instruction
								.get(index).toString(), AIN.getNext().getNext()
								.toString(),
								edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
					}
				}
			}
			Multigraph.addEdge(Integer.toString(nameMethod), AIN.getClass().toString(), Instruction.get(index--).toString(), edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
			break;
		}
		case Opcodes.IF_ICMPLE: {
			if (Multigraph.containsVertex(Instruction.get(index).toString())
					&& Multigraph.containsVertex(AIN.getNext().toString())
					&& Multigraph.containsVertex(AIN.getNext().getNext()
							.toString())) {
				Multigraph.addEdge(Integer.toString(index),
						Instruction.get(index).toString(), AIN.getNext()
								.toString(),
						edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
				Multigraph.addEdge(Integer.toString(index),
						Instruction.get(index).toString(), AIN.getNext()
								.getNext().toString(),
						edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
			}
			// ifA or B or C exist find out which two exist add the other node
			// and create edges between them
			else if (Multigraph.containsVertex(Instruction.get(index)
					.toString())
					|| Multigraph.containsVertex(AIN.getNext().toString())
					|| Multigraph.containsVertex(AIN.getNext().getNext()
							.toString())) {
				if (Multigraph
						.containsVertex(Instruction.get(index).toString()) == true) {
					if (Multigraph.containsVertex(AIN.getNext().toString())) {
						Multigraph
								.addVertex(AIN.getNext().getNext().toString());
						Multigraph.addEdge(Integer.toString(index), Instruction
								.get(index).toString(), AIN.getNext()
								.toString(),
								edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
						Multigraph.addEdge(Integer.toString(index), Instruction
								.get(index).toString(), AIN.getNext().getNext()
								.toString(),
								edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
					} else if (Multigraph.containsVertex(AIN.getNext()
							.getNext().toString())) {
						Multigraph.addVertex(AIN.getNext().toString());
						Multigraph.addEdge(Integer.toString(index), Instruction
								.get(index).toString(), AIN.getNext()
								.toString(),
								edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
						Multigraph.addEdge(Integer.toString(index), Instruction
								.get(index).toString(), AIN.getNext().getNext()
								.toString(),
								edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
					}
				} else if (Multigraph.containsVertex(AIN.getNext().toString())) {
					if (Multigraph.containsVertex(Instruction.get(index)
							.toString())) {
						Multigraph
								.addVertex(AIN.getNext().getNext().toString());
						Multigraph.addEdge(Integer.toString(index), Instruction
								.get(index).toString(), AIN.getNext()
								.toString(),
								edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
						Multigraph.addEdge(Integer.toString(index), Instruction
								.get(index).toString(), AIN.getNext().getNext()
								.toString(),
								edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
					} else if (Multigraph.containsVertex(AIN.getNext()
							.getNext().toString())) {
						Multigraph.addVertex(Instruction.get(index).toString());
						Multigraph.addEdge(Integer.toString(index), Instruction
								.get(index).toString(), AIN.getNext()
								.toString(),
								edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
						Multigraph.addEdge(Integer.toString(index), Instruction
								.get(index).toString(), AIN.getNext().getNext()
								.toString(),
								edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
					}
				}
			}
			Multigraph.addEdge(Integer.toString(nameMethod), AIN.getClass().toString(), Instruction.get(index--).toString(), edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
			break;
		}
		case Opcodes.IF_ICMPLT: {
			if (Multigraph.containsVertex(Instruction.get(index).toString())
					&& Multigraph.containsVertex(AIN.getNext().toString())
					&& Multigraph.containsVertex(AIN.getNext().getNext()
							.toString())) {
				Multigraph.addEdge(Integer.toString(index),
						Instruction.get(index).toString(), AIN.getNext()
								.toString(),
						edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
				Multigraph.addEdge(Integer.toString(index),
						Instruction.get(index).toString(), AIN.getNext()
								.getNext().toString(),
						edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
			}
			// ifA or B or C exist find out which two exist add the other node
			// and create edges between them
			else if (Multigraph.containsVertex(Instruction.get(index)
					.toString())
					|| Multigraph.containsVertex(AIN.getNext().toString())
					|| Multigraph.containsVertex(AIN.getNext().getNext()
							.toString())) {
				if (Multigraph
						.containsVertex(Instruction.get(index).toString()) == true) {
					if (Multigraph.containsVertex(AIN.getNext().toString())) {
						Multigraph
								.addVertex(AIN.getNext().getNext().toString());
						Multigraph.addEdge(Integer.toString(index), Instruction
								.get(index).toString(), AIN.getNext()
								.toString(),
								edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
						Multigraph.addEdge(Integer.toString(index), Instruction
								.get(index).toString(), AIN.getNext().getNext()
								.toString(),
								edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
					} else if (Multigraph.containsVertex(AIN.getNext()
							.getNext().toString())) {
						Multigraph.addVertex(AIN.getNext().toString());
						Multigraph.addEdge(Integer.toString(index), Instruction
								.get(index).toString(), AIN.getNext()
								.toString(),
								edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
						Multigraph.addEdge(Integer.toString(index), Instruction
								.get(index).toString(), AIN.getNext().getNext()
								.toString(),
								edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
					}
				} else if (Multigraph.containsVertex(AIN.getNext().toString())) {
					if (Multigraph.containsVertex(Instruction.get(index)
							.toString())) {
						Multigraph
								.addVertex(AIN.getNext().getNext().toString());
						Multigraph.addEdge(Integer.toString(index), Instruction
								.get(index).toString(), AIN.getNext()
								.toString(),
								edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
						Multigraph.addEdge(Integer.toString(index), Instruction
								.get(index).toString(), AIN.getNext().getNext()
								.toString(),
								edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
					} else if (Multigraph.containsVertex(AIN.getNext()
							.getNext().toString())) {
						Multigraph.addVertex(Instruction.get(index).toString());
						Multigraph.addEdge(Integer.toString(index), Instruction
								.get(index).toString(), AIN.getNext()
								.toString(),
								edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
						Multigraph.addEdge(Integer.toString(index), Instruction
								.get(index).toString(), AIN.getNext().getNext()
								.toString(),
								edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
					}
				}
			}
			Multigraph.addEdge(Integer.toString(nameMethod), AIN.getClass().toString(), Instruction.get(index--).toString(), edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
			break;
		}
		case Opcodes.IF_ICMPNE: {
			if (Multigraph.containsVertex(Instruction.get(index).toString())
					&& Multigraph.containsVertex(AIN.getNext().toString())
					&& Multigraph.containsVertex(AIN.getNext().getNext()
							.toString())) {
				Multigraph.addEdge(Integer.toString(index),
						Instruction.get(index).toString(), AIN.getNext()
								.toString(),
						edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
				Multigraph.addEdge(Integer.toString(index),
						Instruction.get(index).toString(), AIN.getNext()
								.getNext().toString(),
						edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
			}
			// ifA or B or C exist find out which two exist add the other node
			// and create edges between them
			else if (Multigraph.containsVertex(Instruction.get(index)
					.toString())
					|| Multigraph.containsVertex(AIN.getNext().toString())
					|| Multigraph.containsVertex(AIN.getNext().getNext()
							.toString())) {
				if (Multigraph
						.containsVertex(Instruction.get(index).toString()) == true) {
					if (Multigraph.containsVertex(AIN.getNext().toString())) {
						Multigraph
								.addVertex(AIN.getNext().getNext().toString());
						Multigraph.addEdge(Integer.toString(index), Instruction
								.get(index).toString(), AIN.getNext()
								.toString(),
								edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
						Multigraph.addEdge(Integer.toString(index), Instruction
								.get(index).toString(), AIN.getNext().getNext()
								.toString(),
								edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
					} else if (Multigraph.containsVertex(AIN.getNext()
							.getNext().toString())) {
						Multigraph.addVertex(AIN.getNext().toString());
						Multigraph.addEdge(Integer.toString(index), Instruction
								.get(index).toString(), AIN.getNext()
								.toString(),
								edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
						Multigraph.addEdge(Integer.toString(index), Instruction
								.get(index).toString(), AIN.getNext().getNext()
								.toString(),
								edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
					}
				} else if (Multigraph.containsVertex(AIN.getNext().toString())) {
					if (Multigraph.containsVertex(Instruction.get(index)
							.toString())) {
						Multigraph
								.addVertex(AIN.getNext().getNext().toString());
						Multigraph.addEdge(Integer.toString(index), Instruction
								.get(index).toString(), AIN.getNext()
								.toString(),
								edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
						Multigraph.addEdge(Integer.toString(index), Instruction
								.get(index).toString(), AIN.getNext().getNext()
								.toString(),
								edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
					} else if (Multigraph.containsVertex(AIN.getNext()
							.getNext().toString())) {
						Multigraph.addVertex(Instruction.get(index).toString());
						Multigraph.addEdge(Integer.toString(index), Instruction
								.get(index).toString(), AIN.getNext()
								.toString(),
								edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
						Multigraph.addEdge(Integer.toString(index), Instruction
								.get(index).toString(), AIN.getNext().getNext()
								.toString(),
								edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
					}
				}
			}
			Multigraph.addEdge(Integer.toString(nameMethod), AIN.getClass().toString(), Instruction.get(index--).toString(), edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
			break;
		}
		case Opcodes.IFEQ: {
			if (Multigraph.containsVertex(Instruction.get(index).toString())
					&& Multigraph.containsVertex(AIN.getNext().toString())
					&& Multigraph.containsVertex(AIN.getNext().getNext()
							.toString())) {
				Multigraph.addEdge(Integer.toString(index),
						Instruction.get(index).toString(), AIN.getNext()
								.toString(),
						edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
				Multigraph.addEdge(Integer.toString(index),
						Instruction.get(index).toString(), AIN.getNext()
								.getNext().toString(),
						edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
			}
			// ifA or B or C exist find out which two exist add the other node
			// and create edges between them
			else if (Multigraph.containsVertex(Instruction.get(index)
					.toString())
					|| Multigraph.containsVertex(AIN.getNext().toString())
					|| Multigraph.containsVertex(AIN.getNext().getNext()
							.toString())) {
				if (Multigraph
						.containsVertex(Instruction.get(index).toString()) == true) {
					if (Multigraph.containsVertex(AIN.getNext().toString())) {
						Multigraph
								.addVertex(AIN.getNext().getNext().toString());
						Multigraph.addEdge(Integer.toString(index), Instruction
								.get(index).toString(), AIN.getNext()
								.toString(),
								edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
						Multigraph.addEdge(Integer.toString(index), Instruction
								.get(index).toString(), AIN.getNext().getNext()
								.toString(),
								edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
					} else if (Multigraph.containsVertex(AIN.getNext()
							.getNext().toString())) {
						Multigraph.addVertex(AIN.getNext().toString());
						Multigraph.addEdge(Integer.toString(index), Instruction
								.get(index).toString(), AIN.getNext()
								.toString(),
								edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
						Multigraph.addEdge(Integer.toString(index), Instruction
								.get(index).toString(), AIN.getNext().getNext()
								.toString(),
								edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
					}
				} else if (Multigraph.containsVertex(AIN.getNext().toString())) {
					if (Multigraph.containsVertex(Instruction.get(index)
							.toString())) {
						Multigraph
								.addVertex(AIN.getNext().getNext().toString());
						Multigraph.addEdge(Integer.toString(index), Instruction
								.get(index).toString(), AIN.getNext()
								.toString(),
								edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
						Multigraph.addEdge(Integer.toString(index), Instruction
								.get(index).toString(), AIN.getNext().getNext()
								.toString(),
								edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
					} else if (Multigraph.containsVertex(AIN.getNext()
							.getNext().toString())) {
						Multigraph.addVertex(Instruction.get(index).toString());
						Multigraph.addEdge(Integer.toString(index), Instruction
								.get(index).toString(), AIN.getNext()
								.toString(),
								edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
						Multigraph.addEdge(Integer.toString(index), Instruction
								.get(index).toString(), AIN.getNext().getNext()
								.toString(),
								edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
					}
				}
			}
			Multigraph.addEdge(Integer.toString(nameMethod), AIN.getClass().toString(), Instruction.get(index--).toString(), edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
			break;
		}
		case Opcodes.IFGE: {
			if (Multigraph.containsVertex(Instruction.get(index).toString())
					&& Multigraph.containsVertex(AIN.getNext().toString())
					&& Multigraph.containsVertex(AIN.getNext().getNext()
							.toString())) {
				Multigraph.addEdge(Integer.toString(index),
						Instruction.get(index).toString(), AIN.getNext()
								.toString(),
						edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
				Multigraph.addEdge(Integer.toString(index),
						Instruction.get(index).toString(), AIN.getNext()
								.getNext().toString(),
						edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
			}
			// ifA or B or C exist find out which two exist add the other node
			// and create edges between them
			else if (Multigraph.containsVertex(Instruction.get(index)
					.toString())
					|| Multigraph.containsVertex(AIN.getNext().toString())
					|| Multigraph.containsVertex(AIN.getNext().getNext()
							.toString())) {
				if (Multigraph
						.containsVertex(Instruction.get(index).toString()) == true) {
					if (Multigraph.containsVertex(AIN.getNext().toString())) {
						Multigraph
								.addVertex(AIN.getNext().getNext().toString());
						Multigraph.addEdge(Integer.toString(index), Instruction
								.get(index).toString(), AIN.getNext()
								.toString(),
								edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
						Multigraph.addEdge(Integer.toString(index), Instruction
								.get(index).toString(), AIN.getNext().getNext()
								.toString(),
								edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
					} else if (Multigraph.containsVertex(AIN.getNext()
							.getNext().toString())) {
						Multigraph.addVertex(AIN.getNext().toString());
						Multigraph.addEdge(Integer.toString(index), Instruction
								.get(index).toString(), AIN.getNext()
								.toString(),
								edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
						Multigraph.addEdge(Integer.toString(index), Instruction
								.get(index).toString(), AIN.getNext().getNext()
								.toString(),
								edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
					}
				} else if (Multigraph.containsVertex(AIN.getNext().toString())) {
					if (Multigraph.containsVertex(Instruction.get(index)
							.toString())) {
						Multigraph
								.addVertex(AIN.getNext().getNext().toString());
						Multigraph.addEdge(Integer.toString(index), Instruction
								.get(index).toString(), AIN.getNext()
								.toString(),
								edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
						Multigraph.addEdge(Integer.toString(index), Instruction
								.get(index).toString(), AIN.getNext().getNext()
								.toString(),
								edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
					} else if (Multigraph.containsVertex(AIN.getNext()
							.getNext().toString())) {
						Multigraph.addVertex(Instruction.get(index).toString());
						Multigraph.addEdge(Integer.toString(index), Instruction
								.get(index).toString(), AIN.getNext()
								.toString(),
								edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
						Multigraph.addEdge(Integer.toString(index), Instruction
								.get(index).toString(), AIN.getNext().getNext()
								.toString(),
								edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
					}
				}
			}
			Multigraph.addEdge(Integer.toString(nameMethod), AIN.getClass().toString(), Instruction.get(index--).toString(), edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
			break;
		}
		case Opcodes.IFGT: {
			if (Multigraph.containsVertex(Instruction.get(index).toString())
					&& Multigraph.containsVertex(AIN.getNext().toString())
					&& Multigraph.containsVertex(AIN.getNext().getNext()
							.toString())) {
				Multigraph.addEdge(Integer.toString(index),
						Instruction.get(index).toString(), AIN.getNext()
								.toString(),
						edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
				Multigraph.addEdge(Integer.toString(index),
						Instruction.get(index).toString(), AIN.getNext()
								.getNext().toString(),
						edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
			}
			// ifA or B or C exist find out which two exist add the other node
			// and create edges between them
			else if (Multigraph.containsVertex(Instruction.get(index)
					.toString())
					|| Multigraph.containsVertex(AIN.getNext().toString())
					|| Multigraph.containsVertex(AIN.getNext().getNext()
							.toString())) {
				if (Multigraph
						.containsVertex(Instruction.get(index).toString()) == true) {
					if (Multigraph.containsVertex(AIN.getNext().toString())) {
						Multigraph
								.addVertex(AIN.getNext().getNext().toString());
						Multigraph.addEdge(Integer.toString(index), Instruction
								.get(index).toString(), AIN.getNext()
								.toString(),
								edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
						Multigraph.addEdge(Integer.toString(index), Instruction
								.get(index).toString(), AIN.getNext().getNext()
								.toString(),
								edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
					} else if (Multigraph.containsVertex(AIN.getNext()
							.getNext().toString())) {
						Multigraph.addVertex(AIN.getNext().toString());
						Multigraph.addEdge(Integer.toString(index), Instruction
								.get(index).toString(), AIN.getNext()
								.toString(),
								edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
						Multigraph.addEdge(Integer.toString(index), Instruction
								.get(index).toString(), AIN.getNext().getNext()
								.toString(),
								edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
					}
				} else if (Multigraph.containsVertex(AIN.getNext().toString())) {
					if (Multigraph.containsVertex(Instruction.get(index)
							.toString())) {
						Multigraph
								.addVertex(AIN.getNext().getNext().toString());
						Multigraph.addEdge(Integer.toString(index), Instruction
								.get(index).toString(), AIN.getNext()
								.toString(),
								edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
						Multigraph.addEdge(Integer.toString(index), Instruction
								.get(index).toString(), AIN.getNext().getNext()
								.toString(),
								edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
					} else if (Multigraph.containsVertex(AIN.getNext()
							.getNext().toString())) {
						Multigraph.addVertex(Instruction.get(index).toString());
						Multigraph.addEdge(Integer.toString(index), Instruction
								.get(index).toString(), AIN.getNext()
								.toString(),
								edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
						Multigraph.addEdge(Integer.toString(index), Instruction
								.get(index).toString(), AIN.getNext().getNext()
								.toString(),
								edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
					}
				}
			}
			Multigraph.addEdge(Integer.toString(nameMethod), AIN.getClass().toString(), Instruction.get(index--).toString(), edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
			break;
		}
		case Opcodes.IFLE: {
			if (Multigraph.containsVertex(Instruction.get(index).toString())
					&& Multigraph.containsVertex(AIN.getNext().toString())
					&& Multigraph.containsVertex(AIN.getNext().getNext()
							.toString())) {
				Multigraph.addEdge(Integer.toString(index),
						Instruction.get(index).toString(), AIN.getNext()
								.toString(),
						edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
				Multigraph.addEdge(Integer.toString(index),
						Instruction.get(index).toString(), AIN.getNext()
								.getNext().toString(),
						edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
			}
			// ifA or B or C exist find out which two exist add the other node
			// and create edges between them
			else if (Multigraph.containsVertex(Instruction.get(index)
					.toString())
					|| Multigraph.containsVertex(AIN.getNext().toString())
					|| Multigraph.containsVertex(AIN.getNext().getNext()
							.toString())) {
				if (Multigraph
						.containsVertex(Instruction.get(index).toString()) == true) {
					if (Multigraph.containsVertex(AIN.getNext().toString())) {
						Multigraph
								.addVertex(AIN.getNext().getNext().toString());
						Multigraph.addEdge(Integer.toString(index), Instruction
								.get(index).toString(), AIN.getNext()
								.toString(),
								edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
						Multigraph.addEdge(Integer.toString(index), Instruction
								.get(index).toString(), AIN.getNext().getNext()
								.toString(),
								edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
					} else if (Multigraph.containsVertex(AIN.getNext()
							.getNext().toString())) {
						Multigraph.addVertex(AIN.getNext().toString());
						Multigraph.addEdge(Integer.toString(index), Instruction
								.get(index).toString(), AIN.getNext()
								.toString(),
								edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
						Multigraph.addEdge(Integer.toString(index), Instruction
								.get(index).toString(), AIN.getNext().getNext()
								.toString(),
								edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
					}
				} else if (Multigraph.containsVertex(AIN.getNext().toString())) {
					if (Multigraph.containsVertex(Instruction.get(index)
							.toString())) {
						Multigraph
								.addVertex(AIN.getNext().getNext().toString());
						Multigraph.addEdge(Integer.toString(index), Instruction
								.get(index).toString(), AIN.getNext()
								.toString(),
								edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
						Multigraph.addEdge(Integer.toString(index), Instruction
								.get(index).toString(), AIN.getNext().getNext()
								.toString(),
								edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
					} else if (Multigraph.containsVertex(AIN.getNext()
							.getNext().toString())) {
						Multigraph.addVertex(Instruction.get(index).toString());
						Multigraph.addEdge(Integer.toString(index), Instruction
								.get(index).toString(), AIN.getNext()
								.toString(),
								edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
						Multigraph.addEdge(Integer.toString(index), Instruction
								.get(index).toString(), AIN.getNext().getNext()
								.toString(),
								edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
					}
				}
			}
			Multigraph.addEdge(Integer.toString(nameMethod), AIN.getClass().toString(), Instruction.get(index--).toString(), edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
			break;
		}
		case Opcodes.IFLT: {
			if (Multigraph.containsVertex(Instruction.get(index).toString())
					&& Multigraph.containsVertex(AIN.getNext().toString())
					&& Multigraph.containsVertex(AIN.getNext().getNext()
							.toString())) {
				Multigraph.addEdge(Integer.toString(index),
						Instruction.get(index).toString(), AIN.getNext()
								.toString(),
						edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
				Multigraph.addEdge(Integer.toString(index),
						Instruction.get(index).toString(), AIN.getNext()
								.getNext().toString(),
						edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
			}
			// ifA or B or C exist find out which two exist add the other node
			// and create edges between them
			else if (Multigraph.containsVertex(Instruction.get(index)
					.toString())
					|| Multigraph.containsVertex(AIN.getNext().toString())
					|| Multigraph.containsVertex(AIN.getNext().getNext()
							.toString())) {
				if (Multigraph
						.containsVertex(Instruction.get(index).toString()) == true) {
					if (Multigraph.containsVertex(AIN.getNext().toString())) {
						Multigraph
								.addVertex(AIN.getNext().getNext().toString());
						Multigraph.addEdge(Integer.toString(index), Instruction
								.get(index).toString(), AIN.getNext()
								.toString(),
								edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
						Multigraph.addEdge(Integer.toString(index), Instruction
								.get(index).toString(), AIN.getNext().getNext()
								.toString(),
								edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
					} else if (Multigraph.containsVertex(AIN.getNext()
							.getNext().toString())) {
						Multigraph.addVertex(AIN.getNext().toString());
						Multigraph.addEdge(Integer.toString(index), Instruction
								.get(index).toString(), AIN.getNext()
								.toString(),
								edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
						Multigraph.addEdge(Integer.toString(index), Instruction
								.get(index).toString(), AIN.getNext().getNext()
								.toString(),
								edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
					}
				} else if (Multigraph.containsVertex(AIN.getNext().toString())) {
					if (Multigraph.containsVertex(Instruction.get(index)
							.toString())) {
						Multigraph
								.addVertex(AIN.getNext().getNext().toString());
						Multigraph.addEdge(Integer.toString(index), Instruction
								.get(index).toString(), AIN.getNext()
								.toString(),
								edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
						Multigraph.addEdge(Integer.toString(index), Instruction
								.get(index).toString(), AIN.getNext().getNext()
								.toString(),
								edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
					} else if (Multigraph.containsVertex(AIN.getNext()
							.getNext().toString())) {
						Multigraph.addVertex(Instruction.get(index).toString());
						Multigraph.addEdge(Integer.toString(index), Instruction
								.get(index).toString(), AIN.getNext()
								.toString(),
								edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
						Multigraph.addEdge(Integer.toString(index), Instruction
								.get(index).toString(), AIN.getNext().getNext()
								.toString(),
								edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
					}
				}
			}
			Multigraph.addEdge(Integer.toString(nameMethod), AIN.getClass().toString(), Instruction.get(index--).toString(), edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
			break;
		}
		case Opcodes.IFNE: {
			if (Multigraph.containsVertex(Instruction.get(index).toString())
					&& Multigraph.containsVertex(AIN.getNext().toString())
					&& Multigraph.containsVertex(AIN.getNext().getNext()
							.toString())) {
				Multigraph.addEdge(Integer.toString(index),
						Instruction.get(index).toString(), AIN.getNext()
								.toString(),
						edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
				Multigraph.addEdge(Integer.toString(index),
						Instruction.get(index).toString(), AIN.getNext()
								.getNext().toString(),
						edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
			}
			// ifA or B or C exist find out which two exist add the other node
			// and create edges between them
			else if (Multigraph.containsVertex(Instruction.get(index)
					.toString())
					|| Multigraph.containsVertex(AIN.getNext().toString())
					|| Multigraph.containsVertex(AIN.getNext().getNext()
							.toString())) {
				if (Multigraph
						.containsVertex(Instruction.get(index).toString()) == true) {
					if (Multigraph.containsVertex(AIN.getNext().toString())) {
						Multigraph
								.addVertex(AIN.getNext().getNext().toString());
						Multigraph.addEdge(Integer.toString(index), Instruction
								.get(index).toString(), AIN.getNext()
								.toString(),
								edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
						Multigraph.addEdge(Integer.toString(index), Instruction
								.get(index).toString(), AIN.getNext().getNext()
								.toString(),
								edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
					} else if (Multigraph.containsVertex(AIN.getNext()
							.getNext().toString())) {
						Multigraph.addVertex(AIN.getNext().toString());
						Multigraph.addEdge(Integer.toString(index), Instruction
								.get(index).toString(), AIN.getNext()
								.toString(),
								edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
						Multigraph.addEdge(Integer.toString(index), Instruction
								.get(index).toString(), AIN.getNext().getNext()
								.toString(),
								edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
					}
				} else if (Multigraph.containsVertex(AIN.getNext().toString())) {
					if (Multigraph.containsVertex(Instruction.get(index)
							.toString())) {
						Multigraph
								.addVertex(AIN.getNext().getNext().toString());
						Multigraph.addEdge(Integer.toString(index), Instruction
								.get(index).toString(), AIN.getNext()
								.toString(),
								edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
						Multigraph.addEdge(Integer.toString(index), Instruction
								.get(index).toString(), AIN.getNext().getNext()
								.toString(),
								edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
					} else if (Multigraph.containsVertex(AIN.getNext()
							.getNext().toString())) {
						Multigraph.addVertex(Instruction.get(index).toString());
						Multigraph.addEdge(Integer.toString(index), Instruction
								.get(index).toString(), AIN.getNext()
								.toString(),
								edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
						Multigraph.addEdge(Integer.toString(index), Instruction
								.get(index).toString(), AIN.getNext().getNext()
								.toString(),
								edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
					}
				}
			}
			Multigraph.addEdge(Integer.toString(nameMethod), AIN.getClass().toString(), Instruction.get(index--).toString(), edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
			break;
		}
		case Opcodes.IFNONNULL: {
			if (Multigraph.containsVertex(Instruction.get(index).toString())
					&& Multigraph.containsVertex(AIN.getNext().toString())
					&& Multigraph.containsVertex(AIN.getNext().getNext()
							.toString())) {
				Multigraph.addEdge(Integer.toString(index),
						Instruction.get(index).toString(), AIN.getNext()
								.toString(),
						edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
				Multigraph.addEdge(Integer.toString(index),
						Instruction.get(index).toString(), AIN.getNext()
								.getNext().toString(),
						edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
			}
			// ifA or B or C exist find out which two exist add the other node
			// and create edges between them
			else if (Multigraph.containsVertex(Instruction.get(index)
					.toString())
					|| Multigraph.containsVertex(AIN.getNext().toString())
					|| Multigraph.containsVertex(AIN.getNext().getNext()
							.toString())) {
				if (Multigraph
						.containsVertex(Instruction.get(index).toString()) == true) {
					if (Multigraph.containsVertex(AIN.getNext().toString())) {
						Multigraph
								.addVertex(AIN.getNext().getNext().toString());
						Multigraph.addEdge(Integer.toString(index), Instruction
								.get(index).toString(), AIN.getNext()
								.toString(),
								edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
						Multigraph.addEdge(Integer.toString(index), Instruction
								.get(index).toString(), AIN.getNext().getNext()
								.toString(),
								edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
					} else if (Multigraph.containsVertex(AIN.getNext()
							.getNext().toString())) {
						Multigraph.addVertex(AIN.getNext().toString());
						Multigraph.addEdge(Integer.toString(index), Instruction
								.get(index).toString(), AIN.getNext()
								.toString(),
								edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
						Multigraph.addEdge(Integer.toString(index), Instruction
								.get(index).toString(), AIN.getNext().getNext()
								.toString(),
								edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
					}
				} else if (Multigraph.containsVertex(AIN.getNext().toString())) {
					if (Multigraph.containsVertex(Instruction.get(index)
							.toString())) {
						Multigraph
								.addVertex(AIN.getNext().getNext().toString());
						Multigraph.addEdge(Integer.toString(index), Instruction
								.get(index).toString(), AIN.getNext()
								.toString(),
								edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
						Multigraph.addEdge(Integer.toString(index), Instruction
								.get(index).toString(), AIN.getNext().getNext()
								.toString(),
								edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
					} else if (Multigraph.containsVertex(AIN.getNext()
							.getNext().toString())) {
						Multigraph.addVertex(Instruction.get(index).toString());
						Multigraph.addEdge(Integer.toString(index), Instruction
								.get(index).toString(), AIN.getNext()
								.toString(),
								edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
						Multigraph.addEdge(Integer.toString(index), Instruction
								.get(index).toString(), AIN.getNext().getNext()
								.toString(),
								edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
					}
				}
			}
			Multigraph.addEdge(Integer.toString(nameMethod), AIN.getClass().toString(), Instruction.get(index--).toString(), edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
			break;
		}
		case Opcodes.IFNULL: {
			if (Multigraph.containsVertex(Instruction.get(index).toString())
					&& Multigraph.containsVertex(AIN.getNext().toString())
					&& Multigraph.containsVertex(AIN.getNext().getNext()
							.toString())) {
				Multigraph.addEdge(Integer.toString(index),
						Instruction.get(index).toString(), AIN.getNext()
								.toString(),
						edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
				Multigraph.addEdge(Integer.toString(index),
						Instruction.get(index).toString(), AIN.getNext()
								.getNext().toString(),
						edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
			}
			// ifA or B or C exist find out which two exist add the other node
			// and create edges between them
			else if (Multigraph.containsVertex(Instruction.get(index)
					.toString())
					|| Multigraph.containsVertex(AIN.getNext().toString())
					|| Multigraph.containsVertex(AIN.getNext().getNext()
							.toString())) {
				if (Multigraph
						.containsVertex(Instruction.get(index).toString()) == true) {
					if (Multigraph.containsVertex(AIN.getNext().toString())) {
						Multigraph
								.addVertex(AIN.getNext().getNext().toString());
						Multigraph.addEdge(Integer.toString(index), Instruction
								.get(index).toString(), AIN.getNext()
								.toString(),
								edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
						Multigraph.addEdge(Integer.toString(index), Instruction
								.get(index).toString(), AIN.getNext().getNext()
								.toString(),
								edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
					} else if (Multigraph.containsVertex(AIN.getNext()
							.getNext().toString())) {
						Multigraph.addVertex(AIN.getNext().toString());
						Multigraph.addEdge(Integer.toString(index), Instruction
								.get(index).toString(), AIN.getNext()
								.toString(),
								edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
						Multigraph.addEdge(Integer.toString(index), Instruction
								.get(index).toString(), AIN.getNext().getNext()
								.toString(),
								edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
					}
				} else if (Multigraph.containsVertex(AIN.getNext().toString())) {
					if (Multigraph.containsVertex(Instruction.get(index)
							.toString())) {
						Multigraph
								.addVertex(AIN.getNext().getNext().toString());
						Multigraph.addEdge(Integer.toString(index), Instruction
								.get(index).toString(), AIN.getNext()
								.toString(),
								edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
						Multigraph.addEdge(Integer.toString(index), Instruction
								.get(index).toString(), AIN.getNext().getNext()
								.toString(),
								edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
					} else if (Multigraph.containsVertex(AIN.getNext()
							.getNext().toString())) {
						Multigraph.addVertex(Instruction.get(index).toString());
						Multigraph.addEdge(Integer.toString(index), Instruction
								.get(index).toString(), AIN.getNext()
								.toString(),
								edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
						Multigraph.addEdge(Integer.toString(index), Instruction
								.get(index).toString(), AIN.getNext().getNext()
								.toString(),
								edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
					}
				}
			}
			Multigraph.addEdge(Integer.toString(nameMethod), AIN.getClass().toString(), Instruction.get(index--).toString(), edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
			break;
		}
		case Opcodes.JSR: {
			// TODO IMPLEMENT THIS
			break;
		}

		}
//		System.out.println(Instruction.get(index).toString());
//		System.out.println(Instruction.get(index -- ).toString() + "FUCK");
		
		nameMethod++;
	}	

	@Override
	public InsnList codeAnalyzer(File ByteCodeTextFileLocation) {

		return null;
	}

}
/*
 * ****OLD CODE **** System.out.println("NEW HIT: PROCESSING");
 * System.out.println(Instruction.get(index).getClass().getCanonicalName());
 * System.out.println("Calling Class: "+clno.name);
 * //System.out.println("Class being called: "+
 * Instruction.get(index).getClass().getClasses().toString());
 * System.out.println("Class being called: " +
 * Instruction.get(index).getClass()); //a prefix for referencing object
 * //research pulling from constant pool //try pulling instruction signature.
 * //stackMapTabl
 */

// This caused exponential crashes of the system however the logic was fairly
// solid
/*
 * if(Multigraph.containsVertex(Instruction.get(index).toString()) == false &&
 * Multigraph.containsVertex(AIN.getNext().toString()) == false &&
 * Multigraph.containsVertex(AIN.getNext().getNext() .toString()) == false){//
 * if none of them exist add them all add // edges, Note this should only ever
 * execute on the first pass :)
 * Multigraph.addVertex(Instruction.get(index).toString());
 * Multigraph.addVertex(AIN.getNext().toString());
 * Multigraph.addVertex(AIN.getNext().getNext().toString()); //TODO This is the
 * breaking point of the software Multigraph.addEdge(Integer.toString(index),
 * Instruction .get(index).toString(), AIN.getNext() .toString(),
 * edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
 * 
 * Multigraph.addEdge(Integer.toString(index), Instruction
 * .get(index).toString(), AIN.getNext().getNext() .toString(),
 * edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
 */