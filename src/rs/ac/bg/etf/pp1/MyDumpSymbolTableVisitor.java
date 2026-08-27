package rs.ac.bg.etf.pp1;

import rs.etf.pp1.symboltable.concepts.Struct;
import rs.etf.pp1.symboltable.visitors.DumpSymbolTableVisitor;

/**
 * Prosirenje ispisa tabele simbola iz symboltable-1-1.jar.
 *
 * DumpSymbolTableVisitor.visitStructNode ne poznaje Struct.Bool - ni kao samostalan
 * tip ni kao tip elementa niza - pa bi "bool x;" ispisao kao "Var x: , ..." a
 * "bool a[];" kao "Var a: Arr of , ...". Ovde se pokrivaju samo ta dva slucaja,
 * sve ostalo ide na originalnu implementaciju.
 *
 * Jar se ne dira - prosiruje se iskljucivo nasledjivanjem (polje output je protected).
 */
public class MyDumpSymbolTableVisitor extends DumpSymbolTableVisitor {

	@Override
	public void visitStructNode(Struct structToVisit) {
		if (structToVisit.getKind() == Struct.Bool) {
			output.append("bool");
			return;
		}

		if (structToVisit.getKind() == Struct.Array
				&& structToVisit.getElemType() != null
				&& structToVisit.getElemType().getKind() == Struct.Bool) {
			output.append("Arr of bool");
			return;
		}

		super.visitStructNode(structToVisit);
	}
}
