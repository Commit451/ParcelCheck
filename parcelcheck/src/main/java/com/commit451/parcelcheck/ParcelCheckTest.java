package com.commit451.parcelcheck;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.test.AndroidTestCase;

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
 * Test to see if a package of models are all properly parcelable. Simply override and provide the packages that contain your parcelable models
 */
public abstract class ParcelCheckTest extends AndroidTestCase {

    private static final String TEST_STRING = "This is a test string";
    private static final int TEST_INT = 42;
    private static final float TEST_FLOAT = 33.333f;
    private static final boolean TEST_BOOL = true;
    private static final char TEST_CHAR = 'q';

    /**
     * Helps us to not get into infinite loops if we have a structure in our models where something
     * like a Person model can have a Person field
     */
    private ObjectStack mObjectStack;

    /**
     * The one method you need to implement.
     * @return the packages you want to check
     */
    public abstract String[] getModelPackageNames();

    /**
     * Test all parcels within package to see if they properly implement {@link Parcelable}
     * @throws Exception
     */
    public void testParcels() throws Exception {
        String[] packages = getModelPackageNames();
        for (String pkg : packages) {
            ArrayList<String> classes = getClassesOfPackage(getContext(), pkg);
            Assert.assertFalse("No classes found in package " + pkg, classes == null || classes.size() == 0);
            for (String className : classes) {
                Class currentClass = getContext().getClassLoader().loadClass(pkg + "." + className);
                if (isParcelable(currentClass) && !Modifier.isAbstract(currentClass.getModifiers())) {
                    mObjectStack = new ObjectStack();
                    Parcelable parcelable = (Parcelable) getTestObject(currentClass);
                    Parcel parcel = Parcel.obtain();
                    parcelable.writeToParcel(parcel, 0);
                    parcel.setDataPosition(0);
                    Field creatorField = currentClass.getField("CREATOR");
                    Parcelable.Creator creator = (Parcelable.Creator) creatorField.get(null);
                    Parcelable read = (Parcelable) creator.createFromParcel(parcel);
                    assertTrue(currentClass.getSimpleName() + " did not equal after parceling", checkFieldsEqual(currentClass.getSimpleName(), parcelable, read));
                }
            }
        }
    }


    private boolean checkIfEquals(String fieldName, Object first, Object second) throws IllegalAccessException {
        Class objectClass = first.getClass();

        if (Collection.class.isAssignableFrom(objectClass)) {
            ArrayList firstObjects = new ArrayList((Collection) first);
            ArrayList secondObjects = new ArrayList((Collection) second);
            for (int i = 0; i < firstObjects.size(); i++) {
                Object firstObject = firstObjects.get(i);
                Object secondObject = secondObjects.get(i);
                return checkIfEquals(fieldName, firstObject, secondObject);
            }
            return true;
        } else if (objectClass.isArray()) {
            for (int i = 0; i < ((Object[]) first).length; i++) {
                Object firstObject = ((Object[]) second)[i];
                Object secondObject = ((Object[]) second)[i];
                return checkIfEquals(fieldName, firstObject, secondObject);
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


        return checkFieldsEqual(fieldName, first, second);
    }

    public boolean checkFieldsEqual(String fieldName, Object first, Object second) throws IllegalAccessException {
        List<Field> fields = getClassFields(first.getClass());
        for (Field field : fields) {
            field.setAccessible(true);
            if (Modifier.isTransient(field.getModifiers()) || Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            Object firstField = field.get(first);
            Object secondField = field.get(second);
            boolean equals = checkIfEquals(fieldName + " > " + field.getName(), firstField, secondField);
            assertTrue("Objects \"" + firstField + "\" and \"" + secondField + "\" of type \"" + field.getType().getSimpleName() + "\" in \"" + fieldName + " > " + field.getName() + "\" do not match", equals);
            return equals;
        }
        return false;
    }



    private void fillWithGarbage(Object object) throws Exception {
        List<Field> fields = getClassFields(object.getClass());
        for (Field field : fields) {
            if (Modifier.isTransient(field.getModifiers()) || Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            field.setAccessible(true);
            field.set(object, getObjectForClass(field.getType(), field));
        }
    }

    private List<Field> getClassFields(Class clazz) {
        ArrayList<Field> fields = new ArrayList<>(Arrays.asList(clazz.getDeclaredFields()));
        if (clazz.getSuperclass() != null) {
            fields.addAll(getClassFields(clazz.getSuperclass()));
        }
        return fields;
    }

    private Object getObjectForClass(Class clazz, Field field) throws Exception {
        if (String.class.isAssignableFrom(clazz)) {
            return TEST_STRING;
        } else if (Integer.class.isAssignableFrom(clazz) || int.class.isAssignableFrom(clazz) || Long.class.isAssignableFrom(clazz) || long.class.isAssignableFrom(clazz)) {
            return TEST_INT;
        } else if (Float.class.isAssignableFrom(clazz) || float.class.isAssignableFrom(clazz) || Double.class.isAssignableFrom(clazz) || double.class.isAssignableFrom(clazz)) {
            return TEST_FLOAT;
        } else if (Boolean.class.isAssignableFrom(clazz) || boolean.class.isAssignableFrom(clazz)) {
            return TEST_BOOL;
        } else if (Character.class.isAssignableFrom(clazz) || char.class.isAssignableFrom(clazz)) {
            return TEST_CHAR;
        } else if (Collection.class.isAssignableFrom(clazz)) {
            if (clazz == List.class) {
                clazz = ArrayList.class;
            }
            Collection collection = (Collection) clazz.newInstance();
            ParameterizedType type = (ParameterizedType) field.getGenericType();
            Class<?> genericType = (Class<?>) type.getActualTypeArguments()[0];
            addObjectToCollection(collection, genericType);
            return collection;
        } else if (clazz.isArray()) {
            return getObjectArray(clazz);
        } else {
            Object object = getTestObject(clazz);
            return object;
        }
    }

    private Object getTestObject(Class clazz) throws Exception {
        Constructor constructor = clazz.getDeclaredConstructor();
        constructor.setAccessible(true);
        Assert.assertNotNull("The class " + clazz.getSimpleName() + " does not have an empty constructor", constructor);
        if (mObjectStack.canAdd(clazz)) {
            mObjectStack.push(clazz);
            Object model = constructor.newInstance();
            fillWithGarbage(model);
            mObjectStack.pop();
            return model;
        }
        return null;

    }

    private void addObjectToCollection(Collection collection, Class clazz) throws Exception {
        Object test = getObjectForClass(clazz, null);
        if (test != null) {
            collection.add(test);
        }
    }

    private Object[] getObjectArray(Class clazz) throws Exception {
        Object object = getObjectForClass(clazz, null);
        Object[] arr;
        if (object != null) {
            arr = (Object[]) Array.newInstance(clazz, 1);
            arr[0] = object;
        } else {
            arr = (Object[]) Array.newInstance(clazz, 0);
        }
        return arr;
    }


    /**
     * Is the class parcelable?
     * @param clazz the class
     * @return true if it extends Parcelable, false otherwise
     */
    private boolean isParcelable(Class clazz) {
        return Parcelable.class.isAssignableFrom(clazz);
    }

    /**
     * Gets all of the classes within a package
     * @param context the context
     * @param packageName the package name to fetch classes from
     * @return an list of named classes within package
     */
    private ArrayList<String> getClassesOfPackage(Context context, String packageName) {
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
}
