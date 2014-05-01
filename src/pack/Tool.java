package pack;

import java.awt.Color;
import java.awt.Point;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class Tool{
    protected int default_size;
    protected int size;
    protected int minimum_size;
    protected int maximum_size;
    protected Color color;
    protected Point point;

    public Tool(int minimum_size, int maximum_size, int default_size){
        this.minimum_size = minimum_size;
        this.maximum_size = maximum_size;
        this.default_size = default_size;
        this.size = default_size;
    }

    public Tool(int minimum_size, int maximum_size, int default_size, Color color){
        this.minimum_size = minimum_size;
        this.maximum_size = maximum_size;
        this.default_size = default_size;
        this.size = default_size;
        this.color = color;
    }

    public void setColor(Color color){
        this.color = color;
    }

    public void setColor(String s){
        Pattern pattern = Pattern.compile("^#?([0-9a-fA-F]{6})$");
        Matcher matcher = pattern.matcher(s);
        if(matcher.find()){
            String string = matcher.group(1).toLowerCase();
            int r = Integer.parseInt(string.substring(0, 2), 16);
            int g = Integer.parseInt(string.substring(2, 4), 16);
            int b = Integer.parseInt(string.substring(4), 16);
            this.color = new Color(r, g, b);
        }
    }

    public Color getColor(){
        return this.color;
    }

    public String getColorString(){
        return String.format("#%02x%02x%02x", this.color.getRed(), this.color.getGreen(), this.color.getBlue());
    }

    public void setPoint(Point p){
        this.point = p;
    }

    public Point getPoint(){
        return this.point;
    }

    public void increaseSize(){
        this.size = Math.min(this.size + 1, this.maximum_size);
    }

    public void decreaseSize(){
        this.size = Math.max(this.size - 1, this.minimum_size);
    }

    public void defaultSize(){
        this.size = this.default_size;
    }

    public void setSize(String s){
        Pattern pattern = Pattern.compile("^(\\d+)(?:[pP][tT])?$");
        Matcher matcher = pattern.matcher(s);
        if(matcher.find()){
            int n = Integer.parseInt(matcher.group(1));
            if(n > this.maximum_size){
                this.size = this.maximum_size;
            }else if(n < this.minimum_size){
                this.size = this.minimum_size;
            }else{
                this.size = n;
            }
        }
    }

    public int getSize(){
        return this.size;
    }
}
