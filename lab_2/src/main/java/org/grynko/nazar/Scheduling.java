package main.java.org.grynko.nazar;
import main.java.org.grynko.nazar.model.Results;
import main.java.org.grynko.nazar.model.sProcess;

import java.io.*;
import java.util.*;

public class Scheduling {

  private static int processnum = 5;
  private static int meanDev = 1000;
  private static int standardDev = 100;
  private static int runtime = 1000;
  private static int quantum = 50;
  private static final Vector<sProcess> processVector = new Vector<>();
  private static Results result = new Results("null","null",0);
  private static String resultsFile = "C:/Users/Admin/IntelliJProjects/OperationalSystems/lab_2/src/main/resources/file/Summary-Results";
  private static String logFile = "C:/Users/Admin/IntelliJProjects/OperationalSystems/lab_2/src/main/resources/file/Summary-Processes";

  private static void Init(String file) {
    File f = new File(file);
    String line;
    String tmp;
    int cputime = 0;
    int ioblocking = 0;
    double X = 0.0;

    try {
      //BufferedReader in = new BufferedReader(new FileReader(f));
      DataInputStream in = new DataInputStream(new FileInputStream(f));
      while ((line = in.readLine()) != null) {
        if (line.startsWith("numprocess")) {
          StringTokenizer st = new StringTokenizer(line);
          st.nextToken();
          processnum = Common.s2i(st.nextToken());
        }
        if (line.startsWith("meandev")) {
          StringTokenizer st = new StringTokenizer(line);
          st.nextToken();
          meanDev = Common.s2i(st.nextToken());
        }
        if (line.startsWith("standdev")) {
          StringTokenizer st = new StringTokenizer(line);
          st.nextToken();
          standardDev = Common.s2i(st.nextToken());
        }
        if (line.startsWith("process")) {
          StringTokenizer st = new StringTokenizer(line);
          st.nextToken();
          ioblocking = Common.s2i(st.nextToken());
          X = Common.R1();
          while (X == -1.0) {
            X = Common.R1();
          }
          X = X * standardDev;
          cputime = (int) X + meanDev;
          processVector.addElement(new sProcess(cputime, ioblocking, 0, 0, 0, 0,
                  0));
        }
        if (line.startsWith("runtime")) {
          StringTokenizer st = new StringTokenizer(line);
          st.nextToken();
          runtime = Common.s2i(st.nextToken());
        }
        if (line.startsWith("quantum")) {
          StringTokenizer st = new StringTokenizer(line);
          st.nextToken();
          quantum = Common.s2i(st.nextToken());
        }
        if (line.startsWith("summary_file")) {
          StringTokenizer st = new StringTokenizer(line);
          st.nextToken();
          resultsFile = "C:/Users/Admin/IntelliJProjects/OperationalSystems/lab_2/src/main/resources/file/" + st.nextToken();
        }
        if (line.startsWith("log_file")) {
          StringTokenizer st = new StringTokenizer(line);
          st.nextToken();
          logFile = "C:/Users/Admin/IntelliJProjects/OperationalSystems/lab_2/src/main/resources/file/" + st.nextToken();
        }
      }
      in.close();
    } catch (IOException e) { /* Handle exceptions */ }
  }

  private static void debug() {
    int i = 0;

    System.out.println("processnum " + processnum);
    System.out.println("meandevm " + meanDev);
    System.out.println("standdev " + standardDev);
    System.out.println("quantum " + quantum);
    int size = processVector.size();
    for (i = 0; i < size; i++) {
      sProcess process = (sProcess) processVector.elementAt(i);
      System.out.println("process " + i + " " + process.cpuTime + " " + process.ioBlocking + " " + process.cpuDone + " " + process.numBlocked);
    }
    System.out.println("runtime " + runtime);
  }

  public static void main(String[] args) {
    int i = 0;
    String path = "C:/Users/Admin/IntelliJProjects/OperationalSystems/lab_2/src/main/resources/scheduling.conf";

    File f = new File(path);
    if (!(f.exists())) {
      System.out.println("Scheduling: error, file '" + f.getName() + "' does not exist.");
      System.exit(-1);
    }
    if (!(f.canRead())) {
      System.out.println("Scheduling: error, read of " + f.getName() + " failed.");
      System.exit(-1);
    }
    System.out.println("Working...");
    Init(path);
    if (processVector.size() < processnum) {
      i = 0;
      while (processVector.size() < processnum) {
        double X = Common.R1();
        while (X == -1.0) {
          X = Common.R1();
        }
        X = X * standardDev;
        int cputime = (int) X + meanDev;
        processVector.addElement(new sProcess(cputime,i*100,0,0,0, 0, 0));
        i++;
      }
    }
    result = SchedulingAlgorithm.run(runtime, quantum, processVector, logFile);
    try {
      PrintStream out = new PrintStream(new FileOutputStream(resultsFile));
      out.println("Scheduling Type: " + result.schedulingType);
      out.println("Scheduling Name: " + result.schedulingName);
      out.println("Simulation Run Time: " + result.compuTime);
      out.println("Mean: " + meanDev);
      out.println("Standard Deviation: " + standardDev);
      out.println("Quantum: " + quantum);
      out.println("Process #\tCPU Time\tIO Blocking\tCPU Completed\tCPU Blocked\tCPU Preempted");
      for (i = 0; i < processVector.size(); i++) {
        sProcess process = (sProcess) processVector.elementAt(i);
        out.print(Integer.toString(i));
        if (i < 100) { out.print("\t\t"); } else { out.print("\t"); }
        out.print(Integer.toString(process.cpuTime));
        if (process.cpuTime < 100) { out.print(" (ms)\t\t"); } else { out.print(" (ms)\t"); }
        out.print(Integer.toString(process.ioBlocking));
        if (process.ioBlocking < 100) { out.print(" (ms)\t\t"); } else { out.print(" (ms)\t"); }
        out.print(Integer.toString(process.cpuDone));
        if (process.cpuDone < 100) { out.print(" (ms)\t\t"); } else { out.print(" (ms)\t"); }
        out.print(process.numBlocked + " times\t\t");
        out.println(process.numPreempted + " times");
      }
      out.close();
    } catch (IOException e) { /* Handle exceptions */ }
    System.out.println("Completed.");
  }
}
