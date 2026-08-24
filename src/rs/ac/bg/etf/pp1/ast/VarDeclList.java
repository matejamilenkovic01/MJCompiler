// generated with ast extension for cup
// version 0.8
// 24/7/2026 23:4:17


package rs.ac.bg.etf.pp1.ast;

public class VarDeclList implements SyntaxNode {

    private SyntaxNode parent;
    private int line;
    private Type Type;
    private VarItem VarItem;
    private VarItemMore VarItemMore;

    public VarDeclList (Type Type, VarItem VarItem, VarItemMore VarItemMore) {
        this.Type=Type;
        if(Type!=null) Type.setParent(this);
        this.VarItem=VarItem;
        if(VarItem!=null) VarItem.setParent(this);
        this.VarItemMore=VarItemMore;
        if(VarItemMore!=null) VarItemMore.setParent(this);
    }

    public Type getType() {
        return Type;
    }

    public void setType(Type Type) {
        this.Type=Type;
    }

    public VarItem getVarItem() {
        return VarItem;
    }

    public void setVarItem(VarItem VarItem) {
        this.VarItem=VarItem;
    }

    public VarItemMore getVarItemMore() {
        return VarItemMore;
    }

    public void setVarItemMore(VarItemMore VarItemMore) {
        this.VarItemMore=VarItemMore;
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
        if(VarItem!=null) VarItem.accept(visitor);
        if(VarItemMore!=null) VarItemMore.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(Type!=null) Type.traverseTopDown(visitor);
        if(VarItem!=null) VarItem.traverseTopDown(visitor);
        if(VarItemMore!=null) VarItemMore.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(Type!=null) Type.traverseBottomUp(visitor);
        if(VarItem!=null) VarItem.traverseBottomUp(visitor);
        if(VarItemMore!=null) VarItemMore.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("VarDeclList(\n");

        if(Type!=null)
            buffer.append(Type.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(VarItem!=null)
            buffer.append(VarItem.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(VarItemMore!=null)
            buffer.append(VarItemMore.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [VarDeclList]");
        return buffer.toString();
    }
}
