package com.github.alziibun;

import com.github.alziibun.discord.DiscordBot;
import org.omg.CORBA.Any;

import java.io.*;
import java.util.Properties;
import java.util.Scanner;

public class Program {
    public static Properties config = new Properties();
    public static File configFile = new File("pz-manager.config");
    public static void main(String[] args) {
        // start out by loading our config file
        try {
            if (!configFile.exists()) {
                // if there is no config file, make a new one
                System.out.println(configFile.createNewFile()
                        ? "A new config file was created for you."
                        : "Config file was found!");
            }
            // now load the config file as properties regardless of result.
            FileReader reader = new FileReader(configFile);
            config.load(reader);
            reader.close();
        } catch (IOException ex) {
            System.out.println("Couldn't create new config file.");
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }

        // start discord bot
        DiscordBot.start();
//        try {
//            Steam.install();
//            //Zomboid.linux();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
    }
}
