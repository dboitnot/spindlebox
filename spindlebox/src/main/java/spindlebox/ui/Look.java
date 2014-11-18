package spindlebox.ui;

import com.google.inject.ImplementedBy;

@ImplementedBy(LookImpl.class)
public interface Look {
    public String getThemeName();
    public String getTrayIconSize();
}
