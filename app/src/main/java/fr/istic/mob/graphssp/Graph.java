package fr.istic.mob.graphssp;


import android.content.res.Resources;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Iterator;


public class Graph {

    private ArrayList<Node> nodes;
    private ArrayList<ArcFinal> arcs;
    private ArcTemp arcTemp;
    private float[] dimensions;


    public Graph(float[] dimensions) {
        this.dimensions = dimensions;
        nodes = new ArrayList<Node>();
        arcs = new ArrayList<ArcFinal>();

        float width = this.getDimensions()[0];
        float height= this.getDimensions()[1];

        Log.d("TEST1", Float.toString(this.getDimensions()[0]));
        float div = (float) 1.2;
/*
        nodes.add(new Node(100,100,100,Color.BLACK,"00"));

        nodes.add(new Node(height - 100,width - 100,100,Color.BLACK,"01"));
        nodes.add(new Node(height - 100,100,100,Color.BLACK,"02"));
        nodes.add(new Node(100,width - 100,100,Color.BLACK,"03"));
        nodes.add(new Node(100,height - 100,100,Color.BLACK,"04"));
        nodes.add(new Node(width -100, 100,100,Color.BLACK,"05"));
*/


        nodes.add(new Node(width/2,height/2,(width+height)/60, Color.BLACK,"5"));
        Log.d("TEST",Float.toString(width/2));
        Log.d("TEST",Float.toString(width));
        nodes.add(new Node((float)width/2,(float)height/8,(float)(width+height)/60, Color.BLACK,"2"));
        nodes.add(new Node((float)width/2,(float)height/div,(float)(width+height)/60, Color.BLACK,"8"));
        nodes.add(new Node((float)width/div,(float)height/2,(float)(width+height)/60, Color.BLACK,"6"));
        nodes.add(new Node((float)width/div,(float)height/8,(float)(width+height)/60, Color.BLACK,"3"));
        nodes.add(new Node((float)width/div,(float)height/div,(float)(width+height)/60, Color.BLACK,"9"));
        nodes.add(new Node((float)width/8,(float)height/2,(float)(width+height)/60, Color.BLACK,"4"));
        nodes.add(new Node((float)width/8,(float)height/8,(float)(width+height)/60, Color.BLACK,"1"));
        nodes.add(new Node((float)width/8,(float)height/div,(float)(width+height)/60, Color.BLACK,"7"));



        arcs.add(new ArcFinal(nodes.get(3), nodes.get(2),"TestArc"));
    }

    public void rotateGraph(){

        ArrayList<Node> oldNodes = nodes;
        ArrayList<Node> nodesTemp = new ArrayList<Node>();

        ArrayList<ArcFinal> oldArcs = arcs;
        ArrayList<ArcFinal> arcsTemp = new ArrayList<ArcFinal>();

        ArcFinal a;
        Node n;



    }


    public ArrayList<Node> getNodes(){
        return nodes;
    }

    public void addNode(Node n){
        nodes.add(n);
    }

    public Node checkNode(float x, float y) {
        for (Node n : nodes) {
            if (n.contains(x, y)) {
                return n;
            }
        }
        return null;
    }

    public void removeNode(Node n) {
        nodes.remove(n);

        Iterator<ArcFinal> it= arcs.iterator();
        while (it.hasNext()){
            ArcFinal arc = it.next();
            if(arc.getNodeDest()==n || arc.getNodeOrigine()==n){
                arcs.remove(arc);
            }
        }
    }

    public void changeNodeColor(int color, Node n){
        n.setColor(color);
    }

    public void changeNodeLabel(String label, Node n){
        n.setLabel(label);
    }

    public ArcTemp getArcTemp(){
        return arcTemp;
    }

    public void setArcTemp(float x, float y) {
        arcTemp.setNodeX(x);
        arcTemp.setNodeY(y);
    }

    public void initArcTemp(float x, float y) {
        arcTemp = new ArcTemp(checkNode(x, y));
    }

    public ArrayList<ArcFinal> getArcs(){return arcs; }

    public void addArc(ArcFinal a) {
        arcs.add(a);
    }

    public void removeArc(ArcFinal a) {
        arcs.remove(a);
    }

    public void removeArcTemp() {
        arcTemp=null;
    }

    public void setDimensions(float[] dimensions){
        this.dimensions = dimensions;
    }
    public float[] getDimensions(){
        return dimensions;
    }

    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }


}