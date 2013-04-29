package metrics;

import japa.parser.JavaParser;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.ImportDeclaration;
import japa.parser.ast.body.BodyDeclaration;
import japa.parser.ast.body.JavadocComment;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.Parameter;
import japa.parser.ast.body.TypeDeclaration;
import japa.parser.ast.stmt.AssertStmt;
import japa.parser.ast.stmt.BlockStmt;
import japa.parser.ast.stmt.BreakStmt;
import japa.parser.ast.stmt.ContinueStmt;
import japa.parser.ast.stmt.DoStmt;
import japa.parser.ast.stmt.EmptyStmt;
import japa.parser.ast.stmt.ExplicitConstructorInvocationStmt;
import japa.parser.ast.stmt.ExpressionStmt;
import japa.parser.ast.stmt.ForStmt;
import japa.parser.ast.stmt.ForeachStmt;
import japa.parser.ast.stmt.IfStmt;
import japa.parser.ast.stmt.LabeledStmt;
import japa.parser.ast.stmt.ReturnStmt;
import japa.parser.ast.stmt.Statement;
import japa.parser.ast.stmt.SwitchEntryStmt;
import japa.parser.ast.stmt.SwitchStmt;
import japa.parser.ast.stmt.ThrowStmt;
import japa.parser.ast.stmt.TryStmt;
import japa.parser.ast.stmt.TypeDeclarationStmt;
import japa.parser.ast.stmt.WhileStmt;
import japa.parser.ast.visitor.VoidVisitorAdapter;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Metric {

	private String className = null;
	private int classNo = 0;
	// This metric class will be able to calculate the metrics of a given class.
	// These metrics will be:
	// WMC: Weighted methods per class
	// DIT: Depth of Inheritance Tree
	// NOC: Number of Children
	// CBO: Coupling between object classes
	// RFC: Response for a Class
	// LCOM: Lack of cohesion in methods
	// Ca: Afferent couplings
	// NPM: Number of public methods

	public Metric(File file, Data data) throws Exception {
		/*
		 * Take in the class to analyse (Currently a file manager to choose the
		 * file and create a input stream for that file. Get the className and
		 * then create an input stream to read the file into a CompilationUnit
		 */
		className = file.getName();
		data.addClass(className);
		FileInputStream in = new FileInputStream(file);
		CompilationUnit cu;
		try {
			// parse the file
			cu = JavaParser.parse(in);

		} finally {
			in.close();
		}
		// Create a new method visitor with the compilation unit.
		new Visitor().visit(cu, data);
		// Create a new class visitor with the compilation unit
		new ClassVisitor().visit(cu, data);
		// Get package name (if no package is assigned then set to no package)
		if (cu.getPackage() != null) {
			// Get the package and print for the first method
			String packageName = cu.getPackage().getName().toString();
			data.addPackageName(packageName);
		} else
			data.addPackageName("default");

		// Get the imports for the class
		if (cu.getImports() != null) {
			ArrayList<String> imports = new ArrayList<String>();
			List<ImportDeclaration> getImports = cu.getImports();
			for (int i = 0; i < getImports.size(); i++) {
				String s = getImports.get(i).getName().toString();
				imports.add(s);
			}
			data.addImports(imports);
		} else {
			data.addImports(null);
			// Else if there are no imports
		}
		// Get the types for the class
		if (cu.getTypes() != null) {
			ArrayList<String> parameterNames = new ArrayList<String>();
			List<TypeDeclaration> getTypes = cu.getTypes();
			for (int i = 0; i < getTypes.size(); i++) {
				List<BodyDeclaration> bodyDec = getTypes.get(i).getMembers();
				// Check if list of BodyDeclarations is null
				if (bodyDec != null) {
					// If it isn't loop for every BodyDeclaration
					for (BodyDeclaration a : bodyDec) {
						if (a instanceof MethodDeclaration) {
							// If it is a MethodDeclaration then cast to method.
							MethodDeclaration method = (MethodDeclaration) a;
							// Use the method to get the parameters
							List<Parameter> paras = method.getParameters();
							// Check if the parameters are equal to null.
//							if (paras != null) {
//								// For all the parameters
//								for (int j = 0; j < paras.size(); j++) {
//									parameterNames.add(paras.get(j).toString());
//									//System.out.println(paras.get(j).toString());
//								}
//							}
//							else {
//							}				
//							data.addAllParamerters(parameterNames);
							// Get JavaDoc Comment
							if (method.getJavaDoc() != null) {
								JavadocComment javaDoc = method.getJavaDoc();
							}
							// Get body of method
							if (method.getBody() != null) {
								// If there are statements in the body
								List<Statement> listStatements = method.getBody().getStmts();
								if (listStatements != null) {
									for (int j = 0; j < listStatements.size(); j++) {
										// Check that the statement is one of
										// the following: (got from
										// parser/ast/stmt folder of javaparser)
										if (listStatements.get(j) instanceof AssertStmt) {
										}
										if (listStatements.get(j) instanceof BlockStmt) {
											for (Statement st : ((BlockStmt) listStatements
													.get(j)).getStmts()) {
												// add for block statements
												listStatements.add(st);
											}

										}
										if (listStatements.get(j) instanceof BreakStmt) {

										}
										if (listStatements.get(j) instanceof ContinueStmt) {

										}
										if (listStatements.get(j) instanceof DoStmt) {

										}
										if (listStatements.get(j) instanceof EmptyStmt) {

										}
										if (listStatements.get(j) instanceof ExplicitConstructorInvocationStmt) {

										}
										if (listStatements.get(j) instanceof ExpressionStmt) {

										}
										if (listStatements.get(j) instanceof ForeachStmt) {

										}
										if (listStatements.get(j) instanceof ForStmt) {
											ForStmt st = (ForStmt) listStatements.get(j);
											//listStatements.add(st);
										}
										if (listStatements.get(j) instanceof IfStmt) {
											if(((IfStmt)listStatements.get(j)).getElseStmt() != null)
												listStatements.add(((IfStmt)listStatements.get(j)).getElseStmt());
											if(((IfStmt)listStatements.get(j)).getThenStmt() != null)
												listStatements.add(((IfStmt)listStatements.get(j)).getThenStmt());
										}
										if (listStatements.get(j) instanceof LabeledStmt) {

										}
										if (listStatements.get(j) instanceof ReturnStmt) {

										}
										if (listStatements.get(j) instanceof Statement) {

										}
										if (listStatements.get(j) instanceof SwitchStmt) {
											if(((SwitchStmt) listStatements.get(j)).getEntries() != null)
												for(SwitchEntryStmt aswitch : ((SwitchStmt)listStatements.get(j)).getEntries()) {
													for(Statement s : aswitch.getStmts()) {
														listStatements.add(s);
													}
													
												}
										}
										if (listStatements.get(j) instanceof ThrowStmt) {

										}
										if (listStatements.get(j) instanceof TryStmt) {

										}
										if (listStatements.get(j) instanceof TypeDeclarationStmt) {

										}
										if (listStatements.get(j) instanceof WhileStmt) {

										}
									}
								}
								data.addStatements(listStatements);
							}
						}
					}
				}
			}
		}
		
		

	}
}
