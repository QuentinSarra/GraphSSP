package fr.istic.mob.graphssp;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import static java.lang.Math.atan;
import static java.lang.Math.toDegrees;

/**
 * @authors Arthur Poilane / Damien Salerno / Quentin Sarrazin
 */
public class DrawableGraph extends Drawable {
    Graph graph;

    public DrawableGraph(Graph graph) {
        this.graph = graph;
    }

    @Override
    public void draw(@NonNull Canvas canvas) {


        Paint paint = new Paint();
        Paint paintLbl = new Paint();
        Paint paintLblArc = new TextPaint();
        paintLbl.setColor(Color.WHITE);
        paintLbl.setTextAlign(Paint.Align.CENTER);
        paintLbl.setTextSize(40);
        paintLblArc.setColor(Color.BLACK);
        paintLblArc.setTextAlign(Paint.Align.CENTER);

        //Nous commençons par dessiner les arcs.
        for (ArcFinal a : graph.getArcs()){
            Paint paintArc = new Paint();
            paintArc.setStrokeWidth(a.getWidth());
            paintArc.setColor(a.getColor());
            paintArc.setStyle(Paint.Style.STROKE);
            paintLblArc.setTextSize(a.getLargeurLabel());
            Path newPath = new Path();
            newPath.moveTo(a.getNodeOrigine().getX(), a.getNodeOrigine().getY());
            if (!(a instanceof ArcLoop)) {
                newPath.lineTo(a.getNodeDest().getX(), a.getNodeDest().getY());
                canvas.drawPath(newPath, paintArc);
                PathMeasure pm = new PathMeasure(newPath,false);
                float [] middlePoint = {0f, 0f};
                float [] tangent = {0f, 0f};
                pm.getPosTan(pm.getLength()/2,middlePoint,tangent);
                a.setMiddlePoint(middlePoint);
                a.setTangent(tangent);
                canvas.save();
                float x = a.getMiddlePoint()[0];
                float y = a.getMiddlePoint()[1];
                float nx = tangent[0];
                float ny = tangent[1];
                float degrees = ny/nx;
                double degs = toDegrees(atan(degrees));
                canvas.rotate((float) degs, x, y);
                if (ny > 0) {
                    canvas.drawText(a.getLabel(), x, y - 25, paintLblArc);
                } else if (ny <= 0) {
                    canvas.drawText(a.getLabel(), x, y + 45, paintLblArc);
                }
                canvas.restore();
            }
            if(a instanceof ArcLoop) {
                Node n = a.getNodeOrigine();
                newPath.cubicTo(n.getX() + n.getRayon() + 150, n.getY() + n.getRayon() + 300,
                        n.getX() + n.getRayon() + 150, n.getY() - n.getRayon() - 300,
                        n.getX(), n.getY());
                canvas.drawPath(newPath, paintArc);
                PathMeasure pm = new PathMeasure(newPath,false);
                float [] middlePoint = {0f, 0f};
                float [] tangent = {0f, 0f};
                pm.getPosTan(pm.getLength()/2,middlePoint,tangent);
                a.setMiddlePoint(middlePoint);
                a.setTangent(tangent);
                canvas.save();
                float x = a.getMiddlePoint()[0];
                float y = a.getMiddlePoint()[1];
                float nx = tangent[0];
                float ny = tangent[1];
                canvas.rotate((float) 270, x, y);
                if (ny > 0) {
                    canvas.drawText(a.getLabel(), x, y - 25, paintLblArc);
                } else if (ny <= 0) {
                    canvas.drawText(a.getLabel(), x, y + 45, paintLblArc);
                }
                canvas.restore();
            }

        }

        Path path;
        Paint paintTemp = new Paint();
        paintTemp.setColor(Color.BLACK);
        paintTemp.setStrokeWidth(5);
        paintTemp.setStyle(Paint.Style.STROKE);
        ArcTemp arctemp = graph.getArcTemp();
        //Ensuite nous dessinons l'arc Temporaire.
        if(arctemp != null){
            path = new Path();
            path.moveTo(arctemp.getNodeOrigine().getX(),arctemp.getNodeOrigine().getY());
            path.lineTo(arctemp.getNodeX(),arctemp.getNodeY());
            canvas.drawPath(path,paintTemp);
        }

        //Puis nous finnisons par les noeuds.
        for(Node n : graph.getNodes()) {
            float tailleLbl = paintLbl.measureText(n.getLabel());
            if(n.getRayon()<tailleLbl){
                n.setAgRayon(tailleLbl);
            }
            paint.setColor(n.getColor());
            canvas.drawOval(n,paint);
            canvas.drawText(n.getLabel(),n.getX(),n.getY()+7,paintLbl);

        }
    }

    @Override
    public boolean getPadding(@NonNull Rect padding) {
        return super.getPadding(padding);
    }

    @Override
    public void setAlpha(int alpha) {

    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {

    }

    @Override
    public int getOpacity() {
        return (int) 0;
    }
}