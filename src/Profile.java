import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.Date;
import java.util.HashMap;

public class Profile {

    private final JFrame frame;
    private UndervoltValue undervoltValue = new UndervoltValue();
    private HashMap<String, Double> returnValue;

    //Constructor
    public Profile(JFrame frame){
        this.frame = frame;
    }

    //Save data to file
    public void save(int coreSliderValue, int gpuSliderValue, int cacheSliderValue, int uncoreSliderValue, int analogioSliderValue, int p1SliderValue, int p1TimeWindowSliderValue, int p2SliderValue, int p2TimeWindowSliderValue){
        JFileChooser fileChooser = new JFileChooser(){
            @Override
            protected JDialog createDialog( Component parent ) throws HeadlessException {
                JDialog dialog = super.createDialog( parent );
                try {
                    InputStream in = getClass().getResourceAsStream("/img/icosave.png");
                    if (in == null) {
                        dialog.setIconImage(ImageIO.read(new File("/resources/img/icosave.png")));           // JAR
                    } else {
                        dialog.setIconImage(ImageIO.read(getClass().getResourceAsStream("/img/icosave.png")));   // IDE
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("No icon found");

                }
                return dialog;
            }
        };

        fileChooser.setDialogTitle("Save profile");

        int userSelection = fileChooser.showSaveDialog(frame);

        if (userSelection == JFileChooser.APPROVE_OPTION) {

            try {
                Date today = new Date();
                FileWriter fw = null;
                if (fileChooser.getSelectedFile().getName().endsWith(".txt")) {
                    fw = new FileWriter(fileChooser.getSelectedFile());
                } else {
                    fw = new FileWriter(fileChooser.getSelectedFile() + ".txt");
                }
                fw.write("#Setting profile data: " + today);
                fw.write("\n");
                fw.write("core: " + coreSliderValue + ".0 mV");
                fw.write("\n");
                fw.write("gpu: " + gpuSliderValue + ".0 mV");
                fw.write("\n");
                fw.write("cache: " + cacheSliderValue + ".0 mV");
                fw.write("\n");
                fw.write("uncore: " + uncoreSliderValue + ".0 mV");
                fw.write("\n");
                fw.write("analogio: " + analogioSliderValue + ".0 mV");
                fw.write("\n");
                fw.write("p1: " + p1SliderValue + ".0 W");
                fw.write("\n");
                fw.write("p2: " + p2SliderValue + ".0 W");
                fw.write("\n");
                fw.write("p1TimeWindow: " + p1TimeWindowSliderValue + ".0 s");
                fw.write("\n");
                fw.write("p2TimeWindow: " + p2TimeWindowSliderValue + ".0 s");
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    //Read data from file
    public HashMap<String, Double> read() {

        returnValue = new  HashMap<String, Double>();

        JFileChooser fileChooser = new JFileChooser(){
            @Override
            protected JDialog createDialog( Component parent ) throws HeadlessException {
                JDialog dialog = super.createDialog( parent );

                try {
                    InputStream in = getClass().getResourceAsStream("/img/icoload.png");
                    if (in == null) {
                        dialog.setIconImage(ImageIO.read(new File("/resources/img/icoload.png")));           // JAR
                    } else {
                        dialog.setIconImage(ImageIO.read(getClass().getResourceAsStream("/img/icoload.png")));   // IDE
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("No icon found");

                }
                return dialog;
            }
        };

        fileChooser.setApproveButtonText("Load");

        int userSelection = fileChooser.showOpenDialog(frame);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();

            readfile(file);
        } else {
            returnValue.put("errors", 0.0);
        }
        return UndervoltValue.returnValue;

    }

    //Read the selected file
    private void readfile(File file) {
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(file));

            //Ignore the first row (date) or it make KABOOOM!!!
            String line = reader.readLine();
            System.out.println(line);

            //Now let's learn how to read ;-)
            line = reader.readLine();
            while (line != null) {
                 System.out.println(line);
                 UndervoltValue.addToHashmap(line);
                 line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
