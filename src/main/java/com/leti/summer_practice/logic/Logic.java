package com.leti.summer_practice.logic;

import java.io.File;
import java.util.ArrayList;

public class Logic implements LogicInterface {

    Graph graph;
    Algorithm algorithm;

    public Logic() {
        graph = new Graph();
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
    public Integer getVertexColor(String name) {

        if (algorithm == null) {
            return null;
        }

        Graph.Node vertex = graph.get_vertex(name);
        return algorithm.get_vertex_color(vertex);
    }

    @Override
    public Integer getEdgeColor(String start, String finish) {

        if (algorithm == null) {
            return null;
        }

        Graph.Node start_vertex = graph.get_vertex(start);
        Graph.Node finish_vertex = graph.get_vertex(finish);

        return algorithm.get_edge_color(start_vertex, finish_vertex);
    }

    @Override
    public ArrayList<VertexInfo> getVertices() {
        ArrayList<Graph.Node> nodes = graph.get_vertices();
        ArrayList<VertexInfo> vertexInfos = new ArrayList<>(nodes.size());
        for (Graph.Node node : nodes) {
            VertexInfo vertexInfo = new VertexInfo();
            vertexInfo.name = node.get_name();
            if (algorithm == null) {
                vertexInfo.color = null;
            } else {
                vertexInfo.color = algorithm.get_vertex_color(node);
            }
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
            if (algorithm == null) {
                edge_info.color = null;
            } else {
                edge_info.color = algorithm.get_edge_color(edge.get_start(), edge.get_finish());
            }
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
        if (algorithm != null) {
            return;
        }
        if (!graph.is_connected()) {
            throw new RuntimeException("Graph is not connected");
        }
        algorithm = new Algorithm(graph);
    }

    @Override
    public EdgeInfo[] getNewEdges() {
        if (algorithm == null) {
            throw new RuntimeException("Algorithm is not started");
        }

        Graph.Edge[] edges = algorithm.get_new_edges();

        if (edges == null) {
            return null;
        }

        EdgeInfo[] new_edges = new EdgeInfo[edges.length];

        for (int i = 0; i < edges.length; i++) {
            new_edges[i] = new EdgeInfo();
            new_edges[i].start = edges[i].get_start().get_name();
            new_edges[i].finish = edges[i].get_finish().get_name();
            new_edges[i].weight = edges[i].get_weight();
            new_edges[i].color = i;
        }

        return new_edges;
    }

    @Override
    public void nextBigStep() {
        if (algorithm == null) {
            throw new RuntimeException("Algorithm is not started");
        }
        algorithm.next_step();
    }

    @Override
    public boolean isAlgorithmFinished() {
        return algorithm != null && algorithm.isFinished();
    }

    @Override
    public void killAlgorithm() {
        if (algorithm == null) {
            return;
        }
        algorithm = null;
    }

    @Override
    public boolean isAlgorithmStarted() {
        return !(algorithm == null);
    }


    @Override
    public ArrayList<EdgeInfo> getAnswer() {
        if (algorithm == null) {
            throw new RuntimeException("Algorithm is not finished yet");
        }
        ArrayList<Graph.Edge> answer = algorithm.get_answer();
        ArrayList<EdgeInfo> res = new ArrayList<>();
        for (Graph.Edge edge : answer) {
            EdgeInfo new_edge = new EdgeInfo();
            new_edge.start = edge.get_start().get_name();
            new_edge.finish = edge.get_finish().get_name();
            new_edge.color = null;
            new_edge.weight = edge.get_weight();
            res.add(new_edge);
        }
        return res;
    }

}

