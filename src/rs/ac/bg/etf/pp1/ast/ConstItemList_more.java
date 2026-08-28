// generated with ast extension for cup
// version 0.8
// 27/7/2026 19:17:26


package rs.ac.bg.etf.pp1.ast;

public class ConstItemList_more extends ConstItemList {

    private ConstItemList ConstItemList;
    private ConstItem ConstItem;

    public ConstItemList_more (ConstItemList ConstItemList, ConstItem ConstItem) {
        this.ConstItemList=ConstItemList;
        if(ConstItemList!=null) ConstItemList.setParent(this);
        this.ConstItem=ConstItem;
        if(ConstItem!=null) ConstItem.setParent(this);
    }

    public ConstItemList getConstItemList() {
        return ConstItemList;
    }

    public void setConstItemList(ConstItemList ConstItemList) {
        this.ConstItemList=ConstItemList;
    }

    public ConstItem getConstItem() {
        return ConstItem;
    }

    public void setConstItem(ConstItem ConstItem) {
        this.ConstItem=ConstItem;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(ConstItemList!=null) ConstItemList.accept(visitor);
        if(ConstItem!=null) ConstItem.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(ConstItemList!=null) ConstItemList.traverseTopDown(visitor);
        if(ConstItem!=null) ConstItem.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(ConstItemList!=null) ConstItemList.traverseBottomUp(visitor);
        if(ConstItem!=null) ConstItem.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("ConstItemList_more(\n");

        if(ConstItemList!=null)
            buffer.append(ConstItemList.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(ConstItem!=null)
            buffer.append(ConstItem.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [ConstItemList_more]");
        return buffer.toString();
    }
}
