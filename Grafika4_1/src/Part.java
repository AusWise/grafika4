/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author auswise
 */
public class Part {
    private final Color color;
    private double kd,ks,g;
    
    private int index;
    
    public Part(int index){
        this.index = index;
        color = new Color();
    }
    
    public int getRGB(){
        return color.getRGB();
    }
    
    public int getRed() {
        return color.getRed();
    }

    public void setRed(int red) {
        color.setRed(red);
    }

    public int getGreen() {
        return color.getGreen();
    }

    public void setGreen(int green) {
        color.setGreen(green);
    }

    public int getBlue() {
        return color.getBlue();
    }

    public void setBlue(int blue) {
        color.setBlue(blue);
    }
    
    public double getKd() {
        return kd;
    }

    public void setKd(double kd) {
        this.kd = kd;
    }

    public double getKs() {
        return ks;
    }

    public void setKs(double ks) {
        this.ks = ks;
    }

    public double getG() {
        return g;
    }

    public void setG(double g) {
        this.g = g;
    }

    public int getIndex() {
        return index;
    }

    @Override
    public String toString() {
        return color + " " + kd + " " + ks + " " + g;
    }
    
    
}
