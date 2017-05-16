package com.max_plus.smart;

/**
 *
 */
public class Item {

    private String iId;
    private String iName;

    public Item(String iName, String iId) {
        this.iId = iId;
        this.iName = iName;
    }

    public Item() {
    }

    public String getiId() {
        return iId;
    }

    public String getiName() {
        return iName;
    }

    public void setiId(String iId) {
        this.iId = iId;
    }

    public void setiName(String iName) {
        this.iName = iName;
    }
}
