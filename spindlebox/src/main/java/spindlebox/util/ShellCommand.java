package spindlebox.util;

import org.apache.commons.io.IOUtils;

import java.io.*;

public class ShellCommand {
    public static String getStdOut(String... args) throws IOException, InterruptedException {
        Process p = Runtime.getRuntime().exec(args);
        p.waitFor();

        try (InputStream in = p.getInputStream()) {
            return IOUtils.toString(in).trim();
        }
    }
}
