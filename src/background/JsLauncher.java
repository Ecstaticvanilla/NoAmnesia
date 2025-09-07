package background;

import java.io.*;

public class JsLauncher {
    public static void main(String[] args) {
        try {
            // Launch Node.js inside whatsapp_script directory
            ProcessBuilder pb = new ProcessBuilder("node", "index.js");
            pb.directory(new File("whatsapp_script")); // cd into whatsapp_script
            pb.redirectErrorStream(true);
            Process process = pb.start();

            // Thread to read Node output
            Thread outputReader = new Thread(() -> {
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        System.out.println("[JS] " + line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            outputReader.start();

            // Thread to detect ESC key
            Thread escListener = new Thread(() -> {
                try {
                    System.out.println("Press ESC to stop the Node script...");
                    // Set raw mode
                    Console console = System.console();
                    if(console != null) {
                        Reader reader = console.reader();
                        int c;
                        while ((c = reader.read()) != -1) {
                            if(c == 27) { // ESC key code
                                System.out.println("ESC pressed, triggering graceful shutdown...");
                                process.destroy(); // SIGTERM
                                break;
                            }
                        }
                    } else {
                        System.err.println("No console detected. ESC key listener unavailable.");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            escListener.start();

            // Wait for Node process to exit
            int exitCode = process.waitFor();
            System.out.println("Node script exited with code " + exitCode);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
