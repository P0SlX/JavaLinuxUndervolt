import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {

        //TODO
        // 1: Clean the code
        // 2: Create a better installer
        //      2.1 => Faire une commande curl qui va fetch install.sh et va installer le jar + dependencies
        // 3: When undervolt.py is present but no internet, exiting. not a bug just don't want to fix it

        // Perform some checks before starting up
        new LaunchChecks();

       // CpuInfo cpuInfo = new CpuInfo();

        // Anti aliasing for the font
        System.setProperty("awt.useSystemAAFontSettings","on");

        // Create the ui thread
        Thread runUi = new Thread(() -> {
           Interface ui = new Interface();
           ui.createUi();
        });

        // Run it!
        System.out.println("All good! Starting now!");
        runUi.start();

//        Thread runCpuUi = new Thread(() -> {
//            CpuUi cpuUi = new CpuUi();
//            cpuUi.createUi();
//        });
//
//        runCpuUi.start();
    }
}
