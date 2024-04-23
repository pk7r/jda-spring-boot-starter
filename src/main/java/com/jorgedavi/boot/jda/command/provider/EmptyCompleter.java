package com.jorgedavi.boot.jda.command.provider;

import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.CommandAutoCompleteInteraction;

import java.util.stream.Stream;

public class EmptyCompleter implements AutoCompleter {

    @Override
    public Stream<Command.Choice> autoComplete(CommandAutoCompleteInteraction interaction) {
        return Stream.empty();
    }
}