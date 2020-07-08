import java.util.Random;
import java.io.*;

/**************************************
 * Maze class - A class to generate random mazes.
 *
 * Written by Sherri Goings, adapted by Andy Exley
 * May 22, 2016
 ************************/
public class Maze {

  private int size;
  private Cell[][] grid;

  private int totalnumcells;

  /********************
   * Constructor that builds a maze with the given grid size
   *******************/
  public Maze(int gridSize) {
    Random randw = new Random();
    size = gridSize + 2;
    totalnumcells = (2*gridSize+1) * (2*gridSize+1);
    grid = new Cell[size][size];
    for (int i = 0; i < size; i++) {
      for (int j = 0; j < size; j++) {
        boolean vis = false;
        if (i == 0 || i == size - 1 || j == 0 || j == size-1) {
          vis = true;
        }
        grid[i][j] = new Cell(vis, randw.nextInt(8) + 1);
      }
    }
  }
   
  public void visit(int prevrow, int prevcol, int row, int col) {
    if (grid[row][col].visited) {
      return;
    }
    if (prevrow >= 0) {
      if (prevcol > col) {   //moved to the west
        grid[row][col].removeWall('e');
        grid[prevrow][prevcol].removeWall('w');
      } else if (prevcol < col) {
        grid[row][col].removeWall('w');
        grid[prevrow][prevcol].removeWall('e');
      } else {
        if (prevrow > row) {
          grid[row][col].removeWall('s');
          grid[prevrow][prevcol].removeWall('n');
        } else {
          grid[row][col].removeWall('n');
          grid[prevrow][prevcol].removeWall('s');
        }
      }
    }
    grid[row][col].visited = true;
    int[][] indices = getNeighborIndicesRandomOrder(row,col);

    for (int i=0; i<indices.length; i++) {
      visit(row,col,indices[i][0],indices[i][1]);
    }
  }

  public int[][] getNeighborIndicesRandomOrder(int row, int col) {
    int[][] indices = new int[][] {{row,col-1},{row,col+1},{row-1,col},{row+1,col}};
  
    Random rgen = new Random();
    for (int i=0; i<indices.length; i++) {
      int randomPosition = rgen.nextInt(indices.length);
      int[] temp = indices[i];
      indices[i] =indices[randomPosition];
      indices[randomPosition] = temp;
    }
    return indices;
  }

  public String toString() {
    String retStr = " S ";
    for (int i=0; i<size-3; i++) {
      retStr += "__";
    }
    retStr+='\n';

    for (int i=1; i<size-1; i++) {
      String arow = "|";
      for (int j=1; j<size-1; j++) {
        Cell acell = grid[i][j];
        if (acell.walls[1]=='s') {
          if (acell.walls[2]=='e') {
            arow += "_|";
          } else {
            arow += "__";
          }
        } else {
          if (acell.walls[2]=='e') {
            arow += " |";
          } else {
            arow += "  ";
          }
        }
      }
      retStr += arow+'\n';
    }
    retStr = retStr.substring(0,retStr.length()-4)+" E \n";
    return retStr;
  }

  /***************
   * Used to create unique tags for cells in the maze
   */
  private String nextTag(String curTag) {
    if(curTag == null) {
      System.out.println(totalnumcells);
      if(totalnumcells < 26) { 
        return "a";
      } else if (totalnumcells < 26*26) {
        return "aa";
      } else {
        return "aaa";
      }
    } else {
      char last = curTag.charAt(curTag.length() - 1);
      if(last < 'z') {
        return curTag.substring(0, curTag.length() - 1) + ((Character)((char)(last+1))).toString();
      } else if(totalnumcells < 26*26) {
        last = 'a';
        return "" + ((Character)((char)(curTag.charAt(0)+1))).toString() + last;
      } else {
        last = 'a';
        if(curTag.charAt(1) < 'z') {
          return "" + ((Character)((char)(curTag.charAt(0)))).toString() + ((Character)((char)(curTag.charAt(1)+1))).toString() + 'a';
        } else {
          return "" + ((Character)((char)(curTag.charAt(0)+1))).toString() + "aa";
        }
      }
    }
  }

  public void writeToFileOneZeroFormat(String filename) {
    // remove upper left and lower right walls so will have start and end
    grid[1][1].removeWall('n');
    grid[size-2][size-2].removeWall('s');
    String tag = nextTag(null);
    try{
      Random rgen = new Random();
      FileWriter fstream = new FileWriter(filename);
      BufferedWriter out = new BufferedWriter(fstream);
      out.write("" + ((size - 2) * 2 + 1) + " " + ((size - 2) * 2 + 1) + "\n");
      for (int row = 1; row < size - 1; row++) {
        // write next 2 lines of the maze file
        // write connector line
        String line = tag + "0 ";
        tag = nextTag(tag);
        for (int col = 1; col < size - 1; col++) {
          if (grid[row][col].walls[0] == 'n' && (row == 1 || rgen.nextInt(4) > 2)) {
            line += tag + "0 ";
            tag = nextTag(tag);
          } else {
            //line += "1 ";
            line += tag + grid[row][col].getRandVal() + " ";
            tag = nextTag(tag);
          }
          if (row == size && col == size - 2) {
            line += tag + "1 ";
            tag = nextTag(tag);
            //line += grid[row][col] + " ";
          } else {
            line += tag + "0 ";
            tag = nextTag(tag);
          }
        }
        out.write(line + "\n");
        // write cell containing line
        line = tag + "0 ";
        tag = nextTag(tag);
        for (int col = 1; col < size - 1; col++) {
          //line += "1 ";
          line += tag + grid[row][col].getRandVal() + " ";
          tag = nextTag(tag);
          if (grid[row][col].walls[2] == 'e') {
            line += tag + "0 ";
          } else {
            line += tag + grid[row][col].getRandVal() + " ";
            //line += "1 ";
          }
          tag = nextTag(tag);
        }
        out.write(line + "\n");
      }
      // write final line of maze file
      String line = tag + "0 ";
      tag = nextTag(tag);
      for (int col = 1; col < size - 1; col++) {
        if (grid[size-2][col].walls[1] == 's') {
          line += tag + "0 ";
        } else {
          line += tag + "1 ";
          //line += grid[row][col] + " ";
        }
        tag = nextTag(tag);
        line += tag + "0 ";
        tag = nextTag(tag);
      }
      out.write(line + "\n");
      out.close();
    } catch (Exception e) { 
      System.err.println("Error: " + e.getMessage()); 
    }
  }

  /*******************
   * private class Cell, represents a single maze cell
   ***************/
  private class Cell {
    private char[] walls;
    private boolean visited;
    private int value;

    public Cell(boolean vis, int val) {
      walls = new char[] {'n','s','e','w'};
      visited = vis;
      value = val;
    }
  
    public void removeWall(char wall) {
      for (int i=0; i<4; i++) {
        if (walls[i]==wall) {
          walls[i]='-';
          break;
        }
      }
    }
  
    public String toString() {
    /*
      // herein lies old toString
      String retStr = "";
      for (int i=0; i<4; i++) {
        retStr += walls[i];
      }
      if (visited==true) {
        retStr+='v';
      } else {
        retStr+=' ';
      }
      return retStr;*/
      return "" + value;
    }

    public String getRandVal() {
      Random r = new Random();
      return "" + (value + r.nextInt(2));
    }
          
  }
    
  public static void main(String args[]) {
    if (args.length<1) {
      System.out.println("usage: java Maze.java size");
      return;
    }
    Maze maze = new Maze(Integer.parseInt(args[0]));
    maze.visit(-1,-1,1,1);
//    System.out.println(maze);
    System.out.println("Maze written to maze" + args[0] + ".txt");
    maze.writeToFileOneZeroFormat("maze" + args[0] + ".txt");
  }
}
