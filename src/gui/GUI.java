package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

final class GUI extends JFrame {

    private static ServerSocket server;
    private static final int PORT = 1234;

    private GUI() {
        init();
        new Thread(GUI::run).start();
    }

    private static void startServer() {
        try {
            server = new ServerSocket(PORT, 0, InetAddress.getLoopbackAddress());
            System.out.println("--* Starting server " + server.toString());
        } catch (IOException e) {
            System.err.println("IO Exception: " + e.getMessage());
            System.exit(1);
        }
    }

    private static void run() {
        startServer();

        while (true) {
            try {
                Socket connection = server.accept();
                System.out.println("--> New connection ... "
                        + connection.getInetAddress().getHostName() + ":"
                        + connection.getPort());
                Robot r = new Robot();
                int px = 0, py = 0;
                boolean start = true;
                while (true) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String message = reader.readLine().trim();
                    // maybe check for a short/long blink for selection?
                    System.out.println(message);
                    String[] coords = message.split(":");
                    int x = Math.round(Integer.parseInt(coords[0]));
                    int y = Math.round(Integer.parseInt(coords[1]));
                    if (start) {
                        px = x;
                        py = y;
                        r.mouseMove(x, y);
                        start = false;
                    }
                    if (Math.abs(px - x) > 100 || Math.abs(py - y) > 100) {
                        r.mouseMove(x, y);
                        px = x;
                        py = y;
                    }
                }
            } catch (IOException e) {
                System.err.println("IO Exception: " + e.getMessage());
            } catch (NullPointerException ignored) {
                System.out.println("Client disconnected.");
            } catch (AWTException e) {
                System.err.println("AWT Exception: " + e.getMessage());
            }
        }
    }

    private void init() {
        JPanel panel = new JPanel(new GridLayout(1, 2));
        List<JComponent> components = new ArrayList<JComponent>() {{
            add(makeButton("YES"));
            add(makeButton("NO"));
        }};
        components.forEach(panel::add);
        add(panel);
        addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                System.out.println("Quit.");
                System.exit(0);
            }
        });
        setFocusable(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        //setSize(1000, 500);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setVisible(true);

    }


    private JButton makeButton(String s) {
        JButton b = new JButton(s);
        b.setFont(new Font("Arial", Font.PLAIN, 80));
        b.addMouseListener(new MouseAdapter() {
            Thread t;
            boolean kill;
            @Override
            public void mouseEntered(MouseEvent e) {
                b.setForeground(Color.RED);
                kill = false;
                t = new Thread(() -> {
                    int secs = 0;
                    while (true) {
                        try {
                            Thread.sleep(1000);
                        } catch (Exception ignored) {}
                        if (kill) {
                            break;
                        }
                        if (secs == 2) {
                            b.setForeground(Color.GREEN);
                            b.setText("SELECTED");
                        }
                        System.out.println(s + " FOCUSED FOR " + ++secs + " SECONDS");
                    }
                });
                t.start();

            }

            @Override
            public void mouseExited(MouseEvent e) {
                b.setText(s);
                b.setForeground(Color.BLACK);
                kill = true;
            }
        });
        return b;
    }

    public static void main(String[] args) {
        new GUI();
    }

}
