package com.commit451.parcelcheck;

import android.content.Context;
import android.os.Parcelable;

import junit.framework.Assert;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;

import dalvik.system.DexFile;

/**
 * Helper which helps out with reflection on objects
 */
public class ObjectHelper {

    public static final String TEST_STRING = "This is a test string";
    public static final int TEST_INT = 42;
    public static final long TEST_LONG = 42L;
    public static final float TEST_FLOAT = 33.333f;
    public static final double TEST_DOUBLE = 33.333;
    public static final boolean TEST_BOOL = true;
    public static final char TEST_CHAR = 'q';

    public static boolean objectHasEquals(Class clazz) {
        try {
            clazz.getDeclaredMethod("equals", Object.class);
            return true;
        } catch (NoSuchMethodException e) {
        }
        //If no exception thrown, then the method exists!
        return false;
    }

    private static void fillWithGarbage(ObjectStack objectStack, Object object) throws Exception {
        List<Field> fields = getClassFields(object.getClass());
        for (Field field : fields) {
            if (Modifier.isTransient(field.getModifiers()) || Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            field.setAccessible(true);
            field.set(object, getObjectForClass(objectStack, field.getType(), field));
        }
    }

    private static List<Field> getClassFields(Class clazz) {
        ArrayList<Field> fields = new ArrayList<>(Arrays.asList(clazz.getDeclaredFields()));
        if (clazz.getSuperclass() != null) {
            fields.addAll(getClassFields(clazz.getSuperclass()));
        }
        return fields;
    }

    private static Object getObjectForClass(ObjectStack objectStack, Class clazz, Field field) throws Exception {
        if (String.class.isAssignableFrom(clazz)) {
            return ObjectHelper.TEST_STRING;
        } else if (Integer.class.isAssignableFrom(clazz) || int.class.isAssignableFrom(clazz)) {
            return ObjectHelper.TEST_INT;
        } else if (Long.class.isAssignableFrom(clazz) || long.class.isAssignableFrom(clazz)) {
            return ObjectHelper.TEST_LONG;
        } else if (Float.class.isAssignableFrom(clazz) || float.class.isAssignableFrom(clazz)) {
            return ObjectHelper.TEST_FLOAT;
        } else if (Double.class.isAssignableFrom(clazz) || double.class.isAssignableFrom(clazz)) {
            return ObjectHelper.TEST_DOUBLE;
        } else if (Boolean.class.isAssignableFrom(clazz) || boolean.class.isAssignableFrom(clazz)) {
            return ObjectHelper.TEST_BOOL;
        } else if (Character.class.isAssignableFrom(clazz) || char.class.isAssignableFrom(clazz)) {
            return ObjectHelper.TEST_CHAR;
        } else if (Collection.class.isAssignableFrom(clazz)) {
            if (clazz == List.class) {
                clazz = ArrayList.class;
            }
            Collection collection = (Collection) clazz.newInstance();
            ParameterizedType type = (ParameterizedType) field.getGenericType();
            Class<?> genericType = (Class<?>) type.getActualTypeArguments()[0];
            addObjectToCollection(objectStack, collection, genericType);
            return collection;
        } else if (clazz.isArray()) {
            return getObjectArray(objectStack, clazz);
        } else {
            Object object = getTestObject(objectStack, clazz);
            return object;
        }
    }

    /**
     * Is the class parcelable?
     * @param clazz the class
     * @return true if it extends Parcelable, false otherwise
     */
    public static boolean isParcelable(Class clazz) {
        return Parcelable.class.isAssignableFrom(clazz);
    }

    /**
     * Gets all of the classes within a package
     * @param context the context
     * @param packageName the package name to fetch classes from
     * @return an list of named classes within package
     */
    public static ArrayList<String> getClassesOfPackage(Context context, String packageName) {
        ArrayList<String> classes = new ArrayList<>();
        try {
            String packageCodePath = context.getPackageCodePath();
            DexFile df = new DexFile(packageCodePath);
            for (Enumeration<String> iter = df.entries(); iter.hasMoreElements(); ) {
                String className = iter.nextElement();
                if (className.contains(packageName)) {
                    classes.add(className.substring(className.lastIndexOf(".") + 1, className.length()));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return classes;
    }

    public static Object getTestObject(ObjectStack objectStack, Class clazz) throws Exception {
        Constructor constructor = clazz.getDeclaredConstructor();
        constructor.setAccessible(true);
        Assert.assertNotNull("The class " + clazz.getSimpleName() + " does not have an empty constructor", constructor);
        if (objectStack.canAdd(clazz)) {
            objectStack.push(clazz);
            Object model = constructor.newInstance();
            fillWithGarbage(objectStack, model);
            objectStack.pop();
            return model;
        }
        return null;

    }

    private static void addObjectToCollection(ObjectStack objectStack, Collection collection, Class clazz) throws Exception {
        Object test = getObjectForClass(objectStack, clazz, null);
        if (test != null) {
            collection.add(test);
        }
    }

    private static Object[] getObjectArray(ObjectStack objectStack, Class clazz) throws Exception {
        Object object = getObjectForClass(objectStack, clazz, null);
        Object[] arr;
        if (object != null) {
            arr = (Object[]) Array.newInstance(clazz, 1);
            arr[0] = object;
        } else {
            arr = (Object[]) Array.newInstance(clazz, 0);
        }
        return arr;
    }

    public static boolean checkFieldsEqual(String fieldName, Object first, Object second, boolean checkingSuccess) throws IllegalAccessException {
        List<Field> fields = getClassFields(first.getClass());
        if (!fields.isEmpty() && !containsOnlyTransientAndStaticFields(fields)) {
            for (Field field : fields) {
                field.setAccessible(true);
                if (Modifier.isTransient(field.getModifiers()) || Modifier.isStatic(field.getModifiers())) {
                    continue;
                }
                Object firstField = field.get(first);
                Object secondField = field.get(second);
                boolean equals = checkIfEquals(fieldName + " > " + field.getName(), firstField, secondField, checkingSuccess);
                if (checkingSuccess) {
                    Assert.assertTrue("Objects \"" + firstField + "\" and \"" + secondField + "\" of type \"" + field.getType().getSimpleName() + "\" in \"" + fieldName + " > " + field.getName() + "\" do not match", equals);
                }
                return equals;
            }
            return false;
        }
        //No fields to check so I guess we are good?
        return true;
    }

    /**
     * Only contains transient or static fields, which is the case for a model that is empty, but
     * says it "implements Parcelable"
     * @param fields the list of fields
     * @return true if contains only transient and static fields, false otherwise
     */
    private static boolean containsOnlyTransientAndStaticFields(List<Field> fields) {
        boolean containsNormalField = false;
        for (Field field : fields) {
            if (!Modifier.isTransient(field.getModifiers()) && !Modifier.isStatic(field.getModifiers())) {
                containsNormalField = true;
            }
        }
        return !containsNormalField;
    }

    private static boolean checkIfEquals(String fieldName, Object first, Object second, boolean checkingSuccess) throws IllegalAccessException {
        Class objectClass = first.getClass();

        if (Collection.class.isAssignableFrom(objectClass)) {
            ArrayList firstObjects = new ArrayList((Collection) first);
            ArrayList secondObjects = new ArrayList((Collection) second);
            for (int i = 0; i < firstObjects.size(); i++) {
                Object firstObject = firstObjects.get(i);
                Object secondObject = secondObjects.get(i);
                return checkIfEquals(fieldName, firstObject, secondObject, checkingSuccess);
            }
            return true;
        } else if (objectClass.isArray()) {
            for (int i = 0; i < ((Object[]) first).length; i++) {
                Object firstObject = ((Object[]) second)[i];
                Object secondObject = ((Object[]) second)[i];
                return checkIfEquals(fieldName, firstObject, secondObject, checkingSuccess);
            }
            return true;
        } else if (String.class.isAssignableFrom(objectClass)
                || Integer.class.isAssignableFrom(objectClass)
                || int.class.isAssignableFrom(objectClass)
                || Long.class.isAssignableFrom(objectClass)
                || long.class.isAssignableFrom(objectClass)
                || Float.class.isAssignableFrom(objectClass)
                || float.class.isAssignableFrom(objectClass)
                || Double.class.isAssignableFrom(objectClass)
                || double.class.isAssignableFrom(objectClass)
                || Boolean.class.isAssignableFrom(objectClass)
                || boolean.class.isAssignableFrom(objectClass)
                || Character.class.isAssignableFrom(objectClass)
                || char.class.isAssignableFrom(objectClass)
                || ObjectHelper.objectHasEquals(objectClass)) {
            return first.equals(second);
        }


        return checkFieldsEqual(fieldName, first, second, checkingSuccess);
    }
}
