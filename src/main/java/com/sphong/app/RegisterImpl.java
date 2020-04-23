package com.sphong.app;

import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Component("Register")
public class RegisterImpl implements Register {
    private Map<Class<?>, Map<String, EntryPoint>> entryPoints;

    public RegisterImpl() {
        init();
    }

    @Override
    public void init() {
        entryPoints = new HashMap<>();
    }

    @Override
    public void add(Class<?> entryPointClass, EntryPoint newObj) {
        Map<String, EntryPoint> foundedEntryPoint = entryPoints.computeIfAbsent(entryPointClass, k -> new HashMap<>());
        foundedEntryPoint.put(newObj.getIdentity(), newObj);
    }

    @Override
    public EntryPoint get(Class<?> entryPointClass, String objName) {
        Map<String, EntryPoint> foundedEntryPoint = entryPoints.computeIfAbsent(entryPointClass, k -> new HashMap<>());
        return foundedEntryPoint.get(objName);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Collection<? extends EntryPoint> getAll(Class<?> entryPointClass) {
        Map<String, EntryPoint> foundedEntryPoint = entryPoints.get(entryPointClass);
        return (Collection<? extends EntryPoint>)Collections.unmodifiableCollection(foundedEntryPoint != null ? entryPoints.get(entryPointClass).values() : Collections.EMPTY_SET);
    }

    @Override
    public EntryPoint delete(Class<?> entryPointClass, String objName) {
        Map<String, EntryPoint> foundedEntryPoint = entryPoints.get(entryPointClass);
        return foundedEntryPoint.remove(objName);
    }
}
