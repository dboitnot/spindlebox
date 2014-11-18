package spindlebox.ui;

import com.google.inject.ImplementedBy;

import java.awt.*;

@ImplementedBy(MenuBuilderImpl.class)
public interface MenuBuilder {
    public void build(Menu root);
}
