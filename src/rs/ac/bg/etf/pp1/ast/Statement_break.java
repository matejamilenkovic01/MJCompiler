// generated with ast extension for cup
// version 0.8
// 27/7/2026 16:1:0


package rs.ac.bg.etf.pp1.ast;

public class Statement_break extends Statement {

    public Statement_break () {
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
        buffer.append("Statement_break(\n");

        buffer.append(tab);
        buffer.append(") [Statement_break]");
        return buffer.toString();
    }
}
