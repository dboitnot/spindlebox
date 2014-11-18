package spindlebox.ui.tray;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import spindlebox.ui.Look;
import spindlebox.ui.MenuBuilder;
import spindlebox.ui.StateMonitor;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static spindlebox.ui.StateMonitor.State.PROCESSING;
import static spindlebox.util.Logging.DEBUG;
import static spindlebox.util.Logging.INFO;

@Singleton
public class TrayManager implements StateMonitor {
    private final Image defaultImage;
    private final Map<State,Image> stateImages = new HashMap<>();
    private final TrayIcon trayIcon;
    private final Look look;

    @Inject
    public TrayManager(Look look, MenuBuilder menuBuilder) {
        this.look = look;

        // If system tray icons aren't supported, don't initialize anything.
        if (!SystemTray.isSupported()) {
            INFO("System tray not supported.");
            defaultImage = null;
            trayIcon = null;
            return;
        }

        try {
            DEBUG("Loading images");
            defaultImage = imageNamed("default");
            stateImages.put(PROCESSING, imageNamed("arrow"));

            DEBUG("Adding tray icon");
            trayIcon = new TrayIcon(defaultImage, "Spindlebox");

            PopupMenu popup = new PopupMenu("Spindlebox");
            menuBuilder.build(popup);
            trayIcon.setPopupMenu(popup);

            SystemTray.getSystemTray().add(trayIcon);
        } catch (AWTException e) {
            throw new RuntimeException(e);
        }
    }

    private Image imageNamed(String name) {
        String path = String.format("resources/%s-tray-%s-%s.png",
                look.getThemeName(), name, look.getTrayIconSize());
        try {
            DEBUG("Loading tray icon image: " + path);
            return ImageIO.read(this.getClass().getResourceAsStream(path));
        } catch (IOException e) {
            throw new RuntimeException("Error loading tray icon: " + path, e);
        }
    }

    private boolean isActive() {
        return defaultImage != null;
    }

    @Override
    public void stateChanged(State newState) {
        if (!isActive())
            return;
        DEBUG("State change: " + newState);
        SwingUtilities.invokeLater(() -> {
            trayIcon.setImage(stateImages.getOrDefault(newState, defaultImage));
            trayIcon.setToolTip("Spindlebox: " + newState);
        });
    }
}
