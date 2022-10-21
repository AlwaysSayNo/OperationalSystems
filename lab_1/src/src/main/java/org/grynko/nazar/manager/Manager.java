package src.main.java.org.grynko.nazar.manager;

import src.main.java.org.grynko.nazar.client.Function;
import src.main.java.org.grynko.nazar.client.FunctionF;
import src.main.java.org.grynko.nazar.client.FunctionG;
import src.main.java.org.grynko.nazar.util.FunctionName;

import java.io.IOException;
import java.nio.channels.Pipe;
import java.util.ArrayList;
import java.util.List;

public class Manager {

    private final List<Integer> results;
    private final List<Thread> daemons;
    private final List<Thread> clients;


    public Manager() {
        this.results = new ArrayList<>();
        this.daemons = new ArrayList<>();
        this.clients = new ArrayList<>();
    }

    public void run(Integer parameter) throws IOException, InterruptedException {
        processClient(FunctionName.F, parameter);
        processClient(FunctionName.G, parameter);

        for(Thread thread: daemons) {
            thread.join();
        }
    }

    public void stop() {
        for(Thread thread: this.daemons) {
            thread.interrupt();
        }

        for(Thread thread: this.clients) {
            thread.interrupt();
        }

        System.exit(0);
    }

    public void tryCalculate(Integer value) {
        synchronized (results) {
            results.add(value);

            if(results.size() == 2) {
                Integer firstResult = results.get(0);
                Integer secondResult = results.get(1);

                //binary operation
                System.out.println("Final result: " + firstResult + "*" +  secondResult + "=" + firstResult*secondResult);
            }

        }
    }

    public void handleHardFail(String message){
        System.out.println(message);
        System.out.println("Stop application");

        stop();
    }

    public void handleSoftFail(String message){
        System.out.println(message);
    }

    private void processClient(FunctionName functionName, Integer parameter) throws IOException {
        Pipe pipe = Pipe.open();

        startDaemon(pipe);
        startClient(pipe, functionName, parameter);
    }

    private void startDaemon(Pipe pipe) {
        ManagerDaemon daemonRunnable = new ManagerDaemon(this, pipe.source());

        Thread daemon = new Thread(daemonRunnable);
        this.daemons.add(daemon);

        daemon.start();
    }

    private void startClient(Pipe pipe, FunctionName functionName, Integer parameter) {
        Function clientRunnable = getClient(functionName, parameter, pipe.sink());

        Thread client = new Thread(clientRunnable);
        this.clients.add(client);

        client.start();
    }

    private Function getClient(FunctionName functionName, Integer parameter, Pipe.SinkChannel channel) {
        Function result = null;

        switch (functionName) {
            case F: {
                result = new FunctionF(functionName, parameter, channel);
                break;
            }
            case G: {
                result = new FunctionG(functionName, parameter, channel);
            }
        }

        return result;
    }

}
