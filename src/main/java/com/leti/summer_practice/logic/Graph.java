package com.leti.summer_practice.logic;

import java.io.*;
import java.util.*;

public class Graph {
    private ArrayList<ArrayList<Integer>> matrix;
    private Map<String, Integer> adress;
    int max_index;
    LinkedList<Integer> available_numbers;

    public Graph() {
        matrix = new ArrayList<ArrayList<Integer>>();
        adress = new HashMap<String, Integer>();
        available_numbers = new LinkedList<Integer>();
        max_index = 0;
    }

    private static ArrayList<Integer> get_row_values(String line) {
        ArrayList<Integer> res = new ArrayList<>();
        String[] split = line.split(" +");
        for (int i = 0; i < split.length; i++) {
            Integer number;
            if (split[i].equals("-")) {
                number = null;
            } else {
                number = Integer.parseInt(split[i]);
            }
            res.add(number);
        }
        return res;
    }

    private static String next_string(String str) {
        int current = str.length() - 1;
        StringBuffer res = new StringBuffer();
        while (current >= 0 && str.charAt(current) == 'Z') {
            res.append('A');
            current--;

        }
        char new_symbol;
        if (current >= 0) {
            new_symbol = (char) (str.charAt(current) + 1);

        } else {
            new_symbol = 'A';
            current = 0;
        }
        return new String(str.substring(0, current) + new_symbol + res);
    }

    private static boolean symmetry_check(ArrayList<ArrayList<Integer>> matrix) {
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

    boolean read_file(String file_path) {
        File file = new File(file_path);
        try (FileReader reader = new FileReader(file); BufferedReader buffer = new BufferedReader(reader)) {
            ArrayList<ArrayList<Integer>> new_matrix = new ArrayList<>();
            Integer length = null;
            String line = buffer.readLine();
            while (line != null) {
                ArrayList<Integer> new_row = get_row_values(line);
                if (length != null && new_row.size() != length) {
                    System.out.println("Strings with different lengths");
                    return false;
                }
                length = new_row.size();
                new_matrix.add(new_row);
                line = buffer.readLine();
            }
            if (length != null && new_matrix.size() != length) {
                System.out.println("The matrix is not square");
                return false;
            }
            if (!symmetry_check(new_matrix)) {
                System.out.println("The matrix is not symmetrical");
                return false;
            }
            matrix = new_matrix;
            available_numbers.clear();
            adress.clear();
            max_index = Objects.requireNonNullElse(length, 0);
            String vertex_name = "A";
            for (int i = 0; i < max_index; i++) {
                adress.put(vertex_name, i);
                vertex_name = next_string(vertex_name);
            }

        } catch (IOException e) {
            System.out.println("РћС€РёР±РєР° РїСЂРё РѕС‚РєСЂС‹С‚РёРё С„Р°Р№Р»Р°");
            return false;
        }
        catch (NumberFormatException e){
            System.out.println("РћС€РёР±РєР° РІ С‡РёСЃР»Р°С… РјР°С‚СЂРёС†С‹");
            return false;

        }

        return true;
    }

    public Node create_vertex(String name) {
        if (adress.containsKey(name)) {
            throw new UnsupportedOperationException("Vertex with same name already exists");
        }
        Node new_node = new Node(name);
        int new_index;
        if (available_numbers.size() > 0) {
            new_index = available_numbers.removeLast();
            adress.put(name, new_index);
        } else {
            new_index = max_index;
            max_index++;
            adress.put(name, new_index);
            resize_matrix();
        }

        return new_node;
    }

    public void delete_vertex(Node vertex) {
        if (!adress.containsKey(vertex.name)) {
            throw new UnsupportedOperationException("Invalid vertex name");
        }
        int vertex_index = adress.get(vertex.name);
        adress.remove(vertex.name);
        available_numbers.addLast(vertex_index);
        for (int i = 0; i < matrix.size(); i++) {
            matrix.get(i).set(vertex_index, null);
            matrix.get(vertex_index).set(i, null);
        }
    }

    public ArrayList<Node> get_vertices() {
        ArrayList<Node> res = new ArrayList<>();
        for (String name : adress.keySet()) {
            Node current = new Node(name);
            res.add(current);
        }
        return res;
    }

    public Node get_vertex(String name) {
        if (!adress.containsKey(name)) {
            throw new UnsupportedOperationException("there is no such vertex of the graph");
        }
        return new Node(name);
    }

    public Edge get_edge(String start, String finish) {
        if (!adress.containsKey(start) || !adress.containsKey(finish)) {
            throw new UnsupportedOperationException("Invalid vertices for edge");

        }
        int start_index = adress.get(start);
        int finish_index = adress.get(finish);
        int weight = matrix.get(start_index).get(finish_index);
        if (weight == 0) {
            throw new UnsupportedOperationException("Edge does not exist");
        }
        return new Edge(new Node(start), new Node(finish), weight);
    }

    public Edge create_edge(Node start, Node finish, int weight) {
        if (!adress.containsKey(start.name) || !adress.containsKey(finish.name)) {
            throw new UnsupportedOperationException("Invalid vertices for edge");
        }
        int start_index = adress.get(start.name);
        int finish_index = adress.get(finish.name);
        if (matrix.get(start_index).get(finish_index) != null) {
            if (matrix.get(start_index).get(finish_index) == weight) {
                return new Edge(start, finish, weight);
            }
            throw new UnsupportedOperationException("such an edge already exists");
        }
        matrix.get(start_index).set(finish_index, weight);
        matrix.get(finish_index).set(start_index, weight);
        return new Edge(start, finish, weight);
    }

    public void delete_edge(Edge to_delete) {
        if (!adress.containsKey(to_delete.start.name) || !adress.containsKey(to_delete.finish.name)) {
            throw new UnsupportedOperationException("Edge does not exist");
        }
        int start_index = adress.get(to_delete.start.name);
        int finish_index = adress.get(to_delete.finish.name);
        matrix.get(start_index).set(finish_index, null);
        matrix.get(finish_index).set(start_index, null);
    }

    public ArrayList<Edge> get_edges() {
        ArrayList<Edge> res = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : adress.entrySet()) {
            int current_vertex = entry.getValue();
            for (Map.Entry<String, Integer> entry1 : adress.entrySet()) {
                int current_index = entry1.getValue();
                if (current_vertex < current_index || matrix.get(current_vertex).get(current_index) == null) {
                    continue;
                }
                Node start = new Node(entry.getKey());
                Node finish = new Node(entry1.getKey());
                int weight = matrix.get(current_vertex).get(current_index);
                res.add(new Edge(start, finish, weight));
            }
        }
        return res;
    }

    ArrayList<Node> get_neighbours(Node current) {
        if (!adress.containsKey(current.name)) {
            throw new UnsupportedOperationException("there is no such vertex of the graph");
        }
        ArrayList<Node> res = new ArrayList<>();
        int current_index = adress.get(current.name);
        for (Map.Entry<String, Integer> entry : adress.entrySet()) {
            if (entry.getValue() == current_index || matrix.get(current_index).get(entry.getValue()) == null) {
                continue;
            }
            Node new_node = new Node(entry.getKey());
            res.add(new_node);
        }
        return res;
    }


    private void resize_matrix() {
        for (int i = 0; i < matrix.size(); i++) {
            matrix.get(i).add(null);
        }
        ArrayList<Integer> new_list = new ArrayList<>();
        for (int i = 0; i <= matrix.size(); i++) {
            new_list.add(null);
        }
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

        @Override //РґР»СЏ С‚РµСЃС‚РѕРІ - РїРѕС‚РѕРј СѓРґР°Р»РёРј
        public String toString() {
            return name;
        }

        @Override
        public boolean equals(Object other) {
            if (other == this) {
                return true;
            }

            if (other instanceof Node) {
                Node current = (Node) other;
                if (current.name.equals(this.name)) {
                    return true;
                }
            }

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

        @Override //РґР»СЏ С‚РµСЃС‚РѕРІ = РїРѕС‚РѕРј СѓРґР°Р»РёРј
        public String toString() {
            return new String("( " + start.name + " " + finish.name + " " + weight + " )");
        }
    }
}