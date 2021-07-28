package com.xiaowen.shorm.test;

/**
 * @author: xiaowen
 * @date: 2018/8/14
 * @since:
 */

public class Vehicle {
    private String rowkey;

    private String c;

    public Vehicle(String rowkey, String c) {
        this.rowkey = rowkey;
        this.c = c;
    }

    public String getRowkey() {
        return rowkey;
    }

    public void setRowkey(String rowkey) {
        this.rowkey = rowkey;
    }

    public String getC() {
        return c;
    }

    public void setC(String c) {
        this.c = c;
    }

    @Override
    public String toString() {
        return "Vehicle{" +
                "rowkey='" + rowkey + '\'' +
                '}';
    }
}
