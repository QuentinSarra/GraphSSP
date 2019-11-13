 package fr.istic.mob.graphssp;


 public class Arc {
     private Node noeudOrigine;

     public Arc(Node noeudOrigine) {
         this.noeudOrigine = noeudOrigine;
     }


     public Node getNodeOrigine() {
         return noeudOrigine;
     }
 }