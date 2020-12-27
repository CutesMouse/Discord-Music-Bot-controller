/*
 * Created by JFormDesigner on Sat Dec 26 13:24:28 CST 2020
 */

package com.cutesmouse.mmusic.frames;

import java.awt.event.*;
import javax.swing.event.*;
import com.cutesmouse.mmusic.GuildMusicHandler;
import com.cutesmouse.mmusic.Main;
import com.cutesmouse.mmusic.utils.Stringify;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.entities.Guild;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.*;
import javax.swing.border.*;

/**
 * @author CutesMouse
 */
public class GuildSettingFrame extends JPanel {
    private final Guild GUILD;
    public boolean isClosed = false;
    private final GuildMusicHandler HANDLER;
    public GuildSettingFrame(Guild g) {
        initComponents();
        this.GUILD = g;
        this.HANDLER = Main.MUSICHANDLER.getHandler(g);
        update();
        updateIcon();
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                if (isClosed) {
                    this.cancel();
                    return;
                }
                AudioTrack track = HANDLER.getAudioPlayer().getPlayingTrack();
                if (track == null && !songTitle.getText().equals("Empty!")) {
                    update();
                    return;
                }
                if (track== null) return;
                if (!songTitle.getText().equals(Stringify.titleDisplay(track.getInfo().title))) {
                    update();
                }
                songPlay.setValue((int) (track.getPosition()*1000/(double) track.getDuration()));
                setSongPlayBorder(Stringify.formatTimeStamp(track.getPosition())+"/"+Stringify.formatTimeStamp(track.getDuration()));
            }
        },0L,1000L);
    }
    public void update() {
        if (HANDLER.getAudioPlayer().getPlayingTrack() == null) {
            playingList.setListData(new ListData[0]);
            songTitle.setText("Empty!");
            songPlay.setValue(0);
            setSongPlayBorder("00:00/00:00");
            return;
        }
        ArrayList<ListData> lists = new ArrayList<>();
        lists.add(new ListData(HANDLER.getAudioPlayer().getPlayingTrack(),(HANDLER.getAudioPlayer().isPaused() ? "[暫停中]":"[播放中-"+Stringify.formatTimeStamp(HANDLER.playTime())+"]")+ Stringify.loadInfo(HANDLER.getAudioPlayer().getPlayingTrack())));
        for (AudioTrack track : HANDLER.getTracks()) {
            lists.add(new ListData(track,Stringify.loadInfo(track)));
        }
        playingList.setListData(lists.toArray());
        songTitle.setText(Stringify.titleDisplay(HANDLER.getAudioPlayer().getPlayingTrack().getInfo().title));
    }

    private void volumeSet(ChangeEvent e) {
        HANDLER.getAudioPlayer().setVolume(slider1.getValue());
    }

    private void skipSong(ActionEvent e) {
        AudioTrack playing = HANDLER.getAudioPlayer().getPlayingTrack();
        if (playing == null) return;
        boolean skipCurrent = false;
        for (Object o : playingList.getSelectedValuesList()) {
            ListData data = ((ListData) o);
            AudioTrack track = ((AudioTrack) data.obj);
            if (playing == track) skipCurrent = true;
            HANDLER.getTracks().remove(track);
        }
        if (skipCurrent) HANDLER.skip();
        update();
    }

    private void removeAllSongs(ActionEvent e) {
        HANDLER.getTracks().clear();
        HANDLER.getAudioPlayer().stopTrack();
        update();
    }

    private void shuffleSongs(ActionEvent e) {
        Collections.shuffle(HANDLER.getTracks());
        update();
    }

    private void togglePlayStatus(ActionEvent e) {
        boolean set = !HANDLER.getAudioPlayer().isPaused();
        if (set) HANDLER.pause();
        else HANDLER.play();
        updateIcon();
    }
    private void updateIcon() {
        if (HANDLER.getAudioPlayer().isPaused()) {
            togglePlayStatus.setIcon(new ImageIcon(getClass().getResource("/play.png")));
        } else {
            togglePlayStatus.setIcon(new ImageIcon(getClass().getResource("/pause.png")));
        }
        if (HANDLER.isLoop()) {
            loopToggle.setIcon(new ImageIcon(getClass().getResource("/loop.png")));
        } else {
            loopToggle.setIcon(new ImageIcon(getClass().getResource("/noloop.png")));
        }
    }

    private void last10s(ActionEvent e) {
        AudioTrack track = HANDLER.getAudioPlayer().getPlayingTrack();
        if (track == null) {
            return;
        }
        long current = track.getPosition();
        long target = current - 10000L;
        if (target < 0) target = 0;
        track.setPosition(target);
    }

    private void next10s(ActionEvent e) {
        AudioTrack track = HANDLER.getAudioPlayer().getPlayingTrack();
        if (track == null) {
            return;
        }
        long current = track.getPosition();
        long target = current + 10000L;
        if (target >= track.getDuration()) {
            HANDLER.skip();
            return;
        }
        track.setPosition(target);
    }

    private void setSongPlayBorder(String title) {
        songPlay.setBorder(new TitledBorder(BorderFactory.createEmptyBorder(), title, TitledBorder.LEFT, TitledBorder.TOP));
    }

    private void loopToggle(ActionEvent e) {
        boolean set = !HANDLER.isLoop();
        HANDLER.setLoop(set);
        updateIcon();
    }

    private void shuffleActionPerformed(ActionEvent e) {
        shuffleSongs(e);
    }

    private void songPlayStateChanged(MouseEvent e) {
        AudioTrack track = HANDLER.getAudioPlayer().getPlayingTrack();
        if (track == null) return;
        track.setPosition((long) (track.getDuration()*(songPlay.getValue()/1000.0)));
    }

    private void select(ListSelectionEvent e) {
        List select = playingList.getSelectedValuesList();
        if (select.size() == 0) {
            info.setText("-");
            return;
        }
        if (select.size() > 1) {
            info.setText("選取了 " +select.size()+" 個項目");
            return;
        }
        AudioTrack track = ((AudioTrack) ((ListData) select.get(0)).obj);
        String label = "";
        AudioTrackInfo i = track.getInfo();
        if (i.title != null) label += "標題: " + i.title+ "\n";
        if (i.author != null) label += "作者: " +i.author +"\n";
        if (i.uri != null) label += "URI: " + i.uri+"\n";
        label += "長度: " + Stringify.formatTimeStamp(i.length);
        info.setText(label);
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        upperLine = new JPanel();
        upper_leftLine = new JPanel();
        scrollPane1 = new JScrollPane();
        playingList = new JList();
        upper_rightLine = new JPanel();
        panel3 = new JPanel();
        skipSong = new JButton();
        button1 = new JButton();
        button4 = new JButton();
        bottomLine = new JPanel();
        bottom_leftLine = new JPanel();
        info = new JTextArea();
        bottom_rightLine = new JPanel();
        para = new JPanel();
        panel4 = new JPanel();
        songTitle = new JLabel();
        songPlay = new JSlider();
        panel5 = new JPanel();
        shuffle = new JButton();
        last10s = new JButton();
        togglePlayStatus = new JButton();
        next10s = new JButton();
        loopToggle = new JButton();
        slider1 = new JSlider();

        //======== this ========
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        //======== upperLine ========
        {
            upperLine.setMaximumSize(new Dimension(700, 250));
            upperLine.setPreferredSize(new Dimension(700, 250));
            upperLine.setLayout(new BoxLayout(upperLine, BoxLayout.X_AXIS));

            //======== upper_leftLine ========
            {
                upper_leftLine.setPreferredSize(new Dimension(350, 250));
                upper_leftLine.setMaximumSize(new Dimension(350, 250));
                upper_leftLine.setBorder(new TitledBorder(null, "\u64ad\u653e\u6e05\u55ae", TitledBorder.LEADING, TitledBorder.DEFAULT_POSITION,
                    new Font("\u5fae\u8edf\u6b63\u9ed1\u9ad4", Font.PLAIN, 14)));
                upper_leftLine.setMinimumSize(new Dimension(350, 250));
                upper_leftLine.setLayout(new FlowLayout());

                //======== scrollPane1 ========
                {
                    scrollPane1.setBorder(null);
                    scrollPane1.setPreferredSize(new Dimension(340, 225));
                    scrollPane1.setMinimumSize(new Dimension(340, 225));

                    //---- playingList ----
                    playingList.setBorder(null);
                    playingList.setFont(new Font("\u5fae\u8edf\u6b63\u9ed1\u9ad4", Font.PLAIN, 12));
                    playingList.setPreferredSize(null);
                    playingList.setMinimumSize(new Dimension(340, 225));
                    playingList.setMaximumSize(new Dimension(340, 225));
                    playingList.addListSelectionListener(e -> select(e));
                    scrollPane1.setViewportView(playingList);
                }
                upper_leftLine.add(scrollPane1);
            }
            upperLine.add(upper_leftLine);

            //======== upper_rightLine ========
            {
                upper_rightLine.setPreferredSize(new Dimension(350, 250));
                upper_rightLine.setMaximumSize(new Dimension(350, 250));
                upper_rightLine.setBorder(new TitledBorder(null, "\u66f2\u76ee\u52d5\u4f5c", TitledBorder.TRAILING, TitledBorder.DEFAULT_POSITION,
                    new Font("\u5fae\u8edf\u6b63\u9ed1\u9ad4", Font.PLAIN, 14)));
                upper_rightLine.setMinimumSize(new Dimension(350, 250));
                upper_rightLine.setLayout(new FlowLayout());

                //======== panel3 ========
                {
                    panel3.setLayout(new BoxLayout(panel3, BoxLayout.Y_AXIS));

                    //---- skipSong ----
                    skipSong.setText("\u79fb\u9664\u6240\u9078\u6b4c\u66f2");
                    skipSong.addActionListener(e -> skipSong(e));
                    panel3.add(skipSong);

                    //---- button1 ----
                    button1.setText("\u79fb\u9664\u5168\u90e8\u6b4c\u66f2");
                    button1.addActionListener(e -> removeAllSongs(e));
                    panel3.add(button1);

                    //---- button4 ----
                    button4.setText("\u4e82\u5e8f\u64ad\u653e\u6e05\u55ae");
                    button4.addActionListener(e -> shuffleSongs(e));
                    panel3.add(button4);
                }
                upper_rightLine.add(panel3);
            }
            upperLine.add(upper_rightLine);
        }
        add(upperLine);

        //======== bottomLine ========
        {
            bottomLine.setMaximumSize(new Dimension(700, 250));
            bottomLine.setPreferredSize(new Dimension(700, 250));
            bottomLine.setLayout(new BoxLayout(bottomLine, BoxLayout.X_AXIS));

            //======== bottom_leftLine ========
            {
                bottom_leftLine.setPreferredSize(new Dimension(350, 250));
                bottom_leftLine.setMaximumSize(new Dimension(350, 250));
                bottom_leftLine.setBorder(new TitledBorder(null, "\u6b4c\u66f2\u8cc7\u8a0a", TitledBorder.LEADING, TitledBorder.BOTTOM,
                    new Font("\u5fae\u8edf\u6b63\u9ed1\u9ad4", Font.PLAIN, 14)));
                bottom_leftLine.setLayout(new FlowLayout());

                //---- info ----
                info.setText("-");
                info.setForeground(null);
                info.setCaretColor(null);
                info.setBorder(null);
                info.setEditable(false);
                info.setBackground(null);
                info.setAutoscrolls(false);
                info.setFocusable(false);
                info.setLineWrap(true);
                info.setPreferredSize(new Dimension(340, 240));
                info.setMinimumSize(new Dimension(340, 240));
                info.setMaximumSize(new Dimension(340, 240));
                bottom_leftLine.add(info);
            }
            bottomLine.add(bottom_leftLine);

            //======== bottom_rightLine ========
            {
                bottom_rightLine.setPreferredSize(new Dimension(350, 250));
                bottom_rightLine.setMaximumSize(new Dimension(350, 250));
                bottom_rightLine.setBorder(new TitledBorder(null, "\u64ad\u653e\u5668", TitledBorder.TRAILING, TitledBorder.BOTTOM,
                    new Font("\u5fae\u8edf\u6b63\u9ed1\u9ad4", Font.PLAIN, 14)));
                bottom_rightLine.setLayout(new FlowLayout());

                //======== para ========
                {
                    para.setLayout(new BoxLayout(para, BoxLayout.Y_AXIS));

                    //======== panel4 ========
                    {
                        panel4.setLayout(new FlowLayout());

                        //---- songTitle ----
                        songTitle.setText("SONG TITLE");
                        songTitle.setAutoscrolls(true);
                        panel4.add(songTitle);
                    }
                    para.add(panel4);

                    //---- songPlay ----
                    songPlay.setPreferredSize(new Dimension(340, 50));
                    songPlay.setBorder(new TitledBorder(BorderFactory.createEmptyBorder(), "00:00/00:00", TitledBorder.LEFT, TitledBorder.TOP));
                    songPlay.setValue(0);
                    songPlay.setMaximum(1000);
                    songPlay.setMinimumSize(new Dimension(340, 50));
                    songPlay.setMaximumSize(new Dimension(340, 50));
                    songPlay.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseReleased(MouseEvent e) {
                            songPlayStateChanged(e);
                        }
                    });
                    para.add(songPlay);

                    //======== panel5 ========
                    {
                        panel5.setLayout(new FlowLayout());

                        //---- shuffle ----
                        shuffle.setIcon(new ImageIcon(getClass().getResource("/shuffle.png")));
                        shuffle.setBackground(null);
                        shuffle.setContentAreaFilled(false);
                        shuffle.setFocusPainted(false);
                        shuffle.setForeground(null);
                        shuffle.setBorderPainted(false);
                        shuffle.setPreferredSize(new Dimension(30, 30));
                        shuffle.setMinimumSize(new Dimension(30, 30));
                        shuffle.setMaximumSize(new Dimension(30, 36));
                        shuffle.setToolTipText("\u6253\u4e82\u64ad\u653e\u6e05\u55ae");
                        shuffle.addActionListener(e -> shuffleActionPerformed(e));
                        panel5.add(shuffle);

                        //---- last10s ----
                        last10s.setIcon(new ImageIcon(getClass().getResource("/last.png")));
                        last10s.setBackground(null);
                        last10s.setContentAreaFilled(false);
                        last10s.setFocusPainted(false);
                        last10s.setForeground(null);
                        last10s.setBorderPainted(false);
                        last10s.setPreferredSize(new Dimension(30, 30));
                        last10s.setMinimumSize(new Dimension(30, 30));
                        last10s.setMaximumSize(new Dimension(30, 36));
                        last10s.setToolTipText("\u5f8c\u9000\u5341\u79d2");
                        last10s.addActionListener(e -> last10s(e));
                        panel5.add(last10s);

                        //---- togglePlayStatus ----
                        togglePlayStatus.setIcon(new ImageIcon(getClass().getResource("/pause.png")));
                        togglePlayStatus.setBackground(null);
                        togglePlayStatus.setContentAreaFilled(false);
                        togglePlayStatus.setFocusPainted(false);
                        togglePlayStatus.setForeground(null);
                        togglePlayStatus.setBorderPainted(false);
                        togglePlayStatus.setPreferredSize(new Dimension(30, 30));
                        togglePlayStatus.setMinimumSize(new Dimension(30, 30));
                        togglePlayStatus.setMaximumSize(new Dimension(30, 36));
                        togglePlayStatus.setToolTipText("\u5207\u63db\u64ad\u653e/\u66ab\u505c");
                        togglePlayStatus.addActionListener(e -> togglePlayStatus(e));
                        panel5.add(togglePlayStatus);

                        //---- next10s ----
                        next10s.setIcon(new ImageIcon(getClass().getResource("/Next.png")));
                        next10s.setBackground(null);
                        next10s.setContentAreaFilled(false);
                        next10s.setFocusPainted(false);
                        next10s.setForeground(null);
                        next10s.setBorderPainted(false);
                        next10s.setPreferredSize(new Dimension(30, 30));
                        next10s.setMinimumSize(new Dimension(30, 30));
                        next10s.setMaximumSize(new Dimension(30, 36));
                        next10s.setToolTipText("\u524d\u9032\u5341\u79d2");
                        next10s.addActionListener(e -> next10s(e));
                        panel5.add(next10s);

                        //---- loopToggle ----
                        loopToggle.setIcon(new ImageIcon(getClass().getResource("/noloop.png")));
                        loopToggle.setBackground(null);
                        loopToggle.setContentAreaFilled(false);
                        loopToggle.setFocusPainted(false);
                        loopToggle.setForeground(null);
                        loopToggle.setBorderPainted(false);
                        loopToggle.setPreferredSize(new Dimension(30, 30));
                        loopToggle.setMinimumSize(new Dimension(30, 30));
                        loopToggle.setMaximumSize(new Dimension(30, 36));
                        loopToggle.setToolTipText("\u5faa\u74b0\u64ad\u653e");
                        loopToggle.addActionListener(e -> loopToggle(e));
                        panel5.add(loopToggle);

                        //---- slider1 ----
                        slider1.setPreferredSize(new Dimension(130, 30));
                        slider1.setBorder(new TitledBorder(new EmptyBorder(3, 0, 0, 0), "\u97f3\u91cf", TitledBorder.LEFT, TitledBorder.ABOVE_TOP));
                        slider1.setValue(25);
                        slider1.setForeground(new Color(0, 102, 0));
                        slider1.setMinimumSize(new Dimension(130, 30));
                        slider1.addChangeListener(e -> volumeSet(e));
                        panel5.add(slider1);
                    }
                    para.add(panel5);
                }
                bottom_rightLine.add(para);
            }
            bottomLine.add(bottom_rightLine);
        }
        add(bottomLine);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel upperLine;
    private JPanel upper_leftLine;
    private JScrollPane scrollPane1;
    private JList playingList;
    private JPanel upper_rightLine;
    private JPanel panel3;
    private JButton skipSong;
    private JButton button1;
    private JButton button4;
    private JPanel bottomLine;
    private JPanel bottom_leftLine;
    private JTextArea info;
    private JPanel bottom_rightLine;
    private JPanel para;
    private JPanel panel4;
    private JLabel songTitle;
    private JSlider songPlay;
    private JPanel panel5;
    private JButton shuffle;
    private JButton last10s;
    private JButton togglePlayStatus;
    private JButton next10s;
    private JButton loopToggle;
    private JSlider slider1;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
