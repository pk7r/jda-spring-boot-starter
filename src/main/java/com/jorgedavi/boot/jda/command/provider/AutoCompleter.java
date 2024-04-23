package com.jorgedavi.boot.jda.command.provider;

import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.CommandAutoCompleteInteraction;

import java.util.stream.Stream;

@FunctionalInterface
public interface AutoCompleter {

    Stream<Command.Choice> autoComplete(CommandAutoCompleteInteraction interaction);

}