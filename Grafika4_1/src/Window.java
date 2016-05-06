
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author auswise
 */
public class Window extends JFrame implements ChangeListener, ActionListener{
    public static final Window INSTANCE = new Window();
    
    private final String title = "Grafika 4";
    
    JPanel imagePanel;
    OrtogonalView xoyView;
    OrtogonalView xozView;
    OrtogonalView yozView;
    PerspectiveView perspectiveView;
    Scene scene;
    
    JMenuBar menuBar;
    JMenu file;
    JMenuItem load;
    JMenuItem save;
    
    ButtonGroup shading;
    JRadioButton none;
    JRadioButton flat;
    JRadioButton guarand;
    JRadioButton phong;
    JSlider angleSlider;
        
    JPanel options;
    
    JFileChooser fileChooser;
    
    private Window() {
        try {
            scene = Scene.INSTANCE;
            this.initComponents();
        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(Window.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void initComponents() throws CloneNotSupportedException{
        this.setLayout(new BorderLayout());
        
        this.imagePanel = new JPanel();
        this.imagePanel.setLayout(new GridLayout(2,2));
        this.add(imagePanel, BorderLayout.CENTER);
        
        this.xoyView = new OrtogonalView(OrtogonalView.ORIENTATION_XY);
        this.xoyView.setBorder(BorderFactory.createLineBorder(Color.WHITE));
        this.imagePanel.add(this.xoyView);
        
        this.xozView = new OrtogonalView(OrtogonalView.ORIENTATION_XZ);
        this.xozView.setBorder(BorderFactory.createLineBorder(Color.WHITE));
        this.imagePanel.add(this.xozView);
        
        this.yozView = new OrtogonalView(OrtogonalView.ORIENTATION_YZ);
        this.yozView.setBorder(BorderFactory.createLineBorder(Color.WHITE));
        this.imagePanel.add(this.yozView);
        
        this.perspectiveView = new PerspectiveView();
        this.perspectiveView.setBorder(BorderFactory.createLineBorder(Color.WHITE));
        this.imagePanel.add(this.perspectiveView);
        
        this.options = new JPanel(new FlowLayout());
        this.add(options, BorderLayout.SOUTH);
        
        this.angleSlider = new JSlider();
        this.angleSlider.addChangeListener(this);
        this.options.add(this.angleSlider, BorderLayout.SOUTH);
        
        this.shading = new ButtonGroup();
        
        this.none = new JRadioButton("None");
        this.none.addActionListener(this);
        this.options.add(none);
        this.shading.add(none);
        
        this.flat = new JRadioButton("Flat");
        this.flat.addActionListener(this);
        this.options.add(flat);
        this.shading.add(flat);
        
        this.guarand = new JRadioButton("Guarand");
        this.options.add(guarand);
        this.guarand.addActionListener(this);
        this.shading.add(guarand);
        
//        this.phong = new JRadioButton("Phong");
//        this.options.add(phong);
//        this.phong.addActionListener(this);
//        this.shading.add(phong);
//        
        this.menuBar = new JMenuBar();
        this.add(menuBar, BorderLayout.NORTH);
        
        this.file = new JMenu("File");
        this.menuBar.add(file);
        
        this.load = new JMenuItem("Load");
        this.load.addActionListener(this);
        this.file.add(this.load);
        
        this.save = new JMenuItem("Save");
        this.save.addActionListener(this);
        this.file.add(this.save);
        
        this.setSize(300,300);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle(title);
        
        this.fileChooser = new JFileChooser();
        this.fileChooser.setFileFilter(new FileNameExtensionFilter("Opis sceny (pliki brp)", "brp"));
		
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        scene.camera.a = angleSlider.getValue();
        repaint();
    }

    @Override
    public void paint(Graphics g) {
        try {
            if(scene.isInitialized()){
                setCone();
                this.angleSlider.setValue((int) scene.camera.a);    
            }
            super.paint(g); //To change body of generated methods, choose Tools | Templates.
        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(Window.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void setCone() throws CloneNotSupportedException{
        
        
        Matrix inverted = scene.camera.perspective().inverse();
        
        double coneBaseWidth = scene.camera.coneBaseWidth;
        double coneBaseHeight = coneBaseWidth * ((double)perspectiveView.getHeight())/((double)perspectiveView.getHeight());
        
        scene.leftTop.setX(-coneBaseWidth);
        scene.leftTop.setY(coneBaseHeight);
        scene.leftTop.setZ(0);
        scene.leftTop.setTransformed(inverted, scene.leftTop);
        
        scene.rightTop.setX(coneBaseWidth);
        scene.rightTop.setY(coneBaseHeight);
        scene.rightTop.setZ(0);
        scene.rightTop.setTransformed(inverted, scene.rightTop);
        
        scene.leftBottom.setX(-coneBaseWidth);
        scene.leftBottom.setY(-coneBaseHeight);
        scene.leftBottom.setZ(0);
        scene.leftBottom.setTransformed(inverted, scene.leftBottom);
        
        scene.rightBottom.setX(coneBaseWidth);
        scene.rightBottom.setY(-coneBaseHeight);
        scene.rightBottom.setZ(0);
        scene.rightBottom.setTransformed(inverted, scene.rightBottom);
    }
    

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == load){
             
            int answer = fileChooser.showOpenDialog(null);
            if(answer == JFileChooser.APPROVE_OPTION){
                try {
                    File file = fileChooser.getSelectedFile();
                    scene.load(file);
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(Window.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            repaint();
        }
        else if(e.getSource() == save){
            int answer = fileChooser.showSaveDialog(null);
            if(answer == JFileChooser.APPROVE_OPTION){
                try {
                    File file = fileChooser.getSelectedFile();
                    scene.save(file);
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(Window.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(Window.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        else if(e.getSource() == none){
            this.perspectiveView.setShading(PerspectiveView.NONE);
        }
        else if(e.getSource() == flat){
            this.perspectiveView.setShading(PerspectiveView.FLAT);
        }
        else if(e.getSource() == guarand){
            this.perspectiveView.setShading(PerspectiveView.GUARAND);
        }
        else if(e.getSource() == phong){
            this.perspectiveView.setShading(PerspectiveView.PHONG);
        }
        
    }
}
