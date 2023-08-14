package com.github.alziibun.discord.commands;

import com.github.alziibun.Zomboid;
import com.github.alziibun.discord.DiscordBot;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.SlashCommand;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandOption;
import org.javacord.api.interaction.SlashCommandOptionType;
import org.javacord.api.interaction.callback.InteractionOriginalResponseUpdater;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class server {
    public SlashCommand command;
    public server() {
        this.command = SlashCommand.with("server", "Manage your Project Zomboid server.",
                Arrays.asList(
                        SlashCommandOption.create(SlashCommandOptionType.SUB_COMMAND,
                                "start", "Start your Project Zomboid server."),
                        SlashCommandOption.create(SlashCommandOptionType.SUB_COMMAND,
                                "stop", "Stop your Project Zomboid server safely."),
                        SlashCommandOption.create(SlashCommandOptionType.SUB_COMMAND,
                                "restart", "Stops, then starts your server again safely.")
                )).createGlobal(DiscordBot.api).join();
    }
    public static void handle(SlashCommandCreateEvent event) {
        SlashCommandInteraction interaction = event.getSlashCommandInteraction();
        // why the hell is this so long?
        CompletableFuture<InteractionOriginalResponseUpdater> promise = interaction.respondLater();
        System.out.println(interaction.getFullCommandName());
        switch (interaction.getFullCommandName()) {
            case "server start":
                try {
                    promise.thenAccept(updater -> {
                        updater
                                .setContent("Server is starting.")
                                .setFlags(MessageFlag.EPHEMERAL)
                                .update();
                    });
                    Zomboid.linux();
                } catch (IOException ex) {
                    promise.thenAccept(updater -> {
                        updater
                                .setContent("Failed to start server.")
                                .setFlags(MessageFlag.EPHEMERAL)
                                .update();
                    });
                    ex.printStackTrace();
                }
            case "server stop":
                Zomboid.send("quit");
                promise.thenAccept(updater -> {
                    updater
                            .setContent("Server stopping.")
                            .setFlags(MessageFlag.EPHEMERAL)
                            .update();
                        });
            case "server restart":
                    promise.thenAccept(updater -> {
                        if (Objects.requireNonNull(Zomboid.server).isAlive()) {
                            // if the server is still running, stop it
                            Zomboid.send("quit");
                            try {
                                Zomboid.server.waitFor();
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        try {
                            // start ubuntu version, TODO: replace with agnostic method
                            Zomboid.linux();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
        }
    }
}
