package main;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.Color;
import java.awt.Font;
import javax.swing.ImageIcon;

public class SplashScreen {

    public JFrame frame;
    JLabel per = new JLabel("0%");
    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    SplashScreen window = new SplashScreen();
                    window.frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the application.
     */
    public SplashScreen() {
        initialize();
    }

    /**
     * Initialize the contents of the frame.
     */
    public void initialize() {
        frame = new JFrame();
        frame.getContentPane().setBackground(Color.BLACK);
        frame.setBounds(100, 100, 629, 406);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);

        JLabel lblNewLabel = new JLabel("Search Engine");
        lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 24));
        lblNewLabel.setForeground(Color.RED);
        lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
        lblNewLabel.setBounds(201, 11, 223, 72);
        frame.getContentPane().add(lblNewLabel);

        JLabel lblNewLabel_1 = new JLabel("");
        lblNewLabel_1.setIcon(new ImageIcon("C:\\Users\\Alishan Maredia\\eclipse-workspace\\IR-A2-K173757\\img\\gif.gif"));
        lblNewLabel_1.setBounds(124, 79, 403, 233);
        frame.getContentPane().add(lblNewLabel_1);


        per.setFont(new Font("Tahoma", Font.BOLD, 18));
        per.setHorizontalAlignment(SwingConstants.CENTER);
        per.setForeground(Color.RED);
        per.setBounds(261, 323, 100, 33);
        frame.getContentPane().add(per);
    }

}
