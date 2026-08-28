// generated with ast extension for cup
// version 0.8
// 28/7/2026 17:58:9


package rs.ac.bg.etf.pp1.ast;

public class Expr_ternary extends Expr {

    private Condition Condition;
    private TernQ TernQ;
    private Expr Expr;
    private TernC TernC;
    private Expr Expr1;

    public Expr_ternary (Condition Condition, TernQ TernQ, Expr Expr, TernC TernC, Expr Expr1) {
        this.Condition=Condition;
        if(Condition!=null) Condition.setParent(this);
        this.TernQ=TernQ;
        if(TernQ!=null) TernQ.setParent(this);
        this.Expr=Expr;
        if(Expr!=null) Expr.setParent(this);
        this.TernC=TernC;
        if(TernC!=null) TernC.setParent(this);
        this.Expr1=Expr1;
        if(Expr1!=null) Expr1.setParent(this);
    }

    public Condition getCondition() {
        return Condition;
    }

    public void setCondition(Condition Condition) {
        this.Condition=Condition;
    }

    public TernQ getTernQ() {
        return TernQ;
    }

    public void setTernQ(TernQ TernQ) {
        this.TernQ=TernQ;
    }

    public Expr getExpr() {
        return Expr;
    }

    public void setExpr(Expr Expr) {
        this.Expr=Expr;
    }

    public TernC getTernC() {
        return TernC;
    }

    public void setTernC(TernC TernC) {
        this.TernC=TernC;
    }

    public Expr getExpr1() {
        return Expr1;
    }

    public void setExpr1(Expr Expr1) {
        this.Expr1=Expr1;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(Condition!=null) Condition.accept(visitor);
        if(TernQ!=null) TernQ.accept(visitor);
        if(Expr!=null) Expr.accept(visitor);
        if(TernC!=null) TernC.accept(visitor);
        if(Expr1!=null) Expr1.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(Condition!=null) Condition.traverseTopDown(visitor);
        if(TernQ!=null) TernQ.traverseTopDown(visitor);
        if(Expr!=null) Expr.traverseTopDown(visitor);
        if(TernC!=null) TernC.traverseTopDown(visitor);
        if(Expr1!=null) Expr1.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(Condition!=null) Condition.traverseBottomUp(visitor);
        if(TernQ!=null) TernQ.traverseBottomUp(visitor);
        if(Expr!=null) Expr.traverseBottomUp(visitor);
        if(TernC!=null) TernC.traverseBottomUp(visitor);
        if(Expr1!=null) Expr1.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("Expr_ternary(\n");

        if(Condition!=null)
            buffer.append(Condition.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(TernQ!=null)
            buffer.append(TernQ.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(Expr!=null)
            buffer.append(Expr.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(TernC!=null)
            buffer.append(TernC.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(Expr1!=null)
            buffer.append(Expr1.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [Expr_ternary]");
        return buffer.toString();
    }
}
