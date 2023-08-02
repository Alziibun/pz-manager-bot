package com.github.alziibun.discord.commands;

import com.github.alziibun.Zomboid;
import com.github.alziibun.discord.DiscordBot;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.SlashCommand;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandOption;
import org.javacord.api.interaction.SlashCommandOptionType;

import java.io.IOException;
import java.util.Arrays;

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
    public void handle(SlashCommandCreateEvent event) {
        SlashCommandInteraction interaction = event.getSlashCommandInteraction();
        switch (interaction.getFullCommandName()) {
            case "server start":
                try {
                    Zomboid.start();
                    interaction
                            .createImmediateResponder()
                            .setContent("Server starting.");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            case "server stop":
                try {
                    Zomboid.stop();
                    interaction
                            .createImmediateResponder()
                            .setContent("Server stopped.");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
        }
    }
}
