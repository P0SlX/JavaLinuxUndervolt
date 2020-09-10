import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.UIManager;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public class Interface {
    private JFrame frame;
    private JPanel MainPanel;
    private JSlider analogioSlider;
    private JSlider uncoreSlider;
    private JSlider cacheSlider;
    private JSlider gpuSlider;
    private JSlider coreSlider;
    private JSlider p1Slider;
    private JSlider p2Slider;
    private JSlider p1TimeWindowSlider;
    private JSlider p2TimeWindowSlider;
    private JLabel valueCore;
    private JLabel valueGpu;
    private JLabel valueCache;
    private JLabel valueUncore;
    private JLabel valueAnalogio;
    private JLabel p1Value;
    private JLabel p2Value;
    private JLabel p1TimeWindowValue;
    private JLabel p2TimeWindowValue;
    private JButton applyAndStressButton;
    private JButton faqbutton;
    private JButton resetbutton;
    private JButton buttonLoad;
    private JButton buttonSave;
    private JButton applybutton;
    private JToggleButton stressTestButton;

    private int coreSliderValue;
    private int gpuSliderValue;
    private int cacheSliderValue;
    private int uncoreSliderValue;
    private int analogioSliderValue;
    private int p1SliderValue;
    private int p2SliderValue;
    private int p1TimeWindowSliderValue;
    private int p2TimeWindowSliderValue;

    private final int     VOLTAGE = 1;
    private final int     PSTATES = 2;
    private final int PTIMEWINDOW = 3;

    private final Profile profile = new Profile(frame);
    private final UndervoltValue value = new UndervoltValue();
    private final Stresstest stresstest = new Stresstest();



    // Set the listener and the actual params
    public Interface() {

        // Slider listener
        coreSlider.addChangeListener(changeEvent -> {
            // Set the value from the slider
            coreSliderValue = coreSlider.getValue();

            // Set te label for the slider
            setLabel(coreSliderValue, valueCore, this.VOLTAGE);
        });

        gpuSlider.addChangeListener(changeEvent -> {
            gpuSliderValue = gpuSlider.getValue();
            setLabel(gpuSliderValue, valueGpu, this.VOLTAGE);
        });

        cacheSlider.addChangeListener(changeEvent -> {
            cacheSliderValue = cacheSlider.getValue();
            setLabel(cacheSliderValue, valueCache, this.VOLTAGE);
        });

        uncoreSlider.addChangeListener(changeEvent -> {
            uncoreSliderValue = uncoreSlider.getValue();
            setLabel(uncoreSliderValue, valueUncore, this.VOLTAGE);
        });

        analogioSlider.addChangeListener(changeEvent -> {
            analogioSliderValue = analogioSlider.getValue();
            setLabel(analogioSliderValue, valueAnalogio, this.VOLTAGE);
        });

        p1Slider.addChangeListener(changeEvent -> {
            p1SliderValue = p1Slider.getValue();
            setLabel(p1SliderValue, p1Value, this.PSTATES);
        });

        p1TimeWindowSlider.addChangeListener(changeEvent -> {
            p1TimeWindowSliderValue = p1TimeWindowSlider.getValue();
            setLabel(p1TimeWindowSliderValue, p1TimeWindowValue, this.PTIMEWINDOW);
        });

        p2Slider.addChangeListener(changeEvent -> {
            p2SliderValue = p2Slider.getValue();
            setLabel(p2SliderValue, p2Value, this.PSTATES);
        });

        p2TimeWindowSlider.addChangeListener(changeEvent -> {
            p2TimeWindowSliderValue = p2TimeWindowSlider.getValue();
            setLabel(p2TimeWindowSliderValue, p2TimeWindowValue, this.PTIMEWINDOW);
        });


        //Button listener
        applybutton.addActionListener(actionEvent -> {
            set(false);
        });

        applyAndStressButton.addActionListener(actionEvent -> {
            set(true);
        });

        stressTestButton.addActionListener(actionEvent -> {
            if (stressTestButton.isSelected()) {
                stressTestButton.setText("Stop");
                stresstest.startStress(Long.MAX_VALUE);
            } else {
                stressTestButton.setText("Stress test only");
                stresstest.stopStress();
            }
        });

        resetbutton.addActionListener(actionEvent -> {
            reset();
        });

        faqbutton.addActionListener(actionEvent -> {
            Faq faq = new Faq();
            faq.createUi();
        });

        buttonLoad.addActionListener(actionEvent -> read());

        buttonSave.addActionListener(actionEvent -> save());

        //Set actual value when application start
        setUiValue();
    }

    // Ask to read the profile
    private void read() {
        HashMap<String, Double> prof = profile.read();
        if(!prof.containsKey("errors")){
            setSliderValue(prof);
        }
    }

    // Save the profile
    private void save() {
        profile.save(coreSliderValue, gpuSliderValue, cacheSliderValue, uncoreSliderValue,
                analogioSliderValue, p1SliderValue, p1TimeWindowSliderValue, p2SliderValue, p2TimeWindowSliderValue);
    }

    // Set actual slider value as undervolt
    private void set(boolean doStressTest) {
        setEnableUi(false);
        // Set value
        boolean correct = value.setValue(coreSliderValue, gpuSliderValue, cacheSliderValue, uncoreSliderValue, analogioSliderValue,
                                         p1SliderValue, p1TimeWindowSliderValue, p2SliderValue, p2TimeWindowSliderValue);

        if (correct) {
            // Refresh ui value
            setUiValue();

            if (doStressTest) {
                try {
                    System.out.println("Starting stress test.");
                    stresstest.startStress(10000);
                    // Wait for the stress test ending
                    Thread.sleep(11000);
                    setEnableUi(true);
                } catch (InterruptedException e) {
                    System.out.println("Cannot start the stress test, this is a bug, please report it.");
                }
            }

            // Re enable all ui
            setEnableUi(true);
            System.out.println("Done!");

        } else {
            generateError(2);
        }
    }

    // Set 0 to all value
    private void reset() {
        //First I disable the ui
        setEnableUi(false);

        //TODO
        // Create a temp file when starting w/ current values and when reset, applies back theses values
        //Set value
        boolean correct = value.setValue(0, 0, 0, 0, 0, 0, 0, 0, 0);

        if(correct){
            //Refresh ui value
            setUiValue();
        }else{
            generateError(2);
        }
        setEnableUi(true);
    }

    // ONLY FOR FIRST INSTANCE
    public void createUi() {

        UIManager.getDefaults().put("Button.disabledText",Color.decode("#EAB23B"));
        frame = new JFrame("Undervolt");
        frame.setContentPane(new Interface().MainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // This... is just to know if it's a jar file or exec in an IDE
        try {
            InputStream in = getClass().getResourceAsStream("/img/icoover.png");
            if (in == null) {
                frame.setIconImage(ImageIO.read(new File("/resources/img/icoover.png")));           // JAR
            } else {
                frame.setIconImage(ImageIO.read(getClass().getResourceAsStream("/img/icoover.png")));  // IDE
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    // Enable and disable de ui minus faq button
    private void setEnableUi(boolean state){
        coreSlider.setEnabled(state);
        gpuSlider.setEnabled(state);
        cacheSlider.setEnabled(state);
        uncoreSlider.setEnabled(state);
        analogioSlider.setEnabled(state);
        p1Slider.setEnabled(state);
        p2Slider.setEnabled(state);
        p1TimeWindowSlider.setEnabled(state);
        p2TimeWindowSlider.setEnabled(state);
        applybutton.setEnabled(state);
        resetbutton.setEnabled(state);
        applyAndStressButton.setEnabled(state);
        stressTestButton.setEnabled(state);
    }

    // Called after set value
    private void setUiValue() {
        // Get the data from undervolt.py
        HashMap<String, Double> valueHashmap = value.getValue();
        setSliderValue(valueHashmap);
    }

    // Set the value from the hashmap to the ui
    private void setSliderValue( HashMap<String, Double> valueHashmap) {

        if (valueHashmap.containsKey("errors")) {
            // If there is something wrong with the readings
            generateError(valueHashmap.get("errors").intValue());
        } else {
            // Set actual core value
            coreSlider.setValue(valueHashmap.get("core").intValue());
            setLabel(valueHashmap.get("core").intValue(), valueCore, this.VOLTAGE);

            // Set actual gpu value
            gpuSlider.setValue(valueHashmap.get("gpu").intValue());
            setLabel(valueHashmap.get("gpu").intValue(), valueGpu, this.VOLTAGE);

            // Set actual cache value
            cacheSlider.setValue(valueHashmap.get("cache").intValue());
            setLabel(valueHashmap.get("cache").intValue(), valueCache, this.VOLTAGE);

            // Set actual uncore value
            uncoreSlider.setValue(valueHashmap.get("uncore").intValue());
            setLabel(valueHashmap.get("uncore").intValue(), valueUncore, this.VOLTAGE);

            // Set actual analogio value
            analogioSlider.setValue(valueHashmap.get("analogio").intValue());
            setLabel(valueHashmap.get("analogio").intValue(), valueAnalogio, this.VOLTAGE);

            // Set actual P1 value
            p1Slider.setValue(valueHashmap.get("p1").intValue());
            setLabel(valueHashmap.get("p1").intValue(), p1Value, this.PSTATES);

            // Set actual P2 value
            p2Slider.setValue(valueHashmap.get("p2").intValue());
            setLabel(valueHashmap.get("p2").intValue(), p2Value, this.PSTATES);

            // Set actual P1 time window value
            p1TimeWindowSlider.setValue(valueHashmap.get("p1TimeWindow").intValue());
            setLabel(valueHashmap.get("p1TimeWindow").intValue(), p1TimeWindowValue, this.PTIMEWINDOW);

            // Set actual P2 time window value
            p2TimeWindowSlider.setValue(valueHashmap.get("p2TimeWindow").intValue());
            setLabel(valueHashmap.get("p2TimeWindow").intValue(), p2TimeWindowValue, this.PTIMEWINDOW);
        }

    }

    // Create the label from the value
    private void setLabel(int value, JLabel label, int unit) {
        String labeltext = switch (unit) {
            case VOLTAGE -> value + " mV";
            case PSTATES -> value + " W";
            case PTIMEWINDOW -> value + " s";
            default -> "Error";
        };

        if (value > 0) {
            labeltext = '+' + labeltext;
        }
        label.setText(labeltext);
    }

    // Show error
    public void generateError(int errorCode) {
        setEnableUi(false);
        applybutton.setText("Error code: " + errorCode);
    }

}
