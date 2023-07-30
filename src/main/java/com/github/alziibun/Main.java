package com.github.alziibun;

import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.interaction.SlashCommand;

import java.io.*;
import java.util.Properties;
import java.util.Scanner;

import com.github.alziibun.Zomboid;

public class Main {
    public static void main(String[] args) {
        // init config file
        Discord.init();
        try {
            Zomboid.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
