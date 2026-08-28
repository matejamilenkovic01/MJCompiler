// generated with ast extension for cup
// version 0.8
// 28/7/2026 16:37:36


package rs.ac.bg.etf.pp1.ast;

public class IfCondition_err extends IfCondition {

    public IfCondition_err () {
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
        buffer.append("IfCondition_err(\n");

        buffer.append(tab);
        buffer.append(") [IfCondition_err]");
        return buffer.toString();
    }
}
