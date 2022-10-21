package src.main.java.org.grynko.nazar.manager;

import src.main.java.org.grynko.nazar.util.TokenPattern;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Pipe;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ManagerDaemon implements Runnable{

    private final Pipe.SourceChannel channelFromClient;
    private final Manager manager;

    public ManagerDaemon(Manager manager, Pipe.SourceChannel channelFromClient) {
        this.manager = manager;
        this.channelFromClient = channelFromClient;
    }

    @Override
    public void run() {
        ByteBuffer buffer = ByteBuffer.allocate(256);
        boolean isFinished = false;

        try {
            while (!isFinished) {
                int byteNumber = channelFromClient.read(buffer);
                String tmp = readArray(buffer.array(), byteNumber);

                isFinished = processMessage(tmp);

                buffer.clear();
            }
        } catch (IOException | IllegalStateException e) {
            System.out.println("The manager`s daemon was forcibly terminated");
        }
    }

    private boolean processMessage(String message) {
        if (message != null) {
            // Is success
            if(checkByPattern(message, TokenPattern.SUCCESS)){
                Integer value = getResultFromString(message);
                handleFunctionSuccess(message);

                manager.tryCalculate(value);
            }
            else if(checkByPattern(message, TokenPattern.SOFT_CALCULATE_FAILURE)){
                // Is soft failure
                manager.handleSoftFail(message);
                return false;
            }
            else if(checkByPattern(message, TokenPattern.HARD_CALCULATE_FAILURE)
                    || checkByPattern(message, TokenPattern.HARD_ATTEMPTS_FAILURE)
                    || checkByPattern(message, TokenPattern.HARD_EXTERNAL_FAILURE)){
                // Is hard failure
                manager.handleHardFail(message);
            }
            else {
                throw new RuntimeException();
            }
        }
        return true;
    }

    private boolean checkByPattern(String message, TokenPattern tokenPattern) {
        return message.matches(tokenPattern.getValue());
    }

    private Integer getResultFromString(String message) {
        String resultStr = getFromString(message, TokenPattern.RESULT);
        return Integer.parseInt(resultStr);
    }

    private String getFromString(String message, TokenPattern tokenPattern) {
        Pattern p = Pattern.compile(tokenPattern.getValue());
        Matcher m = p.matcher(message);

        if(m.find()){
            String result = m.group();
            return result.replace('[', ' ').replace(']', ' ').trim();
        }
        throw new IllegalStateException("Invalid result message");
    }

    private static String readArray(byte[] src, int byteNumber) {
        byte[] dst = new byte[byteNumber];

        for(int i = 0; i < dst.length; ++i) {
            dst[i] = src[i];
        }

        return new String(dst);
    }

    private void handleFunctionSuccess(String message) {
        System.out.println(message);
    }

}
