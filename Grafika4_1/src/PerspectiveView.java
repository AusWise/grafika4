
import java.awt.Graphics;
import java.awt.Graphics2D;
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
public class PerspectiveView extends JPanel {
    public static final int NONE = 0;
    public static final int FLAT = 1;
    public static final int GUARAND = 2;
    public static final int PHONG = 3;
    
    Scene scene;
    String name;
    
    Matrix matrix;
    
    int shading;

    public PerspectiveView() {
        super();
        name = "Perspective";
        shading = GUARAND;
        scene = Scene.INSTANCE;
    }
    
    private void setMatrix() throws CloneNotSupportedException{
        matrix = Matrix.move(super.getWidth()/2, super.getHeight()/2, 0)
                        .multiply(Matrix.scale(((double)getWidth())/scene.camera.coneBaseWidth/2,((double)getWidth())/scene.camera.coneBaseWidth/2,1))
                        .multiply(Matrix.scale(1, -1, 1))
                        .multiply(scene.camera.perspective());
    }
    
    @Override
    public void paintComponent(Graphics g){
        if(!scene.isInitialized())
            return;
            
        try {
            Graphics2D g2d = (Graphics2D)g;
            this.paintImage(g2d);
            this.paintName(g2d);
        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(PerspectiveView.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void paintImage(Graphics2D g2d) throws CloneNotSupportedException{
        BufferedImage image = new BufferedImage(super.getWidth(), super.getHeight(), BufferedImage.TYPE_INT_RGB);
        
        setMatrix();
        
        scene.setNormals();
        scene.setColors();
        
        double [][] zbuffer = new double [this.getWidth()][this.getHeight()];
        
        for(int x=0;x<zbuffer.length;x++)
            for(int y=0;y<zbuffer[x].length;y++)
                zbuffer[x][y] = Double.POSITIVE_INFINITY;
    
        double z;
        int rgb = -1;
        for(Triangle triangle : scene.triangles)
        {
                triangle.transform(matrix);
                
                if(triangle.transformedVertices[0].getX()<0 && 
                        triangle.transformedVertices[1].getX()<0 && 
                        triangle.transformedVertices[2].getX()<0)
                    continue;
                
                if(triangle.transformedVertices[0].getX()>getWidth()-1 && 
                        triangle.transformedVertices[1].getX()>getWidth()-1 && 
                        triangle.transformedVertices[2].getX()>getWidth()-1)
                    continue;
                
                if(triangle.transformedVertices[0].getY()<0 && 
                        triangle.transformedVertices[1].getY()<0 && 
                        triangle.transformedVertices[2].getY()<0)
                    continue;
                
                if(triangle.transformedVertices[0].getY()>getHeight()-1 && 
                        triangle.transformedVertices[1].getY()>getHeight()-1 && 
                        triangle.transformedVertices[2].getY()>getHeight()-1)
                    continue;
                
                double d = scene.camera.coneHeight;
                double zmin = d*d/(1-d);
                
                int ymax = (int) triangle.getMaxY();
                int ymin = (int) triangle.getMinY();
                int xmax = (int) triangle.getMaxX();
                int xmin = (int) triangle.getMinX();
                
                if(ymax > super.getHeight()-1)
                    ymax = super.getHeight() - 1;
                
                if(ymin < 0)
                    ymin = 0;
                
                if(xmax > super.getWidth() - 1)
                    xmax = super.getWidth() -1;
                
                if(xmin < 0)
                    xmin = 0;
                
                int r,g,b;
                for(int y=ymin;y<=ymax;y++){
                    for(int x=xmin;x<xmax;x++){
                        if(triangle.contains(x, y)){
                            z = triangle.interpolateZ(x, y);
                            if(/*z > zmin && */z < zbuffer[x][y]){
                                zbuffer[x][y] = z;
                                if(shading == PerspectiveView.NONE)
                                    rgb = triangle.getRGB();
                                else if(shading == PerspectiveView.FLAT)
                                    rgb = triangle.flat();
                                else if(shading == PerspectiveView.GUARAND)
                                    rgb = triangle.guarand(x,y).getRGB();
                                
                                image.setRGB(x, y, rgb);
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
    
    public void setShading(int shading){
        this.shading = shading;
        Window.INSTANCE.repaint();
    }
}
