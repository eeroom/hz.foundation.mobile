package com.azeroth.webcontainer;

@FunctionalInterface
public interface Action<T,B> {

    void invoke(T t,B b);
}
