/*
 * Created by JFormDesigner on Sat Dec 26 11:09:16 CST 2020
 */

package com.cutesmouse.mmusic.frames;

import java.awt.event.*;
import javax.swing.border.*;
import javax.swing.event.*;
import com.cutesmouse.mmusic.BotHandler;
import com.cutesmouse.mmusic.Main;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.VoiceChannel;

import java.awt.*;
import java.io.File;
import javax.swing.*;

/**
 * @author CutesMouse
 */
public class MainFrame extends JFrame {
    private final BotHandler HANDLER;
    public MainFrame(BotHandler bh) {
        initComponents();
        this.HANDLER = bh;
        reloadServerList(null);
    }

    public VoiceChannel getSelectedChannel() {
        if (!(voiceChannels.getSelectedValue() instanceof ListData)) return null;
        return (VoiceChannel) (((ListData) voiceChannels.getSelectedValue()).obj);
    }
    public Guild getSelectedGuild() {
        if (!(servers.getSelectedValue() instanceof ListData)) return null;
        return (Guild) (((ListData) servers.getSelectedValue()).obj);
    }

    private void serversValueChanged(ListSelectionEvent e) {
        if (servers.getSelectedValue() == null) return;
        voiceChannels.setListData(((Guild) (((ListData) servers.getSelectedValue()).obj)).getVoiceChannels().stream().map(p ->
                new ListData(p,p.getName())).toArray());
    }

    private File lastDir;
    private void playFromFile(ActionEvent e) {
        if (getSelectedChannel() == null || getSelectedGuild() == null) {
            JOptionPane.showMessageDialog(this,"請先選擇要點歌的頻道!");
            return;
        }
        JFileChooser fc;
        if (lastDir == null) fc = new JFileChooser();
        else fc = new JFileChooser(lastDir);
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        if (fc.showOpenDialog(this) == JOptionPane.YES_OPTION) {
            lastDir = fc.getSelectedFile().getParentFile();
            Main.MUSICHANDLER.getHandler(getSelectedGuild()).playByFile(fc.getSelectedFile());
        }
    }

    private void playBySearch(ActionEvent e) {
        if (getSelectedChannel() == null || getSelectedGuild() == null) {
            JOptionPane.showMessageDialog(this,"請先選擇要點歌的頻道!");
            return;
        }
        String s = JOptionPane.showInputDialog(this,"請輸入關鍵字或網址","播放輸入框", JOptionPane.PLAIN_MESSAGE);
        if (s == null || s.isEmpty()) return;
        Main.MUSICHANDLER.getHandler(getSelectedGuild()).playBySearch(s);
    }

    private void openGuildSettingFrame(ActionEvent e) {
        if (getSelectedGuild() == null) {
            JOptionPane.showMessageDialog(this,"請選擇伺服器!");
            return;
        }
        GuildSettingFrame frame = new GuildSettingFrame(getSelectedGuild());
        JOptionPane.showMessageDialog(this, frame,"Discord音樂播放器 - 伺服器設定("+getSelectedGuild().getName()+")",JOptionPane.PLAIN_MESSAGE);
        frame.isClosed = true;
    }

    private void reloadServerList(ActionEvent e) {
        servers.setListData(HANDLER.getJDA().getGuilds().stream().map(p -> new ListData(p,p.getName())).toArray());
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        menuBar1 = new JMenuBar();
        menu1 = new JMenu();
        menuItem2 = new JMenuItem();
        menuItem3 = new JMenuItem();
        menu2 = new JMenu();
        menuItem1 = new JMenuItem();
        menuItem4 = new JMenuItem();
        scrollPane1 = new JScrollPane();
        servers = new JList();
        scrollPane2 = new JScrollPane();
        voiceChannels = new JList();

        //======== this ========
        setTitle("Discord \u97f3\u6a02\u64ad\u653e\u5668");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setIconImage(new ImageIcon(getClass().getResource("/icon.png")).getImage());
        Container contentPane = getContentPane();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.X_AXIS));

        //======== menuBar1 ========
        {

            //======== menu1 ========
            {
                menu1.setText(" \u64ad\u653e ");

                //---- menuItem2 ----
                menuItem2.setText("\u5f9e\u6a94\u6848");
                menuItem2.addActionListener(e -> playFromFile(e));
                menu1.add(menuItem2);

                //---- menuItem3 ----
                menuItem3.setText("\u5f9eYouTube/\u7dda\u4e0a\u641c\u5c0b");
                menuItem3.addActionListener(e -> playBySearch(e));
                menu1.add(menuItem3);
            }
            menuBar1.add(menu1);

            //======== menu2 ========
            {
                menu2.setText(" \u4f3a\u670d\u5668\u64ad\u653e\u8a2d\u5b9a ");

                //---- menuItem1 ----
                menuItem1.setText("\u958b\u555f\u8a2d\u5b9a\u8996\u7a97");
                menuItem1.addActionListener(e -> openGuildSettingFrame(e));
                menu2.add(menuItem1);
                menu2.addSeparator();

                //---- menuItem4 ----
                menuItem4.setText("\u91cd\u65b0\u6574\u7406\u4f3a\u670d\u5668\u5217\u8868");
                menuItem4.addActionListener(e -> reloadServerList(e));
                menu2.add(menuItem4);
            }
            menuBar1.add(menu2);
        }
        setJMenuBar(menuBar1);

        //======== scrollPane1 ========
        {
            scrollPane1.setBorder(null);
            scrollPane1.setPreferredSize(new Dimension(450, 500));

            //---- servers ----
            servers.setBorder(new TitledBorder(null, "\u9078\u64c7\u4f3a\u670d\u5668", TitledBorder.LEADING, TitledBorder.DEFAULT_POSITION,
                new Font("\u5fae\u8edf\u6b63\u9ed1\u9ad4", Font.PLAIN, 20)));
            servers.setMinimumSize(new Dimension(450, 500));
            servers.setMaximumSize(new Dimension(450, 500));
            servers.setFont(new Font("\u5fae\u8edf\u6b63\u9ed1\u9ad4", Font.PLAIN, 16));
            servers.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            servers.addListSelectionListener(e -> serversValueChanged(e));
            scrollPane1.setViewportView(servers);
        }
        contentPane.add(scrollPane1);

        //======== scrollPane2 ========
        {
            scrollPane2.setBorder(null);
            scrollPane2.setPreferredSize(new Dimension(450, 500));

            //---- voiceChannels ----
            voiceChannels.setFont(new Font("\u5fae\u8edf\u6b63\u9ed1\u9ad4", Font.PLAIN, 16));
            voiceChannels.setBorder(new TitledBorder(null, "\u9078\u64c7\u8a9e\u97f3\u983b\u9053", TitledBorder.LEADING, TitledBorder.DEFAULT_POSITION,
                new Font("\u5fae\u8edf\u6b63\u9ed1\u9ad4", Font.PLAIN, 20)));
            voiceChannels.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            scrollPane2.setViewportView(voiceChannels);
        }
        contentPane.add(scrollPane2);
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JMenuBar menuBar1;
    private JMenu menu1;
    private JMenuItem menuItem2;
    private JMenuItem menuItem3;
    private JMenu menu2;
    private JMenuItem menuItem1;
    private JMenuItem menuItem4;
    private JScrollPane scrollPane1;
    private JList servers;
    private JScrollPane scrollPane2;
    private JList voiceChannels;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
