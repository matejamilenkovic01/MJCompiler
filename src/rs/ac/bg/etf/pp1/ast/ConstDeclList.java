// generated with ast extension for cup
// version 0.8
// 24/7/2026 23:4:17


package rs.ac.bg.etf.pp1.ast;

public class ConstDeclList implements SyntaxNode {

    private SyntaxNode parent;
    private int line;
    private Type Type;
    private ConstItem ConstItem;
    private ConstItemMore ConstItemMore;

    public ConstDeclList (Type Type, ConstItem ConstItem, ConstItemMore ConstItemMore) {
        this.Type=Type;
        if(Type!=null) Type.setParent(this);
        this.ConstItem=ConstItem;
        if(ConstItem!=null) ConstItem.setParent(this);
        this.ConstItemMore=ConstItemMore;
        if(ConstItemMore!=null) ConstItemMore.setParent(this);
    }

    public Type getType() {
        return Type;
    }

    public void setType(Type Type) {
        this.Type=Type;
    }

    public ConstItem getConstItem() {
        return ConstItem;
    }

    public void setConstItem(ConstItem ConstItem) {
        this.ConstItem=ConstItem;
    }

    public ConstItemMore getConstItemMore() {
        return ConstItemMore;
    }

    public void setConstItemMore(ConstItemMore ConstItemMore) {
        this.ConstItemMore=ConstItemMore;
    }

    public SyntaxNode getParent() {
        return parent;
    }

    public void setParent(SyntaxNode parent) {
        this.parent=parent;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line=line;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(Type!=null) Type.accept(visitor);
        if(ConstItem!=null) ConstItem.accept(visitor);
        if(ConstItemMore!=null) ConstItemMore.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(Type!=null) Type.traverseTopDown(visitor);
        if(ConstItem!=null) ConstItem.traverseTopDown(visitor);
        if(ConstItemMore!=null) ConstItemMore.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(Type!=null) Type.traverseBottomUp(visitor);
        if(ConstItem!=null) ConstItem.traverseBottomUp(visitor);
        if(ConstItemMore!=null) ConstItemMore.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("ConstDeclList(\n");

        if(Type!=null)
            buffer.append(Type.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(ConstItem!=null)
            buffer.append(ConstItem.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(ConstItemMore!=null)
            buffer.append(ConstItemMore.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [ConstDeclList]");
        return buffer.toString();
    }
}
