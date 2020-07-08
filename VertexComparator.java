import java.util.Comparator;
import java.lang.*;

/*********************
 * @Authors: Angel Villa, Qimeng Yu
 *
 * Vertex comparator
 *********************/
 
public class VertexComparator implements Comparator<Vertex<String>> {
  
  /******************
   * takes two vertices as input and compare them by their distances 
   * return -1 if the first vertex has shorter distance
   * return 0 if the same; return 1 otherwise
   **/
  public int compare(Vertex<String> a, Vertex<String> b) {
    if (Math.abs(a.getDistance() - b.getDistance()) < 0.001) {
      return 0;
    } else if (a.getDistance() - b.getDistance() < -0.001 ) {
      return -1;
    } else {
      return 1;
    }
  }
}