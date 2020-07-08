import java.util.*;
import java.lang.*;
import java.io.*;

/*********************
 * @Authors: Angel Villa, Qimeng Yu
 *
 * This class reads a maze file and creates either weighted or 
 * unweighted graphs. It also implements BFS, DFS, Dijkstra
 * search methods for the created graph.
 **********************/

public class MazeGraph {
  
  private BasicGraph<String> graph;
  // an array for vertices' labels
  private String[][] labArray;
  // an array for vertices' initial distances (weights)
  private int[][] distanceArray;
  // width, or height of the input maze
  private int size;
  
  /**********
   * creates a graph with non-wall vertices
   * each vertex is assigned with its neighbors and distance
   * @param input file name, "weighted" or "unweighted"
   **/
  public MazeGraph(String fname, String w) {  
    if (w.equals("weighted")) {
      makeGraph(fname);
    } else if (w.equals("unweighted")) {
      makeUnweightedGraph(fname);
    } else {
      throw new IllegalArgumentException("Please specify weighted or unweighted");
    }
    this.graph = new BasicGraph<String>();
    for (int i = 0; i < this.size; i++) {
      for (int j = 0; j < this.size; j++) {
        int dist = distanceArray[i][j];
        if (dist != 0) {
          // put non-wall vertices to graph 
          this.graph.addVertex(labArray[i][j]);
          // assign each non-wall vertex its weight
          this.graph.getVertex(labArray[i][j]).setWeight((double) dist); 
        }
      }
    }
    makeEdges();
    System.out.println(this.graph.toString());
  }
  
  /**********
   * helper function for MazeGraph
   * reads user's input file, collects information including 
   * non-wall vertices' labels, distances, and connections
   * and create a weighted graph
   **/
  public void makeGraph(String fname) {
    File tfile = new File(fname);
    try {
      Scanner scan = new Scanner(tfile);
      // read the first number in the file to determine the size
      // of arrays containing vertices' labels and distances
      String next = scan.next();
      this.size = Integer.parseInt(next);	
      // an array for vertices labels
      this.labArray = new String[this.size][this.size];
      // an array for vertices distances
      this.distanceArray = new int[this.size][this.size];
      String nextLine = scan.nextLine();
      // read the file word by word
      for (int i = 0; i < this.size; i++) {
        for (int j = 0; j < this.size; j++) {
          String str = scan.next();
          // add alphabets to label array
          labArray[i][j] = str.split("\\d+")[0];
          // add numbers to distance array
          distanceArray[i][j] = Integer.parseInt(str.split("[a-zA-Z]+")[1]);
        }
      }
    } catch(FileNotFoundException e) {
      System.err.println("Error, cannot find file");
      System.exit(1);
    }
  }
  
  /**********
   * helper function for MazeGraph
   * reads user's input file as an unweighted graph, collects information including 
   * non-wall vertices' labels, and connections
   **/
  public void makeUnweightedGraph(String fname) {
    File tfile = new File(fname);
    try {
      Scanner scan = new Scanner(tfile);
      // read the first number in the file to determine the size
      // of arrays containing vertices' labels and distances
      String next = scan.next();
      this.size = Integer.parseInt(next);	
      // an array for vertices labels
      this.labArray = new String[this.size][this.size];
      // an array for vertices distances
      this.distanceArray = new int[this.size][this.size];
      String nextLine = scan.nextLine();
      // read the file word by word
      for (int i = 0; i < this.size; i++) {
        for (int j = 0; j < this.size; j++) {
          String str = scan.next();
          // add alphabets to label array
          labArray[i][j] = str.split("\\d+")[0];
          // add numbers to distance array
          if (str.split("[a-zA-Z]+")[1].equals("0")) {
            distanceArray[i][j] = 0;
          } else {
           // distance is arbitrary since it's an unweighted graph
            distanceArray[i][j] = 1;
          }
        }
      }
    } catch(FileNotFoundException e) {
      System.err.println("Error, cannot find file");
      System.exit(1);
    }
  }
  
  /**********
   * helper function for MazeGraph
   * add edges between related non-wall vertices
   **/
  public void makeEdges() {
    // Note: since walls are not included in our graph, though we call addEdge 
    // on some of these "illegal" vertices, those edges will not be made
    for (int i = 1; i < this.size - 1; i++) {
      for (int j = 1; j < this.size - 1; j++) {
        this.graph.addEdge(labArray[i][j], labArray[i-1][j]);
        this.graph.addEdge(labArray[i][j], labArray[i+1][j]);
        this.graph.addEdge(labArray[i][j], labArray[i][j-1]);
        this.graph.addEdge(labArray[i][j], labArray[i][j+1]);
      }
    }
  }
  
  /**********
   * This method returns an array of a vertex's indices in 
   * label/weight arrays
   * @Param vertex label
   * @Return an array of corresponding vertex's indices
   **/
  public int[] getVertInd(String lab) {
    int[] indArray = new int[2];
    // run through label array till it finds the right vertex
    if (this.graph.hasVertex(lab)) {
      for (int i = 0; i < this.size; i++) {
        for (int j = 0; j < this.size; j++) {
          if (labArray[i][j].equals(lab)) {
            indArray[0] = i;
            indArray[1] = j;
            return indArray;
          }
        }
      } 
    }
    throw new NoSuchElementException("Vertex not found!");
  }
  
  /**********
   * This method finds a path from a start vertex to an end vertex
   * following DFS algorithm
   * @Param two vertices' labels
   * @Return a list of labels of vertices along the found path
   **/
  public List<String> solveMazeDepthFirst(String b, String e) {
    // use getVertInd to determine the position of start and end vertices
    int startRow = getVertInd(b)[0];
    int startCol = getVertInd(b)[1];
    int endRow = getVertInd(e)[0];
    int endCol = getVertInd(e)[1];
    Vertex<String> start = this.graph.getVertex(labArray[startRow][startCol]);
    Vertex<String> end = this.graph.getVertex(labArray[endRow][endCol]);
    Vertex<String> currentVert;
    LinkedList<Vertex<String>> S = new LinkedList<Vertex<String>>();
    LinkedList<Vertex<String>> VL = new LinkedList<Vertex<String>>();
    ArrayList<String> VLString = new ArrayList<String>();
    // empty start vertex's path list so that when running search multiple
    // times it doesn't show previous searched paths.
    ArrayList<Vertex<String>> startPath = new ArrayList<Vertex<String>>();
    startPath.add(start);
    start.setPath(startPath);
    S.add(start);
    while (S.size() != 0) {
      currentVert = S.removeLast();
      if (!VL.contains(currentVert)) {
        VL.add(currentVert);
        if (currentVert.getLabel().equals(end.getLabel())) {
          // copy labels of vertices along the found path to a 
          // String list
          for (int i = 0; i < currentVert.getPath().size(); i++) {
            VLString.add(i, currentVert.getPath().get(i).getLabel());
          }
          break;
        }
        else {
          List<Vertex<String>> pathList = currentVert.getPath();
          for (int i = 0; i < currentVert.getNeighbors().size(); i++) {
            Vertex<String> n = currentVert.getNeighbors().get(i);
            // resetting n's pathlist avoids overriding visited vertices
            n.setPath(new ArrayList<Vertex<String>>());
            for (int j = 0; j < pathList.size(); j++) {
              n.getPath().add(n.getPath().size(), pathList.get(j));
            }
            n.getPath().add(n.getPath().size(), n);
            S.add(n);
          }
        }
      }
    }
    return VLString;
  }
  
  /**********
   * This method finds a path from a start vertex to an end vertex
   * following BFS algorithm
   * @Param two vertices' labels
   * @Return a list of labels of vertices along the found path
   **/
  public List<String> solveMazeBreadthFirst(String b, String e) {
    // find the start and end vertices
    int startRow = getVertInd(b)[0];
    int startCol = getVertInd(b)[1];
    int endRow = getVertInd(e)[0];
    int endCol = getVertInd(e)[1];
    Vertex<String> start = this.graph.getVertex(labArray[startRow][startCol]);
    Vertex<String> end = this.graph.getVertex(labArray[endRow][endCol]);
    Vertex<String> currentVert;
    LinkedList<Vertex<String>> Q = new LinkedList<Vertex<String>>();
    LinkedList<Vertex<String>> VL = new LinkedList<Vertex<String>>();
    ArrayList<String> VLString = new ArrayList<String>();
    Q.addLast(start);
    // empty start vertex's path list so that when running search multiple
    // times it doesn't show previous searched paths.
    ArrayList<Vertex<String>> startPath = new ArrayList<Vertex<String>>();
    startPath.add(start);
    start.setPath(startPath);
    while (Q.size() != 0) {
      currentVert = Q.removeFirst();
      VL.add(currentVert);
      if (currentVert.getLabel().equals(end.getLabel())) {
        for (int i = 0; i < currentVert.getPath().size(); i++) {
          VLString.add(i, currentVert.getPath().get(i).getLabel());
        }
        break;
      }
      else {
        List<Vertex<String>> pathList = currentVert.getPath();
        for (int i = 0; i < currentVert.getNeighbors().size(); i++) {
          Vertex<String> n = currentVert.getNeighbors().get(i);
          if (!Q.contains(n) && !VL.contains(n)) {
            // resetting n's pathlist avoids overriding visited vertices
            n.setPath(new ArrayList<Vertex<String>>());
            for (int j = 0; j < pathList.size(); j++) {
              n.getPath().add(n.getPath().size(), pathList.get(j));
            }
            n.getPath().add(n.getPath().size(), n);
            Q.addLast(n);
          }
        }    
      }
    }  
    return VLString;
  }
  
  /**********
   * This method finds a path from a start vertex to an end vertex
   * following Dijkstra algorithm
   * @Param two vertices' labels
   * @Return a list of labels of vertices along the found path
   **/
  public List<String> ShortestPathSearch(String b, String e) {
    LinkedList<Vertex<String>> VL = new LinkedList<Vertex<String>>();
    ArrayList<String> VLString = new ArrayList<String>();
    VertexComparator c = new VertexComparator();
    PriorityQueue<Vertex<String>> PQ = new PriorityQueue<Vertex<String>>(c);
    // find start and end vertices
    int startRow = getVertInd(b)[0];
    int startCol = getVertInd(b)[1];
    int endRow = getVertInd(e)[0];
    int endCol = getVertInd(e)[1];
    Vertex<String> start = this.graph.getVertex(labArray[startRow][startCol]);
    Vertex<String> end = this.graph.getVertex(labArray[endRow][endCol]);
    Vertex<String> currentVert;
    PQ.add(start);
    ArrayList<Vertex<String>> startPath = new ArrayList<Vertex<String>>();
    startPath.add(start);
    start.setPath(startPath);
    start.setDistance(0);
    while (PQ.size() != 0) {
      currentVert = PQ.poll();
      if (currentVert.getLabel().equals(end.getLabel())) {
        // copy vertices labels to the list to be returned
        for (int i = 0; i < currentVert.getPath().size(); i++) {
          VLString.add(i, currentVert.getPath().get(i).getLabel());
        }
        break;
      } else {
        List<Vertex<String>> pathList = currentVert.getPath();
        for (int i = 0; i < currentVert.getNeighbors().size(); i++) {
          Vertex<String> n = currentVert.getNeighbors().get(i);
          if (!PQ.contains(n) && !VL.contains(n)) {
            // compute the edge weight between n and currentVert
            double edgeWeight = currentVert.getWeight() + n.getWeight();
            n.setDistance(currentVert.getDistance() + edgeWeight);
            // resetting n's pathlist avoids overriding visited vertices
            n.setPath(new ArrayList<Vertex<String>>());
            for (int j = 0; j < pathList.size(); j++) {
              n.getPath().add(n.getPath().size(), pathList.get(j));
            }
            n.getPath().add(n.getPath().size(), n);
            PQ.add(n);   
          }
        }
      }
    }
    return VLString;
  }

  /**********
   * test
   * Interacts with users, read a maze file, create a weighted or
   * unweighted  graph
   * give solutions corresponding to input search algorithms
   **/
  public static void main(String[] args) {
    System.out.print("Enter 'filename weighted/unweighted' : ");
    Scanner bar = new Scanner(System.in);
    MazeGraph g = new MazeGraph(bar.next(), bar.next());
    String quit = "";
    
    while (quit.equals("quit") == false) {
      System.out.print("======================================" + "\n" +
                       "type quit to quit, or" + "\n" + 
                       "type BFS, DFS, or Dijkstra for a respective solution: ");
      String decision = bar.next();
      if (decision.equals("BFS")) {
        Scanner bee = new Scanner(System.in);
        System.out.print("enter 'startVertex endVertex': ");
        String startV = bee.next();
        String endV = bee.next();      
        List<String> AL = g.solveMazeBreadthFirst(startV, endV);
        for (int i = 0; i < AL.size(); i++) {
          System.out.println(AL.get(i));
        }
      } else if (decision.equals("DFS")) {
        Scanner bee = new Scanner(System.in);
        System.out.print("enter 'startVertex endVertex': ");
        String startV = bee.next();
        String endV = bee.next();      
        List<String> AL = g.solveMazeDepthFirst(startV, endV);
        for (int i = 0; i < AL.size(); i++) {
          System.out.println(AL.get(i));
        }
      } else if (decision.equals("Dijkstra")) {
        Scanner bee = new Scanner(System.in);
        System.out.print("enter 'startVertex endVertex': ");
        String startV = bee.next();
        String endV = bee.next();      
        List<String> AL = g.ShortestPathSearch(startV, endV);
        for (int i = 0; i < AL.size(); i++) {
          System.out.println(AL.get(i));
        }
      } else if (decision.equals("quit")) {
        System.exit(0);
      } else {
        System.out.println("invalid input");
      }
    }
    
    // quick test
    /***
    System.out.println("DFS test");
    List<String> testll = g.solveMazeDepthFirst("b", "x");
    for (int i = 0; i < testll.size(); i++) {
      System.out.println(testll.get(i));
    }
    
    System.out.println("BFS test");
    List<String> testlll = g.solveMazeBreadthFirst("b", "x");
    for (int i = 0; i < testlll.size(); i++) {
      System.out.println(testlll.get(i));
    }
    
    System.out.println("Dijasdflu test");
    List<String> testll = g.ShortestPathSearch("b", "q");
    for (int i = 0; i < testll.size(); i++) {
      System.out.println(testll.get(i));
    }
    ***/
  }
}