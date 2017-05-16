package com.max_plus.smart;

/**
 *
 */
public class Group {
    private String gName;
    private String gId;

    public String getgId() {
        return gId;
    }

    public void setgId(String gId) {
        this.gId = gId;
    }

    public Group(String gName, String gId) {
        this.gName = gName;
        this.gId = gId;
    }

    public Group() {
    }

    public String getgName() {
        return gName;
    }

    public void setgName(String gName) {
        this.gName = gName;
    }
}
