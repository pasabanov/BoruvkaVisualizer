package com.leti.summer_practice.gui.prog;

import javafx.scene.paint.Color;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.function.Function;

public class GraphDataGUI {

    public static class GraphVertex {

        double x, y;
        Color color;

        public GraphVertex(double x, double y, Color color) {
            this.x = x;
            this.y = y;
            this.color = color;
        }

        public GraphVertex(double x, double y) {
            this.x = x;
            this.y = y;
            this.color = Color.YELLOW;
        }
    }

    public static class GraphEdge {

        int form, to;
        Color color;

        public GraphEdge(int form, int to, Color color) {
            this.form = form;
            this.to = to;
            this.color = color;
        }

        public GraphEdge(int form, int to) {
            this.form = form;
            this.to = to;
            this.color = Color.ORANGE;
        }
    }

//    private static final Function<Integer,ArrayList<Pair<Double,Double>>>
//            DEFAULT_COORDS_GENERATOR = n -> {
//        double step = 2 * Math.PI / n;
//        double angle = Math.PI;
//        ArrayList<Pair<Double,Double>> verticesCoords = new ArrayList<>(n);
//        for (int i = 0; i < n; ++i, angle += step)
//            verticesCoords.add(new Pair<>(Math.cos(angle), Math.sin(angle)));
//        return verticesCoords;
//    };

//    private ArrayList<Pair<Double,Double>> verticesCoords;
    private ArrayList<GraphVertex> vertices;
    private ArrayList<GraphEdge> edges;

    public GraphDataGUI(ArrayList<GraphVertex> vertices, ArrayList<GraphEdge> edges) {
        this.vertices = vertices;
        this.edges = edges;
    }
}