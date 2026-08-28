// generated with ast extension for cup
// version 0.8
// 27/7/2026 19:17:26


package rs.ac.bg.etf.pp1.ast;

public class VarDeclListOpt_more extends VarDeclListOpt {

    private VarDeclListOpt VarDeclListOpt;
    private VarDecl VarDecl;

    public VarDeclListOpt_more (VarDeclListOpt VarDeclListOpt, VarDecl VarDecl) {
        this.VarDeclListOpt=VarDeclListOpt;
        if(VarDeclListOpt!=null) VarDeclListOpt.setParent(this);
        this.VarDecl=VarDecl;
        if(VarDecl!=null) VarDecl.setParent(this);
    }

    public VarDeclListOpt getVarDeclListOpt() {
        return VarDeclListOpt;
    }

    public void setVarDeclListOpt(VarDeclListOpt VarDeclListOpt) {
        this.VarDeclListOpt=VarDeclListOpt;
    }

    public VarDecl getVarDecl() {
        return VarDecl;
    }

    public void setVarDecl(VarDecl VarDecl) {
        this.VarDecl=VarDecl;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(VarDeclListOpt!=null) VarDeclListOpt.accept(visitor);
        if(VarDecl!=null) VarDecl.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(VarDeclListOpt!=null) VarDeclListOpt.traverseTopDown(visitor);
        if(VarDecl!=null) VarDecl.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(VarDeclListOpt!=null) VarDeclListOpt.traverseBottomUp(visitor);
        if(VarDecl!=null) VarDecl.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("VarDeclListOpt_more(\n");

        if(VarDeclListOpt!=null)
            buffer.append(VarDeclListOpt.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(VarDecl!=null)
            buffer.append(VarDecl.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [VarDeclListOpt_more]");
        return buffer.toString();
    }
}
