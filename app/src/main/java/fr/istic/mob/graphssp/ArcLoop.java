package fr.istic.mob.graphssp;

public class ArcLoop extends ArcFinal{
    public ArcLoop(Node n, String etiquette) {
        super(n, n, etiquette);
    }
}
