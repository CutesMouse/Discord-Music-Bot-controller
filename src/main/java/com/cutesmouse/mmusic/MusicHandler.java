package com.cutesmouse.mmusic;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;

import java.util.HashMap;

public class MusicHandler {
    private final JDA jda;
    private final HashMap<Guild,GuildMusicHandler> Handler;
    public MusicHandler(JDA jda) {
        this.jda = jda;
        this.Handler = new HashMap<>();
    }
    public GuildMusicHandler getHandler(Guild g) {
        if (Handler.containsKey(g)) return Handler.get(g);
        Handler.put(g, new GuildMusicHandler(g));
        return getHandler(g);
    }
}
