package src.main.java.org.grynko.nazar.operation;

import os.lab1.compfuncs.advanced.IntOps;

import java.util.Optional;
import java.util.Random;

// 0 - (F 0.5, G 0.5) success
// 2 - (G 0.5) success, (F 0.1) lock
// 2 - (G 0.9) attempt failure, (F 0.1) lock
// 1 - (G 0.5) calculate failure, (F 0.5) lock
public class IntOpsWrapper {

    private static final double F_SOFT_FAIL_BORDER = 0.5;
    private static final double G_SOFT_FAIL_BORDER = 0.5;

    private static final Random random = new Random(10);

    public static Optional<Optional<Integer>> trialF(Integer parameter) throws InterruptedException {
        double successRate = random.nextDouble();

        if(successRate < F_SOFT_FAIL_BORDER) {
            return Optional.empty();
        }

        return IntOps.trialF(parameter);
    }

    public static Optional<Optional<Integer>> trialG(Integer parameter) throws InterruptedException {
        double successRate = random.nextDouble();

        if(successRate < G_SOFT_FAIL_BORDER) {
            return Optional.empty();
        }

        return IntOps.trialG(parameter);
    }

}
