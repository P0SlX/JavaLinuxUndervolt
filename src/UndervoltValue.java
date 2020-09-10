import java.io.*;
import java.util.HashMap;

/**
 *
 *   This class is made to interface my ui with undervolt.py
 *
 *   undervolt.py IS NOT MINE it belongs to georgewhewell, if you like it
 *   thanks him on his github page:
 *
 *   https://github.com/georgewhewell/undervolt
 *
 */

public class UndervoltValue {

    public static HashMap<String, Double> returnValue;

    public UndervoltValue() { }

    // Read the actual config
    public HashMap<String, Double> getValue() {
        returnValue = new HashMap<>();
        runScript("--read", true);
        return returnValue;
    }

    // Set the slider config
    public boolean setValue(int core, int gpu, int cache, int uncore, int analogio, int  p1, int p1TimeWindow, int p2, int p2TimeWindow) {
        String undervolt = "--gpu " + gpu + " --core " + core + " --cache " + cache + " --uncore " + uncore + " --analogio " + analogio +
                " -p1 " + p1 + " " + p1TimeWindow + " -p2 " + p2 + " " + p2TimeWindow;
        System.out.println("Applying: " + undervolt);
        returnValue = new HashMap<String, Double>();
        return runScript(undervolt, false);

    }

    //TODO
    // Erase sudo -S
    //This run the script and read the output
    private boolean runScript(String param, boolean read) {
        try {
            // Create process

//            String cmd = "sudo -S /tmp/Undervolt/undervolt.py ";    // This is to run the python program as root in IntelliJ
                                                                      // bc I can't figure out how to run it as root natively

            String cmd = "sudo /tmp/Undervolt/undervolt.py ";
            Process p = Runtime.getRuntime().exec(cmd + param);

            BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));

//            writer.write("password");     // This is to write your password into the buffer and so run the script w/ root privilege
//            writer.newLine();
//            writer.close();

            // Read process return
            String ret = in.readLine();

            // For setting error
            if (ret == null && !read) {
                return true;
            }
            else if (ret != null && !read) {
                return false;
            }
            // Read file
            else if (ret == null && read) {
                // No permission or no script
                System.out.println("Error while loading undervolt.py");
                returnValue.put("errors", 1.0);
            } else {
                // Read all return data form the second line
                ret = in.readLine();

                while (ret != null){
                    // Create hashmap for return
                    addToHashmap(ret);
                    // Read all line
                    ret = in.readLine();
                }
            }

        } catch (IOException e) {
            // No script
            e.printStackTrace();
        }
        return false;
    }


    // Create the hashmap that the ui use
    static void addToHashmap(String scrptString) {
        // String preparation
        scrptString = scrptString.replace("mV", "");
        scrptString = scrptString.replace(" ", "");

        // This right here is not my best code but hey... It works... So don't touch it
        if (scrptString.contains("powerlimit")) {   // Regex hell
            scrptString = scrptString.replaceAll("W.*short", "");
            scrptString = scrptString.replaceAll("s.*/", ":");
            scrptString = scrptString.replaceAll("W.*long", "");
            scrptString = scrptString.replaceAll("s.*", "");
            String[] parts = scrptString.split(":");
            // And this is worst, I can replace the hashmap w/ Integers instead of Double but hey! I'm lazy...
            returnValue.put("p2", (double)Math.round(Double.parseDouble(parts[1])));            // Power limit short
            returnValue.put("p2TimeWindow", (double)Math.round(Double.parseDouble(parts[2])));  // Power time window short
            returnValue.put("p1", (double)Math.round(Double.parseDouble(parts[3])));            // Power limit long
            returnValue.put("p1TimeWindow", (double)Math.round(Double.parseDouble(parts[4])));  // Power time window long
        } else {
            scrptString = scrptString.replace("W", "");
            scrptString = scrptString.replace("s", "");
            String[] parts = scrptString.split(":");
            String name  = parts[0]; // Eg. Core
            String value = parts[1]; //     0.0
            // Add to hashmap
            returnValue.put(name, (double)Math.round(Double.parseDouble(value)));
        }
    }
}
