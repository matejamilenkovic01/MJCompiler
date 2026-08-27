package rs.ac.bg.etf.pp1;

import rs.etf.pp1.symboltable.Tab;
import rs.etf.pp1.symboltable.concepts.Obj;
import rs.etf.pp1.symboltable.concepts.Struct;

/**
 * Prosirenje tabele simbola iz symboltable-1-1.jar.
 *
 * Tab.init() u universe opseg ubacuje samo int, char, eol, null, chr, ord i len.
 * Tip "bool" iz [MJ] specifikacije nije predefinisan, pa se dodaje ovde.
 * Jar se ne dira - prosiruje se iskljucivo nasledjivanjem.
 *
 * Compiler.main mora da zove MyTab.init(), a ne Tab.init().
 */
public class MyTab extends Tab {

	public static final Struct boolType = new Struct(Struct.Bool);

	public static void init() {
		Tab.init();
		// posle Tab.init() je currentScope == universe
		currentScope.addToLocals(new Obj(Obj.Type, "bool", boolType));
	}
}
