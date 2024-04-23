package com.jorgedavi.boot.jda.command.annotation;

import com.jorgedavi.boot.jda.command.ISlashCommand;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface SubcommandGroup {

    String name();

    String description();

    Class<? extends ISlashCommand>[] subcommands() default {};

}
