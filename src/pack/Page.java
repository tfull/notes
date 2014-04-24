package pack;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class Page{
    private static BufferedImage PLAIN_PAGE = null;

    private String path;
    private BufferedImage image;
    private boolean changed;

    public Page(String dname, String fname){
        this.path = dname + File.separator + fname;

        try{
            this.image = ImageIO.read(new File(this.path));
        }catch(IOException e){
            this.image = new BufferedImage(PLAIN_PAGE.getWidth(), PLAIN_PAGE.getHeight(), PLAIN_PAGE.getType());
            this.image.setData(PLAIN_PAGE.getData());
        }

        this.changed = false;
    }

    public static void setPlainPage(BufferedImage bi){
        Page.PLAIN_PAGE = bi;
    }

    public Page(String dname){
        this.path = dname + File.separator + System.currentTimeMillis() + ".png";
        this.image = new BufferedImage(PLAIN_PAGE.getWidth(), PLAIN_PAGE.getHeight(), PLAIN_PAGE.getType());
        this.image.setData(PLAIN_PAGE.getData());
        this.changed = true;
    }

    public String getPath(){
        return this.path;
    }

    public String getName(){
        int index = this.path.lastIndexOf(File.separatorChar);
        return this.path.substring(index + 1);
    }

    public BufferedImage getImage(){
        return this.image;
    }

    public void write() throws IOException{
        if(this.changed){
            ImageIO.write(this.image, "png", new File(this.path));
            this.changed = false;
        }
    }

    public void change(){
        this.changed = true;
    }
}
