package com.cutesmouse.mmusic.frames;

public class ListData {
    public Object obj;
    public String string;
    public ListData(Object obj, String string) {
        this.obj = obj;
        this.string= string;
    }
    @Override
    public String toString() {
        return string;
    }
}
