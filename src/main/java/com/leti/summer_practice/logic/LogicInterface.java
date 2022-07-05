package com.leti.summer_practice.logic;

import java.io.File;
import java.util.ArrayList;

public interface LogicInterface {

    class VertexInfo {
        public String name;
        public Integer color;
    }

    class EdgeInfo {
        public String start;
        public String finish;
        public int weight;
        public Integer color;

        @Override
        public String toString() {
            return start + " --> " + finish + " = " + weight + ", color=" + color;
        }
    }

    void removeVertex(String name);

    void addVertex(String name);

    void addEdge(String start, String finish, int weight);

    void removeEdge(String start, String finish);

    Integer getVertexColor(String name);

    Integer getEdgeColor(String start, String finish);

    ArrayList<VertexInfo> getVertices();

    ArrayList<EdgeInfo> getEdges();

    void loadFile(File file);

    void startAlgorithm();

    EdgeInfo[] getNewEdges();

    void nextBigStep();

    boolean isAlgorithmFinished();

    ArrayList<EdgeInfo> getAnswer();

    boolean isAlgorithmStarted();

    void killAlgorithm();
}