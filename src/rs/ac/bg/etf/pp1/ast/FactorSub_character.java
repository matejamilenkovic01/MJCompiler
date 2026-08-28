// generated with ast extension for cup
// version 0.8
// 28/7/2026 16:37:36


package rs.ac.bg.etf.pp1.ast;

public class FactorSub_character extends FactorSub {

    private Character val;

    public FactorSub_character (Character val) {
        this.val=val;
    }

    public Character getVal() {
        return val;
    }

    public void setVal(Character val) {
        this.val=val;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("FactorSub_character(\n");

        buffer.append(" "+tab+val);
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [FactorSub_character]");
        return buffer.toString();
    }
}
