package metrics;

import java.util.ArrayList;
import java.util.List;

import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.Parameter;
import japa.parser.ast.visitor.VoidVisitorAdapter;

public class Visitor extends VoidVisitorAdapter<Data> {
	public ArrayList<String> methodNames = new ArrayList<String>();
	int counter = 0;
	int publicNo = 0;
	ArrayList<String> parameterNames;

	// Here you can access the attributes of the method.
	// this method will be called for all methods in this
	// CompilationUnit, including inner class methods
	@Override
	public void visit(MethodDeclaration n, Data data) {
		// Get name of the method and add to array
		if (n != null) {
			String methodName = n.getName();
			methodNames.add(methodName);
			List<Parameter> paras = n.getParameters();
			parameterNames = new ArrayList<String>();
			if (paras != null) {
				// For all the parameters
				for (int j = 0; j < paras.size(); j++) {
					parameterNames.add(paras.get(j).getId().getName());
					//System.out.println(paras.get(j).getId().getName());
				}
				data.addParameters(parameterNames);
			}						
		}
		else
			methodNames.add("none");
			if (counter == 0) {
				data.addMethods(methodNames);
				data.addAllParamerters(parameterNames);
			}
			counter++;
			data.addMethod();
	}
}