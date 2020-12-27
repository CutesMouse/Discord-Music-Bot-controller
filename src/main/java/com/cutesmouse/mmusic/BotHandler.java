package com.cutesmouse.mmusic;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

import javax.security.auth.login.LoginException;

public class BotHandler {
    private final String token;
    private JDA JDA;
    public BotHandler(String token) {
        this.token = token;
        try {
            this.JDA = JDABuilder.createDefault(token).build().awaitReady();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (LoginException e) {
            e.printStackTrace();
        }
    }

    public JDA getJDA() {
        return JDA;
    }
}
