package com.sphong.app;

import org.springframework.stereotype.Component;

import java.util.Collection;

public interface Register {
    void init();
    void add(Class<?> entryPointClass, EntryPoint newObj);
    EntryPoint get(Class<?> entryPointClass, String objName);
    Collection<? extends EntryPoint> getAll(Class<?> entryPointClass);
    EntryPoint delete(Class<?> entryPointClass, String objName);
}
