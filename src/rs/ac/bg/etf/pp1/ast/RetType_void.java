// generated with ast extension for cup
// version 0.8
// 25/7/2026 0:36:4


package rs.ac.bg.etf.pp1.ast;

public class RetType_void extends RetType {

    public RetType_void () {
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
        buffer.append("RetType_void(\n");

        buffer.append(tab);
        buffer.append(") [RetType_void]");
        return buffer.toString();
    }
}
