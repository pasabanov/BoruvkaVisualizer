package com.leti.summer_practice.logic;

import java.io.File;
import java.util.ArrayList;

public class Logic implements LogicInterface {

    private Graph graph;
    private Algorithm algorithm;

    private int current_edge;
    Graph.Edge[] current_step_edges;

    public Logic() {
        graph = new Graph();
        current_edge = 0;
        current_step_edges = null;
    }

    @Override
    public void removeVertex(String name) {
        Graph.Node delete = graph.get_vertex(name);
        graph.delete_vertex(delete);
    }

    @Override
    public void addVertex(String name) {
        graph.create_vertex(name);
    }

    @Override
    public void addEdge(String start, String finish, int weight) {
        Graph.Node start_vertex = graph.get_vertex(start);
        Graph.Node finish_vertex = graph.get_vertex(finish);
        graph.create_edge(start_vertex, finish_vertex, weight);
    }

    @Override
    public void removeEdge(String start, String finish) {
        graph.delete_edge(graph.get_edge(start, finish));
    }

    @Override
    public VertexInfo getVertexInfo(String name) {
        Integer color;
        if (algorithm == null) {
            color = null;
        } else {
            Graph.Node vertex = graph.get_vertex(name);
            color = algorithm.get_vertex_color(vertex);
        }
        return new VertexInfo(name, color);
    }

    @Override
    public EdgeInfo getEdgeInfo(String start, String finish) {
        Integer color;
        if (algorithm == null) {
            color = null;
        } else {
            Graph.Node start_vertex = graph.get_vertex(start);
            Graph.Node finish_vertex = graph.get_vertex(finish);
            color = algorithm.get_edge_color(start_vertex, finish_vertex);
        }
        int weight = graph.get_edge(start, finish).get_weight();
        return new EdgeInfo(start, finish, weight, color);
    }

    @Override
    public ArrayList<VertexInfo> getVertices() {
        ArrayList<Graph.Node> nodes = graph.get_vertices();
        ArrayList<VertexInfo> vertexInfos = new ArrayList<>(nodes.size());
        for (Graph.Node node : nodes) {
            VertexInfo vertexInfo = new VertexInfo();
            vertexInfo.name = node.get_name();
            if (algorithm == null)
                vertexInfo.color = null;
            else
                vertexInfo.color = algorithm.get_vertex_color(node);
            vertexInfos.add(vertexInfo);
        }
        return vertexInfos;
    }

    @Override
    public ArrayList<EdgeInfo> getEdges() {
        ArrayList<Graph.Edge> edges = graph.get_edges();
        ArrayList<EdgeInfo> edge_infos = new ArrayList<>(edges.size());
        for (Graph.Edge edge : edges) {
            EdgeInfo edge_info = new EdgeInfo();
            edge_info.start = edge.get_start().get_name();
            edge_info.finish = edge.get_finish().get_name();
            edge_info.weight = edge.get_weight();
            if (algorithm == null)
                edge_info.color = null;
            else
                edge_info.color = algorithm.get_edge_color(edge.get_start(), edge.get_finish());
            edge_infos.add(edge_info);
        }
        return edge_infos;
    }

    @Override
    public void loadFile(File file) {
        killAlgorithm();
        graph = Graph.read_file(file);
    }

    @Override
    public void startAlgorithm() {
        if (algorithm != null)
            return;
        if (!graph.is_connected())
            throw new RuntimeException("Graph is not connected");
        algorithm = new Algorithm(graph);
        current_step_edges = algorithm.get_new_edges();
        current_edge = 0;
    }

    @Override
    public EdgeInfo getNewEdge() {
        if (algorithm == null)
            throw new RuntimeException("Algorithm is not started");
        if (current_step_edges == null || current_edge == current_step_edges.length)
            return null;
        EdgeInfo new_edge = new EdgeInfo();
        new_edge.start = current_step_edges[current_edge].get_start().get_name();
        new_edge.finish = current_step_edges[current_edge].get_finish().get_name();
        new_edge.color = current_edge;
        new_edge.weight = current_step_edges[current_edge].get_weight();
        current_edge++;
        return new_edge;
    }

    @Override
    public void nextBigStep() {
        if (algorithm == null)
            throw new RuntimeException("Algorithm is not started");
        algorithm.next_step();
        current_step_edges = algorithm.get_new_edges();
        current_edge = 0;
    }

    @Override
    public boolean isAlgorithmFinished() {
        return algorithm != null && algorithm.isFinished();
    }

    @Override
    public void killAlgorithm() {
        algorithm = null;
    }

    @Override
    public boolean isAlgorithmStarted() {
        return algorithm != null;
    }

    @Override
    public ArrayList<EdgeInfo> getAnswer() {
        if (algorithm == null)
            throw new RuntimeException("Algorithm is not finished yet");
        ArrayList<Graph.Edge> answer = algorithm.get_answer();
        ArrayList<EdgeInfo> res = new ArrayList<>();
        for (Graph.Edge edge : answer) {
            EdgeInfo new_edge = new EdgeInfo();
            new_edge.start = edge.get_start().get_name();
            new_edge.finish = edge.get_finish().get_name();
            if (algorithm == null)
                new_edge.color = null;
            else
                new_edge.color = algorithm.get_edge_color(edge.get_start(), edge.get_finish());
            new_edge.weight = edge.get_weight();
            res.add(new_edge);
        }
        return res;
    }

    @Override
    public boolean isGraphEmpty() {
        return graph.get_vertex_count() == 0;
    }

    @Override
    public void clearGraph() {
        killAlgorithm();
        graph.clear();
    }

    @Override
     public boolean edgeExists(String start, String finish) {
        return graph.edge_exists(start, finish);
    }

    @Override
    public boolean vertexExists(String name) {
        return graph.vertex_exists(name);
    }
}