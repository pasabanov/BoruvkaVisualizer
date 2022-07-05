package com.leti.summer_practice.logic;

import java.io.File;
import java.util.ArrayList;

public interface LogicInterface {

    class VertexInfo {
        public String name;
        public Integer color;

        VertexInfo() {
        }

        VertexInfo(String name, Integer color) {
            this.name = name;
            this.color = color;
        }
    }

    class EdgeInfo {
        public String start;
        public String finish;
        public int weight;
        public Integer color;

        EdgeInfo() {
        }

        EdgeInfo(String start, String finish, int weight, Integer color) {
            this.start = start;
            this.finish = finish;
            this.weight = weight;
            this.color = color;
        }

    }

    void removeVertex(String name);

    void addVertex(String name);

    void addEdge(String start, String finish, int weight);

    void removeEdge(String start, String finish);

    public VertexInfo getVertexInfo(String name);

    public EdgeInfo getEdgeInfo(String start, String finish);

    ArrayList<VertexInfo> getVertices();

    ArrayList<EdgeInfo> getEdges();

    void loadFile(File file);

    void startAlgorithm();

    EdgeInfo getNewEdge();

    void nextBigStep();

    boolean isAlgorithmFinished();

    ArrayList<EdgeInfo> getAnswer();

    boolean isAlgorithmStarted();

    void killAlgorithm();

    public boolean isGraphEmpty();

    public void deleteGraph();
}