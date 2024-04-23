package com.jorgedavi.boot.jda.command;

import com.jorgedavi.boot.jda.command.annotation.SlashCommand;
import com.jorgedavi.boot.jda.command.annotation.SlashSubcommand;
import com.jorgedavi.boot.jda.command.annotation.SubcommandGroup;
import com.jorgedavi.boot.jda.command.annotation.option.Option;
import com.jorgedavi.boot.jda.command.annotation.security.HasPermission;
import com.jorgedavi.boot.jda.command.provider.EmptyCompleter;
import com.jorgedavi.boot.jda.command.utility.CommandUtils;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.*;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.core.annotation.AnnotatedElementUtils;

import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter(AccessLevel.PRIVATE)
public class SlashCommandDefinition {

    private String name;

    private String fullCommandName;

    private String description;

    private final boolean nsfw;

    private Collection<Option> options;

    private final ISlashCommand instance;

    private final Class<? extends ISlashCommand> commandClass;

    private Permission[] requiredPermissions = {};

    private final boolean subcommand;

    private Collection<SlashCommandDefinition> subcommands;

    private SubcommandGroup subcommandGroup;

    private Map<SubcommandGroup, List<SlashCommandDefinition>> subcommandGroups;

    private final boolean guildOnly;

    public SlashCommandDefinition(ISlashCommand command, Class<? extends ISlashCommand> commandClass) {
        if (!CommandUtils.isValidSlashCommand(commandClass)) {
            throw new IllegalArgumentException("The command class " + commandClass.getName() + " is not annotated with @SlashCommand or @SlashSubcommand");
        }
        this.instance = command;
        this.commandClass = commandClass;
        this.subcommand = CommandUtils.isSlashSubcommand(getCommandClass());
        this.nsfw = CommandUtils.isNotSafeForWorkCommand(getCommandClass());
        this.guildOnly = CommandUtils.isGuildOnlyCommand(getCommandClass());
        if (AnnotatedElementUtils.hasAnnotation(commandClass, HasPermission.class)) {
            var hasPermission = AnnotatedElementUtils.findMergedAnnotation(commandClass, HasPermission.class);
            if (Objects.nonNull(hasPermission)) setRequiredPermissions(hasPermission.value());
        }
    }

    public Optional<SlashCommandDefinition> build(BeanFactory beanFactory) {
        if (isSubcommand()) {
            var spec = AnnotatedElementUtils.findMergedAnnotation(commandClass, SlashSubcommand.class);
            if (Objects.isNull(spec)) {
                return Optional.empty();
            }
            var mainCommandBean = beanFactory.getBean(spec.mainCommand());
            var mainCommand = new SlashCommandDefinition(mainCommandBean, spec.mainCommand());
            setSubcommandGroup(mainCommand.getSubcommandGroups()
                    .entrySet()
                    .stream()
                    .filter(entry -> entry
                            .getValue()
                            .stream()
                            .anyMatch(subcommandGroup -> subcommandGroup.getCommandClass().equals(commandClass)))
                    .findFirst()
                    .map(Map.Entry::getKey)
                    .orElse(null));
            setDescription(spec.description());
            setName(spec.name().toLowerCase());
            setFullCommandName(String.join(" ",
                    mainCommand.getFullCommandName(),
                    Objects.isNull(getSubcommandGroup()) ? "" : getSubcommandGroup().name(),
                    spec.name()
            ).toLowerCase());
            setOptions(Arrays.stream(spec.options()).toList());
            return Optional.of(this);
        }
        var spec = AnnotatedElementUtils.findMergedAnnotation(commandClass, SlashCommand.class);
        if (Objects.isNull(spec)) {
            return Optional.empty();
        }
        setDescription(spec.description());
        setName(spec.name().toLowerCase());
        setFullCommandName(getName().toLowerCase());
        setOptions(Arrays.stream(spec.options()).toList());
        setSubcommands(Arrays.stream(spec.subcommands())
                .map(c -> new SlashCommandDefinition(beanFactory.getBean(c), c))
                .toList());
        setSubcommandGroups(Arrays.stream(spec.subcommandGroups())
                .collect(Collectors.toMap(
                        group -> group,
                        group -> Arrays.stream(group.subcommands())
                                .map(c -> new SlashCommandDefinition(beanFactory.getBean(c), c))
                                .collect(Collectors.toList()),
                        (existingList, newList) -> existingList
                )));
        return Optional.of(this);
    }

    public Optional<SlashCommandData> toSlashCommandData() {
        if (isSubcommand()) return Optional.empty();
        var cmd = Commands.slash(getName(), getDescription());
        getOptions().stream()
                .map(this::wrapOption)
                .forEach(cmd::addOptions);
        getSubcommands().stream()
                .map(SlashCommandDefinition::toSubcommandData)
                .filter(Optional::isPresent)
                .map(Optional::orElseThrow)
                .forEach(cmd::addSubcommands);
        getSubcommandGroups()
                .entrySet()
                .stream()
                .map(cmdGroup -> {
                    var data = new SubcommandGroupData(cmdGroup.getKey().name(), cmdGroup.getKey().description());
                    cmdGroup.getValue().stream()
                            .map(SlashCommandDefinition::toSubcommandData)
                            .filter(Optional::isPresent)
                            .map(Optional::orElseThrow)
                            .forEach(data::addSubcommands);
                    return data;
                })
                .forEach(cmd::addSubcommandGroups);
        if (getRequiredPermissions().length > 0) {
            cmd.setDefaultPermissions(DefaultMemberPermissions.enabledFor(getRequiredPermissions()));
        }
        return Optional.of(cmd);
    }

    private Optional<SubcommandData> toSubcommandData() {
        if (!isSubcommand()) return Optional.empty();
        val cmd = new SubcommandData(getName(), getDescription());
        getOptions().stream()
                .map(this::wrapOption)
                .forEach(cmd::addOptions);
        return Optional.of(cmd);
    }

    private OptionData wrapOption(Option option) {
        var data = new OptionData(
                option.type(),
                option.name(),
                option.description(),
                option.required(),
                !option.autoCompleter().equals(EmptyCompleter.class));
        Arrays.stream(option.choices())
                .forEach(c -> data.addChoice(c.name(), c.value()));
        return data;
    }
}