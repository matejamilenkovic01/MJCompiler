package rs.ac.bg.etf.pp1;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;

import rs.ac.bg.etf.pp1.ast.*;
import rs.etf.pp1.symboltable.Tab;
import rs.etf.pp1.symboltable.concepts.Obj;
import rs.etf.pp1.symboltable.concepts.Struct;

public class SemanticAnalyzer extends VisitorAdaptor {

	
	private boolean errorDetected = false;
	
	Logger log = Logger.getLogger(getClass());

	private Obj currentProgram;
	private Struct currentType;
	private int constant;
	private Struct constantType;
	/* Tip "bool" nije predefinisan u Tab-u iz jara - dolazi iz MyTab-a (staticko finalno
	   polje, pa ne zavisi od toga da li je MyTab.init() vec pozvan). */
	private Struct boolType = MyTab.boolType;

	private Obj currentMethod;

	/* Cuva se Obj cvor, a ne samo zastavica: [MJ] A.4 trazi da main bude void i bez
	   argumenata, pa su potrebni i tip i broj parametara. */
	private Obj mainMethod = null;

	/* Da li je u telu tekuce metode vidjen bar jedan return. */
	private boolean returnHappened = false;

	/* Dubina ugnezdenosti for petlji. Uvecava se u visit(ForStart), koji se pri
	   bottom-up obilasku posecuje PRE tela petlje, a umanjuje u visit(Statement_for),
	   koji se posecuje posle njega. Bez markera ForStart brojac ne bi radio. */
	private int loopCnt = 0;
	
	
	/* LOG MESSAGES */

	/* Prefiks je obavezan: TestRunner po njemu broji semanticke greske (kljuc SEMANTIC>=n),
	   isto kao sto broji "Leksicka greska" i "Sintaksna greska". */
	private static final String SEMANTIC_ERROR_PREFIX = "Semanticka greska: ";

	public void report_error(String message, SyntaxNode info) {
		errorDetected = true;
		StringBuilder msg = new StringBuilder(SEMANTIC_ERROR_PREFIX).append(message);
		int line = (info == null) ? 0 : info.getLine();
		if (line != 0) {
			msg.append(" na liniji ").append(line);
		}
		log.error(msg.toString());
	}
	
	public void report_info(String message, SyntaxNode info) {
		StringBuilder msg = new StringBuilder(message);
		int line = (info == null) ? 0 : info.getLine();
		if (line != 0) {
			msg.append(" na liniji ").append(line);
		}
		log.info(msg.toString());
	}
	
	/**
	 * Prijava detektovane upotrebe simbola, u formatu koji trazi Prilog 2 postavke:
	 *
	 *   Pretraga na 9(x), nadjeno Var x: int, 0, 1
	 *
	 * Poruka mora da sadrzi liniju izvornog koda, naziv simbola i ispis objektnog cvora
	 * iz tabele simbola. Ispis cvora se ne formatira rucno nego se uzima od
	 * MyDumpSymbolTableVisitor-a, da bi bio identican onom iz tsdump() - ukljucujuci i
	 * tip bool, koji vizitor iz jara ne poznaje.
	 *
	 * Ide na standardni izlaz (log.info), jer na stderr po postavci idu samo greske.
	 */
	public void report_detection(String name, Obj obj, SyntaxNode info) {
		MyDumpSymbolTableVisitor dump = new MyDumpSymbolTableVisitor();
		obj.accept(dump);

		int line = (info == null) ? 0 : info.getLine();
		StringBuilder msg = new StringBuilder("Pretraga na ").append(line)
				.append("(").append(name).append("), nadjeno ")
				.append(dump.getOutput().trim());

		log.info(msg.toString());
	}

	public boolean passed() {
		return !errorDetected;
	}
	
	/* SEMANTIC PASS CODE */
	
	@Override
	public void visit(ProgramName programName) {
		currentProgram = Tab.insert(Obj.Prog, programName.getProgName(), Tab.noType);
		Tab.openScope();
	}
	
	@Override
	public void visit(Program program) {
		Tab.chainLocalSymbols(currentProgram);
		Tab.closeScope();
		
		/* [MJ] A.4, opsti kontekstni uslovi: "U programu mora postojati metoda sa imenom
		   main. Ona mora biti deklarisana kao void metoda bez argumenata." */
		if (mainMethod == null) {
			report_error("Program nema metodu main", program);
		} else if (mainMethod.getType() != Tab.noType) {
			report_error("Metoda main mora biti deklarisana kao void", program);
		} else if (mainMethod.getLevel() > 0) {
			/* getLevel() je broj formalnih argumenata - uvecava ga visit(FormParDecl_*). */
			report_error("Metoda main ne sme imati formalne argumente", program);
		}
	}
	
	@Override
	public void visit(ConstItem constItem) {
		Obj constItemObj = Tab.find(constItem.getConstName());
		if (constItemObj != Tab.noObj) {
			report_error("Dvostruka definicija konstante: " + constItem.getConstName(), constItem);
		}
		else {
			
			if (constantType.assignableTo(currentType)) {
				constItemObj = Tab.insert(Obj.Con, constItem.getConstName(), currentType);
				constItemObj.setAdr(constant);
			}
			else {
				report_error("Neadekvatna dodela konstanti: " + constItem.getConstName(), constItem);
			}

		}	
	}
	
	@Override
	public void visit(Constant_number constantNumber) {
		constant = constantNumber.getVal();
		constantType = Tab.intType;
	}
	
	@Override
	public void visit(Constant_character constantCharacter) {
		constant = constantCharacter.getVal();
		constantType = Tab.charType;
	}
	
	@Override
	public void visit(Constant_bool constantBool) {
		constant = constantBool.getVal();
		constantType = boolType;
	}
	
	/* VAR DECLARATIONS */
	@Override
	public void visit(VarItem_var varItemVar) {
			
		Obj varItemObj = null;
		if (currentMethod == null) {
			varItemObj = Tab.find(varItemVar.getVarName());
		}
		else {
			varItemObj = Tab.currentScope().findSymbol(varItemVar.getVarName());
		}
		
		if (varItemObj == null || varItemObj == Tab.noObj) {
			varItemObj = Tab.insert(Obj.Var, varItemVar.getVarName(), currentType);
		}
		else {
			report_error("Dvostruka definicija promenljive: " + varItemVar.getVarName(), varItemVar);
		}
	}
	
	@Override
	public void visit(VarItem_array varItemArray) {		
		Obj varItemObj = null;
		if (currentMethod == null) {
			varItemObj = Tab.find(varItemArray.getVarName());
		}
		else {
			varItemObj = Tab.currentScope().findSymbol(varItemArray.getVarName());
		}
		
		if (varItemObj == null || varItemObj == Tab.noObj) {
			varItemObj = Tab.insert(Obj.Var, varItemArray.getVarName(), new Struct(Struct.Array, currentType));
		}
		else {
			report_error("Dvostruka definicija promenljive: " + varItemArray.getVarName(), varItemArray);
		}
	}
	
	/* METHOD DECLARATIONS */
	
	@Override
	public void visit(MethodTypeName methodTypeName) {
		
		Obj methodObj = Tab.find(methodTypeName.getMethName());
		if (methodObj != Tab.noObj) {
			report_error("Dvostruka definicija metode: " + methodTypeName.getMethName(), methodTypeName);
			/* Cvor se ne ubacuje u tabelu, ali se pravi odvojen Obj da currentMethod ne bi
			   ostao null (formalni parametri ga koriste) i da se lokali druge definicije ne
			   bi zakacili na prvu. */
			currentMethod = new Obj(Obj.Meth, methodTypeName.getMethName(), currentType);
		}
		else {
			currentMethod = Tab.insert(Obj.Meth, methodTypeName.getMethName(), currentType);
		}
		/* Opseg se otvara i u slucaju greske: visit(MethodDecl) ga bezuslovno zatvara, pa bi
		   preskakanje openScope() zatvorilo opseg programa. */
		Tab.openScope();
		returnHappened = false;
	}
	
	@Override
	public void visit(RetType_void retTypeVoid) {
		currentType = Tab.noType; // Tab.noType je VOID
	}
	
	@Override
	public void visit(MethodDecl methodDecl) {
		Tab.chainLocalSymbols(currentMethod);
		Tab.closeScope();
		if ("main".equals(currentMethod.getName())) {
			mainMethod = currentMethod;
		}

		/* [MJ] A.4 ovaj uslov opisuje kao proveru u vreme izvrsavanja, ali se prijavljuje
		   ovde - ranije otkrivena greska, i tako radi i referentna implementacija. */
		if (currentMethod.getType() != Tab.noType && !returnHappened) {
			report_error("Metoda " + currentMethod.getName()
					+ " nije void, a nema iskaz return", methodDecl);
		}

		currentMethod = null;
		returnHappened = false;
	}
	
	/* FORM PARAMS DECLARATION */
	public void visit(FormParDecl_var formParVar) {
		
		Obj formParObj = Tab.currentScope().findSymbol(formParVar.getParName());
		if (formParObj != null && formParObj != Tab.noObj) {
			report_error("Dvostruka definicija formalnog parametra " + formParVar.getParName(), formParVar);
		}
		else if(currentType == Tab.noType) {
			report_error("Neadekvatan tip " + currentType.toString() + " formalnog parametra " + formParVar.getParName(), formParVar);
		}
		else {
			formParObj = Tab.insert(Obj.Var, formParVar.getParName(), currentType);
			formParObj.setFpPos(1);
			currentMethod.setLevel(currentMethod.getLevel() + 1);
		}
		
	}
	
	public void visit(FormParDecl_array formParArr) {
		
		Obj formParObj = Tab.currentScope().findSymbol(formParArr.getParName());
		if (formParObj != null && formParObj != Tab.noObj) {
			report_error("Dvostruka definicija formalnog parametra " + formParArr.getParName(), formParArr);
		}
		else if(currentType == Tab.noType) {
			report_error("Neadekvatan tip " + currentType.toString() + " formalnog parametra " + formParArr.getParName(), formParArr);
		}
		else {
			formParObj = Tab.insert(Obj.Var, formParArr.getParName(), new Struct(Struct.Array, currentType));
			formParObj.setFpPos(1);
			currentMethod.setLevel(currentMethod.getLevel() + 1);
		}
	}
	
	
	@Override
	public void visit(Type type) {
		Obj typeObj = Tab.find(type.getTypeName());
		if (typeObj == Tab.noObj) {
			report_error("Nepostojeci tip podatka: " + type.getTypeName(), type);
			currentType = Tab.noType;
		}
		else if (typeObj.getKind() != Obj.Type) {
			report_error("Neadekvatan tip podatka: " + type.getTypeName(), type);
			currentType = Tab.noType;
		}
		else {
			currentType = typeObj.getType();
		}
		/* Isti tip se pamti i na cvoru, da bi ga "new Type [ Expr ]" citao odatle
		   umesto iz deljivog polja currentType. */
		type.struct = currentType;
	}
	
	/* CONTEXT CONDITIONS */

	/** Ugradjeni tipovi - jedini dozvoljeni za read, print i elemente nizova u findAny/map. */
	private boolean isBuiltInType(Struct type) {
		return type == MyTab.intType || type == MyTab.charType || type == MyTab.boolType;
	}

	/** Vrste simbola u koje se sme upisivati: promenljiva, element niza, polje objekta. */
	private boolean isAssignableTarget(int kind) {
		return kind == Obj.Var || kind == Obj.Elem || kind == Obj.Fld;
	}

	// ---------------------------------------------------------------- Designator

	@Override
	public void visit(Designator_var designatorVar) {
		Obj varObj = MyTab.find(designatorVar.getName());

		if (varObj == MyTab.noObj) {
			report_error("Pristup nedefinisanom simbolu: " + designatorVar.getName(), designatorVar);
			designatorVar.obj = MyTab.noObj;
			return;
		}

		int kind = varObj.getKind();
		if (kind != Obj.Var && kind != Obj.Con && kind != Obj.Meth) {
			report_error("Simbol " + designatorVar.getName()
					+ " nije promenljiva, konstanta ni metoda", designatorVar);
			designatorVar.obj = MyTab.noObj;
			return;
		}

		designatorVar.obj = varObj;

		/* Detekcija upotrebe simbola (NIVO A): simbolicke konstante, globalne i lokalne
		   promenljive, i formalni argumenti (NIVO B) - svi su ovde Con ili Var.
		   Metode se NE prijavljuju ovde nego na mestu poziva, jer postavka trazi
		   detekciju "poziva globalne funkcije", a ne pominjanja imena. */
		if (kind == Obj.Con || kind == Obj.Var) {
			report_detection(designatorVar.getName(), varObj, designatorVar);
		}
	}

	@Override
	public void visit(Designator_array designatorArray) {
		Obj arrObj = designatorArray.getDesignator().obj;

		/* Greska je vec prijavljena nize u stablu - ne prijavljuje se dvaput. */
		if (arrObj == MyTab.noObj) {
			designatorArray.obj = MyTab.noObj;
			return;
		}

		if (arrObj.getType().getKind() != Struct.Array) {
			report_error("Indeksira se " + arrObj.getName() + ", sto nije niz", designatorArray);
			designatorArray.obj = MyTab.noObj;
			return;
		}

		if (!MyTab.intType.equals(designatorArray.getExpr().struct)) {
			report_error("Indeks niza " + arrObj.getName() + " nije tipa int", designatorArray);
			designatorArray.obj = MyTab.noObj;
			return;
		}

		/* Sintetickim Obj.Elem cvorom se predstavlja jedan element niza. Tip mu je tip
		   elementa, pa se odatle dalje ponasa kao obicna promenljiva u izrazima i dodelama. */
		designatorArray.obj = new Obj(Obj.Elem, arrObj.getName() + "[$]",
				arrObj.getType().getElemType());

		/* Detekcija (NIVO B): pristup elementu niza. */
		report_detection(arrObj.getName(), arrObj, designatorArray);
	}

	@Override
	public void visit(Designator_length designatorLength) {
		Obj arrObj = designatorLength.getDesignator().obj;

		if (arrObj == MyTab.noObj) {
			designatorLength.obj = MyTab.noObj;
			return;
		}

		if (arrObj.getType().getKind() != Struct.Array) {
			report_error("Polje length se trazi nad " + arrObj.getName() + ", sto nije niz",
					designatorLength);
			designatorLength.obj = MyTab.noObj;
			return;
		}

		/* Obj.Con, a ne Obj.Var: length je samo za citanje, pa ga provera vrste u
		   dodeli/inkrementu automatski odbija kao odrediste. */
		designatorLength.obj = new Obj(Obj.Con, arrObj.getName() + ".length", MyTab.intType);
	}

	// ---------------------------------------------------------------- FactorSub

	@Override
	public void visit(FactorSub_number factorSubNumber) {
		factorSubNumber.struct = MyTab.intType;
	}

	@Override
	public void visit(FactorSub_character factorSubCharacter) {
		factorSubCharacter.struct = MyTab.charType;
	}

	@Override
	public void visit(FactorSub_bool factorSubBool) {
		factorSubBool.struct = MyTab.boolType;
	}

	@Override
	public void visit(FactorSub_designator factorSubDesignator) {
		factorSubDesignator.struct = factorSubDesignator.getDesignator().obj.getType();
	}

	@Override
	public void visit(FactorSub_call factorSubCall) {
		Obj methObj = factorSubCall.getDesignator().obj;

		if (methObj.getKind() != Obj.Meth) {
			if (methObj != MyTab.noObj) {
				report_error("Poziva se " + methObj.getName() + ", sto nije metoda", factorSubCall);
			}
			factorSubCall.struct = MyTab.noType;
			return;
		}

		checkActualParameters(methObj, factorSubCall.getActParsOpt(), factorSubCall);

		/* Detekcija (NIVO B): poziv globalne funkcije. */
		report_detection(methObj.getName(), methObj, factorSubCall);

		factorSubCall.struct = methObj.getType();
	}

	@Override
	public void visit(FactorSub_newArray factorSubNewArray) {
		if (!MyTab.intType.equals(factorSubNewArray.getExpr().struct)) {
			report_error("Velicina niza nije tipa int", factorSubNewArray);
			factorSubNewArray.struct = MyTab.noType;
			return;
		}
		factorSubNewArray.struct = new Struct(Struct.Array, factorSubNewArray.getType().struct);
	}

	@Override
	public void visit(FactorSub_expr factorSubExpr) {
		factorSubExpr.struct = factorSubExpr.getExpr().struct;
	}

	// ---------------------------------------------------------------- Factor

	@Override
	public void visit(Factor_sub factorSub) {
		factorSub.struct = factorSub.getFactorSub().struct;
	}

	@Override
	public void visit(Factor_neg factorNeg) {
		/* [MJ] A.4: Expr = "-" Term. -> Term mora biti tipa int. */
		if (!MyTab.intType.equals(factorNeg.getFactorSub().struct)) {
			report_error("Operand unarnog minusa nije tipa int", factorNeg);
			factorNeg.struct = MyTab.noType;
			return;
		}
		factorNeg.struct = MyTab.intType;
	}

	// ---------------------------------------------------------------- Term / AddExpr / Expr

	@Override
	public void visit(Term_factor termFactor) {
		termFactor.struct = termFactor.getFactor().struct;
	}

	@Override
	public void visit(Term_mulop termMulop) {
		Struct left = termMulop.getTerm().struct;
		Struct right = termMulop.getFactor().struct;

		if (!MyTab.intType.equals(left) || !MyTab.intType.equals(right)) {
			report_error("Operandi operatora * / % moraju biti tipa int", termMulop);
			termMulop.struct = MyTab.noType;
			return;
		}
		termMulop.struct = MyTab.intType;
	}

	@Override
	public void visit(AddExpr_term addExprTerm) {
		addExprTerm.struct = addExprTerm.getTerm().struct;
	}

	@Override
	public void visit(AddExpr_addop addExprAddop) {
		Struct left = addExprAddop.getAddExpr().struct;
		Struct right = addExprAddop.getTerm().struct;

		if (!MyTab.intType.equals(left) || !MyTab.intType.equals(right)) {
			report_error("Operandi operatora + i - moraju biti tipa int", addExprAddop);
			addExprAddop.struct = MyTab.noType;
			return;
		}
		addExprAddop.struct = MyTab.intType;
	}

	@Override
	public void visit(Expr_add exprAdd) {
		exprAdd.struct = exprAdd.getAddExpr().struct;
	}

	@Override
	public void visit(Expr_ternary exprTernary) {
		Struct thenType = exprTernary.getExpr().struct;
		Struct elseType = exprTernary.getExpr1().struct;

		if (!MyTab.boolType.equals(exprTernary.getCondition().struct)) {
			report_error("Uslov ternarnog operatora nije tipa bool", exprTernary);
		}

		/* [MJ] A.4: drugi i treci izraz moraju biti istog tipa. */
		if (!thenType.equals(elseType)) {
			report_error("Grane ternarnog operatora nisu istog tipa", exprTernary);
			exprTernary.struct = MyTab.noType;
			return;
		}
		exprTernary.struct = thenType;
	}

	// ---------------------------------------------------------------- Uslovi

	@Override
	public void visit(CondFact_expr condFactExpr) {
		if (!MyTab.boolType.equals(condFactExpr.getAddExpr().struct)) {
			report_error("Uslovni izraz bez relacionog operatora mora biti tipa bool", condFactExpr);
			condFactExpr.struct = MyTab.noType;
			return;
		}
		condFactExpr.struct = MyTab.boolType;
	}

	@Override
	public void visit(CondFact_relop condFactRelop) {
		Struct left = condFactRelop.getAddExpr().struct;
		Struct right = condFactRelop.getAddExpr1().struct;

		if (!left.compatibleWith(right)) {
			report_error("Operandi relacionog operatora nisu kompatibilni", condFactRelop);
			condFactRelop.struct = MyTab.noType;
			return;
		}

		/* [MJ] A.4: uz nizove i klase smeju samo == i !=. */
		if (left.isRefType() || right.isRefType()) {
			Relop relop = condFactRelop.getRelop();
			if (!(relop instanceof Relop_eq) && !(relop instanceof Relop_neq)) {
				report_error("Uz referentne tipove dozvoljeni su samo operatori == i !=",
						condFactRelop);
				condFactRelop.struct = MyTab.noType;
				return;
			}
		}
		condFactRelop.struct = MyTab.boolType;
	}

	@Override
	public void visit(CondTerm_fact condTermFact) {
		condTermFact.struct = condTermFact.getCondFact().struct;
	}

	@Override
	public void visit(CondTerm_and condTermAnd) {
		Struct left = condTermAnd.getCondTerm().struct;
		Struct right = condTermAnd.getCondFact().struct;

		if (!MyTab.boolType.equals(left) || !MyTab.boolType.equals(right)) {
			report_error("Operandi operatora && moraju biti tipa bool", condTermAnd);
			condTermAnd.struct = MyTab.noType;
			return;
		}
		condTermAnd.struct = MyTab.boolType;
	}

	@Override
	public void visit(Condition_term conditionTerm) {
		conditionTerm.struct = conditionTerm.getCondTerm().struct;
	}

	@Override
	public void visit(Condition_or conditionOr) {
		Struct left = conditionOr.getCondition().struct;
		Struct right = conditionOr.getCondTerm().struct;

		if (!MyTab.boolType.equals(left) || !MyTab.boolType.equals(right)) {
			report_error("Operandi operatora || moraju biti tipa bool", conditionOr);
			conditionOr.struct = MyTab.noType;
			return;
		}
		conditionOr.struct = MyTab.boolType;
	}

	@Override
	public void visit(IfCondition_cond ifConditionCond) {
		ifConditionCond.struct = ifConditionCond.getCondition().struct;
		if (!MyTab.boolType.equals(ifConditionCond.struct)) {
			report_error("Uslov if iskaza nije tipa bool", ifConditionCond);
		}
	}

	@Override
	public void visit(ForCondOpt_cond forCondOptCond) {
		if (!MyTab.boolType.equals(forCondOptCond.getCondition().struct)) {
			report_error("Uslov for petlje nije tipa bool", forCondOptCond);
		}
	}

	// ---------------------------------------------------------------- DesignatorStatement

	@Override
	public void visit(DesignatorStatement_assign designatorStatementAssign) {
		Obj destObj = designatorStatementAssign.getDesignator().obj;
		if (destObj == MyTab.noObj) {
			return;
		}

		if (!isAssignableTarget(destObj.getKind())) {
			report_error("U " + destObj.getName() + " se ne moze upisivati", designatorStatementAssign);
			return;
		}

		if (!designatorStatementAssign.getExpr().struct.assignableTo(destObj.getType())) {
			report_error("Tip izraza nije kompatibilan pri dodeli sa tipom simbola "
					+ destObj.getName(), designatorStatementAssign);
		}
	}

	@Override
	public void visit(DesignatorStatement_inc designatorStatementInc) {
		checkIncDecTarget(designatorStatementInc.getDesignator().obj, "Inkrement",
				designatorStatementInc);
	}

	@Override
	public void visit(DesignatorStatement_dec designatorStatementDec) {
		checkIncDecTarget(designatorStatementDec.getDesignator().obj, "Dekrement",
				designatorStatementDec);
	}

	@Override
	public void visit(DesignatorStatement_call designatorStatementCall) {
		Obj methObj = designatorStatementCall.getDesignator().obj;

		if (methObj.getKind() != Obj.Meth) {
			if (methObj != MyTab.noObj) {
				report_error("Poziva se " + methObj.getName() + ", sto nije metoda",
						designatorStatementCall);
			}
			return;
		}

		checkActualParameters(methObj, designatorStatementCall.getActParsOpt(),
				designatorStatementCall);

		/* Detekcija (NIVO B): poziv globalne funkcije. */
		report_detection(methObj.getName(), methObj, designatorStatementCall);
	}

	// ---------------------------------------------------------------- Iskazi

	@Override
	public void visit(Statement_read statementRead) {
		Obj destObj = statementRead.getDesignator().obj;
		if (destObj == MyTab.noObj) {
			return;
		}

		if (!isAssignableTarget(destObj.getKind())) {
			report_error("U " + destObj.getName() + " se ne moze citati", statementRead);
			return;
		}

		if (!isBuiltInType(destObj.getType())) {
			report_error("read zahteva simbol tipa int, char ili bool, a " + destObj.getName()
					+ " to nije", statementRead);
		}
	}

	@Override
	public void visit(Statement_print statementPrint) {
		if (!isBuiltInType(statementPrint.getExpr().struct)) {
			report_error("print zahteva izraz tipa int, char ili bool", statementPrint);
		}
	}

	@Override
	public void visit(Statement_return statementReturn) {
		if (currentMethod == null) {
			report_error("Iskaz return se ne nalazi unutar tela metode", statementReturn);
			return;
		}

		returnHappened = true;
		Struct methodType = currentMethod.getType();
		ReturnExprOpt returnExprOpt = statementReturn.getReturnExprOpt();

		if (returnExprOpt instanceof ReturnExprOpt_epsilon) {
			/* [MJ] A.4: ako Expr nedostaje, metoda mora biti void. */
			if (methodType != Tab.noType) {
				report_error("Metoda " + currentMethod.getName()
						+ " nije void, pa return mora imati izraz", statementReturn);
			}
			return;
		}

		Struct returnType = ((ReturnExprOpt_expr) returnExprOpt).getExpr().struct;

		if (methodType == Tab.noType) {
			report_error("Metoda " + currentMethod.getName()
					+ " je void, pa return ne sme imati izraz", statementReturn);
			return;
		}

		/* [MJ] A.4: tip izraza mora biti ekvivalentan povratnom tipu metode. */
		if (!returnType.equals(methodType)) {
			report_error("Tip izraza u return-u ne odgovara povratnom tipu metode "
					+ currentMethod.getName(), statementReturn);
		}
	}

	@Override
	public void visit(ForStart forStart) {
		loopCnt++;
	}

	@Override
	public void visit(Statement_for statementFor) {
		loopCnt--;
	}

	@Override
	public void visit(Statement_break statementBreak) {
		if (loopCnt == 0) {
			report_error("Iskaz break se ne nalazi unutar for petlje", statementBreak);
		}
	}

	@Override
	public void visit(Statement_continue statementContinue) {
		if (loopCnt == 0) {
			report_error("Iskaz continue se ne nalazi unutar for petlje", statementContinue);
		}
	}

	// ---------------------------------------------------------------- Amandmani

	/**
	 * Amandman (NIVO A): Designator "=" Designator "." "findAny" "(" Expr ")" ";".
	 *
	 *  - Designator sa desne strane mora oznacavati jednodimenzionalni niz ugradjenog tipa
	 *  - Designator sa leve strane mora oznacavati promenljivu tipa bool
	 *
	 * Uslov na tip izraza Expr nije doslovno napisan u amandmanu, ali sledi iz opisa:
	 * rezultat je true ako u nizu postoji element koji "po vrednosti odgovara rezultatu
	 * izraza Expr", sto ima smisla samo ako je Expr uporediv sa tipom elementa.
	 */
	@Override
	public void visit(Statement_findAny statementFindAny) {
		Obj destObj = statementFindAny.getDesignator().obj;
		Obj sourceObj = statementFindAny.getDesignator1().obj;

		Struct elemType = checkBuiltInArray(sourceObj, "findAny", statementFindAny);

		if (destObj != MyTab.noObj) {
			if (!isAssignableTarget(destObj.getKind()) || !MyTab.boolType.equals(destObj.getType())) {
				report_error("Rezultat funkcije findAny se dodeljuje simbolu "
						+ destObj.getName() + ", koji nije promenljiva tipa bool",
						statementFindAny);
			}
		}

		if (elemType != null
				&& !statementFindAny.getExpr().struct.compatibleWith(elemType)) {
			report_error("Izraz u findAny nije uporediv sa tipom elemenata niza "
					+ sourceObj.getName(), statementFindAny);
		}
	}

	/**
	 * Amandman (NIVO B): Designator "=" Designator "." "map" "(" ident "=>" Expr ")" ";".
	 *
	 *  - Designator mora oznacavati jednodimenzionalni niz ugradjenog tipa
	 *  - ident mora biti lokalna ili globalna promenljiva istog tipa kao elementi tog niza
	 *  - rezultat je novi niz iste duzine, koji prethodno mora biti deklarisan
	 *
	 * Amandman pominje "Designator" u jednini, ali smena ima dva. Uslov za niz se
	 * primenjuje na oba: desni je niz kroz koji se iterira, a levi je "novi niz" koji po
	 * poslednjoj stavci amandmana mora biti prethodno deklarisan - dakle takodje niz.
	 */
	@Override
	public void visit(Statement_map statementMap) {
		Obj destObj = statementMap.getDesignator().obj;
		Obj sourceObj = statementMap.getDesignator1().obj;

		Struct sourceElemType = checkBuiltInArray(sourceObj, "map", statementMap);
		Struct destElemType = checkBuiltInArray(destObj, "map", statementMap);

		/* ident: lokalna ili globalna promenljiva tipa elementa ulaznog niza. */
		Obj iterObj = MyTab.find(statementMap.getIterName());
		if (iterObj == MyTab.noObj) {
			report_error("Iterator " + statementMap.getIterName()
					+ " u map nije deklarisan", statementMap);
		} else if (iterObj.getKind() != Obj.Var) {
			report_error("Iterator " + statementMap.getIterName()
					+ " u map nije promenljiva", statementMap);
		} else if (sourceElemType != null && !sourceElemType.equals(iterObj.getType())) {
			report_error("Iterator " + statementMap.getIterName()
					+ " u map nije istog tipa kao elementi niza " + sourceObj.getName(),
					statementMap);
		}

		if (destElemType != null
				&& !statementMap.getExpr().struct.assignableTo(destElemType)) {
			report_error("Izraz u map nije kompatibilan sa tipom elemenata niza "
					+ destObj.getName(), statementMap);
		}
	}

	// ---------------------------------------------------------------- Pomocne

	/**
	 * Provera da simbol oznacava jednodimenzionalni niz ugradjenog tipa, kako traze
	 * amandmani za findAny i map.
	 *
	 * @return tip elementa niza, ili null ako simbol nije takav niz (greska je prijavljena)
	 *         odnosno ako je greska vec prijavljena nize u stablu.
	 */
	private Struct checkBuiltInArray(Obj arrayObj, String functionName, SyntaxNode info) {
		if (arrayObj == MyTab.noObj) {
			return null;
		}

		if (arrayObj.getType().getKind() != Struct.Array) {
			report_error("Funkcija " + functionName + " se primenjuje na "
					+ arrayObj.getName() + ", sto nije niz", info);
			return null;
		}

		Struct elemType = arrayObj.getType().getElemType();
		if (!isBuiltInType(elemType)) {
			report_error("Funkcija " + functionName + " zahteva niz ugradjenog tipa, a "
					+ arrayObj.getName() + " to nije", info);
			return null;
		}

		return elemType;
	}

	private void checkIncDecTarget(Obj destObj, String operation, SyntaxNode info) {
		if (destObj == MyTab.noObj) {
			return;
		}
		if (!isAssignableTarget(destObj.getKind())) {
			report_error(operation + " se primenjuje na " + destObj.getName()
					+ ", u sta se ne moze upisivati", info);
			return;
		}
		if (!MyTab.intType.equals(destObj.getType())) {
			report_error(operation + " zahteva simbol tipa int, a " + destObj.getName()
					+ " to nije", info);
		}
	}

	/**
	 * [MJ] A.4 za ActPars: broj stvarnih i formalnih argumenata mora biti isti, i tip
	 * svakog stvarnog argumenta mora biti kompatibilan pri dodeli sa tipom formalnog
	 * argumenta na odgovarajucoj poziciji.
	 *
	 * Broj formalnih argumenata se cita iz Obj.getLevel() - visit(FormParDecl_*) ga
	 * uvecava za svaki parametar. Tipovi se citaju iz lokalnih simbola metode, gde su
	 * parametri prvih getLevel() po redu umetanja (tabela simbola koristi LinkedHashMap,
	 * pa je redosled ocuvan; fpPos se NE moze koristiti jer je svim parametrima 1).
	 */
	private void checkActualParameters(Obj methObj, ActParsOpt actParsOpt, SyntaxNode info) {

		List<Struct> actualTypes = new ArrayList<Struct>();
		collectActualParameters(actParsOpt, actualTypes);

		int formalCount = methObj.getLevel();

		if (actualTypes.size() != formalCount) {
			report_error("Metoda " + methObj.getName() + " ocekuje " + formalCount
					+ " argumenata, a pozvana je sa " + actualTypes.size(), info);
			return;
		}

		/* Rekurzivni poziv: lokali tekuce metode se ulancavaju tek u visit(MethodDecl),
		   pa bi getLocalSymbols() ovde vratio praznu listu. Opseg je jos otvoren, pa se
		   parametri citaju iz njega. */
		Collection<Obj> formalParams = (methObj == currentMethod)
				? Tab.currentScope().values()
				: methObj.getLocalSymbols();

		int position = 0;
		for (Obj formalParam : formalParams) {
			if (position >= formalCount) {
				break;
			}
			if (!actualTypes.get(position).assignableTo(formalParam.getType())) {
				report_error("Tip " + (position + 1) + ". argumenta u pozivu metode "
						+ methObj.getName() + " nije kompatibilan sa formalnim argumentom "
						+ formalParam.getName(), info);
			}
			position++;
		}
	}

	/**
	 * Skuplja tipove stvarnih argumenata, sleva nadesno. Namerno rekurzivno kroz stablo,
	 * bez polja u analizatoru - tako ugnezdeni pozivi kao f(g(x)) rade sami od sebe, dok
	 * bi resenje sa jednim deljivim poljem tu palo.
	 */
	private void collectActualParameters(ActParsOpt actParsOpt, List<Struct> actualTypes) {
		if (actParsOpt instanceof ActParsOpt_pars) {
			collectActualParameters(((ActParsOpt_pars) actParsOpt).getActPars(), actualTypes);
		}
	}

	private void collectActualParameters(ActPars actPars, List<Struct> actualTypes) {
		if (actPars instanceof ActPars_more) {
			ActPars_more more = (ActPars_more) actPars;
			/* ActPars je levo rekurzivan, pa se prvo silazi ulevo da bi redosled ostao
			   onakav kakav je u izvornom kodu. */
			collectActualParameters(more.getActPars(), actualTypes);
			actualTypes.add(more.getExpr().struct);
		} else {
			actualTypes.add(((ActPars_one) actPars).getExpr().struct);
		}
	}

}
