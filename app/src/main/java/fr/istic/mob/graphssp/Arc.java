 package fr.istic.mob.graphssp;

 /**
  * @authors Arthur Poilane / Damien Salerno / Quentin Sarrazin
  */
 public class Arc {
     private Node noeudOrigine;

     public Arc(Node noeudOrigine) {
         this.noeudOrigine = noeudOrigine;
     }


     public Node getNodeOrigine() {
         return noeudOrigine;
     }
 }