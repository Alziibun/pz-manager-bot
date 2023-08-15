package com.github.alziibun.discord.commands;

import com.github.alziibun.Zomboid;
import com.github.alziibun.discord.DiscordBot;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.*;
import org.javacord.api.interaction.callback.InteractionOriginalResponseUpdater;

import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class whitelist {
    public SlashCommand command;
    public whitelist() {
        this.command = SlashCommand.with("whitelist", "Manage your Project Zomboid server's whitelist",
                Arrays.asList(
                        SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND,
                                "add", "Add a user to your Project Zomboid server.",
                                Arrays.asList(
                                    SlashCommandOption.create(SlashCommandOptionType.USER,
                                            "user", "Select a discord user to add to your whitelist"),
                                    SlashCommandOption.create(SlashCommandOptionType.STRING,
                                            "username", "Case Sensitive: the player's username, for when you'd like to set a custom name", false),
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
        String username = interaction.getArgumentStringValueByName("username").toString();
        Optional<SlashCommandInteractionOption> user = interaction.getArgumentByName("user");
        // CHECKS
        assert !username.isEmpty();
        // zomboid username char limit is 20
        if (username.length() > 20) {
            promise.thenAccept(updater -> {
                updater.setContent("Username cannot be more than 20 characters.").update();
                    });
        }
        String password = interaction.getArgumentStringValueByName("password").toString();
        String argString = String.format("\"%s\"", username);
        if (password != null) {
            argString = argString.concat(String.format(" \"%s\"", password));
        }

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
