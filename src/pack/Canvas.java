package pack;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

class Canvas extends JPanel{
    private ArrayList<Page> pages;
    private int pages_index;
    private Color background;
    private Point point;
    private String directory_name;
    private ArrayList<String> deleteds;

    public Canvas(File dir, Dimension dim, Color bg) throws IOException{
        if(! dir.isDirectory()){
            throw new IOException();
        }

        this.pages = new ArrayList<Page>();
        this.background = bg;
        this.setSize(dim);

        String dirname = dir.getAbsolutePath();

        this.directory_name = dirname;

        BufferedImage bi = new BufferedImage(dim.width, dim.height, BufferedImage.TYPE_3BYTE_BGR); 
        Graphics2D g2d = (Graphics2D)bi.getGraphics();
        g2d.setColor(bg);
        g2d.fillRect(0, 0, dim.width, dim.height);
        g2d.dispose();

        Page.setPlainPage(bi);

        File index_f = new File(dirname + File.separator + "index.txt");

        if(index_f.exists()){
            FileReader fr = new FileReader(index_f);
            BufferedReader br = new BufferedReader(fr);

            String line;

            while((line = br.readLine()) != null && line.length() > 0){
                this.pages.add(new Page(dirname, line));
            }

            br.close();
            fr.close();
        }

        if(this.pages.isEmpty()){
            this.pages.add(new Page(dirname));
        }

        this.pages_index = 0;

        this.deleteds = new ArrayList<String>();
    }

    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D)g;
        g.drawImage(this.pages.get(this.pages_index).getImage(), 0, 0, this);
    }

    public void paintStart(Point p, Tool tool){
        Page page = this.pages.get(this.pages_index);
        Graphics2D g2d = (Graphics2D)page.getImage().getGraphics();

        if(tool.getToolName() == ToolName.PEN){
            g2d.setColor(tool.getColor());
        }else{
            g2d.setColor(this.background);
        }
        g2d.setStroke(new BasicStroke((float)tool.getSize()));
        g2d.drawLine(p.x, p.y, p.x, p.y);
        this.repaint();
        g2d.dispose();

        page.change();

        this.point = p;
    }

    public void paintLine(Point p, Tool tool){
        Graphics2D g2d = (Graphics2D)this.pages.get(this.pages_index).getImage().getGraphics();

        if(tool.getToolName() == ToolName.PEN){
            g2d.setColor(tool.getColor());
        }else{
            g2d.setColor(this.background);
        }
        g2d.setStroke(new BasicStroke((float)tool.getSize()));
        g2d.drawLine(this.point.x, this.point.y, p.x, p.y);
        this.repaint();
        g2d.dispose();

        this.point = p;
    }

    public void putText(Point p, Tool tool, String string){
        Graphics2D g2d = (Graphics2D)this.pages.get(this.pages_index).getImage().getGraphics();

        g2d.setColor(tool.getColor());
        g2d.setFont(new Font("Arial", Font.PLAIN, tool.getSize()));
        g2d.drawString(string, p.x, p.y);
        this.repaint();
        g2d.dispose();
    }

    public void loadImage(File file){
        BufferedImage img = this.pages.get(this.pages_index).getImage();
        Graphics2D g2d = (Graphics2D)img.getGraphics();

        try{
            BufferedImage pic = ImageIO.read(file);

            g2d.setColor(Color.WHITE);
            g2d.fillRect(0, 0, img.getWidth(), img.getHeight());

            if(img.getWidth() >= pic.getWidth() && img.getHeight() >= pic.getHeight()){
                g2d.drawImage(pic, 0, 0, pic.getWidth(), pic.getHeight(), this);
            }else{
                double ratio;
                double w = (double)img.getWidth() / (double)pic.getWidth();
                double h = (double)img.getHeight() / (double)pic.getHeight();

                ratio = Math.min(1.0, w);
                ratio = Math.min(ratio, h);

                int x = (int)((double)pic.getWidth() * ratio);
                int y = (int)((double)pic.getHeight() * ratio);

                g2d.drawImage(pic, 0, 0, x, y, this);
            }

            this.repaint();
        }catch(IOException e){
            System.err.println("[Error: loadImage()]");
        }
    }

    public void previousPage(){
        if(this.pages_index > 0){
            this.pages_index -= 1;
            this.repaint();
        }
    }

    public void nextPage(){
        if(this.pages_index < this.pages.size() - 1){
            this.pages_index += 1;
            this.repaint();
        }
    }

    public int getPageNumber(){
        return this.pages_index + 1;
    }

    public int getPageCount(){
        return this.pages.size();
    }

    public void addNewPage(boolean is_previous){
        int index;

        if(is_previous){
            index = this.pages_index;
        }else{
            index = this.pages_index + 1;
        }

        this.pages.add(index, new Page(this.directory_name));
        this.pages_index = index;
        this.repaint();
    }

    public void saveCurrentPage(){
        try{
            this.pages.get(this.pages_index).write();
        }catch(IOException e){
            System.err.println("[Error: saveCurrentPage()]");
        }
    }

    public void saveIndex(){
        try{
            FileWriter fw = new FileWriter(this.directory_name + File.separator + "index.txt");
            BufferedWriter bw = new BufferedWriter(fw);

            for(Page page : this.pages){
                bw.write(page.getName());
                bw.newLine();
            }

            bw.close();
            fw.close();
        }catch(IOException e){
            System.err.println("[Error: saveIndex()]");
        }
    }

    public void saveChangedPages(){
        try{
            FileWriter fw = new FileWriter(this.directory_name + File.separator + "index.txt");
            BufferedWriter bw = new BufferedWriter(fw);

            for(Page page : this.pages){
                page.write();
                bw.write(page.getName());
                bw.newLine();
            }

            bw.close();
            fw.close();

            for(String path : this.deleteds){
                File file = new File(path);
                if(file.exists()){
                    file.delete();
                }
            }
        }catch(IOException e){
            System.err.println("[Error: saveAll()]");
        }
    }

    public void setPage(String s){
        Pattern pattern = Pattern.compile("^(\\d+)$");
        Matcher matcher = pattern.matcher(s);

        if(matcher.find()){
            int n = Integer.parseInt(matcher.group(1));
            if(n > 0 && n < this.pages.size()){
                if(this.pages_index != n - 1){
                    this.pages_index = n - 1;
                    this.repaint();
                }
            }
        }
    }

    public void deletePage(){
        Page page = this.pages.remove(this.pages_index);
        this.deleteds.add(page.getPath());

        if(this.pages.isEmpty()){
            this.pages.add(new Page(this.directory_name));
        }else if(this.pages_index == this.pages.size()){
            this.pages_index -= 1;
        }

        this.repaint();
    }
}
