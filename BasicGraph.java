import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Stack;
import java.util.HashMap;
import java.util.List;

/*********************
 * @Authors: Angel Villa, Qimeng Yu
 *
 * This class implements BasicGraphADT
 *********************/

public class BasicGraph<T> implements BasicGraphADT<T> {

  private HashMap<T, Vertex<T>> adjList = new HashMap<T, Vertex<T>>();
  private LinkedList<T> labelList = new LinkedList<T>();

   
  /*******************
   * Add a vertex to this graph with given label
   * @return Whether the vertex was successfully added
   ********************/
  public boolean addVertex(T vert) {
    // check if vert is already a key in adjList. If not, add it.
    if (this.adjList.containsKey(vert) == false) {
      Vertex<T> newVert = new Vertex(vert);
      this.adjList.put(vert, newVert);
      this.labelList.addLast(vert);
      return true;
    }
    return false;
  }

  /******************
   * Add an edge to this graph between the two given labels
   * @return Whether the edge was successfully added
   *         return true also if the edge already exists
   ********************/
  public boolean addEdge(T beg, T end) {
    Vertex<T> a = this.adjList.get(beg);
    Vertex<T> b = this.adjList.get(end);
    // if both of these vertices already exist, add the edge
    if (a != null && b != null) {
      a.addNeighbor(b);
      b.addNeighbor(a);
      return true;
    } else {return false;}
  }

  /******************
   * Tests whether a vertex exists in the graph
   * @return Whether the vertex exists
   ********************/
  public boolean hasVertex(T vert) {
    return this.adjList.containsKey(vert);
  }

  /******************
   * Tests whether an edge exists in the graph
   * @return Whether the edge exists
   ********************/
  public boolean hasEdge(T beg, T end) {
    return this.adjList.get(beg).getNeighbors().contains(this.adjList.get(end));
  }

  /*****************
   * Gets a list containing all the neighbors of a given vertex
   * @return the neighbor list as a java List
   *********************/
  public List<Vertex<T>> getNeighbors(T vert) {
    return this.adjList.get(vert).getNeighbors();
  }

  /****************************
   * Gets the vertex object associated with the given label
   * @return the vertex
   ************************/
  public Vertex<T> getVertex(T lab) {
    return this.adjList.get(lab);
  }

  /*****************
   * Tests if the graph is empty
   * @return Whether the graph is empty
   *******************/
  public boolean isEmpty() {
    return this.adjList.size() == 0;
  }

  /********************
   * Gets the number of vertices
   * @return The number of vertices
   *******************/
  public int getNumVertices() {
    return this.adjList.size();
  }

  /********************
   * Gets the number of edges
   * @return The number of edges
   *********************/
  public int getNumEdges() {
    int totalNeighbor = 0;
    int addVal;
    // Compute the sum of edges of all vertices. The number of 
    // edges is half of this sum.
    for (int i = 0; i < this.labelList.size(); i++) {
      addVal = this.adjList.get(this.labelList.get(i)).getNeighbors().size();
      totalNeighbor = totalNeighbor + addVal;
    }
    return totalNeighbor/2;
  }

  /**************
   * Clear all edges and vertices from the graph
   ********************/
  public void clear() {
    this.adjList.clear();
    this.labelList.clear();
  }
  
  /**************
   * Convert the hashtable to strings
   *************/
  public String toString() {
    String s = "";
    String w = "";
    int listSize;
    List<Vertex<T>> tempNeighbor;
    for (int i = 0; i < this.labelList.size(); i++) {
      // this part checks if a weight is assigned to a vertex
      if (this.adjList.get(this.labelList.get(i)).getWeight()
                                    !=Double.POSITIVE_INFINITY) {
        w = "" + this.adjList.get(this.labelList.get(i)).getWeight();
      } else {w = "";}
      s = s + "\n" + this.labelList.get(i) + " " + w + " >>> ";
      tempNeighbor = this.adjList.get(this.labelList.get(i)).getNeighbors();
      listSize = tempNeighbor.size();
      // convert each neighbor name to a string and append it to s
      for (int j = 0; j < listSize; j++) {
        s = s + " " + tempNeighbor.get(j).getLabel();
      }
    }  
    return s;
  }

  /**************
   * test
   *************/
  public static void main(String[] args) {
    BasicGraph<String> gr = new BasicGraph<String>();
    gr.addVertex("foo");
    gr.addVertex("bar");
    gr.addVertex("baz");
    gr.addVertex("ninja");
    gr.addVertex("robot");
    gr.addEdge("foo", "bar");
    gr.addEdge("foo", "baz");
    gr.addEdge("foo", "ninja");
    gr.addEdge("ninja", "robot");
    gr.getVertex("foo");
    System.out.println(gr.toString());
    System.out.println(gr.getNumVertices());
    System.out.println(gr.getNumEdges());
    System.out.println(gr.hasVertex("foo"));
    System.out.println(gr.hasVertex("banana"));
    System.out.println(gr.hasEdge("foo", "bar"));
    System.out.println(gr.hasEdge("bar", "foo"));
    System.out.println(gr.hasEdge("foo", "banana"));
    gr.clear();
  }
}