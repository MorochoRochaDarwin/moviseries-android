package xyz.moviseries.moviseries.models;

/**
 * Created by DARWIN on 19/5/2017.
 */

public class MainMenuItem {
    private String title;
    private int icon;
    private Boolean selected;

    public MainMenuItem(String title, int icon, Boolean selected) {
        this.title = title;
        this.icon = icon;
        this.selected = selected;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public int getIcon() {
        return icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }
}
