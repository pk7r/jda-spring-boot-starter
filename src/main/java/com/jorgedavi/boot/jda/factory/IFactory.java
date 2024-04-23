package com.jorgedavi.boot.jda.factory;

import org.springframework.beans.factory.BeanFactory;

import java.util.Collection;
import java.util.Optional;

interface IFactory<T, D> {

    Optional<D> create(Class<? extends T> component);

    void save(D data);

    boolean exists(Class<? extends T> component);

    Collection<D> getComponents();

    BeanFactory getBeanFactory();

}