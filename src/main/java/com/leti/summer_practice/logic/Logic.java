package com.leti.summer_practice.logic;

import java.io.File;
import java.util.ArrayList;

public class Logic implements LogicInterface {

    Graph graph;
    Algorithm algorithm;

    public Logic() {
        graph = new Graph();
    }

    public void remove_vertex(String name) {
        Graph.Node delete = graph.get_vertex(name);
        graph.delete_vertex(delete);
    }

    public void add_vertex(String name) {
        graph.create_vertex(name);
    }

    public void add_edge(String start, String finish, int weight) {
        Graph.Node start_vertex = graph.get_vertex(start);
        Graph.Node finish_vertex = graph.get_vertex(finish);
        graph.create_edge(start_vertex, finish_vertex, weight);
    }

    public void remove_edge(String start, String finish) {
        graph.delete_edge(graph.get_edge(start, finish));
    }

    public Integer get_vertex_color(String name) {

        if (algorithm == null) {
            return null;
        }

        Graph.Node vertex = graph.get_vertex(name);
        return algorithm.get_vertex_color(vertex);
    }

    public Integer get_edge_color(String start, String finish) {

        if (algorithm == null) {
            return null;
        }

        Graph.Node start_vertex = graph.get_vertex(start);
        Graph.Node finish_vertex = graph.get_vertex(finish);

        return algorithm.get_edge_color(start_vertex, finish_vertex);
    }

    // written by Peter
    @Override
    public ArrayList<Node_info> getVertices() {
        ArrayList<Graph.Node> nodes = graph.get_vertices();
        ArrayList<Node_info> node_infos = new ArrayList<>(nodes.size());
        for (Graph.Node node : nodes) {
            Node_info node_info = new Node_info();
            node_info.name = node.get_name();
            node_info.color = algorithm.get_vertex_color(node);
            node_infos.add(node_info);
        }
        return node_infos;
    }

    // written by Peter
    @Override
    public ArrayList<Edge_info> getEdges() {
        ArrayList<Graph.Edge> edges = graph.get_edges();
        ArrayList<Edge_info> edge_infos = new ArrayList<>(edges.size());
        for (Graph.Edge edge : edges) {
            Edge_info edge_info = new Edge_info();
            edge_info.start = edge.get_start().get_name();
            edge_info.finish = edge.get_finish().get_name();
            edge_info.weight = edge.get_weight();
            edge_info.color = algorithm.get_edge_color(edge.get_start(), edge.get_finish());
            edge_infos.add(edge_info);
        }
        return edge_infos;
    }

    public boolean load_file(File file) {
        return graph.read_file(file);
    }

    public void start_algorithm() {
        if (algorithm != null)
        {
//            throw new RuntimeException("Algorithm was already started");
        }
        algorithm = new Algorithm(graph);
    }

    public Edge_info[] get_new_edges() {
        if (algorithm == null) {
            throw new RuntimeException("Algorithm is not started");
        }

        Graph.Edge[] edges = algorithm.get_new_edges();

        if (edges == null) {
            return null;
        }

        Edge_info[] new_edges = new Edge_info[edges.length];

        for (int i = 0; i < edges.length; i++) {
            new_edges[i] = new Edge_info();
            new_edges[i].start = edges[i].get_start().get_name();
            new_edges[i].finish = edges[i].get_finish().get_name();
            new_edges[i].weight = edges[i].get_weight();
            new_edges[i].color = i;
        }

        return new_edges;
    }

    public void next_big_step() {
        if (algorithm == null) {
            throw new RuntimeException("Algorithm is not started");
        }

        algorithm.next_step();
    }
}

