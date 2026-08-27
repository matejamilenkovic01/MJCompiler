// generated with ast extension for cup
// version 0.8
// 27/7/2026 16:1:0


package rs.ac.bg.etf.pp1.ast;

public class Statement_for extends Statement {

    private ForInitOpt ForInitOpt;
    private ForCondOpt ForCondOpt;
    private ForStepOpt ForStepOpt;
    private Statement Statement;

    public Statement_for (ForInitOpt ForInitOpt, ForCondOpt ForCondOpt, ForStepOpt ForStepOpt, Statement Statement) {
        this.ForInitOpt=ForInitOpt;
        if(ForInitOpt!=null) ForInitOpt.setParent(this);
        this.ForCondOpt=ForCondOpt;
        if(ForCondOpt!=null) ForCondOpt.setParent(this);
        this.ForStepOpt=ForStepOpt;
        if(ForStepOpt!=null) ForStepOpt.setParent(this);
        this.Statement=Statement;
        if(Statement!=null) Statement.setParent(this);
    }

    public ForInitOpt getForInitOpt() {
        return ForInitOpt;
    }

    public void setForInitOpt(ForInitOpt ForInitOpt) {
        this.ForInitOpt=ForInitOpt;
    }

    public ForCondOpt getForCondOpt() {
        return ForCondOpt;
    }

    public void setForCondOpt(ForCondOpt ForCondOpt) {
        this.ForCondOpt=ForCondOpt;
    }

    public ForStepOpt getForStepOpt() {
        return ForStepOpt;
    }

    public void setForStepOpt(ForStepOpt ForStepOpt) {
        this.ForStepOpt=ForStepOpt;
    }

    public Statement getStatement() {
        return Statement;
    }

    public void setStatement(Statement Statement) {
        this.Statement=Statement;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(ForInitOpt!=null) ForInitOpt.accept(visitor);
        if(ForCondOpt!=null) ForCondOpt.accept(visitor);
        if(ForStepOpt!=null) ForStepOpt.accept(visitor);
        if(Statement!=null) Statement.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(ForInitOpt!=null) ForInitOpt.traverseTopDown(visitor);
        if(ForCondOpt!=null) ForCondOpt.traverseTopDown(visitor);
        if(ForStepOpt!=null) ForStepOpt.traverseTopDown(visitor);
        if(Statement!=null) Statement.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(ForInitOpt!=null) ForInitOpt.traverseBottomUp(visitor);
        if(ForCondOpt!=null) ForCondOpt.traverseBottomUp(visitor);
        if(ForStepOpt!=null) ForStepOpt.traverseBottomUp(visitor);
        if(Statement!=null) Statement.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("Statement_for(\n");

        if(ForInitOpt!=null)
            buffer.append(ForInitOpt.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(ForCondOpt!=null)
            buffer.append(ForCondOpt.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(ForStepOpt!=null)
            buffer.append(ForStepOpt.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(Statement!=null)
            buffer.append(Statement.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [Statement_for]");
        return buffer.toString();
    }
}
