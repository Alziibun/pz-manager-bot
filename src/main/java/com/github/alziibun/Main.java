package com.github.alziibun;

import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;

import java.io.*;
import java.util.Properties;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // init config file
        File configFile = new File("bot.config");
        Properties config = new Properties();
        try {
            FileReader reader = new FileReader(configFile);
            config.load(reader);
        } catch (FileNotFoundException ex) {
            System.out.println("Uh oh");
        } catch (IOException ex) {
            System.out.println("why");
        }

        if (config.getProperty("token") == null) {
            try {
                Scanner input = new Scanner(System.in);
                FileWriter writer = new FileWriter(configFile);
                System.out.print("Discord Bot Token: ");
                String token = input.nextLine();
                config.setProperty("token", token);
                config.store(writer, "token");
                writer.close();
            } catch (IOException ex) {
                System.out.println("lol");
            }
        }
        DiscordApi api = new DiscordApiBuilder()
                .setToken(config.getProperty("token"))
                .addServerBecomesAvailableListener( event -> {
                    System.out.println("Loaded " + event.getServer().getName());
                })
                .login()
                .join();
    }
}
