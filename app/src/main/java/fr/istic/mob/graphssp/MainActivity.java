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
    private Node affectedNode;
    private float lastTouchDownX;
    private float lastTouchDownY;
    private boolean creationNodeMode = false, creationArcMode = false, editMode = true, isMoving = false;
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
            Node nodedeb;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                lastTouchDownX = event.getX();
                lastTouchDownY = event.getY();
                switch (event.getAction()){
                    case MotionEvent.ACTION_MOVE :
                        if(isOnNode() && editMode){
                            affectedNode.deplace(lastTouchDownX,lastTouchDownY);
                            isMoving = true;
                            updateView();
                        }


                }
                isMoving=false;
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
                    alertDialogBuilder.setTitle("Create a  new node");
                    alertDialogBuilder.setMessage("Enter the node label").setPositiveButton("Add", new DialogInterface.OnClickListener() {
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
                editMode=false;
                updateView();
                return true;
            case R.id.addPath:
                Toast.makeText( this, this.getText(R.string.add_path_toast), Toast.LENGTH_LONG).show();
                creationNodeMode=false;
                creationArcMode=true;
                editMode=false;
                updateView();
                return true;
            case R.id.edit:
                Toast.makeText( this, this.getText(R.string.edit_toast), Toast.LENGTH_LONG).show();
                creationNodeMode=false;
                creationArcMode=false;
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
            if (this.isOnNode() && !isMoving) {
                super.onCreateContextMenu(menu, v, menuInfo);
                getMenuInflater().inflate(R.menu.node_menu, menu);
                menu.setHeaderTitle("Edit Node");
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
                alertDialogBuilder.setTitle("");
                alertDialogBuilder
                        .setMessage("Enter the new node label")
                        .setPositiveButton("Change",new DialogInterface.OnClickListener() {
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
                alertDialogBuilderTaille.setTitle("Enter the new size");
                alertDialogBuilderTaille
                        .setPositiveButton("Edit",new DialogInterface.OnClickListener() {
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
}
