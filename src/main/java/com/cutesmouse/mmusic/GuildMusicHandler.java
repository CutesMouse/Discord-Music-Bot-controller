package com.cutesmouse.mmusic;

import com.cutesmouse.mmusic.frames.ListData;
import com.cutesmouse.mmusic.utils.Stringify;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEvent;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventListener;
import com.sedmelluq.discord.lavaplayer.player.event.TrackEndEvent;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame;
import dev.lavalink.youtube.YoutubeAudioSourceManager;
import dev.lavalink.youtube.clients.*;
import dev.lavalink.youtube.clients.skeleton.Client;
import net.dv8tion.jda.api.audio.AudioSendHandler;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.io.File;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.LinkedList;

public class GuildMusicHandler implements AudioSendHandler, AudioEventListener {
    private static final DefaultAudioPlayerManager audioManager;

    static {
        audioManager = new DefaultAudioPlayerManager();
        audioManager.registerSourceManager(new YoutubeAudioSourceManager(/*allowSearch:*/ true,
                new Music(),
                new TvHtml5Embedded(),
                new AndroidMusic(),
                new Web(),
                new WebEmbedded(),
                new Android(),
                new Ios()));
        AudioSourceManagers.registerRemoteSources(audioManager);
        AudioSourceManagers.registerLocalSource(audioManager);
    }

    private final Guild GUILD;
    private final LinkedList<AudioTrack> TRACKS;
    private final AudioPlayer audioPlayer;
    private VoiceChannel channel;
    private AudioFrame lastFrame;

    public GuildMusicHandler(Guild g) {
        this.GUILD = g;
        this.TRACKS = new LinkedList<AudioTrack>();
        this.audioPlayer = audioManager.createPlayer();
        this.audioPlayer.setVolume(25);
        this.audioPlayer.addListener(this);
        GUILD.getAudioManager().setSendingHandler(this);
    }

    public AudioPlayer getAudioPlayer() {
        return audioPlayer;
    }

    private boolean loop = false;

    public void setLoop(boolean b) {
        loop = b;
    }

    public boolean isLoop() {
        return loop;
    }

    public void skip() {
        audioPlayer.stopTrack();
    }

    public LinkedList<AudioTrack> getTracks() {
        return TRACKS;
    }

    public void setChannel(VoiceChannel channel) {
        this.channel = channel;
    }

    public void playMusic(AudioTrack track, VoiceChannel channel) {
        setChannel(channel);
        GUILD.getAudioManager().openAudioConnection(channel);
        if (audioPlayer.getPlayingTrack() == null) {
            audioPlayer.playTrack(track);
        } else {
            TRACKS.add(track);
        }
    }

    public void playByFile(File f) {
        playBySearch(f.getAbsolutePath(), true);
    }

    public void playBySearch(String keyWord) {
        playBySearch(keyWord, false);
    }

    public void playBySearch(String keyWord, boolean isFile) {
        if (!keyWord.startsWith("http://") && !keyWord.startsWith("https://") && !isFile) {
            keyWord = "ytsearch: " + keyWord;
        }
        audioManager.loadItemOrdered(Main.getSelectedChannel().getGuild(), keyWord, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                ShowConfirmWindow(track);
            }

            @Override
            public void playlistLoaded(AudioPlaylist audios) {
                ShowConfirmWindow(audios.getTracks().toArray(new AudioTrack[0]));
            }

            @Override
            public void noMatches() {
                JOptionPane.showMessageDialog(Main.MAINWINDOW, "找不到對應的歌曲!", "錯誤", JOptionPane.PLAIN_MESSAGE);
            }

            @Override
            public void loadFailed(FriendlyException e) {
                JOptionPane.showMessageDialog(Main.MAINWINDOW, e.getMessage(), "發生錯誤!", JOptionPane.WARNING_MESSAGE);
            }
        });
    }

    public void ShowConfirmWindow(AudioTrack... tracks) {
        JScrollPane SCROLL = new JScrollPane();
        JList<ListData> LIST = new JList<>(Arrays.stream(tracks).map(t -> new ListData(t, Stringify.loadInfo(t))).toArray(ListData[]::new));
        LIST.setBorder(new TitledBorder(null, "選擇播放源", TitledBorder.LEADING, TitledBorder.DEFAULT_POSITION,
                new Font("\u5fae\u8edf\u6b63\u9ed1\u9ad4", Font.PLAIN, 20)));
        LIST.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        SCROLL.setBorder(null);
        SCROLL.setPreferredSize(new Dimension(450, 300));
        SCROLL.setViewportView(LIST);
        if (JOptionPane.showConfirmDialog(Main.MAINWINDOW, SCROLL, "查詢結果", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            if (LIST.getSelectedValue() == null) return;
            for (ListData data : LIST.getSelectedValuesList()) {
                playMusic(((AudioTrack) data.obj), Main.getSelectedChannel());
            }
        }
    }

    public void pause() {
        audioPlayer.setPaused(true);
    }

    public long playTime() {
        if (audioPlayer.getPlayingTrack() == null) return 0;
        return audioPlayer.getPlayingTrack().getPosition();
    }

    public void play() {
        audioPlayer.setPaused(false);
    }

    @Override
    public boolean canProvide() {
        lastFrame = audioPlayer.provide();
        return lastFrame != null;
    }

    @Override
    public ByteBuffer provide20MsAudio() {
        return ByteBuffer.wrap(lastFrame.getData());
    }

    @Override
    public boolean isOpus() {
        return true;
    }

    @Override
    public void onEvent(AudioEvent event) {
        if (event instanceof TrackEndEvent) {
            if (loop && ((TrackEndEvent) event).endReason != AudioTrackEndReason.STOPPED) {
                TRACKS.add(((TrackEndEvent) event).track.makeClone());
            }
            if (TRACKS.size() == 0) {
                GUILD.getAudioManager().closeAudioConnection();
            } else {
                audioPlayer.playTrack(TRACKS.poll());
            }
        }
    }
}
