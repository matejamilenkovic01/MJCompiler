// generated with ast extension for cup
// version 0.8
// 25/7/2026 11:4:15


package rs.ac.bg.etf.pp1.ast;

public class Statement_return extends Statement {

    private ReturnExprOpt ReturnExprOpt;

    public Statement_return (ReturnExprOpt ReturnExprOpt) {
        this.ReturnExprOpt=ReturnExprOpt;
        if(ReturnExprOpt!=null) ReturnExprOpt.setParent(this);
    }

    public ReturnExprOpt getReturnExprOpt() {
        return ReturnExprOpt;
    }

    public void setReturnExprOpt(ReturnExprOpt ReturnExprOpt) {
        this.ReturnExprOpt=ReturnExprOpt;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(ReturnExprOpt!=null) ReturnExprOpt.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(ReturnExprOpt!=null) ReturnExprOpt.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(ReturnExprOpt!=null) ReturnExprOpt.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("Statement_return(\n");

        if(ReturnExprOpt!=null)
            buffer.append(ReturnExprOpt.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [Statement_return]");
        return buffer.toString();
    }
}
