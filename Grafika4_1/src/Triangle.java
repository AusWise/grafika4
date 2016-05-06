import java.awt.Polygon;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author auswise
 */
public class Triangle {
   public Vector[] vertices;
   public Vector[] transformedVertices;
   public Part part;
   public Vector normal;
   public Vector transformedNormal;
   public Color[] colors;
   public Color color;
   
   private Polygon polygon;
   
   public int[] vertexIndices;
   
   private static Scene scene = Scene.INSTANCE;
   
   public Triangle(){
       vertices = new Vector[3];
       transformedVertices = new Vector[3];
       
       transformedVertices[0] = new Vector();
       transformedVertices[1] = new Vector();
       transformedVertices[2] = new Vector();
       
       colors = new Color[3];
       vertexIndices = new int[3];
       
       polygon = new Polygon();
   }
   
   public void transform(Matrix transformation){
       this.transformedVertices[0].setTransformed(transformation, get(0));
       this.transformedVertices[1].setTransformed(transformation, get(1));
       this.transformedVertices[2].setTransformed(transformation, get(2));
       
       polygon.reset();
       polygon.addPoint((int)transformedVertices[0].getX(), (int)transformedVertices[0].getY());
       polygon.addPoint((int)transformedVertices[1].getX(), (int)transformedVertices[1].getY());
       polygon.addPoint((int)transformedVertices[2].getX(), (int)transformedVertices[2].getY());
   }
   
   public Vector get(int i){
       return vertices[i];
   }
   
   public void set(int i, Vector value){
       vertices[i] = value;
   }
   
   public int flat() throws CloneNotSupportedException{
       return color.getRGB();
       
   }
   
   public int getRGB(){
       return part.getRGB();
   }
   
   public double getMaxY(){
       return Math.max(Math.max(transformedVertices[0].getY(), transformedVertices[1].getY()), transformedVertices[2].getY());
   }
   
   public double getMinY(){
       return Math.min(Math.min(transformedVertices[0].getY(), transformedVertices[1].getY()), transformedVertices[2].getY());
   }
   
   public double getMaxX(){
       return Math.max(Math.max(transformedVertices[0].getX(), transformedVertices[1].getX()), transformedVertices[2].getX());
   }
   
   public double getMinX(){
       return Math.min(Math.min(transformedVertices[0].getX(), transformedVertices[1].getX()), transformedVertices[2].getX());
   }
   
   public boolean contains(double x, double y){
       return polygon.contains(x, y);
   }

   @Override
   public String toString() {
       return this.vertexIndices[0] + " " + this.vertexIndices[1] + " " + this.vertexIndices[2];
   }
   
    public double interpolateZ(double x, double y){
        Vector vertex1 = this.transformedVertices[0];
        Vector vertex2 = this.transformedVertices[1];
        Vector vertex3 = this.transformedVertices[2];

        double x1 = vertex1.getX();
        double y1 = vertex1.getY();
        double z1 = vertex1.getZ();

        double x2 = vertex2.getX();
        double y2 = vertex2.getY();
        double z2 = vertex2.getZ();

        double x3 = vertex3.getX();
        double y3 = vertex3.getY();
        double z3 = vertex3.getZ();

        double A = y1*z2 + z1*y3 + y2*z3 - y3*z2 - y1*z3 - y2*z1;
        double B = -(x1*z2 + z1*x3 + x2*z3 - x3*z2 - x1*z3 - x2*z1);
        double C = x1*y2 + y1*x3 + x2*y3 - x3*y2 - x1*y3 - x2*y1;
        double D = -(x1*y2*z3 + y1*z2*x3 + z1*x2*y3 - x3*y2*z1 - y3*z2*x1 - z3*x2*y1);

        return -(A*x + B*y + D)/C;
    }
   
    public int guarand(double x, double y, int component){
        double x1 = transformedVertices[0].getX();
        double y1 = transformedVertices[0].getY();
        double z1 = colors[0].get(component);
        
        double x2 = transformedVertices[1].getX();
        double y2 = transformedVertices[1].getY();
        double z2 = colors[1].get(component);

        double x3 = transformedVertices[2].getX();
        double y3 = transformedVertices[2].getY();
        double z3 = colors[2].get(component);

        double A = y1*z2 + z1*y3 + y2*z3 - y3*z2 - y1*z3 - y2*z1;
        double B = -(x1*z2 + z1*x3 + x2*z3 - x3*z2 - x1*z3 - x2*z1);
        double C = x1*y2 + y1*x3 + x2*y3 - x3*y2 - x1*y3 - x2*y1;
        double D = -(x1*y2*z3 + y1*z2*x3 + z1*x2*y3 - x3*y2*z1 - y3*z2*x1 - z3*x2*y1);

        return Math.max(Math.min((int)(-(A*x + B*y + D)/C), 255), 0);
    }
    
    public Color guarand(double x, double y){
        int red = guarand(x, y, 0);
        int green = guarand(x, y, 1);
        int blue = guarand(x, y, 2);
        
        Color color = new Color();
        color.setRed(red);
        color.setGreen(green);
        color.setBlue(blue);
        
        return color;
    }
    
//    public Color phong(double x, double y){
//        Vector vertex = new Vector();
//        vertex.setX(x);
//        vertex.setY(y);
//        vertex.normal = this.interpolateN(x, y);
//        
//        return phong(vertex);
//    }
    
    private Color phong(Vector vertex){
        double kd = part.getKd();
        double ks = part.getKs();
        double g = part.getG();
        
        int red = part.getRed();
        int green = part.getGreen();
        int blue = part.getBlue();
        
        int ered = scene.light.getRed();
        int egreen = scene.light.getGreen();
        int eblue = scene.light.getBlue();
        
        Vector N = vertex.normal;
        
        Vector L = scene.light.getPosition().sub(vertex);
        L.normalize();
        
        Vector V = scene.camera.p0.sub(vertex);
        V.normalize();        
        
        Vector H = L.add(V);
        H.normalize();
        
        int phongred = phong(kd,ks,g,red,ered,N,L,H);
        int phonggreen = phong(kd,ks,g,green,egreen,N,L,H);
        int phongblue = phong(kd,ks,g,blue,eblue,N,L,H);
        
        Color color = new Color();
        color.setRed(phongred);
        color.setGreen(phonggreen);
        color.setBlue(phongblue);
        
        return color;
    }
    
    private int phong(double kd, double ks, double g, int c, int ec, Vector N, Vector L, Vector H){
        double NdotL = Math.max(N.dot(L), 0);
        double NdotH = Math.max(N.dot(H), 0);
        
        double pow = Math.pow(NdotH, g);
        
        double Id = kd*c*NdotL*ec/255.;
        double Is = ks*pow*ec*2.;
        
        double I = Id + Is;
        
        return Math.max(Math.min((int)I, 255), 0);
    }
    
//    private Vector interpolateN(double x, double y){
//        Vector vertex1 = this.transformedVertices[0];
//        Vector vertex2 = this.transformedVertices[1];
//        Vector vertex3 = this.transformedVertices[2];
//        
//        double x1 = vertex1.getX();
//        double y1 = vertex1.getY();
//        Vector N1 = get(0).normal;
//        
//        double x2 = vertex2.getX();
//        double y2 = vertex2.getY();
//        Vector N2 = get(1).normal;
//        
//        double x3 = vertex3.getX();
//        double y3 = vertex3.getY();
//        Vector N3 = get(2).normal;
//        
//        Vector A = N2.multiply(y1).add(N1.multiply(y3)).add(N3.multiply(y2)).sub(N2.multiply(y3)).sub(N3.multiply(y1)).sub(N1.multiply(y2));
//        Vector B = N2.multiply(x1).add(N1.multiply(x3)).add(N3.multiply(x2)).sub(N2.multiply(x3)).sub(N3.multiply(x1)).sub(N1.multiply(x2)).multiply(-1.);
//        double C = x1*y2 + y1*x3 + x2*y3 - x3*y2 - x1*y3 - x2*y1;
//        Vector D = N3.multiply(x1*y2).add(N2.multiply(y1*x3)).add(N1.multiply(x2*y3)).sub(N1.multiply(x3*y2)).sub(N2.multiply(y3*x1)).sub(N3.multiply(x2*y1)).multiply(-1.);
//        
//        return A.multiply(x).add(B.multiply(y)).add(D).multiply(-1./C);
//    }
    
    public void setColors(){
        for(int i=0;i<3;i++)
            colors[i] = phong(get(i));
        
       int x = (int)Math.round((get(0).getX() + get(1).getX() + get(2).getX())/3.);
       int y = (int)Math.round((get(0).getY() + get(1).getY() + get(2).getY())/3.);
       int z = (int)Math.round((get(0).getZ() + get(1).getZ() + get(2).getZ())/3.);
       
       Vector vertex = new Vector();
//       Vector vertex = (Vector) get(1).clone();
       vertex.setX(x);
       vertex.setY(y);
       vertex.setZ(z);
       vertex.normal = normal;
       vertex.normal.normalize();
       
       color = phong(vertex);
    }
    
    
}
