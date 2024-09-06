package com.leti.summer_practice.logic;

import java.io.*;
import java.util.*;

public class Graph {

    private ArrayList<ArrayList<Integer>> matrix;
    private Map<String, Integer> address;
    private int max_index;
    private LinkedList<Integer> available_numbers;

    public Graph() {
        matrix = new ArrayList<>();
        address = new HashMap<>();
        available_numbers = new LinkedList<>();
        max_index = 0;
    }

    public int get_vertex_count() {
        return address.size();
    }

    public void clear() {
        matrix.clear();
        address.clear();
        available_numbers.clear();
        max_index = 0;
    }

    public boolean is_connected() {
        Map<Graph.Node, Boolean> closed = new HashMap<>();
        Set<String> entry = address.keySet();
        Iterator<String> iter = entry.iterator();
        if (!iter.hasNext())
            return true;
        Node start_vertex = new Node(iter.next());
        int vertex_count = dfs(closed, start_vertex);
        return vertex_count == get_vertex_count();
    }

    private int dfs(Map<Graph.Node, Boolean> closed, Graph.Node current) {
        closed.put(current, true);
        int res = 1;
        ArrayList<Graph.Node> neighbours = get_neighbours(current);
        for (Node neighbour : neighbours)
            if (!closed.containsKey(neighbour))
                res = res + dfs(closed, neighbour);
        return res;
    }

    private static class GraphBuilder {

        private Graph result;
        private String[] vertex_names;

        private int correct_line_length;
        private boolean column_mode;
        private int current_line_count;

        private boolean first_iteration;

        private boolean triangle_mode;

        GraphBuilder() {
            result = new Graph();
            column_mode = true;
            triangle_mode = false;
            first_iteration = true;
            current_line_count = 0;
        }

        void init_names(String[] names) {
            for (String name : names) {
                if (name.length() > 3 || !name.chars().allMatch(Character::isLetter))
                    throw new RuntimeException("Error while reading file:invalid vertex name");
                result.create_vertex(name);
            }
            vertex_names = names;
        }

        private void add_directed_edge(String start, String finish, Integer weight) {
            int start_index = result.address.get(start);
            int finish_index = result.address.get(finish);
            result.matrix.get(start_index).set(finish_index, weight);
        }

        private void check_mode(String[] row) {

            if (current_line_count > result.get_vertex_count())
                throw new RuntimeException("Error while reading file:The number of adjacency matrix rows does not match yhe number of columns");

            if (row[0].chars().allMatch(Character::isLetter)) {
                if (!column_mode)
                    throw new RuntimeException("Error while reading file:Incorrect matrix");
            } else if (column_mode) {
                if (first_iteration)
                    column_mode = false;
                else
                    throw new RuntimeException("Error while reading file:Incorrect matrix");
            }
            if (first_iteration) {
                if ((row.length == 1 && !column_mode) || (row.length == 2 && column_mode)) {
                    correct_line_length = 1;
                    triangle_mode = true;
                } else {
                    correct_line_length = result.get_vertex_count();
                }
                if (column_mode)
                    correct_line_length++;
            }
            if (triangle_mode && !first_iteration)
                correct_line_length++;

            if (row.length != correct_line_length)
                throw new RuntimeException("Error while reading file:Incorrect row length");

            if (column_mode && !row[0].equals(vertex_names[current_line_count]))
                throw new RuntimeException("Error while reading file: Incorrect  name column");

            first_iteration = false;
        }

        void read_row(String[] row) {
            check_mode(row);
            int start_index = column_mode ? 1 : 0;
            for (int i = start_index; i < row.length; i++) {
                if (!row[i].equals("-")) {
                    try {
                        int new_val = Integer.parseInt(row[i]);
                        String start = vertex_names[current_line_count];
                        String finish = vertex_names[i - start_index];
                        if (!triangle_mode) {
                            add_directed_edge(start, finish, new_val);
                        } else {
                            result.create_edge(result.get_vertex(start), result.get_vertex(finish), new_val);
                        }
                    } catch (NumberFormatException ex) {
                        throw new RuntimeException("Error while reading file:Invalid edge weight");
                    }
                }
            }
	        current_line_count++;
        }

        Graph get_result() {
            if (column_mode)
                current_line_count++;
            if (current_line_count != correct_line_length)
                throw new RuntimeException("Error while reading file: Adjacency matrix is not square");
            if (!result.symmetry_check())
                throw new RuntimeException("Error while reading file:The  Adjacency matrix is not symmetrical");
            return result;
        }
    }

    boolean edge_exists(String start, String finish) {
        if (!address.containsKey(start) || !address.containsKey(finish))
            return false;
        int start_index = address.get(start);
        int finish_index = address.get(finish);
        return matrix.get(start_index).get(finish_index) != null;
    }

    boolean vertex_exists(String name) {
        return address.containsKey(name);
    }

    private static String[] check_first(String[] values) {
        if (values[0].isEmpty()) {
            ArrayList<String> temp = new ArrayList<>(Arrays.asList(values));
            temp.remove(0);
            return temp.toArray(new String[0]);
        }
        return values;
    }

    static public Graph read_file(File file) {
        try (FileReader reader = new FileReader(file); BufferedReader buffer = new BufferedReader(reader)) {
            String line = buffer.readLine();
            if (line == null) {
                return new Graph();
            }
            GraphBuilder builder = new GraphBuilder();
            String[] names = line.split("\\s+");
            names = check_first(names);
            builder.init_names(names);
            line = buffer.readLine();
            while (line != null) {
                String[] new_values = line.split("\\s+");
                new_values = check_first(new_values);
                builder.read_row(new_values);
                line = buffer.readLine();
            }
            return builder.get_result();
        } catch (IOException e) {
            throw new RuntimeException("error while opening file", e);
        }
    }

    private boolean symmetry_check() {
        for (int i = 0; i < matrix.size(); i++) {
            for (int j = 0; j < matrix.size(); j++) {
                if (!Objects.equals(matrix.get(i).get(j), matrix.get(j).get(i))) {
                    return false;
                }
                if (i == j && matrix.get(i).get(j) != null) {
                    return false;
                }
            }
        }
        return true;
    }

    public Node create_vertex(String name) {
        if (address.containsKey(name))
            throw new UnsupportedOperationException("Vertex with same name already exists");
        Node new_node = new Node(name);
        int new_index;
        if (!available_numbers.isEmpty()) {
            new_index = available_numbers.removeLast();
            address.put(name, new_index);
        } else {
            new_index = max_index;
            max_index++;
            address.put(name, new_index);
            resize_matrix();
        }
        return new_node;
    }

    public void delete_vertex(Node vertex) {
        if (!address.containsKey(vertex.name))
            throw new UnsupportedOperationException("Invalid vertex name");
        int vertex_index = address.get(vertex.name);
        address.remove(vertex.name);
        available_numbers.addLast(vertex_index);
        for (int i = 0; i < matrix.size(); i++) {
            matrix.get(i).set(vertex_index, null);
            matrix.get(vertex_index).set(i, null);
        }
    }

    public ArrayList<Node> get_vertices() {
        ArrayList<Node> res = new ArrayList<>();
        for (String name : address.keySet()) {
            Node current = new Node(name);
            res.add(current);
        }
        return res;
    }

    public Node get_vertex(String name) {
        if (!address.containsKey(name))
            throw new UnsupportedOperationException("there is no such vertex of the graph");
        return new Node(name);
    }

    public Edge get_edge(String start, String finish) {
        if (!edge_exists(start, finish))
            throw new UnsupportedOperationException("Edge does not exist");
        int start_index = address.get(start);
        int finish_index = address.get(finish);
        int weight = matrix.get(start_index).get(finish_index);
        return new Edge(new Node(start), new Node(finish), weight);
    }

    public Edge create_edge(Node start, Node finish, int weight) {
        if (!address.containsKey(start.name) || !address.containsKey(finish.name))
            throw new UnsupportedOperationException("Invalid vertices for edge");
        int start_index = address.get(start.name);
        int finish_index = address.get(finish.name);
        if (matrix.get(start_index).get(finish_index) != null)
            if (matrix.get(start_index).get(finish_index) == weight)
                return new Edge(start, finish, weight);
            else
                throw new UnsupportedOperationException("such an edge already exists");
        matrix.get(start_index).set(finish_index, weight);
        matrix.get(finish_index).set(start_index, weight);
        return new Edge(start, finish, weight);
    }

    public void delete_edge(Edge to_delete) {
        if (!address.containsKey(to_delete.start.name) || !address.containsKey(to_delete.finish.name))
            throw new UnsupportedOperationException("Edge does not exist");
        int start_index = address.get(to_delete.start.name);
        int finish_index = address.get(to_delete.finish.name);
        matrix.get(start_index).set(finish_index, null);
        matrix.get(finish_index).set(start_index, null);
    }

    public ArrayList<Edge> get_edges() {
        ArrayList<Edge> res = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : address.entrySet()) {
            int current_vertex = entry.getValue();
            for (Map.Entry<String, Integer> entry1 : address.entrySet()) {
                int current_index = entry1.getValue();
                if (current_vertex < current_index || matrix.get(current_vertex).get(current_index) == null)
                    continue;
                Node start = new Node(entry.getKey());
                Node finish = new Node(entry1.getKey());
                int weight = matrix.get(current_vertex).get(current_index);
                res.add(new Edge(start, finish, weight));
            }
        }
        return res;
    }

    ArrayList<Node> get_neighbours(Node current) {
        if (!address.containsKey(current.name))
            throw new UnsupportedOperationException("there is no such vertex of the graph");
        ArrayList<Node> res = new ArrayList<>();
        int current_index = address.get(current.name);
        for (Map.Entry<String, Integer> entry : address.entrySet()) {
            if (entry.getValue() == current_index || matrix.get(current_index).get(entry.getValue()) == null) {
                continue;
            }
            Node new_node = new Node(entry.getKey());
            res.add(new_node);
        }
        return res;
    }

    private void resize_matrix() {
        for (ArrayList<Integer> integers : matrix)
            integers.add(null);
        ArrayList<Integer> new_list = new ArrayList<>();
        for (int i = 0; i <= matrix.size(); i++)
            new_list.add(null);
        matrix.add(new_list);
    }

    public static class Node {

        private String name;

        private Node(String name) {
            this.name = name;
        }

        public String get_name() {
            return name;
        }

        @Override
        public String toString() {
            return name;
        }

        @Override
        public boolean equals(Object other) {
            if (other == this)
                return true;
            if (other instanceof Node current)
                return current.name.equals(this.name);
            return false;
        }

        @Override
        public int hashCode() {
            return name.hashCode();
        }
    }

    public static class Edge {

        private Node start;
        private Node finish;
        private int weight;

        private Edge(Node start, Node finish, int weight) {
            this.start = start;
            this.finish = finish;
            this.weight = weight;
        }

        public Node get_start() {
            return start;
        }

        public Node get_finish() {
            return finish;
        }

        public int get_weight() {
            return weight;
        }

        public boolean equals(Object other) {
            if (this == other)
                return true;
            if (other instanceof Edge other_edge)
                if ((start.equals(other_edge.start) && finish.equals(other_edge.finish))
                        || (finish.equals(other_edge.start) && start.equals(other_edge.finish)))
                    return weight == other_edge.weight;
            return false;
        }

        @Override
        public int hashCode() {
            return start.hashCode() + finish.hashCode();
        }

        @Override
        public String toString() {
            return "( " + start.name + " " + finish.name + " " + weight + " )";
        }
    }
}