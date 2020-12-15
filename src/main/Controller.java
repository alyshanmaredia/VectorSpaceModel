package main;

import java.io.FileNotFoundException;

class Controller {

    public static int index = 0;
    public static void call () throws FileNotFoundException {
        VSM.readStopList();
        VSM.readDatafiles();
        VSM.DocumentFrequencies();// function which will create the term frequency
    }

    static public void main(String args[]) throws FileNotFoundException {

        splash obj  = new splash();
        obj.frame.setVisible(true);
        GUI gui = new GUI();
        try {

            call();
            for(int i =0; i<=100; i++) {
                Thread.sleep(50);
                obj.per.setText(Integer.toString(i)+"%");
                if(i==100) {
                    obj.frame.dispose();
                    gui.frame.show();
                }
            }
        }catch(Exception e) {

        }
    }
}