package me.matthewe.freealts;

import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.DisconnectEvent;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.EventListener;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by Matthew E on 10/30/2017.
 */
public class DiscordListener implements EventListener {
    public static Map<String, Alt> altMap = new HashMap<>();
    public static Map<Long, Long> waitMap = new HashMap<>();

    @Override
    public void onEvent(Event event) {
        if (event instanceof DisconnectEvent) {
            File file = new File("altx.txt");
            if (file.exists()) {
                file.delete();
            }
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            List<String> fileStringList = new ArrayList<>();
            for (Alt alt : altMap.values()) {
                fileStringList.add(alt.getEmail() + ":" + alt.getPassword());
            }
            try {
                FileUtils.writeLines(file, fileStringList);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (event instanceof ReadyEvent) {
            File file = new File("altx.txt");
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    List<String> strings = FileUtils.readLines(file);
                    if (!strings.isEmpty()) {
                        for (String string : strings) {
                            Alt alt = new Alt(string);
                            altMap.put(alt.getEmail(), alt);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if (event instanceof GuildMessageReceivedEvent) {
            GuildMessageReceivedEvent messageReceivedEvent = (GuildMessageReceivedEvent) event;
            if (messageReceivedEvent.getChannelType() == ChannelType.TEXT) {
                Message message = messageReceivedEvent.getMessage();
                if (message.getContent().equals(">alt")) {
                    if (messageReceivedEvent.getChannel().getIdLong() != Main.FREE_ALTS_ID) {
                        message.delete().queue();
                        return;
                    } else {
                        message.delete().queueAfter(3, TimeUnit.SECONDS);
                        User author = message.getAuthor();
                        author.openPrivateChannel().queue(privateChannel -> {
                            if (waitMap.containsKey(author.getIdLong())) {
                                Long aLong = waitMap.get(author.getIdLong());
                                if (System.currentTimeMillis() < aLong) {
                                    String time = "";
                                    long seconds = TimeUnit.MILLISECONDS.toSeconds(aLong - System.currentTimeMillis());
                                    long minutes = 0;
                                    while (seconds >= 60) {
                                        seconds -= 60;
                                        minutes++;
                                    }
                                    if (minutes > 0) {
                                        time += minutes + "m ";
                                    }
                                    if (seconds > 0) {
                                        time += seconds + "s";
                                    }
                                    if (time.endsWith(" ")) {
                                        time = time.substring(0, time.length() - 1);
                                    }
                                    privateChannel.sendMessage("Could not generate alt.\nYou must wait **" + time + "** for the cooldown to expire.").queue();
                                    return;
                                } else {
                                    waitMap.remove(author.getIdLong());
                                }
                            }
                            String alt = getAlt();
                            if (alt == null) {
                                privateChannel.sendMessage("Could not generate alt").queue();
                            } else {
                                if (altMap.containsKey(alt)) {
                                    Alt alt1 = altMap.get(alt);
                                    altMap.remove(alt);
                                    if (waitMap.containsKey(author.getIdLong())) {
                                        waitMap.remove(author.getIdLong());
                                    }
                                    waitMap.put(author.getIdLong(), System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(30));
                                    privateChannel.sendMessage("Generated alt: \n```\nEmail: " + alt1.getEmail() + "\nPassword: " + alt1.getPassword() + "\n```").queue();
                                }
                            }

                        });
                    }
                } else if (messageReceivedEvent.getChannel().getIdLong() == Main.FREE_ALTS_ID) {
                    if (message.isPinned()||(!message.getAttachments().isEmpty())) {
                        return;
                    }
                    message.delete().queue();
                }
            }
        }
    }

    public static Random random = new Random();

    private String getAlt() {
        List<Alt> altList = new ArrayList<>(altMap.values());
        if (altList.size() == 1) {
            Alt alt = altList.get(0);
            if (alt == null) {
                return null;
            }
            return alt.getEmail();
        }
        if (altList.isEmpty()) {
            return null;
        }
        int index = random.nextInt(altList.size() - 1);
        Alt alt = altList.get(index);
        if (alt != null) {
            return alt.getEmail();
        }
        return null;
    }
}
