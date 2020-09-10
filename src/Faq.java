import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class Faq {

    private JPanel FaqMainPanel;
    private JTextPane faq;
    private  JFrame frame;

    public Faq(){
        faq.setText("<html>"+
                "<p><strong>FAQ: </strong></p>" +
                "<ol>" +
                "<li>Why does the CPU go to maximum when I apply the settings?<br />" +
                "<ol>" +
                "<li>Because a 10-second test is performed to ensure the stability of the system.</li>" +
                "</ol>" +
                "</li>" +
                "<li>Why my CPU is not supported?<br />" +
                "<ol>" +
                "<li>Depends on the architecture and implementation of the manufacturer, for more information, visit the undervolt.py README (see below).</li>" +
                "</ol>" +
                "</li>" +
                "<li>I have an AMD processor why does not work?<br />" +
                "<ol>" +
                "<li>Because the script that I'm using is for INTEL CPU only (<a href='https://github.com/mihic/linux-intel-undervolt'>Script faq</a>).</li>" +
                "</ol>" +
                "</li>" +
                "</ol>" +
                "<p><strong>Error codes:</strong></p>" +
                "<ul>" +
                "<li>Code: 1.0 → Class: Undervolt.java → Method: runScript()" +
                "<ul>" +
                "<li>Solution: Application start with no sudo permission or something is wrong with undervolt.py file.</li>" +
                "</ul>" +
                "</li>" +
                "<li>Code: 2.0 → Class: Undervolt.java → Method: setValue()" +
                "<ul>" +
                "<li>Solution: Error during setting voltage, PC not compatible or something is wrong with undervolt.py file.</li>" +
                "</ul>" +
                "</li>" +
                "</ul>" +
                "<p><strong>Known bug(s): </strong></p>" +
                "</ul>" +
                "<li>Make those links works</li>" +
                "<li>I'm sure I've missed some... If you find one... Or two... Or more, please open an issue ;-)</li>" +
                "</ul>" +
                "<p><strong>Thanks to: </strong></p>" +
                "</ul>" +
                "<li>Georgewhewell and all his contributors for <a href='https://github.com/georgewhewell/undervolt'>undervolt.py</a></li>" +
                "<li>Caffinc for the <a href='https://caffinc.github.io/2016/03/cpu-load-generator'>CPU stress class</a></li>" +
                "</ul>"   +
                "</html>"
        );

    }

    //ONLY FOR FIRST INSTANCE
    public void createUi(){
        UIManager.getDefaults().put("Button.disabledText", Color.decode("#EAB23B"));
        frame = new JFrame("FAQ / Error codes");
        frame.setContentPane(new Faq().FaqMainPanel);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        try {
            InputStream in = getClass().getResourceAsStream("/img/icofaq.png");
            if (in == null) {
                frame.setIconImage(ImageIO.read(new File("/resources/img/icofaq.png")));           // JAR
            } else {
                frame.setIconImage(ImageIO.read(getClass().getResourceAsStream("/img/icofaq.png")));   // IDE
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("No icon found");
        }

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    //END INSTANCE
}
