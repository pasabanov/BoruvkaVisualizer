package com.leti.summer_practice.logic;

import java.util.*;

public class Algorithm {

    private ArrayList<Graph.Node> vertices;
    private ArrayList<Graph.Edge> edges;
    private Graph temporary_graph; // второй граф для поиска МОД
    private Graph.Edge[] result; // список списков ребер,
    // которые были добавлены в компоненты связности в конце шага
    private Map<Graph.Node, Integer> hashTableNode; // хэш-таблица для вершин и компонент
    private Map<Graph.Edge, Integer> edges_color;

    int length_components;

    Algorithm(Graph graph1) {
        vertices = graph1.get_vertices(); // все вершины графа
        edges = graph1.get_edges(); // все ребра графа
        temporary_graph = new Graph();
        length_components = vertices.size();
        edges_color = new HashMap<>();
        hashTableNode = new HashMap<>();

        for (int i = 0; i < vertices.size(); i++) { // заполенение второго графа
            temporary_graph.create_vertex(vertices.get(i).get_name());
            hashTableNode.put(vertices.get(i), i);
        }
    }


    private static class Inner {
        boolean val;

        Inner(boolean val) {
            this.val = val;
        }
    }

    private int update_components() {
        Map<Graph.Node, Inner> all_vertices = new HashMap<>();
        ArrayList<Inner> open_vertices = new ArrayList<>();
        for (Graph.Node vertex : vertices) {
            Inner new_val = new Inner(false);
            all_vertices.put(vertex, new_val);
            open_vertices.add(new_val);
        }
        int counter = 0;
        for (int i = 0; i < open_vertices.size(); i++) {
            if (open_vertices.get(i).val)
                continue;
            dfs(all_vertices, vertices.get(i), counter);
            counter++;
        }
        return counter;
    }

    private void dfs(Map<Graph.Node, Inner> all_vertices, Graph.Node current, int counter) {
        hashTableNode.put(current, counter);
        all_vertices.get(current).val = true;
        ArrayList<Graph.Node> neighbours = temporary_graph.get_neighbours(current);
        for (Graph.Node neighbour : neighbours) {
            if (!all_vertices.get(neighbour).val) {
                Graph.Edge new_edge = temporary_graph.get_edge(current.get_name(), neighbour.get_name());
                edges_color.put(new_edge, counter);
                dfs(all_vertices, neighbour, counter);
            }
        }
    }

    public Graph.Edge[] get_new_edges() {

        if (length_components == 1)
            return null;

        result = new Graph.Edge[length_components];

        for (Graph.Edge current_edge : edges) {

            int start_component = hashTableNode.get(current_edge.get_start());
            int finish_component = hashTableNode.get(current_edge.get_finish());

            if (start_component != finish_component) {
                if (result[start_component] == null || result[start_component].get_weight() > current_edge.get_weight())
                    result[start_component] = current_edge;
                if (result[finish_component] == null || result[finish_component].get_weight() > current_edge.get_weight())
                    result[finish_component] = current_edge;
            }
        }

        for (Graph.Edge current : result)
            temporary_graph.create_edge(current.get_start(), current.get_finish(), current.get_weight());

        return result;
    }

    public void next_step() {
        if (length_components == 1)
            return;
        length_components = update_components();
    }

    boolean isFinished() {
        return length_components == 1;
    }

    ArrayList<Graph.Edge> get_answer() {
        if (!isFinished())
            throw new RuntimeException("Algorithm is not finished yet");
        return temporary_graph.get_edges();
    }

    public Integer get_vertex_color(Graph.Node vertex) {
        return hashTableNode.get(vertex);
    }

    public Integer get_edge_color(Graph.Node start_vertex, Graph.Node finish_vertex) {
        if(!temporary_graph.edge_exists(start_vertex.get_name(), finish_vertex.get_name()))
            return null;
        return edges_color.get(temporary_graph.get_edge(start_vertex.get_name(), finish_vertex.get_name()));
    }
}