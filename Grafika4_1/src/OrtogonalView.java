import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author auswise
 */
public class OrtogonalView extends JPanel implements MouseListener, MouseMotionListener{
    public static final int ORIENTATION_XY = 0;
    public static final int ORIENTATION_XZ = 1;
    public static final int ORIENTATION_YZ = 2;
    
    private static final int P0_SIDE = 20;
    private static final int PT_SIDE = 10;
    
    private final Rectangle p0,pt;
    private final Ellipse2D light;
    
    private static final int LIGHT_RADIUS = 5;
    
    private Scene scene;
    private String name;
    
    private Matrix matrix;
    
    private boolean mouseP0, mousePt, mouseLight;
    
    private int orientation;
    
    private double scale = 1.;

    Vector transformedP0;
    Vector transformedPt;
    Vector transformedLight;
    Vector transformedLeftTop;
    Vector transformedRightTop;
    Vector transformedLeftBottom;
    Vector transformedRightBottom;
    Vector transformedVertex;
    
    public OrtogonalView(int orientation) {
        transformedP0 = new Vector();
        transformedPt = new Vector();
        transformedLight = new Vector();
        transformedLeftTop = new Vector();
        transformedRightTop = new Vector();
        transformedLeftBottom = new Vector();
        transformedRightBottom = new Vector();
        transformedVertex = new Vector();
        
        scene = Scene.INSTANCE;
        this.orientation = orientation;
        if(orientation == OrtogonalView.ORIENTATION_XY)
            name = "XOY";
        else if(orientation == OrtogonalView.ORIENTATION_XZ)
            name = "XOZ";
        else if(orientation == OrtogonalView.ORIENTATION_YZ)
            name = "YOZ";
        
        this.p0 = new Rectangle();
        this.p0.setSize(OrtogonalView.P0_SIDE, OrtogonalView.P0_SIDE);
        this.pt = new Rectangle();
        this.pt.setSize(OrtogonalView.PT_SIDE, OrtogonalView.PT_SIDE);
        
        this.light = new Ellipse2D.Double();
        
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
    }
    
    private void setMatrix() throws CloneNotSupportedException{
        if(orientation == OrtogonalView.ORIENTATION_XY)
            matrix = Matrix.XOYProjection();
        else if(orientation == OrtogonalView.ORIENTATION_XZ)
            matrix = Matrix.XOZProjection();
        else if(orientation == OrtogonalView.ORIENTATION_YZ)
            matrix = Matrix.YOZProjection();
        
        setScale();
        
        matrix = Matrix.move(super.getWidth()/2, super.getHeight()/2, 0)
                        .multiply(Matrix.scale(1, -1, 1))
                        .multiply(Matrix.scale(scale, scale, 1.))
                        .multiply(matrix);
    }
    
    @Override
    public void paintComponent(Graphics g){
        if(!scene.isInitialized())
            return;
        
        try {
            setMatrix();
            
            this.transformedP0.setTransformed(matrix, scene.camera.p0);
            this.transformedPt.setTransformed(matrix, scene.camera.pt);
            this.transformedLight.setTransformed(matrix, scene.light.getPosition());
            
            Graphics2D g2d = (Graphics2D)g;
            this.paintImage(g2d);
            this.paintName(g2d);
            this.paintCamera(g2d);
            this.paintLight(g2d);
            this.paintCone(g2d);
            
        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(OrtogonalView.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void paintImage(Graphics2D g2d) {
        BufferedImage image = new BufferedImage(super.getWidth(), super.getHeight(), BufferedImage.TYPE_INT_RGB);
        
//        scene.setNormals();
//        scene.setColors();
        
        double [][] zbuffer = new double [this.getWidth()][this.getHeight()];
        
        for(int x=0;x<zbuffer.length;x++)
            for(int y=0;y<zbuffer[x].length;y++)
                zbuffer[x][y] = Double.POSITIVE_INFINITY;
    
        double z;
        for(Triangle triangle : scene.triangles)
        {
                triangle.transform(matrix);
                
                int ymax = (int) triangle.getMaxY();
                int ymin = (int) triangle.getMinY();
                int xmax = (int) triangle.getMaxX();
                int xmin = (int) triangle.getMinX();
                
                int r,g,b;
                for(int y=ymin;y<=ymax;y++){
                    for(int x=xmin;x<xmax;x++){
                        if(triangle.contains(x, y)){
                            z = triangle.interpolateZ(x, y);
                            if(z < zbuffer[x][y]){
                                zbuffer[x][y] = z;                                
                                image.setRGB(x, y, triangle.part.getRGB());
                            }
                        }
                    }
                }
        }
        
        g2d.drawImage(image, null, null);
    }
    
    private void paintName(Graphics2D g2d){
        g2d.setColor(java.awt.Color.WHITE);
        g2d.drawString(name, 10, 10);
    }
    
    private void paintCamera(Graphics2D g2d){        
        pt.setLocation((int)transformedPt.getX() - PT_SIDE/2, (int)transformedPt.getY() - PT_SIDE/2);
        p0.setLocation((int)transformedP0.getX() - P0_SIDE/2, (int)transformedP0.getY()- P0_SIDE/2);
        
        g2d.setColor(java.awt.Color.WHITE);
        g2d.draw(p0);
        g2d.draw(pt);
    }
    
    private void paintLight(Graphics2D g2d){        
        light.setFrame(transformedLight.getX() - LIGHT_RADIUS, transformedLight.getY() - LIGHT_RADIUS, 2*LIGHT_RADIUS, 2*LIGHT_RADIUS);
        
        g2d.setColor(java.awt.Color.YELLOW);
        g2d.fill(light);
    }
    
    private void paintCone(Graphics2D g2d){
        g2d.setColor(java.awt.Color.WHITE);
        
        transformedLeftTop.setTransformed(matrix, scene.leftTop);
        transformedRightTop.setTransformed(matrix, scene.rightTop);
        transformedLeftBottom.setTransformed(matrix, scene.leftBottom);
        transformedRightBottom.setTransformed(matrix, scene.rightBottom);
        
        g2d.drawLine((int)transformedLeftTop.getX(), (int)transformedLeftTop.getY(), (int)transformedP0.getX(), (int)transformedP0.getY());
        g2d.drawLine((int)transformedRightTop.getX(), (int)transformedRightTop.getY(), (int)transformedP0.getX(), (int)transformedP0.getY());
        g2d.drawLine((int)transformedLeftBottom.getX(), (int)transformedLeftBottom.getY(), (int)transformedP0.getX(), (int)transformedP0.getY());
        g2d.drawLine((int)transformedRightBottom.getX(), (int)transformedRightBottom.getY(), (int)transformedP0.getX(), (int)transformedP0.getY());
        
        g2d.drawLine((int)transformedLeftTop.getX(), (int)transformedLeftTop.getY(), (int)transformedRightTop.getX(), (int)transformedRightTop.getY());
        g2d.drawLine((int)transformedRightTop.getX(), (int)transformedRightTop.getY(), (int)transformedRightBottom.getX(), (int)transformedRightBottom.getY());
        g2d.drawLine((int)transformedRightBottom.getX(), (int)transformedRightBottom.getY(), (int)transformedLeftBottom.getX(), (int)transformedLeftBottom.getY());
        g2d.drawLine((int)transformedLeftBottom.getX(), (int)transformedLeftBottom.getY(), (int)transformedLeftTop.getX(), (int)transformedLeftTop.getY());
    }
    
    @Override
    public void mouseClicked(MouseEvent e) {
//        Matrix inverted = matrix.inverse();
//        
//        Vector mouse = new Vector();
//        mouse.setX(e.getX());
//        mouse.setY(e.getY());
//        mouse.setZ(0);
//        
//        mouse = inverted.transform(mouse);
//        
//        System.out.println(mouse);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if(p0.contains(e.getPoint()))
            this.mouseP0 = true;
        
        if(pt.contains(e.getPoint()))
            this.mousePt = true;
        
        if(light.contains(e.getPoint()))
            this.mouseLight = true;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        this.mouseP0 = false;
        this.mousePt = false;
        this.mouseLight = false;
    }

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

    @Override
    public void mouseDragged(MouseEvent e) {
        Matrix inverted = matrix.inverse();
        
        if(mouseP0){
            this.transformedP0.setX(e.getX());
            this.transformedP0.setY(e.getY());
            
            scene.camera.p0.setTransformed(inverted, transformedP0); 
        }
        
        if(mousePt){
            this.transformedPt.setX(e.getX());
            this.transformedPt.setY(e.getY());
            scene.camera.pt.setTransformed(inverted, transformedPt);
        }
        
        if(mouseLight){
            this.transformedLight.setX(e.getX());
            this.transformedLight.setY(e.getY());
            scene.light.getPosition().setTransformed(inverted, transformedLight);
        }
        
        Window.INSTANCE.repaint();
    }

    @Override
    public void mouseMoved(MouseEvent e) {}
    
    private double setScale(){
        
        transformedP0.setTransformed(matrix, scene.camera.p0);
        transformedPt.setTransformed(matrix, scene.camera.pt);
        transformedLight.setTransformed(matrix, scene.light.getPosition());
        transformedLeftTop.setTransformed(matrix, scene.leftTop);
        transformedRightTop.setTransformed(matrix, scene.rightTop);
        transformedLeftBottom.setTransformed(matrix, scene.leftBottom);
        transformedRightBottom.setTransformed(matrix, scene.rightBottom);
        
        
        double width = 0;
        double proportion = ((double)getWidth())/getHeight();
        for(Vector vertex: scene.vertices){
            transformedVertex.setTransformed(matrix, vertex);
            
            if(Math.abs(transformedVertex.getX())>width)                             
                width=Math.abs(transformedVertex.getX());
            
            if(Math.abs(transformedVertex.getY())*proportion>width)        
                width=Math.abs(transformedVertex.getY())*proportion;
        }
        
        if(Math.abs(transformedP0.getX())>width)
            width=Math.abs(transformedP0.getX());
        
        if(Math.abs(transformedP0.getY())*proportion>width)        
            width=Math.abs(transformedP0.getY())*proportion;
        
        if(Math.abs(transformedPt.getX())>width)                                
            width=Math.abs(transformedPt.getX());
        
        if(Math.abs(transformedPt.getY()*proportion)>width)           
            width=Math.abs(transformedPt.getY())*proportion;
        
        if(Math.abs(transformedLight.getX())>width)                                
            width=Math.abs(transformedLight.getX());
        
        if(Math.abs(transformedLight.getY()*proportion)>width)           
            width=Math.abs(transformedLight.getY())*proportion;
        
        if(Math.abs(transformedLeftTop.getX())>width)                        
            width=Math.abs(transformedLeftTop.getX());
        
        if(Math.abs(transformedLeftTop.getY()*proportion)>width)   
            width=Math.abs(transformedLeftTop.getY())*proportion;
        
        if(Math.abs(transformedRightTop.getX())>width)                        
            width=Math.abs(transformedRightTop.getX());
        
        if(Math.abs(transformedRightTop.getY()*proportion)>width)   
            width=Math.abs(transformedRightTop.getY())*proportion;
        
        if(Math.abs(transformedLeftBottom.getX())>width)                        
            width=Math.abs(transformedLeftBottom.getX());
        
        if(Math.abs(transformedLeftBottom.getY()*proportion)>width)   
            width=Math.abs(transformedLeftBottom.getY())*proportion;
        
        if(Math.abs(transformedRightBottom.getX())>width)                        
            width=Math.abs(transformedRightBottom.getX());
        
        if(Math.abs(transformedRightBottom.getY()*proportion)>width)   
            width=Math.abs(transformedRightBottom.getY())*proportion;
        
        width *= 1.25;
        scale = getWidth()/2/width;
        return scale;
    }
}
