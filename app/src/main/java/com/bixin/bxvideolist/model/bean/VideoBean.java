package com.bixin.bxvideolist.model.bean;

public class VideoBean {
    private String name;
    private String path;
    private String size;
    private boolean select;
    private int CTR;

    public VideoBean() {

    }


    public VideoBean(String name, String path, String size, boolean select, int CTR) {
        this.name = name;
        this.path = path;
        this.size = size;
        this.select = select;
        this.CTR = CTR;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public boolean getSelect() {
        return select;
    }

    public void setSelect(boolean select) {
        this.select = select;
    }

    public int getCTR() {
        return CTR;
    }

    public void setCTR(int CTR) {
        this.CTR = CTR;
    }
}
