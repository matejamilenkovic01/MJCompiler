// generated with ast extension for cup
// version 0.8
// 25/7/2026 11:4:15


package rs.ac.bg.etf.pp1.ast;

public class FormParDecl_err extends FormParDecl {

    public FormParDecl_err () {
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
        buffer.append("FormParDecl_err(\n");

        buffer.append(tab);
        buffer.append(") [FormParDecl_err]");
        return buffer.toString();
    }
}
