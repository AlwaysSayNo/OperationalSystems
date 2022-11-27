package main.java.org.grynko.nazar;

import main.java.org.grynko.nazar.model.Results;
import main.java.org.grynko.nazar.model.sProcess;

import java.util.Vector;
import java.io.*;

public class SchedulingAlgorithm {
  public static Results run(int runtime, int quantum, Vector<sProcess> processVector, String resultsFile) {
    int compTime = 0;
    int currentProcess = 0;
    int previousProcess;
    int size = processVector.size();
    int completed = 0;
    boolean needSwitch = false;
    boolean isPreempted = false;

    Results result = new Results("Interactive (Preemptive)", "Guaranteed", 0);

    sProcess process = (sProcess) processVector.elementAt(currentProcess);
    long start = System.currentTimeMillis();

    try (PrintStream out = new PrintStream(new FileOutputStream(resultsFile))){
      out.println(getMessage("registered", process, start, currentProcess, compTime, size, needSwitch));

      while (compTime < runtime) {
        if (process.cpuDone == process.cpuTime) {
          completed++;
          out.println(getMessage("completed", process, start, currentProcess, compTime, size, needSwitch));

          if (completed == size) break;

          needSwitch = true;
        }
        else if (process.ioBlocking == process.ioNext) {
          out.println(getMessage("I/O blocked", process, start, currentProcess, compTime, size, needSwitch));

          process.numBlocked++;
          process.ioNext = 0;
          process.preemptedNext = 0;
          needSwitch = true;
        }
        else if (process.preemptedNext == quantum) {
          out.println(getMessage("preempted", process, start, currentProcess, compTime, size, needSwitch));

          process.numPreempted++;
          process.preemptedNext = 0;
          needSwitch = true;
          isPreempted = true;
        }
        if (needSwitch) {
          double ratio;
          double minRatio = Double.MAX_VALUE;
          previousProcess = currentProcess;

          for (int i = 0; i < size; i++) {
            process = (sProcess) processVector.elementAt(i);
            ratio = (double) process.cpuDone / ((double) compTime / size);

            if (process.cpuDone < process.cpuTime && ratio < minRatio && (i != previousProcess || isPreempted)) {
              minRatio = ratio;
              currentProcess = i;
            }
          }

          process = (sProcess) processVector.elementAt(currentProcess);
          out.println(getMessage("registered", process, start, currentProcess, compTime, size, needSwitch));
          needSwitch = false;
          isPreempted = false;
        }

        process.cpuDone++;
        process.preemptedNext++;
        if (process.ioBlocking > 0) {
          process.ioNext++;
        }
        compTime++;
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    result.compuTime = compTime;
    return result;
  }

  private static String getMessage(String message, sProcess process,
                            long start, int currentProcess, int compTime, int size, boolean needSwitch) {
      String currentRatio = "0/0";
      if(!(message.equals("registered") && !needSwitch))
        currentRatio = changeRation(compTime, size);

      String result = "Time elapsed: " + (System.currentTimeMillis() - start) + "ms\tProcess: " + currentProcess +
              " %s... (" + process.cpuTime + " " + process.ioBlocking + " " + process.cpuDone + " " +
              currentRatio + ")";

      return String.format(result, message);
  }

  private static String changeRation(int compTime, int size) {
      return "(" + compTime + "/" + size + ")";
  }

}