package spindlebox.ui;

import com.google.inject.Inject;
import spindlebox.AppControl;

import java.awt.*;
import java.awt.event.ActionListener;

public class MenuBuilderImpl implements MenuBuilder {
    private final AppControl appControl;

    @Inject
    public MenuBuilderImpl(AppControl appControl) {
        this.appControl = appControl;
    }

    @Override
    public void build(Menu root) {
        root.add(item("Quit", appControl::shutdown));
    }

    private MenuItem itemWithEvent(String label, ActionListener block) {
        MenuItem ret = new MenuItem(label);
        ret.addActionListener(block);
        return ret;
    }

    private MenuItem item(String label, Runnable block) {
        return itemWithEvent(label, e -> block.run());
    }
}
