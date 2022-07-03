package Logic;

public interface LogicInterface {

    public void remove_vertex(String name);
    public void add_vertex(String name);
    public void add_edge(String start, String finish, int weight);
    public void remove_edge(String start, String finish);
    public Integer get_vertex_color(String name);
    public Integer get_edge_color(String start, String finish);
    public boolean load_file(String name);
    public void start_algorithm();
    public Logic.Edge_info[] get_new_edges();
    public void next_big_step();


}
