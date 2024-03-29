package fr.istic.mob.graphssp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.text.InputType;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @authors Arthur Poilane / Damien Salerno / Quentin Sarrazin
 */
@RequiresApi(api = Build.VERSION_CODES.M)
public class MainActivity extends AppCompatActivity {

    private static Graph firstGraph;
    private ImageView view;
    private static DrawableGraph graph;
    private Node affectedNode,startingNode, endNode, firstNode;
    private ArcFinal affectedArc;
    private float lastTouchDownX;
    private float lastTouchDownY;
    private boolean creationNodeMode = false, creationArcMode = false, editMode = true, movingMode = false;
    private boolean canMove=true, startedNode=false;
    private AlertDialog alertDialog;

    private String nameOfNode, nameOfArc;
    public static Context context;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.registerForContextMenu(this.findViewById(R.id.imgView));
        //initialisation du graphe par défaut
        if (firstGraph == null) {
            firstGraph = new Graph();
        }
        view = findViewById(R.id.imgView);
        if (graph == null) {
            graph = new DrawableGraph(firstGraph);
        }
        view.setImageDrawable(graph);

        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //à chaque "touch" on récupère les coordonnées de l'endroit touché
                lastTouchDownX = event.getX();
                lastTouchDownY = event.getY();
                //le mode déplacement est activé
                if (movingMode) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            if (!isOnNode()) {
                                canMove = false;
                            } else {
                                return false;
                            }
                            break;
                        case MotionEvent.ACTION_MOVE:
                            if (canMove) {
                                affectedNode.move(lastTouchDownX, lastTouchDownY);
                                updateView();
                            } else {
                                return false;
                            }
                            break;
                        case MotionEvent.ACTION_UP:
                            canMove = true;
                            break;
                        default:
                            return false;
                    }
                }
                //le mode création d'arcs est activé
                else if(creationArcMode){
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            if (isOnNode() && creationArcMode && !startedNode) {
                                startingNode = affectedNode;
                                firstNode = startingNode;
                                firstGraph.initArcTemp(lastTouchDownX, lastTouchDownY);
                                updateView();
                                startedNode = true;
                            }
                            break;
                        case MotionEvent.ACTION_MOVE:
                            if (firstGraph.getArcTemp() != null) {
                                firstGraph.setArcTemp(lastTouchDownX, lastTouchDownY);
                            }
                            updateView();
                            break;
                        case MotionEvent.ACTION_UP :
                                if(isOnNode() && startedNode) {
                                    endNode = affectedNode;
                                    final EditText input = new EditText(MainActivity.this);
                                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                                    alertDialogBuilder.setTitle(R.string.alertCreationArc);
                                    alertDialogBuilder.setMessage(R.string.alertCreationArcMessage).setPositiveButton(R.string.alertCreationNodeAdd, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            String label = input.getText().toString();

                                            if(firstNode == endNode) {
                                                ArcFinal newArc = new ArcLoop(startingNode, label);
                                                firstGraph.addArc(newArc);
                                            }else {
                                                ArcFinal newArc = new ArcFinal(startingNode, endNode, label);
                                                firstGraph.addArc(newArc);
                                            }
                                            if (label.length() > 0) {
                                                ArcFinal newArc = new ArcFinal(startingNode, endNode, "");
                                                firstGraph.addArc(newArc);
                                            }
                                            updateView();
                                        }
                                    });
                                    alertDialogBuilder.setView(input);
                                    alertDialog = alertDialogBuilder.create();
                                    alertDialog.show();
                                    startedNode = false;
                                    firstGraph.removeArcTemp();
                                    updateView();
                                } else {
                                    startingNode = null;
                                    startedNode = false;
                                    firstGraph.removeArcTemp();
                                    updateView();
                                }
                            break;
                        default:
                            break;
                    }
                }
                return false;
            }
        });

        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (isOnNode()) return false;
                //le mode création de noeuds est activé
                if (creationNodeMode) {
                    final EditText input = new EditText(MainActivity.this);
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                    alertDialogBuilder.setTitle(R.string.alertCreationNode);
                    alertDialogBuilder.setMessage(R.string.alertCreationNodeMessage).setPositiveButton(R.string.alertCreationNodeAdd, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            String label = input.getText().toString();
                            Node node = new Node(lastTouchDownX, lastTouchDownY, (float) 40, Color.BLACK, label);
                            firstGraph.addNode(node);
                            updateView();
                        }
                    });
                    alertDialogBuilder.setView(input);
                    alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                    return true;
                }
                return false;
            }
        });



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate( R.menu.main_menu, menu);
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.reset:
                Toast.makeText( this, this.getText(R.string.reset_toast), Toast.LENGTH_LONG).show();
                firstGraph = new Graph();
                updateView();
                return true;
            case R.id.addNode:
                Toast.makeText( this, this.getText(R.string.add_node_toast), Toast.LENGTH_LONG).show();
                creationNodeMode=true;
                creationArcMode=false;
                movingMode =false;
                editMode=false;
                updateView();
                return true;
            case R.id.addPath:
                Toast.makeText( this, this.getText(R.string.add_path_toast), Toast.LENGTH_LONG).show();
                creationNodeMode=false;
                creationArcMode=true;
                movingMode =false;
                editMode=false;
                updateView();
                return true;
            case R.id.move:
                Toast.makeText( this, this.getText(R.string.move_node_toast), Toast.LENGTH_LONG).show();
                creationNodeMode=false;
                creationArcMode=true;
                movingMode =true;
                editMode=false;
                updateView();
                return true;
            case R.id.edit:
                Toast.makeText( this, this.getText(R.string.edit_toast), Toast.LENGTH_LONG).show();
                creationNodeMode=false;
                creationArcMode=false;
                movingMode =false;
                editMode=true;
                updateView();
                return true;
            case R.id.save_graph:
                Toast.makeText( this, this.getText(R.string.save_graph), Toast.LENGTH_LONG).show();
                firstGraph.saveArrayList(firstGraph.getNodes(),"node");
                firstGraph.saveArrayList2(firstGraph.getArcs(),"arc");
                return true;
            case R.id.list_graph:
                Toast.makeText( this, this.getText(R.string.list_graph), Toast.LENGTH_LONG).show();

                updateView();
                return true;
            case R.id.sendMail:
                Toast.makeText( this, this.getText(R.string.sendMail), Toast.LENGTH_LONG).show();
                //Lors de l'envoie de mail il nécessaire de sauvegarder le graphe à l'intérieur de la mémoire du portable. On doit donc demander les autorisations à l'utilisateur.
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                {
                    if(shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE) == true)
                    {
                        explain();
                    }
                    else
                    {
                        askForPermission();
                    }
                }
                else
                {
                    try {
                        sendEmail(view);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        //création des menu contextuels dans le mode édition
        if(editMode) {
            if (this.isOnNode()) {
                super.onCreateContextMenu(menu, v, menuInfo);
                getMenuInflater().inflate(R.menu.node_menu, menu);
                nameOfNode = firstGraph.checkNode(lastTouchDownX,lastTouchDownY).getLabel();
                menu.setHeaderTitle("Edit Node : " + nameOfNode);
            } else if (this.isOnArc()){
                super.onCreateContextMenu(menu, v, menuInfo);
                getMenuInflater().inflate(R.menu.arc_menu, menu);
                nameOfArc = firstGraph.getArc(lastTouchDownX,lastTouchDownY).getLabel();
                menu.setHeaderTitle("Edit Node : " + nameOfArc);
            }
        }
    }


    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_node:
                Toast.makeText(this, this.getText(R.string.delete_node), Toast.LENGTH_LONG).show();
                firstGraph.removeNode(firstGraph.checkNode(lastTouchDownX, lastTouchDownY));
                this.updateView();
                return true;

            case R.id.edit_label__node:
                Toast.makeText(this, this.getText(R.string.edit_label__node), Toast.LENGTH_LONG).show();
                final EditText changeLabel = new EditText(MainActivity.this);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                alertDialogBuilder.setTitle(R.string.alertEditLabelNode);
                alertDialogBuilder
                        .setMessage(R.string.alertEditLabelNodeMessage)
                        .setPositiveButton(R.string.alertEditLabelNodeChange,new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                String label = changeLabel.getText().toString();

                                if(label.length()>0){
                                    firstGraph.changeNodeLabel(label,firstGraph.checkNode(lastTouchDownX, lastTouchDownY));
                                    updateView();
                                }
                            }
                        });
                alertDialogBuilder.setView(changeLabel);
                alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                return true;

            case R.id.edit_size_node:
                Toast.makeText(this, this.getText(R.string.edit_size_node), Toast.LENGTH_LONG).show();

                final EditText inputTaille = new EditText(this);
                inputTaille.setInputType(InputType.TYPE_CLASS_NUMBER);
                AlertDialog.Builder alertDialogBuilderTaille = new AlertDialog.Builder(
                        this);
                alertDialogBuilderTaille.setTitle(R.string.alertEditSizeNode);
                alertDialogBuilderTaille
                        .setPositiveButton(R.string.alertEditSizeNodeEdit,new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                String value = inputTaille.getText().toString();
                                if(value.length()==2){
                                    affectedNode.setRayon(Float.valueOf(value));
                                    updateView();
                                    inputTaille.setText("");
                                }
                            }
                        });
                alertDialogBuilderTaille.setView(inputTaille);
                alertDialog = alertDialogBuilderTaille.create();
                alertDialog.show();
                return true;

            case R.id.edit_color_node:
                Toast.makeText(this, this.getText(R.string.edit_color_node), Toast.LENGTH_LONG).show();
                return true;

            case R.id.edit_color_red:
                firstGraph.changeNodeColor(Color.RED,firstGraph.checkNode(lastTouchDownX, lastTouchDownY));
                this.updateView();
                return true;

            case R.id.edit_color_green:
                firstGraph.changeNodeColor(0xFF32CD32,firstGraph.checkNode(lastTouchDownX, lastTouchDownY));
                this.updateView();
                return true;

            case R.id.edit_color_blue:
                firstGraph.changeNodeColor(Color.BLUE,firstGraph.checkNode(lastTouchDownX, lastTouchDownY));
                this.updateView();
                return true;

            case R.id.edit_color_orange:
                firstGraph.changeNodeColor(0xFFFF8C00,firstGraph.checkNode(lastTouchDownX, lastTouchDownY));
                this.updateView();
                return true;

            case R.id.edit_color_cyan:
                firstGraph.changeNodeColor(0xFF00CED1,firstGraph.checkNode(lastTouchDownX, lastTouchDownY));
                this.updateView();
                return true;

            case R.id.edit_color_magenta:
                firstGraph.changeNodeColor(Color.MAGENTA,firstGraph.checkNode(lastTouchDownX, lastTouchDownY));
                this.updateView();
                return true;

            case R.id.edit_color_black:
                firstGraph.changeNodeColor(Color.BLACK,firstGraph.checkNode(lastTouchDownX, lastTouchDownY));
                this.updateView();
                return true;

            case R.id.delete_arc:
                Toast.makeText(this, this.getText(R.string.delete_arc), Toast.LENGTH_LONG).show();
                firstGraph.removeArc(firstGraph.getArc(lastTouchDownX,lastTouchDownY));
                this.updateView();
                return true;

            case R.id.edit_label__arc:
                Toast.makeText(this, this.getText(R.string.edit_label__arc), Toast.LENGTH_LONG).show();
                final EditText changeArcLabel = new EditText(MainActivity.this);
                AlertDialog.Builder alertArcDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                alertArcDialogBuilder.setTitle(R.string.alertEditLabelArc);
                alertArcDialogBuilder
                        .setMessage(R.string.alertEditLabelArcMessage)
                        .setPositiveButton(R.string.alertEditLabelArcChange,new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                String label = changeArcLabel.getText().toString();
                                firstGraph.changeArcLabel(label,firstGraph.getArc(lastTouchDownX, lastTouchDownY));
                                updateView();
                            }
                        });
                alertArcDialogBuilder.setView(changeArcLabel);
                alertDialog = alertArcDialogBuilder.create();
                alertDialog.show();
                return true;

            case R.id.edit_size_arc:
                Toast.makeText(this, this.getText(R.string.edit_size_arc), Toast.LENGTH_LONG).show();
                final EditText inputTailleArc = new EditText(this);
                inputTailleArc.setInputType(InputType.TYPE_CLASS_NUMBER);
                AlertDialog.Builder alertDialogBuilderArcTaille = new AlertDialog.Builder(
                        this);
                alertDialogBuilderArcTaille.setTitle(R.string.alertEditSizeArc);
                alertDialogBuilderArcTaille
                        .setPositiveButton(R.string.alertEditSizeArcEdit,new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                // if this button is clicked, close
                                // current activity
                                String value = inputTailleArc.getText().toString();
                                if(value.length()==2){
                                    affectedArc.setWidth(Integer.valueOf(value));
                                    updateView();
                                    inputTailleArc.setText("");
                                }
                            }
                        });
                alertDialogBuilderArcTaille.setView(inputTailleArc);
                alertDialog = alertDialogBuilderArcTaille.create();
                alertDialog.show();
                return true;

            case R.id.edit_color_arc:
                Toast.makeText(this, this.getText(R.string.edit_color_arc), Toast.LENGTH_LONG).show();
                return true;

            case R.id.edit_color_arc_red:
                firstGraph.changeArcColor(Color.RED,firstGraph.getArc(lastTouchDownX, lastTouchDownY));
                this.updateView();
                return true;

            case R.id.edit_color_arc_green:
                firstGraph.changeArcColor(0xFF32CD32,firstGraph.getArc(lastTouchDownX, lastTouchDownY));
                this.updateView();
                return true;

            case R.id.edit_color_arc_blue:
                firstGraph.changeArcColor(Color.BLUE,firstGraph.getArc(lastTouchDownX, lastTouchDownY));
                this.updateView();
                return true;

            case R.id.edit_color_arc_orange:
                firstGraph.changeArcColor(0xFFFF8C00,firstGraph.getArc(lastTouchDownX, lastTouchDownY));
                this.updateView();
                return true;

            case R.id.edit_color_arc_cyan:
                firstGraph.changeArcColor(0xFF00CED1,firstGraph.getArc(lastTouchDownX, lastTouchDownY));
                this.updateView();
                return true;

            case R.id.edit_color_arc_magenta:
                firstGraph.changeArcColor(Color.MAGENTA,firstGraph.getArc(lastTouchDownX, lastTouchDownY));
                this.updateView();
                return true;

            case R.id.edit_color_arc_black:
                firstGraph.changeArcColor(Color.BLACK,firstGraph.getArc(lastTouchDownX, lastTouchDownY));
                this.updateView();
                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }

    /**
     * redessine le graphe après chaque modification
     */
    private void updateView(){
        graph = new DrawableGraph(firstGraph);
        view.setImageDrawable(graph);
    }

    /**
     *
     * @return true si lernier click est sur un noeud
     */
    public boolean isOnNode(){
        affectedNode = firstGraph.checkNode(lastTouchDownX,lastTouchDownY);
        return affectedNode != null;
    }

    /**
     *
     * @return true si le dernier click est sur un arc
     */
    public boolean isOnArc() {
        affectedArc = firstGraph.getArc(lastTouchDownX,lastTouchDownY);
        return affectedArc != null;
    }

    // On compresse l'imageview sous forme de bitmap pour obtenir un png par la suite
    public void sendEmail(View view) throws IOException {
        Bitmap bitmap;
        Drawable drawable = graph.getCurrent();
        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        File file = new File(Environment.getExternalStorageDirectory(), "graph.png");
        FileOutputStream fOut = new FileOutputStream(file);
        bitmap.compress(Bitmap.CompressFormat.PNG, 85, fOut);
        fOut.flush();
        fOut.close();
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("image/*");
        i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"qsarrazin35@gmail.com"});
        i.putExtra(Intent.EXTRA_SUBJECT, "Mon Graphe");
        i.putExtra(Intent.EXTRA_STREAM,Uri.parse("content://"+Environment.getExternalStorageDirectory() +"graph.png"));
        try {
            startActivity(Intent.createChooser(i, "Send mail..."));
        } catch (ActivityNotFoundException ex) {
            Toast.makeText(MainActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }


    //Les quatres prochaines fonctions sont les fonctions nécessaire pour la demande de permission.
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void askForPermission()
    {
        requestPermissions(new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE }, 2);
    }

    private void explain()
    {
        Snackbar.make(view, "Cette permission est nécessaire pour envoyer des mails", Snackbar.LENGTH_LONG).setAction("Activer", new View.OnClickListener()
        {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view)
            {
                askForPermission();            }
        }).show();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        if(requestCode == 2)
        {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                try {
                    sendEmail(view);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else if(shouldShowRequestPermissionRationale(permissions[0]) == false)
            {
                displayOptions();
            }
            else
            {
                explain();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void displayOptions()
    {
        Snackbar.make(view, "Vous avez désactivé la permission", Snackbar.LENGTH_LONG).setAction("Paramètres", new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                final Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                final Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            }
        }).show();
    }

}
