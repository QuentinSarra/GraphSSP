package fr.istic.mob.graphssp;

import android.graphics.RectF;

public class Node extends RectF{


    private float x, y;
    private float rayon;
    private int color;
    private String label;


    public Node( float x, float y, float rayon, int color, String label){
        super(x - rayon, y - rayon, x + rayon, y + rayon);
        this.x = x;
        this.y = y;
        this.rayon = rayon;
        this.color = color;
        this.label = label;
    }

    public void move(float x, float y)
    {
        this.x = x;
        this.y = y;
        super.set(x - rayon, y - rayon, x + rayon, y + rayon);
    }

    public int getColor(){
        return this.color;
    }

    public void setColor(int color){
        this.color = color;
    }

    public String getLabel(){
        return this.label;
    }

    public void setLabel(String label){
        this.label = label;
    }

    public float getRayon(){
        return this.rayon;
    }

    public float getX(){
        return this.x;
    }

    public float getY(){
        return this.y;
    }

    public void setAgRayon (float agRayon){
        super.set(x-agRayon,y-rayon,x+agRayon,y+rayon);
    }

    public void setRayon (float rayon){
        this.rayon = rayon;
        super.set(x - rayon, y - rayon, x + rayon, y + rayon);
    }
}
