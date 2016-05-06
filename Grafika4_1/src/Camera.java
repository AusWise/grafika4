/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author auswise
 */
public class Camera {
    public Vector p0, pt;
    public double a;
    
    public double coneHeight;
    public double coneBaseWidth;
    
    public Matrix perspective;
    
    public Camera(){
        p0 = new Vector();
        pt = new Vector();
    }
    
    public Matrix perspective() throws CloneNotSupportedException{
        Matrix transformation1 = Matrix.move(-pt.getX(), -pt.getY(), -pt.getZ());
        
        Vector p0 = (Vector)this.p0.clone();
        
        p0.setTransformed(transformation1, p0);
        
        Matrix transformation2 = Matrix.OYRotation(Math.PI - Math.atan2(p0.getX(), p0.getZ()));
        
        p0.setTransformed(transformation2, p0);
        
        Matrix transformation3 = Matrix.OXRotation(-Math.PI/2 - Math.atan2(p0.getZ(), p0.getY()));
        
        p0.setTransformed(transformation3, p0);
        
        double d = coneHeight = -p0.getZ();
        coneBaseWidth = coneHeight * Math.tan(Math.toRadians(a));
        
        double[] dv = {0, 1};
        
        Matrix transformation4 = Matrix.OZRotation(Math.PI - Math.atan2(dv[1], dv[0]));
        
        Matrix transformation5 = perspective(d);
        
        Matrix transformation = Matrix.identity();
        transformation = transformation1.multiply(transformation);
        transformation = transformation2.multiply(transformation);
        transformation = transformation3.multiply(transformation);
        transformation = transformation4.multiply(transformation);
        transformation = transformation5.multiply(transformation);
        
        this.perspective = transformation;
        
        return transformation;
    }
    
    private Matrix perspective(double d){
        Matrix matrix = Matrix.identity();
        
//        matrix.set(2, 2, 0);
        matrix.set(3, 2, 1.0/d);
        
        return matrix;
    }

    public double getConeBaseWidth() {
        return coneBaseWidth;
    }    
    
    
}
