package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

final class GUI extends JFrame {

    private GUI() {
        new Thread(GUI::run).start();
        init();
    }

    private static void run() {
        try {
            Robot r = new Robot();
            while (true) {
               
                r.keyRelease(KeyEvent.VK_CAPS_LOCK);
                Thread.sleep(5);
            }
        } catch (Exception ignored) {
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
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setSize(1000, 500);
        setVisible(true);
    }


    private JButton makeButton(String s) {
        JButton b = new JButton(s);
        b.setFont(new Font("Arial", Font.PLAIN, 40));
        b.addMouseListener(new MouseAdapter() {
            Thread t;
            boolean kill;
            @Override
            public void mouseEntered(MouseEvent e) {
                b.setText("FOCUSED");
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
