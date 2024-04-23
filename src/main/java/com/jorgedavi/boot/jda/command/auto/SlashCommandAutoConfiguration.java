package com.jorgedavi.boot.jda.command.auto;

import com.jorgedavi.boot.jda.JDAAutoConfiguration;
import com.jorgedavi.boot.jda.command.ISlashCommand;
import com.jorgedavi.boot.jda.command.SlashCommandDefinition;
import com.jorgedavi.boot.jda.command.provider.EmptyCompleter;
import com.jorgedavi.boot.jda.factory.SlashCommandFactory;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationListener;
import org.springframework.context.PayloadApplicationEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Slf4j
@AutoConfiguration
@AutoConfigureAfter(JDAAutoConfiguration.class)
public class SlashCommandAutoConfiguration {

    @SneakyThrows
    @Bean
    @Order(0)
    @ConditionalOnMissingBean
    SlashCommandFactory slashCommandFactory(ListableBeanFactory beanFactory) {
        val factory = new DefaultSlashCommandFactory(beanFactory);
        beanFactory.getBeansOfType(ISlashCommand.class)
                .values()
                .stream()
                .map(ISlashCommand::getClass)
                .map(factory::create)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .forEach(factory::save);
        return factory;
    }

    @Bean
    @Order(1)
    List<SlashCommandData> globalCommands(JDA jda, SlashCommandFactory commandFactory) {
        val commands = commandFactory.getComponents()
                .stream()
                .filter(c -> !c.isSubcommand())
                .filter(c -> !c.isGuildOnly())
                .map(SlashCommandDefinition::toSlashCommandData)
                .filter(Optional::isPresent)
                .map(Optional::orElseThrow)
                .toList();
        jda.updateCommands()
                .addCommands(commands)
                .queue(v -> log.info("Published {} global slash commands", commands.size()));
        return commands;
    }

    @Bean
    @Order(2)
    @ConditionalOnBean(Guild.class)
    List<SlashCommandData> guildCommands(Guild guild, SlashCommandFactory commandFactory) {
        val commands = commandFactory.getComponents()
                .stream()
                .filter(c -> !c.isSubcommand())
                .filter(SlashCommandDefinition::isGuildOnly)
                .map(SlashCommandDefinition::toSlashCommandData)
                .filter(Optional::isPresent)
                .map(Optional::orElseThrow)
                .toList();
        guild.updateCommands()
                .addCommands(commands)
                .queue(v -> log.info("Published {} guild slash commands", commands.size()));
        return commands;
    }

    @Bean
    @Order(3)
    ApplicationListener<PayloadApplicationEvent<SlashCommandInteractionEvent>> onSlashCommand(SlashCommandFactory factory) {
        return event -> factory
                .getByFullCommandName(event.getPayload().getFullCommandName())
                .ifPresent(c -> c.getInstance().interact().accept(event.getPayload()));
    }

    @Bean
    @Order(4)
    ApplicationListener<PayloadApplicationEvent<CommandAutoCompleteInteractionEvent>> onCommandInteraction(SlashCommandFactory factory) {
        return event -> factory
                .getByFullCommandName(event.getPayload().getFullCommandName())
                .map(SlashCommandDefinition::getOptions)
                .stream()
                .flatMap(Collection::stream)
                .filter(o -> event.getPayload().getFocusedOption().getName().equalsIgnoreCase(o.name()))
                .filter(o -> !o.autoCompleter().equals(EmptyCompleter.class))
                .findFirst()
                .ifPresent(command -> event.getPayload().replyChoices(factory.getBeanFactory()
                        .getBean(command.autoCompleter())
                        .autoComplete(event.getPayload())
                        .toList()).queue());
    }
}