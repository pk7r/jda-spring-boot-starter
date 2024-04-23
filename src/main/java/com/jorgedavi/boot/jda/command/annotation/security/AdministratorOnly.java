package com.jorgedavi.boot.jda.command.annotation.security;

import net.dv8tion.jda.api.Permission;

import java.lang.annotation.*;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@HasPermission(Permission.ADMINISTRATOR)
public @interface AdministratorOnly {

}