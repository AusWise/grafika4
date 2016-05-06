/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author auswise
 */
public class Matrix extends Jama.Matrix{

    public Matrix(){
        super(4,4);
    }
    
    private Matrix(Jama.Matrix matrix){
        this();
        
        for(int i=0;i<4;i++)
            for(int j=0;j<4;j++)
                set(i,j, matrix.get(i, j));
    }
    
    public Matrix multiply(Matrix other){
        return new Matrix(super.times(other));
    }
    
    public Matrix inverse(){
        return new Matrix(super.inverse());
    }
    
    public static Matrix identity(){
        Matrix newMatrix = new Matrix();
        
        newMatrix.set(0, 0, 1);
        newMatrix.set(1, 1, 1);
        newMatrix.set(2, 2, 1);
        newMatrix.set(3, 3, 1);
        
        return newMatrix;
    }
  
//    public Vector transform(Vector point){
//        Vector newPoint = new Vector();
//        
//        double x = get(0,0)*point.get(0) + get(0,1)*point.get(1) + get(0,2)*point.get(2) + get(0,3);
//        double y = get(1,0)*point.get(0) + get(1,1)*point.get(1) + get(1,2)*point.get(2) + get(1,3);
//        double z = get(2,0)*point.get(0) + get(2,1)*point.get(1) + get(2,2)*point.get(2) + get(2,3);
//        double w = get(3,0)*point.get(0) + get(3,1)*point.get(1) + get(3,2)*point.get(2) + get(3,3);
//        
//        x /= w;
//        y /= w;
//        z /= w;
//        
//        newPoint.setX(x);
//        newPoint.setY(y);
//        newPoint.setZ(z);
//        
//        return newPoint;
//    }
    
    public static Matrix scale(double sx, double sy, double sz){
        Matrix newMatrix = Matrix.identity();
        
        newMatrix.set(0, 0, sx);
        newMatrix.set(1, 1, sy);
        newMatrix.set(2, 2, sz);
        
        return newMatrix;
    }
    
    public static Matrix move(double px, double py, double pz){
        Matrix newMatrix = Matrix.identity();
        
        newMatrix.set(0, 3, px);
        newMatrix.set(1, 3, py);
        newMatrix.set(2, 3, pz);
        
        return newMatrix;
    }
    
    public static Matrix OZRotation(double angle){
        Matrix newMatrix = Matrix.identity();
        
        newMatrix.set(0,0, Math.cos(angle));
        newMatrix.set(0,1, -Math.sin(angle));
        newMatrix.set(1,0, Math.sin(angle));
        newMatrix.set(1,1, Math.cos(angle));
        
        return newMatrix;
    }
    
    public static Matrix OYRotation(double angle){
        Matrix newMatrix = Matrix.identity();
        
        newMatrix.set(0,0, Math.cos(angle));
        newMatrix.set(0,2, Math.sin(angle));
        newMatrix.set(2,0, -Math.sin(angle));
        newMatrix.set(2,2, Math.cos(angle));
        
        return newMatrix;
    }
    
    public static Matrix OXRotation(double angle){
        Matrix newMatrix = Matrix.identity();
        
        newMatrix.set(1,1, Math.cos(angle));
        newMatrix.set(1,2, -Math.sin(angle));
        newMatrix.set(2,1, Math.sin(angle));
        newMatrix.set(2,2, Math.cos(angle));
        
        return newMatrix;
    }
    
    public static Matrix XOYProjection(){
        return identity();
    }
    
    public static Matrix XOZProjection(){
        Matrix newMatrix = identity();
        
        newMatrix.set(1, 1, -1);
        
        return newMatrix.multiply(Matrix.OXRotation(Math.PI/2));
    }
    
    public static Matrix YOZProjection(){
        Matrix newMatrix = identity();
        
        newMatrix.set(2, 2, -1);
        
        return newMatrix.multiply(Matrix.OYRotation(Math.PI/2)).multiply(Matrix.XOZProjection());
    }
}
