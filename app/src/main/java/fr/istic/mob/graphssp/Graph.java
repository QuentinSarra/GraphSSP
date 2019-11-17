package fr.istic.mob.graphssp;


import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.RectF;

import java.util.ArrayList;
import java.util.Iterator;


public class Graph {

    private ArrayList<Node> nodes;
    private ArrayList<ArcFinal> arcs;
    private ArrayList<ArcLoop> loops;
    private ArrayList<Arc> allArcs;
    private ArcTemp arcTemp;


    public Graph() {
        nodes = new ArrayList<Node>();
        arcs = new ArrayList<ArcFinal>();
        allArcs = new ArrayList<Arc>();

        int height = getScreenHeight();
        int width = getScreenWidth();
        float div = (float) 1.2;

        nodes.add(new Node((float)width/2,(float)height/2,(float)(width+height)/60, Color.BLACK,"5"));
        nodes.add(new Node((float)width/2,(float)height/8,(float)(width+height)/60, Color.BLACK,"2"));
        nodes.add(new Node((float)width/2,(float)height/div,(float)(width+height)/60, Color.BLACK,"8"));
        nodes.add(new Node((float)width/div,(float)height/2,(float)(width+height)/60, Color.BLACK,"6"));
        nodes.add(new Node((float)width/div,(float)height/8,(float)(width+height)/60, Color.BLACK,"3"));
        nodes.add(new Node((float)width/div,(float)height/div,(float)(width+height)/60, Color.BLACK,"9"));
        nodes.add(new Node((float)width/8,(float)height/2,(float)(width+height)/60, Color.BLACK,"4"));
        nodes.add(new Node((float)width/8,(float)height/8,(float)(width+height)/60, Color.BLACK,"1"));
        nodes.add(new Node((float)width/8,(float)height/div,(float)(width+height)/60, Color.BLACK,"7"));

        arcs.add(new ArcLoop(nodes.get(0), "coucou"));
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

    public void changeArcColor(int color, ArcFinal a){
        a.setColor(color);
    }

    public void changeArcLabel(String label, ArcFinal a){
        a.setLabel(label);
    }

    public void removeArcTemp() {
        arcTemp=null;
    }

    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    public ArcFinal getArc(float x, float y){
        RectF r;
        for (ArcFinal a : arcs){
            float xMil = a.getMiddlePoint()[0];
            float yMil = a.getMiddlePoint()[1];

            r = new RectF(xMil-50,yMil-50,xMil+50,yMil+50);
            if(r.contains(x,y)){
                return a;
            }
        }
        return null;
    }

    public ArrayList<ArcLoop> getArcsLoop (){return loops;};


}