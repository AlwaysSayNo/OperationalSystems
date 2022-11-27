package main.java.org.grynko.nazar.model;

public class sProcess {
  public int cpuTime;
  public int ioBlocking;
  public int cpuDone;
  public int ioNext;
  public int numBlocked;
  public int preemptedNext;
  public int numPreempted;


  public sProcess (int cpuTime, int ioBlocking, int cpuDone, int ioNext, int numBlocked, int preemptedNext,
                   int numPreempted) {
    this.cpuTime = cpuTime;
    this.ioBlocking = ioBlocking;
    this.cpuDone = cpuDone;
    this.ioNext = ioNext;
    this.numBlocked = numBlocked;
    this.preemptedNext = preemptedNext;
    this.numPreempted = numPreempted;
  } 	
}
