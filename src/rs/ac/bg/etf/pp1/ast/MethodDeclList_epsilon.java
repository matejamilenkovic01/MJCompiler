// generated with ast extension for cup
// version 0.8
// 24/7/2026 23:4:17


package rs.ac.bg.etf.pp1.ast;

public class MethodDeclList_epsilon extends MethodDeclList {

    public MethodDeclList_epsilon () {
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
        buffer.append("MethodDeclList_epsilon(\n");

        buffer.append(tab);
        buffer.append(") [MethodDeclList_epsilon]");
        return buffer.toString();
    }
}
