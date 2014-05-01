package pack;

import java.awt.Color;
import java.awt.Point;

public class Text extends Tool{
    protected String style;

    public Text(){
        super(12, 72, 18, Color.BLACK);
    }

    public Text(String style, Point p){
        this();
        this.style = style;
        this.point = p;
    }

    public void setStyle(String s){
        this.style = s;
    }

    public String getStyle(){
        return this.style;
    }
}