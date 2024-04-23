package com.jorgedavi.boot.jda.command.utility;

import com.jorgedavi.boot.jda.command.annotation.GuildOnly;
import com.jorgedavi.boot.jda.command.annotation.NotSafeForWork;
import com.jorgedavi.boot.jda.command.annotation.SlashCommand;
import com.jorgedavi.boot.jda.command.annotation.SlashSubcommand;
import lombok.experimental.UtilityClass;
import org.springframework.core.annotation.AnnotatedElementUtils;

import java.lang.reflect.AnnotatedElement;

@UtilityClass
public class CommandUtils {

    public boolean isSlashMainCommand(AnnotatedElement element) {
        return AnnotatedElementUtils.hasAnnotation(element, SlashCommand.class);
    }

    public boolean isSlashSubcommand(AnnotatedElement element) {
        return AnnotatedElementUtils.hasAnnotation(element, SlashSubcommand.class);
    }

    public boolean isValidSlashCommand(AnnotatedElement element) {
        if (isSlashMainCommand(element) && isSlashSubcommand(element)) {
            return false;
        }
        return isSlashMainCommand(element) || isSlashSubcommand(element);
    }

    public boolean isGuildOnlyCommand(AnnotatedElement element) {
        return AnnotatedElementUtils.hasAnnotation(element, GuildOnly.class);
    }

    public boolean isNotSafeForWorkCommand(AnnotatedElement element) {
        return AnnotatedElementUtils.hasAnnotation(element, NotSafeForWork.class);
    }
}
