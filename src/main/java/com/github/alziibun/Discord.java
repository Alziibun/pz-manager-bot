package com.github.alziibun;

import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;

import java.io.*;
import java.util.Properties;
import java.util.Scanner;

public class Discord {
    public static DiscordApi api;
    public static Properties config;
    private static File configFile;

    public Discord() {
    }

    public static void init() {
        System.out.println("Initializing Discord API.");
        try {
            configFile = new File("pz-manager.config");
            configFile.createNewFile();
            FileReader reader = new FileReader(configFile);
            config = new Properties();
            config.load(reader);
            reader.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        api = new DiscordApiBuilder()
                .setToken(getToken())
                .addServerBecomesAvailableListener( event -> {
                    System.out.println("Loaded " + event.getServer().getName());
                })
                .login()
                .join();
    }

    private static String getToken() {
        if (config.getProperty("discord.token") == null) {
            try {
                System.out.println("Couldn't get Discord Bot Token!");
                Scanner input = new Scanner(System.in);
                FileWriter writer = new FileWriter(configFile);
                System.out.print("Discord Bot Token: ");
                String token = input.nextLine();
                config.setProperty("discord.token", token);
                config.store(writer, "Added Discord bot token.");
                writer.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return config.getProperty("discord.token");
    }
}
