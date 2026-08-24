// generated with ast extension for cup
// version 0.8
// 25/7/2026 0:36:4


package rs.ac.bg.etf.pp1.ast;

public abstract class VisitorAdaptor implements Visitor { 

    public void visit(Mulop Mulop) { }
    public void visit(VarDeclListOpt VarDeclListOpt) { }
    public void visit(FormParsOpt FormParsOpt) { }
    public void visit(Constant Constant) { }
    public void visit(AddExpr AddExpr) { }
    public void visit(Relop Relop) { }
    public void visit(ActParsOpt ActParsOpt) { }
    public void visit(ForCondOpt ForCondOpt) { }
    public void visit(ForStepOpt ForStepOpt) { }
    public void visit(StatementList StatementList) { }
    public void visit(Addop Addop) { }
    public void visit(PrintWidthOpt PrintWidthOpt) { }
    public void visit(Factor Factor) { }
    public void visit(ReturnExprOpt ReturnExprOpt) { }
    public void visit(CondTerm CondTerm) { }
    public void visit(DeclList DeclList) { }
    public void visit(Designator Designator) { }
    public void visit(Term Term) { }
    public void visit(RetType RetType) { }
    public void visit(Condition Condition) { }
    public void visit(VarItem VarItem) { }
    public void visit(IfCondition IfCondition) { }
    public void visit(FormParDecl FormParDecl) { }
    public void visit(ForInitOpt ForInitOpt) { }
    public void visit(ConstItemList ConstItemList) { }
    public void visit(Expr Expr) { }
    public void visit(ActPars ActPars) { }
    public void visit(DesignatorStatement DesignatorStatement) { }
    public void visit(Statement Statement) { }
    public void visit(VarItemList VarItemList) { }
    public void visit(CondFact CondFact) { }
    public void visit(MethodDeclList MethodDeclList) { }
    public void visit(FormPars FormPars) { }
    public void visit(Designator_length Designator_length) { visit(); }
    public void visit(Designator_array Designator_array) { visit(); }
    public void visit(Designator_ident Designator_ident) { visit(); }
    public void visit(Factor_expr Factor_expr) { visit(); }
    public void visit(Factor_newArray Factor_newArray) { visit(); }
    public void visit(Factor_bool Factor_bool) { visit(); }
    public void visit(Factor_character Factor_character) { visit(); }
    public void visit(Factor_number Factor_number) { visit(); }
    public void visit(Factor_call Factor_call) { visit(); }
    public void visit(Factor_designator Factor_designator) { visit(); }
    public void visit(Mulop_mod Mulop_mod) { visit(); }
    public void visit(Mulop_div Mulop_div) { visit(); }
    public void visit(Mulop_mul Mulop_mul) { visit(); }
    public void visit(Term_factor Term_factor) { visit(); }
    public void visit(Term_mulop Term_mulop) { visit(); }
    public void visit(Addop_minus Addop_minus) { visit(); }
    public void visit(Addop_plus Addop_plus) { visit(); }
    public void visit(AddExpr_neg AddExpr_neg) { visit(); }
    public void visit(AddExpr_term AddExpr_term) { visit(); }
    public void visit(AddExpr_addop AddExpr_addop) { visit(); }
    public void visit(Expr_ternary Expr_ternary) { visit(); }
    public void visit(Expr_add Expr_add) { visit(); }
    public void visit(Relop_leq Relop_leq) { visit(); }
    public void visit(Relop_lt Relop_lt) { visit(); }
    public void visit(Relop_geq Relop_geq) { visit(); }
    public void visit(Relop_gt Relop_gt) { visit(); }
    public void visit(Relop_neq Relop_neq) { visit(); }
    public void visit(Relop_eq Relop_eq) { visit(); }
    public void visit(CondFact_relop CondFact_relop) { visit(); }
    public void visit(CondFact_expr CondFact_expr) { visit(); }
    public void visit(CondTerm_fact CondTerm_fact) { visit(); }
    public void visit(CondTerm_and CondTerm_and) { visit(); }
    public void visit(Condition_term Condition_term) { visit(); }
    public void visit(Condition_or Condition_or) { visit(); }
    public void visit(ActPars_one ActPars_one) { visit(); }
    public void visit(ActPars_more ActPars_more) { visit(); }
    public void visit(ActParsOpt_epsilon ActParsOpt_epsilon) { visit(); }
    public void visit(ActParsOpt_pars ActParsOpt_pars) { visit(); }
    public void visit(DesignatorStatement_dec DesignatorStatement_dec) { visit(); }
    public void visit(DesignatorStatement_inc DesignatorStatement_inc) { visit(); }
    public void visit(DesignatorStatement_call DesignatorStatement_call) { visit(); }
    public void visit(DesignatorStatement_assign DesignatorStatement_assign) { visit(); }
    public void visit(ForStepOpt_epsilon ForStepOpt_epsilon) { visit(); }
    public void visit(ForStepOpt_stmt ForStepOpt_stmt) { visit(); }
    public void visit(ForCondOpt_epsilon ForCondOpt_epsilon) { visit(); }
    public void visit(ForCondOpt_cond ForCondOpt_cond) { visit(); }
    public void visit(ForInitOpt_epsilon ForInitOpt_epsilon) { visit(); }
    public void visit(ForInitOpt_stmt ForInitOpt_stmt) { visit(); }
    public void visit(PrintWidthOpt_epsilon PrintWidthOpt_epsilon) { visit(); }
    public void visit(PrintWidthOpt_width PrintWidthOpt_width) { visit(); }
    public void visit(ReturnExprOpt_epsilon ReturnExprOpt_epsilon) { visit(); }
    public void visit(ReturnExprOpt_expr ReturnExprOpt_expr) { visit(); }
    public void visit(IfCondition_err IfCondition_err) { visit(); }
    public void visit(IfCondition_cond IfCondition_cond) { visit(); }
    public void visit(Statement_err Statement_err) { visit(); }
    public void visit(Statement_block Statement_block) { visit(); }
    public void visit(Statement_for Statement_for) { visit(); }
    public void visit(Statement_print Statement_print) { visit(); }
    public void visit(Statement_read Statement_read) { visit(); }
    public void visit(Statement_return Statement_return) { visit(); }
    public void visit(Statement_continue Statement_continue) { visit(); }
    public void visit(Statement_break Statement_break) { visit(); }
    public void visit(Statement_ifElse Statement_ifElse) { visit(); }
    public void visit(Statement_if Statement_if) { visit(); }
    public void visit(Statement_map Statement_map) { visit(); }
    public void visit(Statement_findAny Statement_findAny) { visit(); }
    public void visit(Statement_designator Statement_designator) { visit(); }
    public void visit(StatementList_epsilon StatementList_epsilon) { visit(); }
    public void visit(StatementList_more StatementList_more) { visit(); }
    public void visit(FormParDecl_err FormParDecl_err) { visit(); }
    public void visit(FormParDecl_array FormParDecl_array) { visit(); }
    public void visit(FormParDecl_var FormParDecl_var) { visit(); }
    public void visit(FormPars_one FormPars_one) { visit(); }
    public void visit(FormPars_more FormPars_more) { visit(); }
    public void visit(FormParsOpt_epsilon FormParsOpt_epsilon) { visit(); }
    public void visit(FormParsOpt_pars FormParsOpt_pars) { visit(); }
    public void visit(VarDeclListOpt_epsilon VarDeclListOpt_epsilon) { visit(); }
    public void visit(VarDeclListOpt_more VarDeclListOpt_more) { visit(); }
    public void visit(RetType_void RetType_void) { visit(); }
    public void visit(RetType_type RetType_type) { visit(); }
    public void visit(MethodTypeName MethodTypeName) { visit(); }
    public void visit(MethodDecl MethodDecl) { visit(); }
    public void visit(MethodDeclList_epsilon MethodDeclList_epsilon) { visit(); }
    public void visit(MethodDeclList_more MethodDeclList_more) { visit(); }
    public void visit(VarItem_err VarItem_err) { visit(); }
    public void visit(VarItem_array VarItem_array) { visit(); }
    public void visit(VarItem_var VarItem_var) { visit(); }
    public void visit(VarItemList_one VarItemList_one) { visit(); }
    public void visit(VarItemList_more VarItemList_more) { visit(); }
    public void visit(VarDecl VarDecl) { visit(); }
    public void visit(Constant_bool Constant_bool) { visit(); }
    public void visit(Constant_character Constant_character) { visit(); }
    public void visit(Constant_number Constant_number) { visit(); }
    public void visit(ConstItem ConstItem) { visit(); }
    public void visit(ConstItemList_one ConstItemList_one) { visit(); }
    public void visit(ConstItemList_more ConstItemList_more) { visit(); }
    public void visit(ConstDecl ConstDecl) { visit(); }
    public void visit(Type Type) { visit(); }
    public void visit(DeclList_epsilon DeclList_epsilon) { visit(); }
    public void visit(DeclList_var DeclList_var) { visit(); }
    public void visit(DeclList_const DeclList_const) { visit(); }
    public void visit(ProgramName ProgramName) { visit(); }
    public void visit(Program Program) { visit(); }


    public void visit() { }
}
