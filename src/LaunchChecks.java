import java.io.*;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class LaunchChecks {

    public LaunchChecks() throws IOException, InterruptedException {
        if (!this.isIntel()) {
            System.out.println("THIS IS FOR INTEL CPU ONLY!");
            System.exit(1);
        }
        this.checkAndDownloadResources();
    }

    private boolean isIntel() throws IOException {
        String[] cmd = {"/bin/sh", "-c", "grep vendor_id /proc/cpuinfo"};
        return new BufferedReader(new InputStreamReader(Runtime.getRuntime().exec(cmd).getInputStream())).readLine().contains("Intel");
    }

    private void checkAndDownloadResources() throws IOException, InterruptedException {
        String[] pathForFile = {"/tmp/Undervolt/undervolt.py", "/tmp/Undervolt/LICENCE"};
        String[] url = {"https://raw.githubusercontent.com/georgewhewell/undervolt/master/undervolt.py",
                "https://raw.githubusercontent.com/georgewhewell/undervolt/master/LICENSE.txt"};

        // Check if all file are presents, then check sum
        for (int i = 0; i < pathForFile.length; i++) {  // Could use url.length too tho
            String[] fileNameArray = pathForFile[i].split("/");
            String fileName = fileNameArray[fileNameArray.length - 1];

            System.out.print("Checking if " + fileName + " exists... ");
            File file = new File(pathForFile[i]);

            if (file.exists()) {
                // Sum for local file
                byte[] localFileSum = checkSum(file);

                int returnValueDownloadFile = downloadFile(pathForFile[i] + ".tmp", url[i]);

                // Download undervolt.py as temp file and check if exit value is 0
                if (returnValueDownloadFile == 0) {
                    // then check its sum
                    byte[] freshFileSum = checkSum(new File(pathForFile[i] + ".tmp"));

                    // Check if sum are equals
                    if (Arrays.equals(freshFileSum, localFileSum)) {    // Yes? delete tmp file
                        System.out.println(" Yes! And its the latest version.");
                        runBashCommand("rm " + pathForFile[i] + ".tmp");
                        runBashCommand("chmod +x " + pathForFile[i]);       // Just in case

                    } else {    // Not same file? Delete undervolt.py and rename undervolt.py.tmp to undervolt.py
                        System.out.print(" Yes but not the latest version... Downloading...");

                        runBashCommand("rm " + pathForFile[i]);
                        runBashCommand("mv " + pathForFile[i] + ".tmp " + pathForFile[i]);
                        runBashCommand("chmod +x " + pathForFile[i]);

                        System.out.println(" Done!");
                    }
                } else if (returnValueDownloadFile == 6) {
                    System.out.println(fileName + " is present, launching anyway...");
                    runBashCommand("chmod +x " + pathForFile[i]);
                }
            } else {
                System.out.print("No. Downloading... ");
                downloadFile(pathForFile[i], url[i]);
                runBashCommand("chmod +x " + pathForFile[i]);
                System.out.println("Done!");
            }
        }
    }

    private void runBashCommand(String cmd) throws IOException, InterruptedException {
        Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", cmd}).waitFor(10, TimeUnit.SECONDS);
    }

    private int downloadFile(String filePath, String url) {
        // Download resources in /tmp/ folder
        String[] cmd = {"/bin/sh", "-c", "curl " + url +
                " --create-dirs -o " + filePath
        };

        try {
            Process p1 = Runtime.getRuntime().exec(cmd);   // Run the command and wait for 10 sec before timeout
            p1.waitFor(10, TimeUnit.SECONDS);

            if (p1.exitValue() == 6) {
                System.out.println("Could not resolve host... Check your internet connexion.");
                return p1.exitValue();
            } else if (p1.exitValue() != 0) {
                System.out.println("An unknown error occurred... Exiting. CODE ERROR: " + p1.exitValue());
                return p1.exitValue();
            }
            // Then chmod +x, this also chmod +x LICENCE but idc
            runBashCommand("chmod +x " + filePath);

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Unable to get resources, exiting.");
            System.exit(1);
        } catch (InterruptedException e) {
            System.out.println("Timeout... exiting.");
            System.exit(1);
        }
        return 0;
    }

    private byte[] checkSum(File file) {
        try (InputStream in = new FileInputStream(file)) {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] block = new byte[4096];
            int length;
            while ((length = in.read(block)) > 0) {
                digest.update(block, 0, length);
            }
            return digest.digest();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
