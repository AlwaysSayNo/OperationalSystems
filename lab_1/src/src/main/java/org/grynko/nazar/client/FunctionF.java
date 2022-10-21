package src.main.java.org.grynko.nazar.client;

import src.main.java.org.grynko.nazar.operation.IntOpsWrapper;
import src.main.java.org.grynko.nazar.util.FunctionName;

import java.nio.channels.Pipe;
import java.util.Optional;

public class FunctionF extends Function{

    public FunctionF(FunctionName name, Integer parameter, Pipe.SinkChannel channel) {
        super(name, parameter, channel);
    }

    @Override
    protected Optional<Optional<Integer>> processOperation(Integer parameter) throws InterruptedException {
        return IntOpsWrapper.trialF(parameter);
    }



}
