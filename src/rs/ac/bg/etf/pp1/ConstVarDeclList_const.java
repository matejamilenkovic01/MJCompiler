// generated with ast extension for cup
// version 0.8
// 24/7/2026 11:48:26


package src/rs/ac/bg/etf/pp1;

public class ConstVarDeclList_const extends ConstVarDeclList {

    private ConstVarDeclList ConstVarDeclList;
    private ConstDeclList ConstDeclList;

    public ConstVarDeclList_const (ConstVarDeclList ConstVarDeclList, ConstDeclList ConstDeclList) {
        this.ConstVarDeclList=ConstVarDeclList;
        if(ConstVarDeclList!=null) ConstVarDeclList.setParent(this);
        this.ConstDeclList=ConstDeclList;
        if(ConstDeclList!=null) ConstDeclList.setParent(this);
    }

    public ConstVarDeclList getConstVarDeclList() {
        return ConstVarDeclList;
    }

    public void setConstVarDeclList(ConstVarDeclList ConstVarDeclList) {
        this.ConstVarDeclList=ConstVarDeclList;
    }

    public ConstDeclList getConstDeclList() {
        return ConstDeclList;
    }

    public void setConstDeclList(ConstDeclList ConstDeclList) {
        this.ConstDeclList=ConstDeclList;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(ConstVarDeclList!=null) ConstVarDeclList.accept(visitor);
        if(ConstDeclList!=null) ConstDeclList.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(ConstVarDeclList!=null) ConstVarDeclList.traverseTopDown(visitor);
        if(ConstDeclList!=null) ConstDeclList.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(ConstVarDeclList!=null) ConstVarDeclList.traverseBottomUp(visitor);
        if(ConstDeclList!=null) ConstDeclList.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("ConstVarDeclList_const(\n");

        if(ConstVarDeclList!=null)
            buffer.append(ConstVarDeclList.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(ConstDeclList!=null)
            buffer.append(ConstDeclList.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [ConstVarDeclList_const]");
        return buffer.toString();
    }
}
