package com.cutesmouse.mmusic;

import com.cutesmouse.mmusic.frames.MainFrame;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {
    public static BotHandler BOTHANDLER;
    public static MusicHandler MUSICHANDLER;
    public static MainFrame MAINWINDOW;

    public static void main(String[] arg) throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException, IOException {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        setUIFont(new javax.swing.plaf.FontUIResource("微軟正黑體", Font.PLAIN, 14));
        BufferedReader reader = new BufferedReader(new InputStreamReader(Main.class.getResourceAsStream("/account.mdata")));
        String token = reader.readLine();
        if (token == null || token.equals("(Token Here)")) {
            JOptionPane.showMessageDialog(null,"請用解壓縮軟體打開此程式後\n將Token放入\"account.mdata\"\n再重新開啟");
            return;
        }
        BOTHANDLER = new BotHandler(token);
        MUSICHANDLER = new MusicHandler(BOTHANDLER.getJDA());
        MAINWINDOW = new MainFrame(BOTHANDLER);
        MAINWINDOW.setVisible(true);
    }

    public static VoiceChannel getSelectedChannel() {
        return MAINWINDOW.getSelectedChannel();
    }

    public static void setUIFont(javax.swing.plaf.FontUIResource f) {
        java.util.Enumeration keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof javax.swing.plaf.FontUIResource)
                UIManager.put(key, f);
        }
    }
}
