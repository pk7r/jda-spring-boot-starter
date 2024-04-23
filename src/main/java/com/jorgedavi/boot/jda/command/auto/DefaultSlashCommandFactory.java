package com.jorgedavi.boot.jda.command.auto;

import com.jorgedavi.boot.jda.command.ISlashCommand;
import com.jorgedavi.boot.jda.command.SlashCommandDefinition;
import com.jorgedavi.boot.jda.factory.SlashCommandFactory;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.ListableBeanFactory;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;

@Slf4j
@Getter
class DefaultSlashCommandFactory extends SlashCommandFactory {

    private final Collection<SlashCommandDefinition> components;

    DefaultSlashCommandFactory(ListableBeanFactory beanFactory) {
        super(beanFactory);
        this.components = new HashSet<>();
    }

    @Override
    public Optional<SlashCommandDefinition> create(Class<? extends ISlashCommand> component) {
        val bean = getBeanFactory().getBean(component);
        val definition = new SlashCommandDefinition(bean, component);
        return definition.build(getBeanFactory());
    }

    @Override
    public void save(SlashCommandDefinition data) {
        getComponents()
                .stream()
                .filter(d -> exists(d.getFullCommandName()))
                .filter(d -> exists(d.getCommandClass()))
                .findFirst()
                .ifPresentOrElse(c -> {
                    val message = "Command %s is already registered".formatted(c.getFullCommandName());
                    throw new IllegalStateException(message);
                }, () -> getComponents().add(data));
    }

    @Override
    public boolean exists(Class<? extends ISlashCommand> component) {
        return getComponents()
                .stream()
                .anyMatch(c -> c.getCommandClass().equals(component));
    }

    @Override
    public Optional<SlashCommandDefinition> getByFullCommandName(String fullCommandName) {
        return getComponents()
                .stream()
                .filter(c -> c.getFullCommandName().equalsIgnoreCase(fullCommandName))
                .findFirst();
    }

    @Override
    public boolean exists(String fullCommandName) {
        return getComponents()
                .stream()
                .anyMatch(c -> c.getFullCommandName().equalsIgnoreCase(fullCommandName));
    }
}