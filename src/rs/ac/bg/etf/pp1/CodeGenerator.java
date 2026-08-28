package rs.ac.bg.etf.pp1;

import rs.ac.bg.etf.pp1.ast.*;
import rs.etf.pp1.mj.runtime.Code;
import rs.etf.pp1.symboltable.Tab;

public class CodeGenerator extends VisitorAdaptor {
	
	private int mainPC;
	
	public int getMainPc() {
		return this.mainPC;
	}
	
	@Override 
	public void visit(MethodTypeName methodTypeName) {
		Code.put(Code.enter);
		Code.put(methodTypeName.obj.getLevel()); // b1
		Code.put(methodTypeName.obj.getLocalSymbols().size()); // b2
	}
	
	@Override 
	public void visit(MethodDecl methodDecl) {
		Code.put(Code.exit);
		Code.put(Code.return_);
	}
	
	@Override
	public void visit(Statement_print statementPrint) {
		Code.loadConst(0);
		if (statementPrint.getExpr().struct.equals(Tab.charType)) {
			Code.put(Code.bprint);
		}
		else {
			Code.put(Code.print);
		}
		
	}
	
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
	public void visit(AddExpr_addop addExprAddop) {
		if (addExprAddop.getAddop() instanceof Addop_plus) {
			Code.put(Code.add);
		}
		else if (addExprAddop.getAddop() instanceof Addop_minus) {
			Code.put(Code.sub);
		}
	}
	
	@Override
	public void visit(Term_mulop termMulop) {
		if (termMulop.getMulop() instanceof Mulop_mul) {
			Code.put(Code.mul);
		}
		else if (termMulop.getMulop() instanceof Mulop_div) {
			Code.put(Code.div);
		}
		else if (termMulop.getMulop() instanceof Mulop_mod) {
			Code.put(Code.rem);
		}
	}
	
}
