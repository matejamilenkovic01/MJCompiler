// generated with ast extension for cup
// version 0.8
// 27/7/2026 19:17:26


package rs.ac.bg.etf.pp1.ast;

public class CondFact_relop extends CondFact {

    private AddExpr AddExpr;
    private Relop Relop;
    private AddExpr AddExpr1;

    public CondFact_relop (AddExpr AddExpr, Relop Relop, AddExpr AddExpr1) {
        this.AddExpr=AddExpr;
        if(AddExpr!=null) AddExpr.setParent(this);
        this.Relop=Relop;
        if(Relop!=null) Relop.setParent(this);
        this.AddExpr1=AddExpr1;
        if(AddExpr1!=null) AddExpr1.setParent(this);
    }

    public AddExpr getAddExpr() {
        return AddExpr;
    }

    public void setAddExpr(AddExpr AddExpr) {
        this.AddExpr=AddExpr;
    }

    public Relop getRelop() {
        return Relop;
    }

    public void setRelop(Relop Relop) {
        this.Relop=Relop;
    }

    public AddExpr getAddExpr1() {
        return AddExpr1;
    }

    public void setAddExpr1(AddExpr AddExpr1) {
        this.AddExpr1=AddExpr1;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(AddExpr!=null) AddExpr.accept(visitor);
        if(Relop!=null) Relop.accept(visitor);
        if(AddExpr1!=null) AddExpr1.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(AddExpr!=null) AddExpr.traverseTopDown(visitor);
        if(Relop!=null) Relop.traverseTopDown(visitor);
        if(AddExpr1!=null) AddExpr1.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(AddExpr!=null) AddExpr.traverseBottomUp(visitor);
        if(Relop!=null) Relop.traverseBottomUp(visitor);
        if(AddExpr1!=null) AddExpr1.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("CondFact_relop(\n");

        if(AddExpr!=null)
            buffer.append(AddExpr.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(Relop!=null)
            buffer.append(Relop.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(AddExpr1!=null)
            buffer.append(AddExpr1.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [CondFact_relop]");
        return buffer.toString();
    }
}
