// generated with ast extension for cup
// version 0.8
// 25/7/2026 0:36:4


package rs.ac.bg.etf.pp1.ast;

public class PrintWidthOpt_width extends PrintWidthOpt {

    private Integer width;

    public PrintWidthOpt_width (Integer width) {
        this.width=width;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width=width;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("PrintWidthOpt_width(\n");

        buffer.append(" "+tab+width);
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [PrintWidthOpt_width]");
        return buffer.toString();
    }
}
