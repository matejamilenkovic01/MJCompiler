// generated with ast extension for cup
// version 0.8
// 25/7/2026 11:4:15


package rs.ac.bg.etf.pp1.ast;

public class Statement_err extends Statement {

    public Statement_err () {
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
        buffer.append("Statement_err(\n");

        buffer.append(tab);
        buffer.append(") [Statement_err]");
        return buffer.toString();
    }
}
