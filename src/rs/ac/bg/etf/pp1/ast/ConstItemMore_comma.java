// generated with ast extension for cup
// version 0.8
// 24/7/2026 23:4:17


package rs.ac.bg.etf.pp1.ast;

public class ConstItemMore_comma extends ConstItemMore {

    private ConstItem ConstItem;
    private ConstItemMore ConstItemMore;

    public ConstItemMore_comma (ConstItem ConstItem, ConstItemMore ConstItemMore) {
        this.ConstItem=ConstItem;
        if(ConstItem!=null) ConstItem.setParent(this);
        this.ConstItemMore=ConstItemMore;
        if(ConstItemMore!=null) ConstItemMore.setParent(this);
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

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(ConstItem!=null) ConstItem.accept(visitor);
        if(ConstItemMore!=null) ConstItemMore.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(ConstItem!=null) ConstItem.traverseTopDown(visitor);
        if(ConstItemMore!=null) ConstItemMore.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(ConstItem!=null) ConstItem.traverseBottomUp(visitor);
        if(ConstItemMore!=null) ConstItemMore.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("ConstItemMore_comma(\n");

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
        buffer.append(") [ConstItemMore_comma]");
        return buffer.toString();
    }
}
