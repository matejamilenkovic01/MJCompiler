package rs.ac.bg.etf.pp1;

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

	private Struct currentMethodType;

	private Obj currentMethod;

	private boolean mainHappened = false;
	
	
	/* LOG MESSAGES */
	public void report_error(String message, SyntaxNode info) {
		errorDetected = true;
		StringBuilder msg = new StringBuilder(message);
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
		
		if (!mainHappened) {
			report_error("Program nema main metodu.", program);
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
			mainHappened  = true;
		}
		currentMethod = null;
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
	}
}
