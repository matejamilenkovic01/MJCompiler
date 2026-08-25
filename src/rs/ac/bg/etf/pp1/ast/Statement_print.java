// generated with ast extension for cup
// version 0.8
// 25/7/2026 11:4:15


package rs.ac.bg.etf.pp1.ast;

public class Statement_print extends Statement {

    private Expr Expr;
    private PrintWidthOpt PrintWidthOpt;

    public Statement_print (Expr Expr, PrintWidthOpt PrintWidthOpt) {
        this.Expr=Expr;
        if(Expr!=null) Expr.setParent(this);
        this.PrintWidthOpt=PrintWidthOpt;
        if(PrintWidthOpt!=null) PrintWidthOpt.setParent(this);
    }

    public Expr getExpr() {
        return Expr;
    }

    public void setExpr(Expr Expr) {
        this.Expr=Expr;
    }

    public PrintWidthOpt getPrintWidthOpt() {
        return PrintWidthOpt;
    }

    public void setPrintWidthOpt(PrintWidthOpt PrintWidthOpt) {
        this.PrintWidthOpt=PrintWidthOpt;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(Expr!=null) Expr.accept(visitor);
        if(PrintWidthOpt!=null) PrintWidthOpt.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(Expr!=null) Expr.traverseTopDown(visitor);
        if(PrintWidthOpt!=null) PrintWidthOpt.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(Expr!=null) Expr.traverseBottomUp(visitor);
        if(PrintWidthOpt!=null) PrintWidthOpt.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("Statement_print(\n");

        if(Expr!=null)
            buffer.append(Expr.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(PrintWidthOpt!=null)
            buffer.append(PrintWidthOpt.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [Statement_print]");
        return buffer.toString();
    }
}
