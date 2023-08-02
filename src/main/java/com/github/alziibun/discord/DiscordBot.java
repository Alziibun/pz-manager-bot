package com.github.alziibun.discord;

import com.github.alziibun.Program;
import com.github.alziibun.discord.commands.server;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.intent.Intent;
import org.javacord.api.interaction.*;

import java.io.*;
import java.util.*;

public class DiscordBot {
    public static DiscordApi api;
    public static Set<SlashCommand> slashCommands;

    public DiscordBot() {
    }

    public static void start() {
        // log in to discord and initiate API
        api = new DiscordApiBuilder()
                .setToken(getToken())
                .addIntents(Intent.GUILDS)
                .login().join();
        api.addSlashCommandCreateListener(server::handle);
    }

    private static String getToken() {
        if (Program.config.getProperty("discord.token") == null) {
            try {
                System.out.println("Couldn't get Discord Bot Token!");
                Scanner input = new Scanner(System.in);
                FileWriter writer = new FileWriter(Program.configFile);
                System.out.print("Discord Bot Token: ");
                String token = input.nextLine();
                Program.config.setProperty("discord.token", token);
                Program.config.store(writer, "Added Discord bot token.");
                writer.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return Program.config.getProperty("discord.token");
    }
}
