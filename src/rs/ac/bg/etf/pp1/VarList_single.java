// generated with ast extension for cup
// version 0.8
// 24/7/2026 12:9:45


package src/rs/ac/bg/etf/pp1;

public class VarList_single extends VarList {

    private VarItem VarItem;

    public VarList_single (VarItem VarItem) {
        this.VarItem=VarItem;
        if(VarItem!=null) VarItem.setParent(this);
    }

    public VarItem getVarItem() {
        return VarItem;
    }

    public void setVarItem(VarItem VarItem) {
        this.VarItem=VarItem;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(VarItem!=null) VarItem.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(VarItem!=null) VarItem.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(VarItem!=null) VarItem.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("VarList_single(\n");

        if(VarItem!=null)
            buffer.append(VarItem.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [VarList_single]");
        return buffer.toString();
    }
}
