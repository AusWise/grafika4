/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author auswise
 */
public class Color {
    private final int[] color;

    public Color(){
        color = new int[3];
    }
    
    public int getRed() {
        return color[0];
    }

    public void setRed(int r) {
        color[0] = r;
    }

    public int getGreen() {
        return color[1];
    }

    public void setGreen(int g) {
        color[1] = g;
    }

    public int getBlue() {
        return color[2];
    }

    public void setBlue(int b) {
        color[2] = b;
    }
    
    
    public int get(int i){
        return color[i];
    }
    
    public void set(int i, int value){
        color[i] = value;
    }
    
    public int getRGB(){
        return 255 << 24 | ((int) color[0] & 255) << 16 | ((int) color[1] & 255) << 8 | (int) color[2] & 255;
    }

    @Override
    public String toString() {
        return this.getRed() + " " + this.getGreen() + " " + this.getBlue();
    }
    
    
}
