package fr.istic.mob.graphssp;

/**
 * @authors Arthur Poilane / Damien Salerno / Quentin Sarrazin
 */
public class ArcLoop extends ArcFinal{
    public ArcLoop(Node n, String etiquette) {
        super(n, n, etiquette);
    }
}
