package spindlebox.ui;

import com.google.inject.Inject;
import spindlebox.util.OSType;
import spindlebox.util.ShellCommand;

import static spindlebox.util.Logging.DEBUG;

public class LookImpl implements Look {
    private final OSType osType;

    @Inject
    public LookImpl(OSType osType) {
        this.osType = osType;
    }

    @Override
    public String getThemeName() {
        // Deal with Mac OS Light/Dark themes
        if (osType == OSType.MAC_OS) {
            try {
                String style = ShellCommand.getStdOut("defaults", "read", "Apple Global Domain", "AppleInterfaceStyle");
                DEBUG("AppleInterfaceStyle: " + style);
                if ("Dark".equals(style))
                    return "white";
                return "black";
            } catch (Exception e) {
                return "black";
            }
        }

        return "black";
    }

    @Override
    public String getTrayIconSize() {
        return "18x18";
    }
}
