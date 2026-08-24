// generated with ast extension for cup
// version 0.8
// 24/7/2026 12:31:7


package src/rs/ac/bg/etf/pp1;

public class VarItemMore_comma extends VarItemMore {

    private VarItem VarItem;
    private VarItemMore VarItemMore;

    public VarItemMore_comma (VarItem VarItem, VarItemMore VarItemMore) {
        this.VarItem=VarItem;
        if(VarItem!=null) VarItem.setParent(this);
        this.VarItemMore=VarItemMore;
        if(VarItemMore!=null) VarItemMore.setParent(this);
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

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(VarItem!=null) VarItem.accept(visitor);
        if(VarItemMore!=null) VarItemMore.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(VarItem!=null) VarItem.traverseTopDown(visitor);
        if(VarItemMore!=null) VarItemMore.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(VarItem!=null) VarItem.traverseBottomUp(visitor);
        if(VarItemMore!=null) VarItemMore.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("VarItemMore_comma(\n");

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
        buffer.append(") [VarItemMore_comma]");
        return buffer.toString();
    }
}
