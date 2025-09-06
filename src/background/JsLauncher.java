package background;
import java.io.*;

public class JsLauncher {
    public static void main(String[] args) throws Exception {

        ProcessBuilder pb = new ProcessBuilder("node", "./background/index.js");
        pb.redirectErrorStream(true);
        Process process = pb.start();

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream())
        );
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println("[JS] " + line);
        }

        process.waitFor();
        System.out.println("JS script executed!");
    }
}