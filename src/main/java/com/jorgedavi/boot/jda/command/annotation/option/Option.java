package com.jorgedavi.boot.jda.command.annotation.option;

import com.jorgedavi.boot.jda.command.provider.AutoCompleter;
import com.jorgedavi.boot.jda.command.provider.EmptyCompleter;
import net.dv8tion.jda.api.interactions.commands.OptionType;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Option {

    String name();

    String description();

    OptionType type() default OptionType.STRING;

    Class<? extends AutoCompleter> autoCompleter() default EmptyCompleter.class;

    Choice[] choices() default {};

    boolean required() default true;

}