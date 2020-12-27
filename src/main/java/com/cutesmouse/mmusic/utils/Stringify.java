package com.cutesmouse.mmusic.utils;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

public class Stringify {
    public static String loadInfo(AudioTrack track) {
        return "["+formatTimeStamp(track.getDuration())+"] "+track.getInfo().title+"/"+track.getInfo().author;
    }
    public static String formatTimeStamp(long time) {
        time /= 1000;
        return String.format("%02d:%02d", time / 60, time % 60);
    }
    public static String titleDisplay(String s) {
        if (s.length() > 30) {
            return s.substring(0,30)+"...";
        }
        return s;
    }
}
