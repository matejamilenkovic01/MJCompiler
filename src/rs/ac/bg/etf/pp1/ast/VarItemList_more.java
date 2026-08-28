// generated with ast extension for cup
// version 0.8
// 28/7/2026 17:58:9


package rs.ac.bg.etf.pp1.ast;

public class VarItemList_more extends VarItemList {

    private VarItemList VarItemList;
    private VarItem VarItem;

    public VarItemList_more (VarItemList VarItemList, VarItem VarItem) {
        this.VarItemList=VarItemList;
        if(VarItemList!=null) VarItemList.setParent(this);
        this.VarItem=VarItem;
        if(VarItem!=null) VarItem.setParent(this);
    }

    public VarItemList getVarItemList() {
        return VarItemList;
    }

    public void setVarItemList(VarItemList VarItemList) {
        this.VarItemList=VarItemList;
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
        if(VarItemList!=null) VarItemList.accept(visitor);
        if(VarItem!=null) VarItem.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(VarItemList!=null) VarItemList.traverseTopDown(visitor);
        if(VarItem!=null) VarItem.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(VarItemList!=null) VarItemList.traverseBottomUp(visitor);
        if(VarItem!=null) VarItem.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("VarItemList_more(\n");

        if(VarItemList!=null)
            buffer.append(VarItemList.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(VarItem!=null)
            buffer.append(VarItem.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [VarItemList_more]");
        return buffer.toString();
    }
}
