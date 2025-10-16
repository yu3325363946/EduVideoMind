package it.pojo;

public class OutlineItem {
    private int level;
    private String title;

    public OutlineItem() {}
    public OutlineItem(int level, String title) {
        this.level = level;
        this.title = title;
    }

    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
}
