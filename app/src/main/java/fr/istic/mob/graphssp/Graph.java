package fr.istic.mob.graphssp;


import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.RectF;

import android.os.Build;
import android.preference.PreferenceManager;


import androidx.annotation.RequiresApi;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * @authors Arthur Poilane / Damien Salerno / Quentin Sarrazin
 */
public class Graph  {

    private ArrayList<Node> nodes;
    private ArrayList<ArcFinal> arcs;
    private ArcTemp arcTemp;

    public Graph() {
        nodes = new ArrayList<>();
        arcs = new ArrayList<>();


        int height = getScreenHeight();
        int width = getScreenWidth();
        float div = (float) 1.2;

        //on ajoute les noeuds par défaut
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


    //Ces quatres fonctions ont pour but de sauvegarder les arraylist.
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void saveArrayList(ArrayList<Node> list, String key){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.context);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString(key, json);
        editor.apply();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void saveArrayList2(ArrayList<ArcFinal> list, String key){
        SharedPreferences prefs2 = PreferenceManager.getDefaultSharedPreferences(MainActivity.context);
        SharedPreferences.Editor editor = prefs2.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString(key, json);
        editor.apply();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public ArrayList<Node> getArrayList(String key){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.context);
        Gson gson = new Gson();
        String json = prefs.getString(key, null);
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        return gson.fromJson(json, type);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public ArrayList<ArcFinal> getArrayList2(String key){
        SharedPreferences prefs2 = PreferenceManager.getDefaultSharedPreferences(MainActivity.context);
        Gson gson = new Gson();
        String json = prefs2.getString(key, null);
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        return gson.fromJson(json, type);
    }

}