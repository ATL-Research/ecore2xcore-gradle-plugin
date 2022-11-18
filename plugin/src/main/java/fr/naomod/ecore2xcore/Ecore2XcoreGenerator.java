package fr.naomod.ecore2xcore;

import java.io.IOException;
import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.PrintWriter;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EAttribute;


/*
	Extremely simple Ecore to Xcore generator.
	The objective is to be able to generate, from gradle, Java code from the Ecore metamodels used with ATL transformations.
	In general, there is no need to transform Ecore to Xcore to generate Java code, but gradle does not make things easy.
	Moreover, Ecore metamodels used in ATL transformations are generally not immediately suitable for EMF code generation
	(no nsURI, custom primitive types with no instanceClassName), which this generator should generally solve.
*/
public class Ecore2XcoreGenerator {

	public static void build(String basePackage, File source, File target) throws IOException {
		System.out.println("transforming " + source + " to " + target);
		ResourceSet rs = new ResourceSetImpl();

		rs.getResourceFactoryRegistry().getExtensionToFactoryMap().put("ecore", new XMIResourceFactoryImpl());
		rs.getPackageRegistry().put(EcorePackage.eNS_URI, EcorePackage.class);
		Resource r = rs.getResource(URI.createFileURI(source.getAbsolutePath()), true);

		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(target)));

		for(EObject e : r.getContents()) {
			if(e instanceof EPackage) {
				EPackage p = (EPackage)e;
				if(p.getEClassifiers().stream().allMatch(c -> c instanceof EDataType)) {
					System.out.println("Skipping package with only primitive types");
				} else {
					out.println(String.format("@Ecore(nsURI=\"%s\")", p.getName()));
					out.println("@GenModel(importerID=\"org.eclipse.emf.importer.ecore\",	modelDirectory=\".\", complianceLevel=\"8.0\")");
					out.println(String.format("package %s.%s_", basePackage, p.getName().toLowerCase()));

					for(EClassifier c : p.getEClassifiers()) {
						process(out, c);
					}
				}
			}
		}
		out.close();
	}

	private static void process(PrintWriter out, EClassifier c) {
		if(c instanceof EClass) {
			EClass cl = (EClass)c;

			if(cl.isAbstract()) {
				out.print("abstract ");
			}
			out.print(String.format("class ^%s", cl.getName()));
			boolean first = true;
			for(EClass st : cl.getESuperTypes()) {
				if(first) {
					out.print(" extends ^");
				} else {
					out.print(", ^");
				}
				out.print(st.getName());
				first = false;
			}
			out.println(" {");
			for(EStructuralFeature sf : cl.getEStructuralFeatures()) {
				if(sf instanceof EAttribute) {
					EAttribute a = (EAttribute)sf;
					out.println(String.format("\t^%s%s ^%s",
						a.getEType().getName(),
						a.isMany() ? "[]" : "",
						a.getName()
					));
				} else {
					EReference r = (EReference)sf;
					out.println(String.format("\t%s ^%s%s ^%s %s",
						r.isContainment() ?
							"contains"
						:(r.isContainer() ?
							"container"
						:	"refers"
						),
						r.getEType().getName(),
						r.isMany() ? "[]" : "",
						r.getName(),
						r.getEOpposite() != null ? "opposite ^" + r.getEOpposite().getName() : ""
					));
				}
			}

			out.println("}");
		}
	}

}

