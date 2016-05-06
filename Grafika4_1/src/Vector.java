
import java.awt.Color;
import java.util.LinkedList;
import java.util.List;



/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author auswise
 */
public class Vector /*extends Matrix*/ implements Cloneable{
    public double[] array;
    
    public Vector normal;
    
    public Vector(){
        array = new double[3];    
    }
    
    public double get(int i){
        return array[i];
    }

    public double getX(){
        return array[0];
    }
    
    public double getY(){
        return array[1];
    }
    
    public double getZ(){
        return array[2];
    }
    
    public void set(int i, double value){
        array[i] = value;
    }
    
    public void setX(double x){
        array[0] = x;
    }
    
    public void setY(double y){
        array[1] = y;
    }
    
    public void setZ(double z){
        array[2] = z;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        Vector clone =  (Vector)super.clone(); 
        clone.array = array.clone();
        
        return clone;
    }

    @Override
    public String toString() {
        return getX() + " " + getY() + " " + getZ();
    }
    
    public double dot(Vector other){
        double dot=0;
        for(int i=0;i<array.length;i++)
            dot+=this.array[i]*other.array[i];
        
        return dot;
    }
    
    public Vector add(Vector other){
        Vector vector = new Vector();
        
        for(int i=0;i<array.length;i++)
            vector.array[i] = this.array[i] + other.array[i];
        
        return vector;
    }
    
    public Vector sub(Vector other){
        Vector vector = new Vector();
        
        for(int i=0;i<array.length;i++)
            vector.array[i] = this.array[i] - other.array[i];
        
        return vector;
    }
    
    public Vector cross(Vector other){
        Vector cross = new Vector();
        
        double ax = this.array[0];
        double ay = this.array[1];
        double az = this.array[2];
        double bx = other.array[0];
        double by = other.array[1];
        double bz = other.array[2];
        
        cross.setX(ay*bz - az*by);
        cross.setY(az*bx - ax*bz);
        cross.setZ(ax*by - ay*bx);
        
        return cross;
    }
    
    public Vector multiply(double d){
        Vector vector = new Vector();
        
        for(int i=0;i<array.length;i++)
            vector.array[i] = d*this.array[i];
        
        return vector;
    }
    
    public double length(){
        double length = 0;
        
        for(int i=0;i<array.length;i++)
            length += array[i]*array[i];
        
        return Math.sqrt(length);
    }
    
    public void normalize(){
        double length = length();
        for(int i=0;i<array.length;i++)
            array[i]/=length;
    }
    
      public void setTransformed(Matrix matrix, Vector vertex){
        double x = matrix.get(0,0)*vertex.get(0) + matrix.get(0,1)*vertex.get(1) + matrix.get(0,2)*vertex.get(2) + matrix.get(0,3);
        double y = matrix.get(1,0)*vertex.get(0) + matrix.get(1,1)*vertex.get(1) + matrix.get(1,2)*vertex.get(2) + matrix.get(1,3);
        double z = matrix.get(2,0)*vertex.get(0) + matrix.get(2,1)*vertex.get(1) + matrix.get(2,2)*vertex.get(2) + matrix.get(2,3);
        double w = matrix.get(3,0)*vertex.get(0) + matrix.get(3,1)*vertex.get(1) + matrix.get(3,2)*vertex.get(2) + matrix.get(3,3);
        
        x /= w;
        y /= w;
        z /= w;
        
        setX(x);
        setY(y);
        setZ(z);
    }
}
