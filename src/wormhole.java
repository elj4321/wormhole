/*
ID: elj_4321
LANG: JAVA
TASK: wormhole
*/

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;
import java.io.PrintStream;

public class wormhole {

  private static boolean debug = false;
  private static final String task = "wormhole";
  private static PrintStream outs = System.out;
  static int numPairsWithCycle = 0;
  static List<WH> whFullList = new ArrayList<WH>();
  static int xMax = -1;

  /**
   * @param args
   */
  public static void main(String[] args) throws IOException, FileNotFoundException
  {
//    long start = System.currentTimeMillis();
    final String infile = task + ".in";
    final String outfile = task + ".out";
    PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(outfile)));
    Scanner scanr = new Scanner(new File(infile));

    // Up to N=12 wormholes, still not that many possible sets of pairs
    //      11 * 9 * 7 * 5 * 3 = 10,395
    // so brute force. For each possible set of pairings check to see if there is a cycle

    // Read in number of wormholes and wormhole coords
    int numHoles = scanr.nextInt();
//    outd("Num holes: " + numHoles);

    for (int i=0; i<numHoles; i++) {
      int hx = scanr.nextInt();
      int hy = scanr.nextInt();
      WH wh = new WH();
      wh.x = hx;
      wh.y = hy;
      whFullList.add(wh);
      if (hx > xMax) xMax = hx;
//      outd("Hole " + i + " = " + wh);
    }
//    outd("whFullList: " + whFullList.toString());
//    outd("maxX = " + xMax);

    List<WHP> whpList = new ArrayList<WHP>();
    checkR(whpList, whFullList);
//    outd("NumPairsWithCycle: " + numPairsWithCycle);
    out.println(numPairsWithCycle);
    scanr.close();
    out.close();
//    long end = System.currentTimeMillis();
//    System.out.println("Time: " + (end-start)/1000.0);
    System.exit(0);
  }

  // Recursive Check for cycles
  static void checkR(List<WHP> whpList, List<WH> whList) {
    // If incoming list of remaining WHs is empty, then we have
    //   constructed a full set of pairs, time to check for cycles
    //   and return
    if (whList.isEmpty())
    {
//      outd("Checking WHPList: " + whpList.toString());
      if (checkForCycles(whpList)) numPairsWithCycle++ ;
      return;
    }
    // We have more WHPs to construct, keep going
    for (int i = 0; i < whList.size()-1; i++)
    {
      List<WH> whList2 = new ArrayList<WH>(whList);
      WH wh1 = whList2.remove(0);
      WH wh2 = whList2.remove(i);
      WHP whp = new WHP();
      whp.wh1 = wh1;
      whp.wh2 = wh2;
      List<WHP> whpList2 = new ArrayList<WHP>(whpList);
      whpList2.add(whp);
      checkR(whpList2, whList2);
    }
    return;
  }

  // Check for cycles
  // Start at each wormhole
  // Proceed right until we either arrive at the right edge
  // or we visit a previously visited wormhole
  static boolean checkForCycles(List<WHP> whpList)
  {
    // Start at each wormhole as if about to enter the wormhole
    for (WH wh : whFullList)
    {
//      outd("Check start WH: " + wh);
      // Go through the WH and see where we land.
      // If we ever land there again then we have a cycle
      WH exitWH = traverseWH(wh, whpList);
      
      int startX = exitWH.x + 1;
      int startY = exitWH.y;
      int xx = startX;
      int yy = startY;
//      int junk = 0;
      while (true)
      {
        // Now move right until we reach the edge or hit a cycle
        WH nextWH = findNextWH(xx,yy);
        // If no WHs to right then no cycles for initial starting WH
        if (nextWH == null)
        {
//          outd("No WHs to right. No cycles for start WH: " + wh);
          break;
        }
        // Pass through the WH ending somewhere else.
        exitWH = traverseWH(nextWH, whpList);
        xx = exitWH.x+1;
        yy = exitWH.y;
        // If back to start then we have a cycle
        if (xx == startX && yy == startY)
        {
//          outd("Found cycle");
          return true;
        }
//        junk++;
//        if (junk > 100) System.exit(1);
      }
    }
    return false;
//  outd(" WHPList size = " + whpList.size());
  // Return true so for now we are simply counting number checked
//  return true;
  }

//  static void outd(String msg)
//  {
//    if (debug) outs.println(msg);
//  }

  // Given a wormhole find the paired wormhole
  static WH traverseWH(WH whA, List<WHP> whpList)
  {
    WH whB = null;
    for (WHP whp : whpList)
    {
      if (whA == whp.wh1)
      {
        whB = whp.wh2;
        break;
      }
      else if (whA == whp.wh2)
      {
        whB = whp.wh1;
        break;
      }
    }
//    outd("Enter: " + whA + " Exit: " + whB);
    return whB;
  }

  // Given a position find next WH to right (if any)
  // NOTE: If WH is at our given position then it is the next WH
  // return null if no WH to right
  static WH findNextWH(int x1, int y1)
  {
    WH wh2 = null;
    int xMin = xMax+1;
    for (WH wh : whFullList)
    {
      // WH is to right if y's match and x >= given x
      if (wh.y == y1 && wh.x >= x1)
      {
        // If the WH is closer than any previously found then use it
        if (wh.x < xMin)
        {
          xMin = wh.x;
          wh2 = wh;
        }
      }
    }
    return wh2;
  }

}

class WH {
  int x = -1; int y = -1;
//  public String toString()
//  {
//    return "(" + x + "," + y + ")";
//  }
}
class WHP {
  WH wh1 = null; WH wh2 = null;
//  public String toString()
//  {
//    return "WHP: " + wh1 + "->" + wh2;
//  }
}
