import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Locale;
import java.util.Scanner;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author auswise
 */
public class Scene {
    public static final Scene INSTANCE = new Scene();
    
    private boolean initialized;
    
    public Camera camera;
    public Vector[] vertices;
    public Triangle[] triangles;
    public Part[] parts;
    public Light light;
    
    public Vector leftTop;
    public Vector rightTop;
    public Vector leftBottom;
    public Vector rightBottom;
    
    private Scene(){
        leftTop = new Vector();
        rightTop = new Vector();
        leftBottom = new Vector();
        rightBottom = new Vector();
        
        
        this.setInitialized(false);
    }
    
    public boolean isInitialized(){
        return initialized;
    }
    
    public void setInitialized(boolean initialized){
        this.initialized = initialized;
    }
    
    public void setInitialized(){
        this.initialized = true;
    }
    
    public void load(File file) throws FileNotFoundException{
        this.loadScene(file);
        
        File cameraFile = new File(file.getAbsolutePath().replace(".brp", ".cam"));
        this.loadCamera(cameraFile);
        
        this.setInitialized();
    }
    
    private void loadScene(File file) throws FileNotFoundException{
        Scanner fileScanner = new Scanner(file).useLocale(Locale.US);
        Scanner lineScanner;
        
        int n = fileScanner.nextInt();
        System.out.println(n);
        vertices = new Vector[n];
        fileScanner.nextLine();
        for(int i=0;i<n;i++){
            vertices[i] = new Vector();
            String s = fileScanner.nextLine();
//            System.out.println(s);
            lineScanner = new Scanner(s).useLocale(Locale.US);
            vertices[i].set(0, lineScanner.nextDouble());
            vertices[i].set(1, lineScanner.nextDouble());
            vertices[i].set(2, lineScanner.nextDouble());
        }
        
        int m = fileScanner.nextInt();
        triangles = new Triangle[m];
        fileScanner.nextLine();
        for(int i=0;i<m;i++){
            triangles[i] = new Triangle();
            lineScanner = new Scanner(fileScanner.nextLine());
            triangles[i].vertexIndices[0] = lineScanner.nextInt(); 
            triangles[i].vertexIndices[1] = lineScanner.nextInt(); 
            triangles[i].vertexIndices[2] = lineScanner.nextInt(); 
            
            triangles[i].set(0, vertices[triangles[i].vertexIndices[0]]);
            triangles[i].set(1, vertices[triangles[i].vertexIndices[1]]);
            triangles[i].set(2, vertices[triangles[i].vertexIndices[2]]);
        }
        
        parts = new Part[m];
        int[] partIndexes = new int[m];
        for(int i=0;i<m;i++)
            partIndexes[i] = fileScanner.nextInt();
        
        int k = fileScanner.nextInt();
        parts = new Part[k];
        fileScanner.nextLine();
        for(int i=0;i<k;i++){
            parts[i] = new Part(i);
            lineScanner = new Scanner(fileScanner.nextLine()).useLocale(Locale.US);
            parts[i].setRed(lineScanner.nextInt());
            parts[i].setGreen(lineScanner.nextInt());
            parts[i].setBlue(lineScanner.nextInt());
            parts[i].setKd(lineScanner.nextDouble());
            parts[i].setKs(lineScanner.nextDouble());
            parts[i].setG(lineScanner.nextDouble());
        }
        
        for(int i=0;i<m;i++)
            triangles[i].part = parts[partIndexes[i]];

        
        light = new Light();
        light.setX(fileScanner.nextDouble());
        light.setY(fileScanner.nextDouble());
        light.setZ(fileScanner.nextDouble());
        light.setRed(fileScanner.nextInt());
        light.setGreen(fileScanner.nextInt());
        light.setBlue(fileScanner.nextInt());
    }
    
    private void loadCamera(File file) throws FileNotFoundException{
        Scanner fileScanner = new Scanner(file).useLocale(Locale.US);
        Scanner lineScanner;

        camera = new Camera();
        camera.p0 = new Vector();
        camera.pt = new Vector();
        
        lineScanner = new Scanner(fileScanner.nextLine()).useLocale(Locale.US);
        camera.p0.set(0,lineScanner.nextDouble());
        camera.p0.set(1,lineScanner.nextDouble());
        camera.p0.set(2,lineScanner.nextDouble());

        lineScanner = new Scanner(fileScanner.nextLine()).useLocale(Locale.US);
        camera.pt.set(0,lineScanner.nextDouble());
        camera.pt.set(1,lineScanner.nextDouble());
        camera.pt.set(2,lineScanner.nextDouble());

        camera.a = fileScanner.nextDouble();
    }
    
    public void save(File file) throws IOException{
        saveScene(file);
        
        File cameraFile = new File(file.getAbsolutePath().replace(".brp", ".cam"));
        saveCamera(cameraFile);
    }
    
    public void saveScene(File file) throws IOException{
        PrintWriter writer = new PrintWriter(new FileWriter(file));
        
        writer.println(this.vertices.length);
        for(Vector vertex: vertices)
            writer.println(vertex);
        
        writer.println(this.triangles.length);
        for(Triangle triangle: triangles)
            writer.println(triangle);
        
        for(Triangle triangle: triangles)
            writer.println(triangle.part.getIndex());
        
        writer.println(parts.length);
        for(Part part: parts)
            writer.println(part);
        
        writer.println(light);
        
        writer.flush();
        
    }
    
    public void saveCamera(File file) throws IOException{
        PrintWriter writer = new PrintWriter(new FileWriter(file));
        
        writer.println(camera.p0);
        writer.println(camera.pt);
        writer.println(camera.a);
        
        writer.flush();
    }
    
    public void setNormals(){
        for(Vector vertex: vertices){
            vertex.normal = new Vector();
        }
        
        for(Triangle triangle: triangles){
            triangle.normal = triangle.get(2).sub(triangle.get(1)).cross(triangle.get(0).sub(triangle.get(1)));
            for(int i=0;i<3;i++){
                triangle.get(i).normal = triangle.get(i).normal.add(triangle.normal);
                
            }
            
        }
        
        for(Vector vertex: vertices){
            vertex.normal.normalize();
        }
    }
    
    public void setColors(){
        for(Triangle triangle: triangles)
            triangle.setColors();
    }    
}
