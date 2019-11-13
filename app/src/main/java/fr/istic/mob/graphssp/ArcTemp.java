package fr.istic.mob.graphssp;

public class ArcTemp extends Arc{

    private float nodeX,nodeY;

    public ArcTemp(Node NoeudOrigine){
        super(NoeudOrigine);
        nodeX = NoeudOrigine.getX();
        nodeY = NoeudOrigine.getY();
    }
    public float getNodeX(){
        return this.nodeX;
    }
    public float getNodeY(){
        return this.nodeY;
    }
    public void setNodeX(float nodeX){
        this.nodeX = nodeX;
    }
    public void setNodeY(float nodeY){
        this.nodeY = nodeY;
    }
}
