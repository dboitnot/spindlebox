package spindlebox.util;

import java.util.Locale;

public enum OSType {
    WINDOWS, MAC_OS, LINUX, OTHER;

    public static OSType detect() {
        OSType ret;

        String os = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
        if ((os.contains("mac")) || (os.contains("darwin"))) {
            ret = OSType.MAC_OS;
        } else if (os.contains("win")) {
            ret = OSType.WINDOWS;
        } else if (os.contains("nux")) {
            ret = OSType.LINUX;
        } else {
            ret = OSType.OTHER;
        }

        return ret;
    }
}
