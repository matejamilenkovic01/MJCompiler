package rs.ac.bg.etf.pp1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import rs.ac.bg.etf.pp1.ast.*;
import rs.etf.pp1.mj.runtime.Code;
import rs.etf.pp1.symboltable.Tab;
import rs.etf.pp1.symboltable.concepts.Obj;
import rs.etf.pp1.symboltable.concepts.Struct;

/**
 * Generisanje MikroJava bajtkoda (faza 4).
 *
 * Obilazak je isti bottom-up kao u semantickoj analizi, pa se kod nekog cvora emituje tek
 * POSLE koda sve njegove dece. Odatle slede dva obrasca koja se ponavljaju kroz ceo fajl:
 *
 *  1. Marker neterminali (ElseMark, TernQ, TernC, MapArrow, ForStart) daju tacku u kojoj se
 *     kod emituje IZMEDJU dva podstabla. Bez njih bi se, na primer, then i else grana nasle
 *     jedna za drugom bez skoka izmedju.
 *
 *  2. Backpatching: skok se emituje pre nego sto se zna adresa odredista, sa nulom u
 *     operandu; adresa tog operanda (Code.pc - 2) se zapamti, pa se kasnije zakrpi sa
 *     Code.fixup, koji operand postavlja tako da skok vodi na tekuci Code.pc.
 *     Kada je odrediste vec poznato (skok unazad), koristi se Code.putJump(adresa) direktno.
 */
public class CodeGenerator extends VisitorAdaptor {

	/** Broj radnih lokala koji se rezervisu u svakoj metodi, iznad njenih pravih lokala.
	 *  Koriste ih findAny i map za brojac petlje i medjurezultate (vidi dnu fajla). */
	private static final int SCRATCH_SLOTS = 3;

	private int mainPc = -1;

	/** Metoda koja se trenutno generise - potrebna za adrese radnih lokala i za map iterator. */
	private Obj currentMethodObj = null;
	/** Indeks prvog radnog lokala u tekucoj metodi. */
	private int scratchBase = 0;

	public int getMainPc() {
		return mainPc;
	}

	/* ============================================================
	   Predefinisane metode: ord, chr, len

	   Tab.init() ih ubacuje u univerzalni opseg, ali bez tela. Generisu se prve, pre bilo kog
	   korisnickog koda, i njihove adrese se upisuju u tabelu simbola da bi ih poziv nasao.
	   ord i chr su na nivou bajtkoda identicne - obe samo vracaju svoj argument.
	   ============================================================ */

	/** Cvor programa - nosi ulancane globalne simbole (vidi findVar). */
	private final Obj programObj;

	public CodeGenerator(Obj programObj) {
		this.programObj = programObj;
		generatePredeclaredMethods();
	}

	private void generatePredeclaredMethods() {

		Obj ordMethod = Tab.find("ord");
		Obj chrMethod = Tab.find("chr");
		ordMethod.setAdr(Code.pc);
		chrMethod.setAdr(Code.pc);
		Code.put(Code.enter);
		Code.put(1);
		Code.put(1);
		Code.put(Code.load_n);
		Code.put(Code.exit);
		Code.put(Code.return_);

		Obj lenMethod = Tab.find("len");
		lenMethod.setAdr(Code.pc);
		Code.put(Code.enter);
		Code.put(1);
		Code.put(1);
		Code.put(Code.load_n);
		Code.put(Code.arraylength);
		Code.put(Code.exit);
		Code.put(Code.return_);
	}

	/* ============================================================
	   Pomocne
	   ============================================================ */

	private boolean isChar(Struct type) {
		return Tab.charType.equals(type);
	}

	/** Citanje radnog lokala. Za indekse 0-3 postoji kraci oblik instrukcije. */
	private void loadLocal(int index) {
		if (index >= 0 && index <= 3) {
			Code.put(Code.load_n + index);
		} else {
			Code.put(Code.load);
			Code.put(index);
		}
	}

	private void storeLocal(int index) {
		if (index >= 0 && index <= 3) {
			Code.put(Code.store_n + index);
		} else {
			Code.put(Code.store);
			Code.put(index);
		}
	}

	/**
	 * Pretraga promenljive po imenu u vreme generisanja koda.
	 *
	 * Tab.find se ovde NE sme koristiti: semanticka analiza je zatvorila i opseg programa i
	 * opsege metoda, pa Tab.find vidi jos samo univerzalni opseg i za svaki korisnicki simbol
	 * vraca noObj. Simboli se zato citaju iz ulancanih lista - prvo lokali tekuce metode, pa
	 * globali sa cvora programa, sto je isti redosled koji je vazio i pri semantickoj analizi.
	 *
	 * Potrebno je samo za map iterator, koji je po amandmanu goli ident, pa nema svoj
	 * Designator cvor na kome bi mu simbol vec stajao.
	 */
	private Obj findVar(String name) {
		if (currentMethodObj != null) {
			for (Obj local : currentMethodObj.getLocalSymbols()) {
				if (local.getName().equals(name)) {
					return local;
				}
			}
		}
		for (Obj global : programObj.getLocalSymbols()) {
			if (global.getName().equals(name)) {
				return global;
			}
		}
		return Tab.noObj;
	}

	private static List<Integer> newList(int first) {
		List<Integer> list = new ArrayList<Integer>();
		list.add(first);
		return list;
	}

	/* ============================================================
	   Pozivi metoda

	   Operand instrukcije call je pomeraj u odnosu na adresu same instrukcije. Metoda moze
	   biti pozvana pre nego sto je generisana (poziv unapred), pa se u trenutku poziva ne zna
	   njena adresa. Zato se svi pozivi pamte i krpe zajedno u visit(Program), kada su adrese
	   svih metoda poznate.
	   ============================================================ */

	private List<Integer> callPatchAddr = new ArrayList<Integer>();
	private List<Obj> callPatchObj = new ArrayList<Obj>();

	private void emitCall(Obj methodObj) {
		Code.put(Code.call);
		callPatchAddr.add(Code.pc);
		callPatchObj.add(methodObj);
		Code.put2(0);
	}

	@Override
	public void visit(Program program) {
		for (int i = 0; i < callPatchAddr.size(); i++) {
			int operandAddr = callPatchAddr.get(i);
			// operandAddr - 1 je adresa same instrukcije call
			Code.put2(operandAddr, callPatchObj.get(i).getAdr() - (operandAddr - 1));
		}
	}

	/* ============================================================
	   Metode
	   ============================================================ */

	@Override
	public void visit(MethodTypeName methodTypeName) {

		Obj methodObj = methodTypeName.obj;
		methodObj.setAdr(Code.pc);
		currentMethodObj = methodObj;

		/* Opseg metode sadrzi iskljucivo promenljive (formalne argumente i lokale), pa je
		   broj simbola ujedno i broj reci koje treba rezervisati. Radni lokali se dodaju na
		   to, a scratchBase je indeks prvog od njih. */
		int localCount = methodObj.getLocalSymbols().size();
		scratchBase = localCount;

		if ("main".equals(methodObj.getName())) {
			mainPc = Code.pc;
		}

		Code.put(Code.enter);
		Code.put(methodObj.getLevel());              // broj formalnih argumenata
		Code.put(localCount + SCRATCH_SLOTS);        // ukupno reci u aktivacionom zapisu
	}

	@Override
	public void visit(MethodDecl methodDecl) {
		/* Izlaz za void metode i za pad kroz kraj tela. Za metode sa return iskazom je ovo
		   nedostizan kod, sto je bezopasno. */
		Code.put(Code.exit);
		Code.put(Code.return_);
		currentMethodObj = null;
	}

	/* ============================================================
	   Designator

	   Pravilo: Designator_var sam po sebi ne emituje nista - tek onaj ko ga koristi zna da li
	   mu treba citanje (Code.load) ili upis (Code.store). Jedini izuzetak je kada je
	   Designator deo veceg designatora: kod "a[i]" referenca na niz mora na stek PRE indeksa,
	   a indeks je sledbenik u istoj smeni. Zato se tu gleda roditelj.
	   ============================================================ */

	@Override
	public void visit(Designator_var designatorVar) {
		SyntaxNode parent = designatorVar.getParent();
		if (parent instanceof Designator_array || parent instanceof Designator_length) {
			Code.load(designatorVar.obj);
		}
	}

	@Override
	public void visit(Designator_length designatorLength) {
		// referenca na niz je vec na steku (vidi visit(Designator_var))
		Code.put(Code.arraylength);
	}

	/* ============================================================
	   Faktori i izrazi
	   ============================================================ */

	@Override
	public void visit(FactorSub_number factorSubNumber) {
		Code.loadConst(factorSubNumber.getVal());
	}

	@Override
	public void visit(FactorSub_character factorSubCharacter) {
		Code.loadConst(factorSubCharacter.getVal());
	}

	@Override
	public void visit(FactorSub_bool factorSubBool) {
		Code.loadConst(factorSubBool.getVal());
	}

	@Override
	public void visit(FactorSub_designator factorSubDesignator) {
		/* Kod "a.length" je vrednost vec na steku, jer ju je ostavila instrukcija arraylength.
		   Za sve ostalo se ovde emituje citanje. */
		if (!(factorSubDesignator.getDesignator() instanceof Designator_length)) {
			Code.load(factorSubDesignator.getDesignator().obj);
		}
	}

	@Override
	public void visit(FactorSub_call factorSubCall) {
		emitCall(factorSubCall.getDesignator().obj);
	}

	@Override
	public void visit(FactorSub_newArray factorSubNewArray) {
		// velicina niza je vec na steku
		Code.put(Code.newarray);
		Code.put(isChar(factorSubNewArray.getType().struct) ? 0 : 1);
	}

	@Override
	public void visit(Factor_neg factorNeg) {
		Code.put(Code.neg);
	}

	@Override
	public void visit(Term_mulop termMulop) {
		Mulop mulop = termMulop.getMulop();
		if (mulop instanceof Mulop_mul) {
			Code.put(Code.mul);
		} else if (mulop instanceof Mulop_div) {
			Code.put(Code.div);
		} else {
			Code.put(Code.rem);
		}
	}

	@Override
	public void visit(AddExpr_addop addExprAddop) {
		if (addExprAddop.getAddop() instanceof Addop_plus) {
			Code.put(Code.add);
		} else {
			Code.put(Code.sub);
		}
	}

	/* ============================================================
	   Uslovi

	   Sema je standardna sema sa kratkim spajanjem:

	     - svaki CondFact emituje uslovni skok koji se izvrsava kada je fact NETACAN;
	       propadanje kroz njega znaci da je fact tacan
	     - kada se zavrsi ceo CondTerm (lanac &&), propadanje znaci da je term tacan, pa se
	       emituje bezuslovni skok na TACNO, a svi netacni skokovi tog terma se zakrpe na
	       tekucu adresu - a to je pocetak sledeceg terma
	     - kada se zavrsi cela Condition (lanac ||), propadanje znaci da su svi termovi
	       netacni, pa se emituje skok na NETACNO, a svi tacni skokovi se zakrpe ovde
	
	   Liste skokova se drze u mapama kljucevanim AST cvorom, a ne u jednom deljivom steku.
	   Razlog: ternarni operator moze da se ugnezdi unutar uslova ("a && (b ? c : d) > 0"), pa
	   bi unutrasnja Condition potrosila skokove spoljasnje.
	   ============================================================ */

	private Map<SyntaxNode, List<Integer>> falseJumps = new HashMap<SyntaxNode, List<Integer>>();
	private Map<SyntaxNode, List<Integer>> trueJumps = new HashMap<SyntaxNode, List<Integer>>();

	private int relopCode(Relop relop) {
		if (relop instanceof Relop_eq) {
			return Code.eq;
		} else if (relop instanceof Relop_neq) {
			return Code.ne;
		} else if (relop instanceof Relop_gt) {
			return Code.gt;
		} else if (relop instanceof Relop_geq) {
			return Code.ge;
		} else if (relop instanceof Relop_lt) {
			return Code.lt;
		}
		return Code.le;
	}

	@Override
	public void visit(CondFact_relop condFactRelop) {
		Code.putFalseJump(relopCode(condFactRelop.getRelop()), 0);
		falseJumps.put(condFactRelop, newList(Code.pc - 2));
	}

	@Override
	public void visit(CondFact_expr condFactExpr) {
		/* Uslov bez relacionog operatora je izraz tipa bool: tacan je kada nije jednak nuli. */
		Code.loadConst(0);
		Code.putFalseJump(Code.ne, 0);
		falseJumps.put(condFactExpr, newList(Code.pc - 2));
	}

	@Override
	public void visit(CondTerm_fact condTermFact) {
		falseJumps.put(condTermFact, falseJumps.remove(condTermFact.getCondFact()));
		closeTermIfComplete(condTermFact);
	}

	@Override
	public void visit(CondTerm_and condTermAnd) {
		List<Integer> merged = falseJumps.remove(condTermAnd.getCondTerm());
		merged.addAll(falseJumps.remove(condTermAnd.getCondFact()));
		falseJumps.put(condTermAnd, merged);
		closeTermIfComplete(condTermAnd);
	}

	/**
	 * CondTerm je zavrsen samo ako nije levi operand nekog &&. Tada se emituje skok na TACNO,
	 * a netacni skokovi tog terma se krpe na pocetak sledeceg terma - dakle ovde.
	 */
	private void closeTermIfComplete(CondTerm condTerm) {
		if (condTerm.getParent() instanceof CondTerm_and) {
			return;
		}
		Code.putJump(0);
		List<Integer> termTrue = newList(Code.pc - 2);
		for (int address : falseJumps.remove(condTerm)) {
			Code.fixup(address);
		}
		trueJumps.put(condTerm, termTrue);
	}

	@Override
	public void visit(Condition_term conditionTerm) {
		trueJumps.put(conditionTerm, trueJumps.remove(conditionTerm.getCondTerm()));
	}

	@Override
	public void visit(Condition_or conditionOr) {
		List<Integer> merged = trueJumps.remove(conditionOr.getCondition());
		merged.addAll(trueJumps.remove(conditionOr.getCondTerm()));
		trueJumps.put(conditionOr, merged);
	}

	/**
	 * Zatvara uslov: propadanje znaci NETACNO, pa se emituje skok koji ce se zakrpiti na
	 * granu za netacan slucaj, a svi tacni skokovi se dovode ovde - na pocetak grane za
	 * tacan slucaj.
	 *
	 * @return adresa operanda skoka za netacan slucaj, za kasniji Code.fixup
	 */
	private int closeCondition(Condition condition) {
		Code.putJump(0);
		int falseExit = Code.pc - 2;
		for (int address : trueJumps.remove(condition)) {
			Code.fixup(address);
		}
		return falseExit;
	}

	/* ============================================================
	   if / if-else
	   ============================================================ */

	private Stack<Integer> ifFalseJumps = new Stack<Integer>();
	private Stack<Integer> elseEndJumps = new Stack<Integer>();

	@Override
	public void visit(IfCondition_cond ifConditionCond) {
		ifFalseJumps.push(closeCondition(ifConditionCond.getCondition()));
	}

	@Override
	public void visit(Statement_if statementIf) {
		Code.fixup(ifFalseJumps.pop());
	}

	@Override
	public void visit(ElseMark elseMark) {
		/* Kraj then grane: preskoci else granu, pa dovedi netacan slucaj ovde. */
		Code.putJump(0);
		elseEndJumps.push(Code.pc - 2);
		Code.fixup(ifFalseJumps.pop());
	}

	@Override
	public void visit(Statement_ifElse statementIfElse) {
		Code.fixup(elseEndJumps.pop());
	}

	/* ============================================================
	   Ternarni operator
	   ============================================================ */

	private Stack<Integer> ternaryFalseJumps = new Stack<Integer>();
	private Stack<Integer> ternaryEndJumps = new Stack<Integer>();

	@Override
	public void visit(TernQ ternQ) {
		Expr_ternary parent = (Expr_ternary) ternQ.getParent();
		ternaryFalseJumps.push(closeCondition(parent.getCondition()));
	}

	@Override
	public void visit(TernC ternC) {
		Code.putJump(0);
		ternaryEndJumps.push(Code.pc - 2);
		Code.fixup(ternaryFalseJumps.pop());
	}

	@Override
	public void visit(Expr_ternary exprTernary) {
		Code.fixup(ternaryEndJumps.pop());
	}

	/* ============================================================
	   for petlja

	   Delovi zaglavlja se u izvornom kodu pisu redom init, uslov, korak, a izvrsavaju se
	   redom init, uslov, telo, korak. Posto se kod emituje redom kojim se cita, korak zavrsi
	   pre tela, pa se raspored resava skokovima:

	       <init>
	     uslov:  <uslov>            netacno -> kraj
	             jmp telo
	     korak:  <korak>
	             jmp uslov
	     telo:   <telo>
	             jmp korak
	     kraj:

	   continue skace na "korak", cija je adresa poznata pre tela, pa se emituje direktno.
	   break skace na "kraj", koji jos nije poznat, pa ide u listu za krpljenje.
	   ============================================================ */

	private static class LoopFrame {
		int condAddr = -1;
		int stepAddr = -1;
		int bodyJump = -1;
		int falseExit = -1;
		List<Integer> breaks = new ArrayList<Integer>();
	}

	private Stack<LoopFrame> loops = new Stack<LoopFrame>();

	@Override
	public void visit(ForStart forStart) {
		loops.push(new LoopFrame());
	}

	@Override
	public void visit(ForInitOpt_stmt forInitOptStmt) {
		loops.peek().condAddr = Code.pc;
	}

	@Override
	public void visit(ForInitOpt_epsilon forInitOptEpsilon) {
		loops.peek().condAddr = Code.pc;
	}

	@Override
	public void visit(ForCondOpt_cond forCondOptCond) {
		LoopFrame frame = loops.peek();
		frame.falseExit = closeCondition(forCondOptCond.getCondition());
		Code.putJump(0);
		frame.bodyJump = Code.pc - 2;
		frame.stepAddr = Code.pc;
	}

	@Override
	public void visit(ForCondOpt_epsilon forCondOptEpsilon) {
		/* Izostavljen uslov znaci "uvek tacno" - nema izlaznog skoka. */
		LoopFrame frame = loops.peek();
		Code.putJump(0);
		frame.bodyJump = Code.pc - 2;
		frame.stepAddr = Code.pc;
	}

	@Override
	public void visit(ForStepOpt_stmt forStepOptStmt) {
		closeForStep();
	}

	@Override
	public void visit(ForStepOpt_epsilon forStepOptEpsilon) {
		closeForStep();
	}

	private void closeForStep() {
		LoopFrame frame = loops.peek();
		Code.putJump(frame.condAddr);
		Code.fixup(frame.bodyJump);
	}

	@Override
	public void visit(Statement_for statementFor) {
		LoopFrame frame = loops.pop();
		Code.putJump(frame.stepAddr);
		if (frame.falseExit >= 0) {
			Code.fixup(frame.falseExit);
		}
		for (int address : frame.breaks) {
			Code.fixup(address);
		}
	}

	@Override
	public void visit(Statement_break statementBreak) {
		Code.putJump(0);
		loops.peek().breaks.add(Code.pc - 2);
	}

	@Override
	public void visit(Statement_continue statementContinue) {
		Code.putJump(loops.peek().stepAddr);
	}

	/* ============================================================
	   Iskazi
	   ============================================================ */

	@Override
	public void visit(DesignatorStatement_assign designatorStatementAssign) {
		Code.store(designatorStatementAssign.getDesignator().obj);
	}

	@Override
	public void visit(DesignatorStatement_inc designatorStatementInc) {
		emitIncDec(designatorStatementInc.getDesignator().obj, Code.add);
	}

	@Override
	public void visit(DesignatorStatement_dec designatorStatementDec) {
		emitIncDec(designatorStatementDec.getDesignator().obj, Code.sub);
	}

	/**
	 * Kod elementa niza su referenca i indeks vec na steku, ali ih trosi i citanje i upis, pa
	 * se pre citanja udvajaju sa dup2.
	 */
	private void emitIncDec(Obj designatorObj, int operation) {
		if (designatorObj.getKind() == Obj.Elem) {
			Code.put(Code.dup2);
		}
		Code.load(designatorObj);
		Code.loadConst(1);
		Code.put(operation);
		Code.store(designatorObj);
	}

	@Override
	public void visit(DesignatorStatement_call designatorStatementCall) {
		Obj methodObj = designatorStatementCall.getDesignator().obj;
		emitCall(methodObj);
		/* Poziv funkcije kao iskaz: povratna vrednost se odbacuje. */
		if (methodObj.getType() != Tab.noType) {
			Code.put(Code.pop);
		}
	}

	@Override
	public void visit(Statement_read statementRead) {
		Obj destObj = statementRead.getDesignator().obj;
		Code.put(isChar(destObj.getType()) ? Code.bread : Code.read);
		Code.store(destObj);
	}

	@Override
	public void visit(Statement_print statementPrint) {
		PrintWidthOpt widthOpt = statementPrint.getPrintWidthOpt();
		int width = (widthOpt instanceof PrintWidthOpt_width)
				? ((PrintWidthOpt_width) widthOpt).getWidth()
				: 0;
		Code.loadConst(width);
		Code.put(isChar(statementPrint.getExpr().struct) ? Code.bprint : Code.print);
	}

	@Override
	public void visit(Statement_return statementReturn) {
		// vrednost izraza je, ako ga ima, vec na steku
		Code.put(Code.exit);
		Code.put(Code.return_);
	}

	/* ============================================================
	   Amandman (NIVO A): findAny

	   Designator "=" Designator "." "findAny" "(" Expr ")" ";"

	   Trazena vrednost je vec na steku. Ispod nje mogu biti referenca i indeks odredista, ako
	   je odrediste element niza (npr. "nadjeno[i] = niz.findAny(7)"), pa se petlja izvrsava
	   iskljucivo preko radnih lokala i ne dira ono sto je ispod.

	       s0 = trazena vrednost
	       s1 = indeks
	       s2 = rezultat
	   ============================================================ */

	@Override
	public void visit(Statement_findAny statementFindAny) {

		Obj destObj = statementFindAny.getDesignator().obj;
		Obj sourceObj = statementFindAny.getDesignator1().obj;
		boolean charElements = isChar(sourceObj.getType().getElemType());

		int valueSlot = scratchBase;
		int indexSlot = scratchBase + 1;
		int resultSlot = scratchBase + 2;

		storeLocal(valueSlot);
		Code.loadConst(0);
		storeLocal(indexSlot);
		Code.loadConst(0);
		storeLocal(resultSlot);

		int loopStart = Code.pc;

		loadLocal(indexSlot);
		Code.load(sourceObj);
		Code.put(Code.arraylength);
		Code.putFalseJump(Code.lt, 0);
		int exitJump = Code.pc - 2;

		Code.load(sourceObj);
		loadLocal(indexSlot);
		Code.put(charElements ? Code.baload : Code.aload);
		loadLocal(valueSlot);
		Code.putFalseJump(Code.eq, 0);
		int notEqualJump = Code.pc - 2;

		// element je jednak trazenoj vrednosti - postavi rezultat i prekini petlju
		Code.loadConst(1);
		storeLocal(resultSlot);
		Code.putJump(0);
		int foundJump = Code.pc - 2;

		Code.fixup(notEqualJump);
		loadLocal(indexSlot);
		Code.loadConst(1);
		Code.put(Code.add);
		storeLocal(indexSlot);
		Code.putJump(loopStart);

		Code.fixup(exitJump);
		Code.fixup(foundJump);

		loadLocal(resultSlot);
		Code.store(destObj);
	}

	/* ============================================================
	   Amandman (NIVO B): map

	   Designator "=" Designator "." "map" "(" ident "=>" Expr ")" ";"

	   Telo (Expr) se izracunava jednom po elementu, pa mora da se nadje UNUTAR petlje. Posto
	   se kod emituje redom kojim se cita, zaglavlje petlje se emituje na markeru MapArrow -
	   dakle pre Expr - a rep petlje na samom iskazu, posle njega.

	   Rezultat je nov niz iste duzine, kako trazi amandman; prethodna vrednost odredista se
	   prepisuje.
	   ============================================================ */

	private static class MapFrame {
		int indexSlot;
		int loopStart;
		int exitJump;
		boolean charElements;
	}

	private Stack<MapFrame> maps = new Stack<MapFrame>();

	@Override
	public void visit(MapArrow mapArrow) {

		Statement_map statementMap = (Statement_map) mapArrow.getParent();

		Obj destObj = statementMap.getDesignator().obj;
		Obj sourceObj = statementMap.getDesignator1().obj;
		Obj iteratorObj = findVar(statementMap.getIterName());

		MapFrame frame = new MapFrame();
		frame.indexSlot = scratchBase;
		frame.charElements = isChar(destObj.getType().getElemType());
		boolean charSource = isChar(sourceObj.getType().getElemType());

		// odrediste = nov niz duzine ulaznog niza
		Code.load(sourceObj);
		Code.put(Code.arraylength);
		Code.put(Code.newarray);
		Code.put(frame.charElements ? 0 : 1);
		Code.store(destObj);

		Code.loadConst(0);
		storeLocal(frame.indexSlot);

		frame.loopStart = Code.pc;

		loadLocal(frame.indexSlot);
		Code.load(sourceObj);
		Code.put(Code.arraylength);
		Code.putFalseJump(Code.lt, 0);
		frame.exitJump = Code.pc - 2;

		// iterator = ulazniNiz[i]
		Code.load(sourceObj);
		loadLocal(frame.indexSlot);
		Code.put(charSource ? Code.baload : Code.aload);
		Code.store(iteratorObj);

		/* Referenca i indeks odredista idu na stek sada, da bi posle koda tela na vrhu bilo
		   [niz, indeks, vrednost] - tacno ono sto astore ocekuje. */
		Code.load(destObj);
		loadLocal(frame.indexSlot);

		maps.push(frame);
	}

	@Override
	public void visit(Statement_map statementMap) {

		MapFrame frame = maps.pop();

		Code.put(frame.charElements ? Code.bastore : Code.astore);

		loadLocal(frame.indexSlot);
		Code.loadConst(1);
		Code.put(Code.add);
		storeLocal(frame.indexSlot);
		Code.putJump(frame.loopStart);

		Code.fixup(frame.exitJump);
	}

}
