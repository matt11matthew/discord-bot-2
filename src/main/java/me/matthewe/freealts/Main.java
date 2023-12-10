package me.matthewe.freealts;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.exceptions.RateLimitedException;

import javax.security.auth.login.LoginException;

/**
 * Created by Matthew E on 10/30/2017.
 */
public class Main {
    private static final String TOKEN = "";

    public static final long FREE_ALTS_ID = -1L;

    public static void main(String[] args) {
        try {
            new JDABuilder(AccountType.BOT)
                    .setStatus(OnlineStatus.ONLINE)
                    .setToken(TOKEN)
                    .addEventListener(new DiscordListener())
                    .buildBlocking();
        } catch (LoginException | InterruptedException | RateLimitedException e) {
            e.printStackTrace();
        }
    }
}
