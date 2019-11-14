package fr.istic.mob.graphssp;

import android.graphics.Color;

public class ArcFinal extends Arc {

    private Node n2;
    private String label;
    public int largeurLabel;
    private int color;
    private int width;
    private float[] middlePoint;
    private float[] tangent;
    public boolean hasBeenModified = false;

    public ArcFinal(Node NoeudOrigine, Node NoeudDestination, String label) {
        super(NoeudOrigine);
        this.n2 = NoeudDestination;
        this.color = Color.BLACK;
        this.width = 5;
        this.largeurLabel = 35;
        this.label = label;
    }

    public Node getNodeDest() {
        return this.n2;
    }
    public String getLabel() {
        return this.label;
    }
    public float[] getMiddlePoint(){
        return this.middlePoint;
    }
    public int getWidth(){
        return this.width;
    }
    public void setWidth(int newWidth){
        this.width = newWidth;
    }
    public void setLabel(String newLabel){
        this.label = newLabel;
    }
    public int getColor() {
        return this.color;
    }
    public void setColor(int newColor){
        this.color = newColor;
    }
    public void setTangent(float[] newTangent){
        this.tangent = newTangent;
    }
    public float[] getTangent(){
        return this.tangent;
    }
    public void setMiddlePoint(float[] newMiddlePoint){
        this.middlePoint = newMiddlePoint;
    }
    public void setMidPointCourb(float[] midPoint){
        hasBeenModified = true;
        this.setMiddlePoint(midPoint);
    }
    public int getLargeurLabel(){
        return largeurLabel;
    }

}