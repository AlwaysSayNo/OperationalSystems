package src.main.java.org.grynko.nazar.client;

import src.main.java.org.grynko.nazar.util.FunctionName;
import src.main.java.org.grynko.nazar.util.Token;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Pipe;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public abstract class Function implements Runnable{

    protected FunctionName name;
    protected Integer parameter;
    protected Pipe.SinkChannel channel;
    protected Integer CALCULATE_ATTEMPTS = 5;

    public Function(FunctionName name, Integer parameter, Pipe.SinkChannel channel) {
        this.name = name;
        this.parameter = parameter;
        this.channel = channel;
    }

    @Override
    public void run() {
        ByteBuffer buffer = ByteBuffer.allocate(256);
        String message;

        int calculateAttempt = 0;
        boolean isSuccess = false, isResult = false;
        Thread currThread = Thread.currentThread();

        try {
            // Checks if the number of attempts is not exceeded,
            // if the calculation is not completed, if the calculation is not interrupted
            while (calculateAttempt < this.CALCULATE_ATTEMPTS && !isResult
                    && !currThread.isInterrupted()) {

                // Switch to write mode
                buffer.clear();

                // Obtaining an external result
                Optional<Optional<Integer>> outerResult = processOperation(parameter);

                if (outerResult.isEmpty()) {
                    // If soft fail
                    message = String.format(Token.SOFT_CALCULATE_FAILURE.getMessage(),
                            name.getName(), calculateAttempt + 1, this.CALCULATE_ATTEMPTS);

                    calculateAttempt++;
                } else {
                    // Obtaining an inner result
                    Optional<Integer> innerResult = outerResult.get();

                    if (innerResult.isEmpty()) {
                        // If hard fail
                        message = String.format(Token.HARD_CALCULATE_FAILURE.getMessage(),
                                name.getName(), parameter);
                    } else {
                        // If success
                        message = String.format(Token.SUCCESS.getMessage(),
                                name.getName(), innerResult.get());

                        isSuccess = true;
                    }

                    isResult = true;
                }

                write(buffer, message);
                randomSleep(4);
            }

            if(currThread.isInterrupted()) {
                throw new InterruptedException("thread is interrupted");
            }
            else if (!isSuccess && !isResult) {
                buffer.clear();
                message = String.format(Token.HARD_ATTEMPTS_FAILURE.getMessage(),
                        name.getName(), this.CALCULATE_ATTEMPTS, parameter);
                write(buffer, message);
            }
        } catch (InterruptedException e) {
            buffer.clear();
            message = String.format(Token.HARD_EXTERNAL_FAILURE.getMessage(),
                    name.getName(), e.getMessage());
            write(buffer, message);
        }
    }

    private void write(ByteBuffer buffer, String message) {
        buffer.put(message.getBytes(StandardCharsets.UTF_8));

        // Switch to read mode
        buffer.flip();
        try {
            channel.write(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected static void randomSleep(int bound) throws InterruptedException {
        Random random = new Random();

        TimeUnit.SECONDS.sleep(random.nextInt(bound) + 1);
    }

    protected abstract Optional<Optional<Integer>> processOperation(Integer parameter) throws InterruptedException;

    public static void main(String[] args) {
        try {
            Pipe pipe = Pipe.open();

            Integer parameter = 5;
            Pipe.SourceChannel source = pipe.source();
            ByteBuffer buffer = ByteBuffer.allocate(256);

            FunctionF functionF = new FunctionF(FunctionName.F, parameter, pipe.sink());
            Thread t = new Thread(functionF);
            t.start();

            while (true) {
                int bytesNumber = source.read(buffer);
                byte[] dst = new byte[bytesNumber];


                readArray(buffer.array(), dst);

                String tmp = new String(dst);

                System.out.println(tmp);

                buffer.clear();

                t.interrupt();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void readArray(byte[] src, byte[] dst) {
        for(int i = 0; i < dst.length; ++i) {
            dst[i] = src[i];
        }
    }

}
