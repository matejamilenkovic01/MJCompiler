// generated with ast extension for cup
// version 0.8
// 27/7/2026 19:17:26


package rs.ac.bg.etf.pp1.ast;

public class Factor_neg extends Factor {

    private FactorSub FactorSub;

    public Factor_neg (FactorSub FactorSub) {
        this.FactorSub=FactorSub;
        if(FactorSub!=null) FactorSub.setParent(this);
    }

    public FactorSub getFactorSub() {
        return FactorSub;
    }

    public void setFactorSub(FactorSub FactorSub) {
        this.FactorSub=FactorSub;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(FactorSub!=null) FactorSub.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(FactorSub!=null) FactorSub.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(FactorSub!=null) FactorSub.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("Factor_neg(\n");

        if(FactorSub!=null)
            buffer.append(FactorSub.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [Factor_neg]");
        return buffer.toString();
    }
}
