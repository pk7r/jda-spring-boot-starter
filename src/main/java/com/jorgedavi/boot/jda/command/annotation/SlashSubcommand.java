package com.jorgedavi.boot.jda.command.annotation;

import com.jorgedavi.boot.jda.command.ISlashCommand;
import com.jorgedavi.boot.jda.command.annotation.option.Option;
import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Component
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface SlashSubcommand {

    @AliasFor(annotation = Component.class, attribute = "value")
    String name();

    String description();

    Option[] options() default {};

    Class<? extends ISlashCommand> mainCommand();

}