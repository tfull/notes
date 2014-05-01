package pack;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Notes{
    private static final int A4WIDTH = 595;
    private static final int A4HEIGHT = 842;
    private static final Color PAPER_COLOR = new Color(245, 245, 210);
    private static final String[] FONT_FAMILY = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
    private static final Color[] COLORS = {
        Color.BLACK, Color.WHITE, Color.RED, Color.BLUE,
        Color.GREEN, Color.YELLOW, Color.ORANGE, Color.PINK
    };
    private static final String[] COLORS_STRING = {
        "Black", "White", "Red", "Blue",
        "Green", "Yellow", "Orange", "Pink"
    };

    // basic components
    private JFrame frame;
    private Canvas canvas;
    private JPanel canvas_panel;
    private JPanel panel;

    // tools
    private Pen pen;
    private Eraser eraser;
    private Text text;

    private Tool[] tools;
    private int tools_index; // 0: pen, 1: eraser, 2: text

    // text
    private JTextField text_field;
    private JComboBox text_combo;
    private JLabel text_label;

    // buttons of tools
    private JButton tool_pen;
    private JButton tool_eraser;
    private JButton tool_text;

    // component for color
    private JPanel palette;
    private JComboBox color_combo;
    private JTextField color_field;
    private Color custom_color;

    // component for size
    private JTextField font_size_field;

    // component for page
    private JTextField page_field;
    private JLabel pages_label;

    private boolean color_field_validation;
    private boolean color_combo_validation;
    private boolean font_size_field_validation;
    private boolean page_field_validation;
    private boolean text_field_validation;
    private boolean text_combo_validation;

    public static void main(String[] args){
        new Notes();
    }

    public Notes(){
        Font font_l = new Font("Times New Roman", Font.PLAIN, 14);
        Font font_m = new Font("Times New Roman", Font.PLAIN, 12);
        Font font_s = new Font("Times New Roman", Font.PLAIN, 10);

        this.text_combo = new JComboBox();
        this.text_combo.setFont(font_m);
        for(String s : FONT_FAMILY){
            this.text_combo.addItem(s);
        }

        // field
        {
            String[] recommended = { "Times New Roman", "Arial", "SansSerif" };
            String u = null;

            for(String s : recommended){
                for(int i = 0; i < FONT_FAMILY.length; i++){
                    String t = FONT_FAMILY[i];

                    if(s.equals(t)){
                        u = s;
                        this.text_combo.setSelectedIndex(i);
                        break;
                    }
                }
                if(u != null){
                    break;
                }
            }

            if(u == null){
                u = FONT_FAMILY[0];
                this.text_combo.setSelectedIndex(0);
            }

            this.pen = new Pen();
            this.eraser = new Eraser();
            this.text = new Text(u, new Point(0, 0));
        }

        Tool[] tls = { this.pen, this.eraser, this.text };
        this.tools = tls;
        this.tools_index = 0;

        this.custom_color = new Color(128, 128, 128);

        this.color_field_validation = true;
        this.color_combo_validation = true;
        this.font_size_field_validation = true;
        this.page_field_validation = true;
        this.text_combo_validation = true;
        this.text_field_validation = true;

        // JFrame
        this.frame = new JFrame("Notes");
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.frame.setSize(A4WIDTH + 200, A4HEIGHT + 30);
        this.frame.setLocationRelativeTo(null);
        this.frame.setResizable(false);

        this.canvas_panel = new JPanel();
        this.canvas_panel.setLayout(null);
        this.canvas_panel.setSize(A4WIDTH, A4HEIGHT);
        this.canvas_panel.setLocation(0, 0);
        this.frame.add(this.canvas_panel);

        // JPanel
        this.panel = new JPanel();
        this.panel.setLayout(null);
        this.panel.setSize(200, A4HEIGHT);
        this.panel.setLocation(A4WIDTH, 0);
        this.frame.add(this.panel);

        {
            JPanel jp = new JPanel();
            jp.setLayout(null);
            jp.setSize(200, 30);
            jp.setLocation(0, 0);

            this.tool_pen = new JButton("pen");
            this.tool_pen.setFont(font_m);
            this.tool_pen.setSize(60, 30);
            this.tool_pen.setLocation(0, 0);
            this.tool_pen.setForeground(Color.RED);
            this.tool_pen.addActionListener(new ChangeToPen());
            jp.add(this.tool_pen);

            this.tool_eraser = new JButton("eraser");
            this.tool_eraser.setFont(font_m);
            this.tool_eraser.setSize(60, 30);
            this.tool_eraser.setLocation(60, 0);
            this.tool_eraser.addActionListener(new ChangeToEraser());
            jp.add(this.tool_eraser);

            this.tool_text = new JButton("text");
            this.tool_text.setFont(font_m);
            this.tool_text.setSize(60, 30);
            this.tool_text.setLocation(120, 0);
            this.tool_text.addActionListener(new ChangeToText());
            jp.add(this.tool_text);

            this.panel.add(jp);
        }

        // tool & size
        {
            JPanel jp = new JPanel();
            jp.setLayout(null);
            jp.setSize(200, 30);
            jp.setLocation(0, 30);

            this.font_size_field = new JTextField();
            this.font_size_field.setSize(50, 30);
            this.font_size_field.setLocation(100, 0);
            this.font_size_field.setFont(font_l);
            this.font_size_field.setText(this.tools[this.tools_index].getSize() + "pt");
            this.font_size_field.addActionListener(new SetSize());
            jp.add(this.font_size_field);

            JButton jb = new JButton("U");
            jb.setFont(font_s);
            jb.setSize(25, 15);
            jb.setLocation(150, 0);
            jb.addActionListener(new IncreaseSize());
            jp.add(jb);

            jb = new JButton("D");
            jb.setFont(font_s);
            jb.setSize(25, 15);
            jb.setLocation(150, 15);
            jb.addActionListener(new DecreaseSize());
            jp.add(jb);

            jb = new JButton("N");
            jb.setFont(font_s);
            jb.setSize(25, 30);
            jb.setLocation(175, 0);
            jb.addActionListener(new DefaultSize());
            jp.add(jb);

            this.panel.add(jp);
        }

        // color palette
        {
            JPanel jp = new JPanel();
            jp.setLayout(null);
            jp.setSize(200, 30);
            jp.setLocation(0, 60);

            this.palette = new JPanel();
            this.palette.setSize(60, 30);
            this.palette.setBackground(Color.BLACK);
            this.palette.setLocation(0, 0);
            jp.add(this.palette);

            this.color_combo = new JComboBox();
            this.color_combo.setFont(font_s);
            for(String s : COLORS_STRING){
                this.color_combo.addItem(s);
            }
            this.color_combo.addItem("Custom");
            this.color_combo.setSize(70, 30);
            this.color_combo.setLocation(60, 0);
            this.color_combo.addActionListener(new SelectColor());
            jp.add(this.color_combo);

            this.color_field = new JTextField();
            this.color_field.setFont(font_l);
            this.color_field.setText("#000000");
            this.color_field.setSize(70, 30);
            this.color_field.setLocation(130, 0);
            this.color_field.addActionListener(new SetColor());
            jp.add(this.color_field);

            this.panel.add(jp);
        }

        // font
        {
            JPanel jp = new JPanel();
            jp.setLayout(null);
            jp.setSize(200, 30);
            jp.setLocation(0, 90);

            this.text_field = new JTextField("");
            this.text_field.setFont(new Font("Arial", Font.PLAIN, 14));
            this.text_field.setSize(200, 30);
            this.text_field.setLocation(0, 0);
            this.text_field.addActionListener(new PutText());
            jp.add(this.text_field);

            this.panel.add(jp);
        }

        //
        {
            JPanel jp = new JPanel();
            jp.setLayout(null);
            jp.setSize(200, 30);
            jp.setLocation(0, 120);

            Point p = this.text.getPoint();
            this.text_label = new JLabel(String.format("(%d, %d)", p.x, p.y));
            this.text_label.setFont(font_m);
            this.text_label.setSize(50, 30);
            this.text_label.setLocation(0, 0);
            jp.add(this.text_label);

            this.text_combo.setSize(150, 30);
            this.text_combo.setLocation(50, 0);
            this.text_combo.addActionListener(new SetFontStyle());
            jp.add(this.text_combo);

            this.panel.add(jp);
        }

        // load
        {
            JPanel jp = new JPanel();
            jp.setLayout(null);
            jp.setSize(200, 30);
            jp.setLocation(0, A4HEIGHT - 120);

            JButton jb = new JButton("load");
            jb.setFont(font_l);
            jb.setSize(100, 30);
            jb.setLocation(100, 0);
            jb.addActionListener(new LoadImage());
            jp.add(jb);

            this.panel.add(jp);
        }

        // page
        {
            JPanel jp = new JPanel();
            jp.setLayout(null);
            jp.setSize(200, 30);
            jp.setLocation(0, A4HEIGHT - 90);

            JButton jb = new JButton("prev");
            jb.setFont(font_s);
            jb.setSize(50, 30);
            jb.setLocation(0, 0);
            jb.addActionListener(new PreviousPage());
            jp.add(jb);

            jb = new JButton("next");
            jb.setFont(font_s);
            jb.setSize(50, 30);
            jb.setLocation(150, 0);
            jb.addActionListener(new NextPage());
            jp.add(jb);

            this.page_field = new JTextField();
            this.page_field.setFont(font_l);
            this.page_field.setSize(50, 30);
            this.page_field.setLocation(50, 0);
            this.page_field.addActionListener(new SetPage());
            jp.add(this.page_field);

            this.pages_label = new JLabel();
            this.pages_label.setFont(font_l);
            this.pages_label.setSize(50, 30);
            this.pages_label.setLocation(100, 0);
            jp.add(this.pages_label);

            this.panel.add(jp);
        }

        // create | delete
        {
            JPanel jp = new JPanel();
            jp.setLayout(null);
            jp.setSize(200, 30);
            jp.setLocation(0, A4HEIGHT - 60);

            JButton jb = new JButton("new P");
            jb.setFont(font_s);
            jb.setSize(70, 30);
            jb.setLocation(0, 0);
            jb.addActionListener(new AddPreviousPage());
            jp.add(jb);

            jb = new JButton("new N");
            jb.setFont(font_s);
            jb.setSize(70, 30);
            jb.setLocation(70, 0);
            jb.addActionListener(new AddNextPage());
            jp.add(jb);

            jb = new JButton("delete");
            jb.setFont(font_s);
            jb.setSize(60, 30);
            jb.setLocation(140, 0);
            jb.addActionListener(new DeletePage());
            jp.add(jb);

            this.panel.add(jp);
        }

        // note
        {
            JPanel jp = new JPanel();
            jp.setLayout(null);
            jp.setSize(200, 30);
            jp.setLocation(0, A4HEIGHT - 30);

            JButton jb = new JButton("save");
            jb.setFont(font_l);
            jb.setSize(100, 30);
            jb.setLocation(0, 0);
            jb.addActionListener(new Save());
            jp.add(jb);

            jb = new JButton("file");
            jb.setFont(font_l);
            jb.setSize(100, 30);
            jb.setLocation(100, 0);
            jb.addActionListener(new ChooseFile());
            jp.add(jb);

            this.panel.add(jp);
        }

        this.changeTool(0);

        JPanel jpa = new JPanel();
        jpa.setBackground(Color.BLACK);
        this.frame.add(jpa);

        this.frame.setVisible(true);
    }

    private void changeTool(int index){
        this.tools_index = index;
        Tool tl = this.tools[index];
        JButton[] buttons = { this.tool_pen, this.tool_eraser, this.tool_text };

        for(int i = 0; i < this.tools.length; i++){
            if(i != index){
                buttons[i].setForeground(Color.BLACK);
            }else{
                buttons[i].setForeground(Color.RED);
            }
        }

        this.font_size_field_validation = false;
        this.font_size_field.setText(tl.getSize() + "pt");
        this.font_size_field_validation = true;

        if(this.tools_index == 0 || this.tools_index == 2){
            Color color = tl.getColor();
            this.palette.setBackground(color);
            boolean flag = false;

            for(int i = 0; i < COLORS.length; i++){
                if(COLORS[i].equals(color)){
                    this.color_combo_validation = false;
                    this.color_combo.setSelectedIndex(i);
                    this.color_combo_validation = true;
                    flag = true;
                }
            }

            if(! flag){
                this.color_combo_validation = false;
                this.color_combo.setSelectedIndex(this.color_combo.getItemCount() - 1);
                this.color_combo_validation = true;
            }

            this.color_field_validation = false;
            this.color_field.setText(tl.getColorString());
            this.color_field_validation = true;

            this.color_combo.setEnabled(true);
            this.color_field.setEditable(true);
        }else{
            this.color_combo.setEnabled(false);
            this.color_field.setEditable(false);
        }

        if(this.tools_index != 2){
            this.text_label.setText("");
            this.text_combo.setEnabled(false);
            this.text_field.setEditable(false);
        }else{
            this.text_label.setText(String.format("(%d, %d)", tl.getPoint().x, tl.getPoint().y));
            this.text_combo.setEnabled(true);
            this.text_field.setEditable(true);
        }
    }

    private void increaseSize(){
        Tool t = this.tools[this.tools_index];
        t.increaseSize();
        this.font_size_field_validation = false;
        this.font_size_field.setText(t.getSize() + "pt");
        this.font_size_field_validation = true;
    }

    private void decreaseSize(){
        Tool t = this.tools[this.tools_index];
        t.decreaseSize();
        this.font_size_field_validation = false;
        this.font_size_field.setText(t.getSize() + "pt");
        this.font_size_field_validation = true;
    }

    private void defaultSize(){
        Tool t = this.tools[this.tools_index];
        t.defaultSize();
        this.font_size_field_validation = false;
        this.font_size_field.setText(t.getSize() + "pt");
        this.font_size_field_validation = true;
    }

    private void setSize(){
        if(! this.font_size_field_validation){
            return;
        }

        Tool t = this.tools[this.tools_index];
        t.setSize(this.font_size_field.getText());
        this.font_size_field_validation = false;
        this.font_size_field.setText(t.getSize() + "pt");
        this.font_size_field_validation = true;
    }

    private void selectColor(){
        if(! this.color_combo_validation){
            return;
        }

        int index = this.color_combo.getSelectedIndex();
        if(index != -1){
            Color color;

            if(index == this.color_combo.getItemCount() - 1){
                color = this.custom_color;
            }else{
                color = COLORS[index];
            }

            Tool t = this.tools[this.tools_index];
            t.setColor(color);
            this.color_field_validation = false;
            this.color_field.setText(t.getColorString());
            this.color_field_validation = true;
            this.palette.setBackground(color);
        }
    }

    private void setColor(){
        if(! this.color_field_validation){
            return;
        }

        String s = this.color_field.getText();
        Tool t = this.tools[this.tools_index];
        t.setColor(s);
        Color color = t.getColor();

        for(int i = 0; i < COLORS.length; i++){
            if(COLORS[i].equals(color)){
                this.color_combo_validation = false;
                this.color_combo.setSelectedIndex(i);
                this.color_combo_validation = true;
                return;
            }
        }

        this.custom_color = color;
        this.color_combo_validation = false;
        this.color_combo.setSelectedIndex(this.color_combo.getItemCount() - 1);
        this.color_combo_validation = true;

        this.palette.setBackground(color);
    }

    private void previousPage(){
        if(this.canvas == null){
            return;
        }

        this.canvas.previousPage();
        this.page_field_validation = false;
        this.page_field.setText(String.format("%d", this.canvas.getPageNumber()));
        this.page_field_validation = true;
    }

    private void nextPage(){
        if(this.canvas == null){
            return;
        }

        this.canvas.nextPage();
        this.page_field_validation = false;
        this.page_field.setText(String.format("%d", this.canvas.getPageNumber()));
        this.page_field_validation = true;
    }

    private void setPage(){
        if(! this.page_field_validation){
            return;
        }
        
        if(this.canvas == null){
            return;
        }

        this.canvas.setPage(this.page_field.getText());
        this.page_field_validation = false;
        this.page_field.setText(String.format("%d", this.canvas.getPageNumber()));
        this.page_field_validation = true;
    }

    private void chooseFile(){
        JFileChooser chooser = new JFileChooser("data");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int selected = chooser.showOpenDialog(this.frame);
        if(selected == JFileChooser.APPROVE_OPTION){
            if(this.canvas != null){
                this.canvas_panel.remove(this.canvas);
            }

            try{
                this.canvas = new Canvas(chooser.getSelectedFile(), new Dimension(A4WIDTH, A4HEIGHT), PAPER_COLOR);
                this.canvas.setLocation(0, 0);
                Paint paint = new Paint();
                this.canvas.addMouseListener(paint);
                this.canvas.addMouseMotionListener(paint);
                this.canvas_panel.add(this.canvas);
                this.canvas.repaint();

                this.page_field_validation = false;
                this.page_field.setText(String.format("%d", this.canvas.getPageNumber()));
                this.page_field_validation = true;
                this.pages_label.setText(String.format(" / %d", this.canvas.getPageCount()));
            }catch(IOException e){
                System.err.println("JFileChooser");
            }
        }
    }

    private void addNewPage(boolean is_previous){
        if(this.canvas == null){
            return;
        }

        this.canvas.addNewPage(is_previous);
        this.page_field_validation = false;
        this.page_field.setText(String.format("%d", this.canvas.getPageNumber()));
        this.page_field_validation = true;
        this.pages_label.setText(String.format(" / %d", this.canvas.getPageCount()));
    }

    private void deletePage(){
        if(this.canvas == null){
            return;
        }

        this.canvas.deletePage();
        this.page_field_validation = false;
        this.page_field.setText(String.format("%d", this.canvas.getPageNumber()));
        this.page_field_validation = true;
        this.pages_label.setText(String.format(" / %d", this.canvas.getPageCount()));
    }

    private void loadImage(){
        if(this.canvas == null){
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int selected = chooser.showOpenDialog(this.frame);

        if(selected == JFileChooser.APPROVE_OPTION){
            this.canvas.loadImage(chooser.getSelectedFile());
        }
    }

    private void setFontStyle(){
        if(! this.text_combo_validation){
            return;
        }

        int sd = this.text_combo.getSelectedIndex();

        this.text.setStyle(FONT_FAMILY[sd]);
    }

    private void putText(){
        if(! this.text_field_validation){
            return;
        }
        if(this.canvas == null){
            return;
        }

        this.canvas.putText(this.text, this.text_field.getText());
        this.text_field_validation = false;
        this.text_field.setText("");
        this.text_field_validation = true;
        this.text_label.setText(String.format("(%d, %d)", this.text.getPoint().x, this.text.getPoint().y));
    }

    private void press(Point p){
        switch(this.tools_index){
            case 0:
            this.canvas.paintStart(p, this.pen);
            break;
            case 1:
            this.canvas.paintStart(p, this.eraser);
            break;
            case 2:
            this.text.setPoint(p);
            this.text_label.setText(String.format("(%d, %d)", this.text.getPoint().x, this.text.getPoint().y));
            break;
            default:
            break;
        }
    }

    private void drag(Point p){
        switch(this.tools_index){
            case 0:
            this.canvas.paintLine(p, this.pen);
            break;
            case 1:
            this.canvas.paintLine(p, this.eraser);
            break;
            default:
            break;
        }
    }

    class ChangeToPen implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e){
            Notes.this.changeTool(0);
        }
    }

    class ChangeToEraser implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e){
            Notes.this.changeTool(1);
        }
    }

    class ChangeToText implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e){
            Notes.this.changeTool(2);
        }
    }

    class IncreaseSize implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e){
            Notes.this.increaseSize();
        }
    }

    class DecreaseSize implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e){
            Notes.this.decreaseSize();
        }
    }

    class DefaultSize implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e){
            Notes.this.defaultSize();
        }
    }

    class SetSize implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e){
            Notes.this.setSize();
        }
    }

    class SelectColor implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e){
            Notes.this.selectColor();
        }
    }

    class SetColor implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e){
            Notes.this.setColor();
        }
    }

    class PreviousPage implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e){
            Notes.this.previousPage();
        }
    }

    class NextPage implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e){
            Notes.this.nextPage();
        }
    }

    class SetPage implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e){
            Notes.this.setPage();
        }
    }

    class ChooseFile implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e){
            Notes.this.chooseFile();
        }
    }

    class AddPreviousPage implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e){
            Notes.this.addNewPage(true);
        }
    }

    class AddNextPage implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e){
            Notes.this.addNewPage(false);
        }
    }

    class Save implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e){
            Notes.this.canvas.saveChangedPages();
        }
    }

    class DeletePage implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e){
            Notes.this.deletePage();
        }
    }

    class LoadImage implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e){
            Notes.this.loadImage();
        }
    }

    class SetFontStyle implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e){
            Notes.this.setFontStyle();
        }
    }

    class PutText implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e){
            Notes.this.putText();
        }
    }

    class Paint extends MouseAdapter{
        @Override
        public void mousePressed(MouseEvent e){
            Notes.this.press(e.getPoint());
        }

        @Override
        public void mouseDragged(MouseEvent e){
            Notes.this.drag(e.getPoint());
        }
    }
}
