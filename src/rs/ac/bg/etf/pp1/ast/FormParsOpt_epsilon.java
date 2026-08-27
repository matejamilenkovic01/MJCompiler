// generated with ast extension for cup
// version 0.8
// 27/7/2026 16:1:0


package rs.ac.bg.etf.pp1.ast;

public class FormParsOpt_epsilon extends FormParsOpt {

    public FormParsOpt_epsilon () {
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
        buffer.append("FormParsOpt_epsilon(\n");

        buffer.append(tab);
        buffer.append(") [FormParsOpt_epsilon]");
        return buffer.toString();
    }
}
