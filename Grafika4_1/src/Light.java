/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author auswise
 */
public class Light {
    private Vector position;
    private Color color;
    
    public Light(){
        position = new Vector();
        color = new Color();
    }

    public Vector getPosition(){
        return position;
    }
    
    public void setPosition(Vector position){
        this.position = position;
    }
    
    public double getX() {
        return position.getX();
    }

    public void setX(double x) {
        position.setX(x);
    }

    public double getY() {
        return position.getY();
    }

    public void setY(double y) {
        position.setY(y);
    }

    public double getZ() {
        return position.getZ();
    }

    public void setZ(double z) {
        position.setZ(z);
    }

    public int getRed() {
        return color.getRed();
    }

    public void setRed(int r) {
        color.setRed(r);
    }

    public int getGreen() {
        return color.getGreen();
    }

    public void setGreen(int g) {
        color.setGreen(g);
    }

    public int getBlue() {
        return color.getBlue();
    }

    public void setBlue(int b) {
        color.setBlue(b);
    }
    
    public int getRGB(){
        return color.getRGB();
    }

    @Override
    public String toString() {
        return  position + " " + color;
    }
    
    
}
