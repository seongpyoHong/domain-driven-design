package com.sphong;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Register {
    private static Register soleInstance = new Register();
    private Map<Class<?>, Map<String, EntryPoint>> entryPoints;

    public static void init() {
        soleInstance.entryPoints =
                new HashMap<Class<?>, Map<String, EntryPoint>>();
    }

    public static EntryPoint get(Class<? extends EntryPoint> entryPointClass, String objectName) {
        return soleInstance.getObj(entryPointClass, objectName);
    }

    public static void add(Class<? extends EntryPoint> entryPointClass, EntryPoint newObject) {
        soleInstance.addObj(entryPointClass, newObject);
    }

    public static Collection<? extends EntryPoint> getAll(Class<? extends EntryPoint> entryPointClass) {
        return soleInstance.getAllObjects(entryPointClass);
    }

    public static EntryPoint delete(Class<?> entryPointClass, String objectName) {
        return soleInstance.deleteObj(entryPointClass, objectName);
    }

    private EntryPoint deleteObj(Class<?> entryPointClass, String objectName) {
        Map<String, EntryPoint> foundEntryPoint =
                entryPoints.get(entryPointClass);
        return foundEntryPoint.remove(objectName);
    }

    private EntryPoint getObj(Class<? extends EntryPoint> entryPointClass, String objectName) {
        Map<String, EntryPoint> foundEntryPoint =
                entryPoints.get(entryPointClass);
        return foundEntryPoint.get(objectName);
    }

    private void addObj(Class<? extends EntryPoint> entryPointClass, EntryPoint newObject) {
        Map<String, EntryPoint> foundEntryPoint =
                entryPoints.get(entryPointClass);
        if (foundEntryPoint == null) {
            foundEntryPoint = new HashMap<>();
            foundEntryPoint.put(newObject.getIdentity(), newObject);
        }
        foundEntryPoint.put(newObject.getIdentity(), newObject);
    }

    @SuppressWarnings("unchecked")
    private Collection<? extends EntryPoint> getAllObjects(Class<? extends EntryPoint> entryPointClass) {
        Map<String, EntryPoint> foundEntryPoints =
                entryPoints.get(entryPointClass);
        return (Collection<? extends EntryPoint>) Collections.unmodifiableCollection(foundEntryPoints != null ?
                entryPoints.get(entryPointClass).values() :
                Collections.EMPTY_SET);
    }
}
