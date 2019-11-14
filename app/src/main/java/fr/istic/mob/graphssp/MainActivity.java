package fr.istic.mob.graphssp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
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
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static Graph firstGraph;
    private ImageView view;
    private static DrawableGraph graph;
    private Node affectedNode,startingNode, endNode;
    private ArcFinal affectedArc;
    private float lastTouchDownX;
    private float lastTouchDownY;
    private boolean creationNodeMode = false, creationArcMode = false, editMode = true, movingMode = false;
    private boolean canMove=true, startedNode=false;
    private AlertDialog alertDialog;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.registerForContextMenu(this.findViewById(R.id.imgView));
        if (firstGraph == null) {
            firstGraph = new Graph();
        }
        view = findViewById(R.id.imgView);
        if (graph == null) {
            graph = new DrawableGraph(firstGraph);
        }
        view.setImageDrawable(graph);

        view.setOnTouchListener(new View.OnTouchListener() {
            Node startingNode;
            Node nodeDest;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                lastTouchDownX = event.getX();
                lastTouchDownY = event.getY();
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
                else if(creationArcMode){
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            if (isOnNode() && creationArcMode && !startedNode) {
                                startingNode = affectedNode;
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
                                    nodeDest = affectedNode;
                                    final EditText input = new EditText(MainActivity.this);
                                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                                    alertDialogBuilder.setTitle("Create a  new Arc");
                                    alertDialogBuilder.setMessage("Enter the Arc label").setPositiveButton("Add", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            String label = input.getText().toString();
                                            ArcFinal newArc = new ArcFinal(startingNode,nodeDest,label);
                                            if (label.length() > 0) {
                                                firstGraph.addArc(newArc);
                                                updateView();
                                            }
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
                if (creationNodeMode) {
                    final EditText input = new EditText(MainActivity.this);
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                    alertDialogBuilder.setTitle(R.string.alertCreationNode);
                    alertDialogBuilder.setMessage(R.string.alertCreationNodeMessage).setPositiveButton(R.string.alertCreationNodeAdd, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            String label = input.getText().toString();
                            Node node = new Node(lastTouchDownX, lastTouchDownY, (float) 40, Color.BLACK, label);
                            if (label.length() > 0) {
                                firstGraph.addNode(node);
                                updateView();
                            }
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if(editMode) {
            if (this.isOnNode()) {
                super.onCreateContextMenu(menu, v, menuInfo);
                getMenuInflater().inflate(R.menu.node_menu, menu);
                menu.setHeaderTitle("Edit Node");
            } else if (this.isOnArc()){
                super.onCreateContextMenu(menu, v, menuInfo);
                getMenuInflater().inflate(R.menu.arc_menu, menu);
                menu.setHeaderTitle("Edit Path");
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
                                // if this button is clicked, close
                                // current activity
                                String value = inputTaille.getText().toString();
                                if(value.length()>0 && value != null){
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
                firstGraph.changeNodeColor(Color.GREEN,firstGraph.checkNode(lastTouchDownX, lastTouchDownY));
                this.updateView();
                return true;

            case R.id.edit_color_blue:
                firstGraph.changeNodeColor(Color.BLUE,firstGraph.checkNode(lastTouchDownX, lastTouchDownY));
                this.updateView();
                return true;

            case R.id.edit_color_orange:
                firstGraph.changeNodeColor(Color.YELLOW,firstGraph.checkNode(lastTouchDownX, lastTouchDownY));
                this.updateView();
                return true;

            case R.id.edit_color_cyan:
                firstGraph.changeNodeColor(Color.CYAN,firstGraph.checkNode(lastTouchDownX, lastTouchDownY));
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

                                if(label.length()>0){
                                    firstGraph.changeArcLabel(label,firstGraph.getArc(lastTouchDownX, lastTouchDownY));
                                    updateView();
                                }
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
                                if(value.length()>0 && value != null){
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
                firstGraph.changeArcColor(Color.GREEN,firstGraph.getArc(lastTouchDownX, lastTouchDownY));
                this.updateView();
                return true;

            case R.id.edit_color_arc_blue:
                firstGraph.changeArcColor(Color.BLUE,firstGraph.getArc(lastTouchDownX, lastTouchDownY));
                this.updateView();
                return true;

            case R.id.edit_color_arc_orange:
                firstGraph.changeArcColor(Color.YELLOW,firstGraph.getArc(lastTouchDownX, lastTouchDownY));
                this.updateView();
                return true;

            case R.id.edit_color_arc_cyan:
                firstGraph.changeArcColor(Color.CYAN,firstGraph.getArc(lastTouchDownX, lastTouchDownY));
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

    private void updateView(){
        graph = new DrawableGraph(firstGraph);
        view.setImageDrawable(graph);
    }

    public boolean isOnNode(){
        affectedNode = firstGraph.checkNode(lastTouchDownX,lastTouchDownY);
        return affectedNode != null;
    }

    public boolean isOnArc() {
        affectedArc = firstGraph.getArc(lastTouchDownX,lastTouchDownY);
        return affectedArc != null;
    }
}