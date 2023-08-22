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
        CompletableFuture<InteractionOriginalResponseUpdater> promise = interaction.respondLater(true);
        System.out.println(interaction.getFullCommandName());
        switch (interaction.getFullCommandName()) {
            case "server start":
                promise.thenAccept(updater -> {
                    updater.setContent("Server is starting...").update();
                });
                Zomboid.start();
            case "server stop":
                Zomboid.send("quit");
                promise.thenAccept(updater -> {
                    updater.setContent("Server stopping...").update();
                        });
            case "server restart":
                    promise.thenAccept(updater -> {
                        if (Objects.requireNonNull(Zomboid.server).isAlive()) {
                            // if the server is still running, stop it
                            Zomboid.send("quit");
                            updater.setContent("Stopping server...");
                            try {
                                // wait for server process to end
                                Zomboid.server.waitFor();
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        // start zomboid server
                        Zomboid.start();
                        updater.setContent("Starting server...").update();
                    });
        }
    }
}
