package com.jorgedavi.boot.jda.command;

import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;

import java.util.function.Consumer;

public interface ISlashCommand {

    Consumer<SlashCommandInteraction> interact();

}