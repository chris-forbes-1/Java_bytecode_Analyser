package metrics;

import japa.parser.ast.body.BodyDeclaration;
import japa.parser.ast.body.ClassOrInterfaceDeclaration;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.type.ClassOrInterfaceType;
import japa.parser.ast.visitor.VoidVisitorAdapter;
import java.util.List;

class ClassVisitor extends VoidVisitorAdapter<Data> {
	// This visitor is used to see which classes extend others.
	private int publicM = 0;
	private int spublicM = 0;
	
	public void visit(ClassOrInterfaceDeclaration n, Data data) {

		// System.out.println("Name: " + n.getName());
		List<ClassOrInterfaceType> listExtends = n.getExtends();
		List<ClassOrInterfaceType> listImplements = n.getImplements();
		if (listExtends != null) {
			for (ClassOrInterfaceType a : listExtends) {
				// System.out.println("Extends: " + a.getName());
				data.addTree(n.getName(), a.getName());
			}
		} else {
			// System.out.println("Extends: none");
			data.addTree(n.getName(), null);
		}

		if (listImplements != null) {
			for (ClassOrInterfaceType a : listImplements) {
				// System.out.println("Implements: " + a.getName());
				data.addImplements(n.getName(), a.getName());
			}
		} else {
			// System.out.println("Implements: none");
			data.addImplements(n.getName(), null);
		}

		List<BodyDeclaration> bodyDec = n.getMembers();
		if (bodyDec != null) {
			for (BodyDeclaration a : bodyDec) {
				if (a instanceof MethodDeclaration) {
					// If it is a MethodDeclaration then cast to method.
					MethodDeclaration method = (MethodDeclaration) a;
					if (method.getModifiers() == 1)
						publicM = publicM + 1;
					if (method.getModifiers() == 9)
						spublicM = spublicM + 1;
				}
			}
		}
		data.addPublicMethod(publicM);
		data.addSPublicMethod(spublicM);
	}
}
