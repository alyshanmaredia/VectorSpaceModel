package main;

import java.awt.EventQueue;

import javax.swing.*;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class GUI {

    public JFrame frame;
    public String query;
    public static String count ;
    private JPanel panel1;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    GUI window = new GUI();
                    window.frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the application.
     * @throws FileNotFoundException
     */
    public GUI() throws FileNotFoundException {
        initialize();

    }
    public void clear() {
        for (Map.Entry<String,List<Integer>> entry : VSM.dictionary.entrySet()){
            List<Integer> termList = new ArrayList<>();
            termList= entry.getValue();
            termList.set(56, 0);
            VSM.dictionary.put(entry.getKey(), termList);
        }
        VSM.idfFinal.clear();
        VSM.wtgMap.clear();
        VSM.query.clear();
        VSM.DocID.clear();
    }

    /**
     * Initialize the contents of the frame.
     */
    public void initialize() {
        frame = new JFrame();
        frame.getContentPane().setForeground(new Color(135, 206, 250));
        frame.getContentPane().setBackground(new Color(160, 82, 45));
        frame.setBounds(100, 100, 533, 390);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);


        JLabel lblEnterQuery = new JLabel("Enter Query");
        lblEnterQuery.setForeground(new Color(154, 205, 50));
        lblEnterQuery.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 20));
        lblEnterQuery.setHorizontalAlignment(SwingConstants.CENTER);
        lblEnterQuery.setBounds(0, 75, 228, 33);
        frame.getContentPane().add(lblEnterQuery);

        JLabel lblValue = new JLabel("Value");
        lblValue.setForeground(new Color(154, 205, 50));
        lblValue.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 16));
        lblValue.setHorizontalAlignment(SwingConstants.CENTER);
        lblValue.setBounds(429, 82, 65, 22);
        frame.getContentPane().add(lblValue);

        JScrollPane scrollPane_1 = new JScrollPane();
        scrollPane_1.setBounds(18, 113, 375, 56);
        frame.getContentPane().add(scrollPane_1);

        JTextArea QueryArea = new JTextArea();
        QueryArea.setFont(new Font("Monospaced", Font.BOLD | Font.ITALIC, 18));
        QueryArea.setForeground(new Color(0, 0, 255));
        scrollPane_1.setViewportView(QueryArea);
        QueryArea.setBackground(new Color(102, 205, 170));

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(18, 225, 375, 115);
        frame.getContentPane().add(scrollPane);

        JTextArea AnswerArea = new JTextArea();
        AnswerArea.setFont(new Font("Monospaced", Font.BOLD | Font.ITALIC, 18));
        AnswerArea.setForeground(new Color(0, 0, 255));
        scrollPane.setViewportView(AnswerArea);
        AnswerArea.setBackground(new Color(102, 205, 170));

        JScrollPane scrollPane_2 = new JScrollPane();
        scrollPane_2.setBounds(439, 115, 65, 56);
        frame.getContentPane().add(scrollPane_2);

        JTextArea ValueArea = new JTextArea();
        ValueArea.setForeground(new Color(0, 0, 255));
        scrollPane_2.setViewportView(ValueArea);
        ValueArea.setFont(new Font("Monospaced", Font.BOLD | Font.ITALIC, 18));
        ValueArea.setBackground(new Color(102, 205, 170));

        JScrollPane scrollPane_3 = new JScrollPane();
        scrollPane_3.setBounds(429, 282, 65, 58);
        frame.getContentPane().add(scrollPane_3);

        JTextArea CountArea = new JTextArea();
        CountArea.setForeground(new Color(0, 0, 255));
        scrollPane_3.setViewportView(CountArea);
        CountArea.setFont(new Font("Monospaced", Font.BOLD | Font.ITALIC, 20));
        CountArea.setBackground(new Color(102, 205, 170));


        JButton btnNewButton = new JButton("Search");
        btnNewButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    query = QueryArea.getText();
                    VSM.alpha = Double.parseDouble(ValueArea.getText());

                    VSM.ReadQuery(query);
                    VSM.InvertedDocFrequency(); //it will calculate the idf with help of the log(df)/N;
                    VSM.WeightedVector();		//it will create vectors with help of idf*all term frequency
                    VSM.QueryVector();
                    VSM.docWtgArray();
                    AnswerArea.setText(VSM.Sorts(VSM.DocID));
                    CountArea.setText(count);
                } catch (FileNotFoundException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }

        });
        btnNewButton.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 18));
        btnNewButton.setForeground(new Color(154, 205, 50));
        btnNewButton.setBackground(new Color(0, 100, 0));
        btnNewButton.setBounds(115, 180, 104, 34);
        frame.getContentPane().add(btnNewButton);



        JLabel label = new JLabel("");
       // Image img = new ImageIcon(this.getClass().getResource("./img/a.png")).getImage();
        //Image resizeImg = img.getScaledInstance(120, 120, java.awt.Image.SCALE_SMOOTH);
        //label.setIcon(new ImageIcon(resizeImg));
        //label.setBounds(191, 11, 98, 91);
        //frame.getContentPane().add(label);

        JLabel lblCount = new JLabel("Length");
        lblCount.setForeground(new Color(154, 205, 50));
        lblCount.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 16));
        lblCount.setBounds(429, 257, 59, 14);
        frame.getContentPane().add(lblCount);

        JButton btnReset = new JButton("Reset");
        btnReset.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                clear();
                ValueArea.setText("");
                QueryArea.setText("");
                AnswerArea.setText("");
                CountArea.setText("");
            }
        });
        btnReset.setBackground(Color.RED);
        btnReset.setFont(new Font("Tahoma", Font.PLAIN, 16));
        btnReset.setBounds(405, 189, 89, 23);
        frame.getContentPane().add(btnReset);
    }
}
