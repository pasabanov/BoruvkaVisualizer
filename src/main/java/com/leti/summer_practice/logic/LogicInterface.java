package com.leti.summer_practice.logic;

import java.io.File;
import java.util.ArrayList;

public interface LogicInterface {

    class Node_info {
        public String name;
        public Integer color;
    }

    class Edge_info {
        public String start;
        public String finish;
        public int weight;
        public Integer color;
    }

    void remove_vertex(String name);

    void add_vertex(String name);

    void add_edge(String start, String finish, int weight);

    void remove_edge(String start, String finish);

    Integer get_vertex_color(String name);

    Integer get_edge_color(String start, String finish);

    ArrayList<Node_info> getVertices();

    ArrayList<Edge_info> getEdges();

    boolean load_file(File file);

    void start_algorithm();

    Logic.Edge_info[] get_new_edges();

    void next_big_step();

    ArrayList<Edge_info> get_answer();
}