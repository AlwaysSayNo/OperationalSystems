package src.main.java.org.grynko.nazar;

import lc.kra.system.keyboard.GlobalKeyboardHook;
import lc.kra.system.keyboard.event.GlobalKeyAdapter;
import lc.kra.system.keyboard.event.GlobalKeyEvent;
import src.main.java.org.grynko.nazar.manager.Manager;

import java.io.IOException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("Press Ctrl+Q to quit the application");
        System.out.println("Please enter x:");

        Manager manager = new Manager();
        applicationQuitListener(manager);

        Scanner scanner = new Scanner(System.in);
        Integer parameter = scanner.nextInt();

        manager.run(parameter);

    }

    private static void applicationQuitListener(Manager manager) {
        GlobalKeyboardHook keyboardHook = new GlobalKeyboardHook(true);
        keyboardHook.addKeyListener(new GlobalKeyAdapter() {
            @Override
            public void keyPressed(GlobalKeyEvent event) {
                if (event.getVirtualKeyCode() == GlobalKeyEvent.VK_Q && event.isControlPressed()) {
                    System.out.println("User stopped the application");
                    manager.stop();
                }
            }
        });
    }

}
