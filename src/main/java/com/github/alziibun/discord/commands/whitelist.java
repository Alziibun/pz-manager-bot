package com.github.alziibun.discord.commands;

import com.github.alziibun.Zomboid;
import com.github.alziibun.discord.DiscordBot;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.SlashCommand;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandOption;
import org.javacord.api.interaction.SlashCommandOptionType;
import org.javacord.api.interaction.callback.InteractionOriginalResponseUpdater;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

public class whitelist {
    public SlashCommand command;
    public whitelist() {
        this.command = SlashCommand.with("whitelist", "Manage your Project Zomboid server's whitelist",
                Arrays.asList(
                        SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND,
                                "add", "Add a user to your Project Zomboid server.",
                                Arrays.asList(
                                    SlashCommandOption.create(SlashCommandOptionType.STRING,
                                            "username", "Case Sensitive: the player's username", true),
                                    SlashCommandOption.create(SlashCommandOptionType.STRING,
                                            "password", "Create a manual password, if left empty creates a strong password automatically.", false)
                                )),
                        SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND,
                                "remove", "Remove a user from your Project Zomboid server.")
                )).createGlobal(DiscordBot.api).join();
    }
    public static void handle(SlashCommandCreateEvent event) {
        SlashCommandInteraction interaction = event.getSlashCommandInteraction();
        CompletableFuture<InteractionOriginalResponseUpdater> promise = interaction.respondLater(true);
        String username = String.valueOf(interaction.getArgumentStringValueByName("username"));
        String password = String.valueOf(interaction.getArgumentStringValueByName("password"));
        String argString = String.format("\"%s\"", username);
        if (password != null) {
            argString = argString + String.format(" \"%s\"", password);
        }
        assert username != null;
        switch (interaction.getFullCommandName()) {
            case "whitelist add":
                // adduser can take a password optional TODO: create password generator
                Zomboid.send(String.format("adduser %s", argString));
            case "whitelist remove":
                // removeuser only requires 1 argument
                Zomboid.send(String.format("removeuser \"%s\"", username));
        }
    }
}
