package Logic;

import java.util.*;

public class Algorithm {
    private ArrayList<Graph.Node> vertices;
    private ArrayList<Graph.Edge> edges;
    private Graph temporary_graph; //второй граф для поиска МОД
    private Graph.Edge[] result; //список списков ребер,
    // которые были добавлены в компоненты связности в конце шага
    private Map<Graph.Node, Integer> hashTableNode = new HashMap<>(); //хэш-таблица для вершин и компонент

    int length_components;

    Algorithm(Graph graph1) {
        vertices = graph1.get_vertices(); //все вершины графа
        edges = graph1.get_edges(); //все ребра графа
        temporary_graph = new Graph();
        length_components = vertices.size();

        for (int i = 0; i < vertices.size(); i++) { //заполенение второго графа
            temporary_graph.create_vertex(vertices.get(i).get_name());
            hashTableNode.put(vertices.get(i), i);
        }
    }

    private int update_components() {
        Map<Graph.Node, Boolean> opened = new HashMap<>();
        Map<Graph.Node, Boolean> closed = new HashMap<>();
        int counter = 0;

        for (int i = 0; i < vertices.size(); i++) {
            opened.put(vertices.get(i), false);
        }

        while (!opened.isEmpty()) {
            Iterator<Map.Entry<Graph.Node, Boolean>> itr = opened.entrySet().iterator();
            Map.Entry<Graph.Node, Boolean> entry = itr.next();
            Graph.Node key = entry.getKey();
            dfs(opened, closed, key, counter);
            counter++;
        }

        return counter + 1;
    }

    private void dfs(Map<Graph.Node, Boolean> opened, Map<Graph.Node, Boolean> closed, Graph.Node current, int counter) {
        hashTableNode.put(current, counter);
        opened.remove(current);
        closed.put(current, true);

        ArrayList<Graph.Node> neighbours = temporary_graph.get_neighbours(current);
        for (int i = 0; i < neighbours.size(); i++) {
            if (!closed.containsKey(neighbours.get(i))) {
                dfs(opened, closed, neighbours.get(i), counter);
            }
        }
    }

    public Graph.Edge[] get_new_edges() {

        if (length_components == 1) {
            return null;
        }

        result = new Graph.Edge[length_components];

        for (int i = 0; i < edges.size(); i++) {
            Graph.Edge current_edge = edges.get(i);
            int start_component = hashTableNode.get(current_edge.get_start());
            int finish_component = hashTableNode.get(current_edge.get_finish());

            if (start_component != finish_component) {
                if (result[start_component] == null || result[start_component].get_weight() > current_edge.get_weight()) {
                    result[start_component] = current_edge;
                }
                if (result[finish_component] == null || result[finish_component].get_weight() > current_edge.get_weight()) {
                    result[finish_component] = current_edge;
                }
            }
        }

        for (int i = 0; i < result.length; i++) {
            Graph.Edge current = result[i];
            temporary_graph.create_edge(current.get_start(), current.get_finish(), current.get_weight());
        }

        return result;
    }

    public void next_step() {
        if (length_components == 1){
            return;
        }
        length_components = update_components();
    }

    public Integer get_vertex_color(Graph.Node vertex){
        return hashTableNode.get(vertex);
    }

    public Integer get_edge_color(Graph.Node start_vertex, Graph.Node finish_vertex){
        if (hashTableNode.get(start_vertex).equals(hashTableNode.get(finish_vertex))) {
            return hashTableNode.get(start_vertex);
        }
        return null;
    }
}