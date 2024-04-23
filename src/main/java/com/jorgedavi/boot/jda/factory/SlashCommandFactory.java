package com.jorgedavi.boot.jda.factory;

import com.jorgedavi.boot.jda.command.ISlashCommand;
import com.jorgedavi.boot.jda.command.SlashCommandDefinition;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ListableBeanFactory;

import java.util.Optional;

@Getter
@RequiredArgsConstructor
public abstract class SlashCommandFactory implements IFactory<ISlashCommand, SlashCommandDefinition> {

    private final ListableBeanFactory beanFactory;

    public abstract Optional<SlashCommandDefinition> getByFullCommandName(String fullCommandName);

    public abstract boolean exists(String fullCommandName);

}