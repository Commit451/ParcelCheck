package com.commit451.parcelcheck;

/**
 * Helper which helps out with reflection on objects
 */
public class ObjectHelper {

    public static boolean objectHasEquals(Class clazz) {
        try {
            clazz.getDeclaredMethod("equals", Object.class);
            return true;
        } catch (NoSuchMethodException e) {
        }
        //If no exception thrown, then the method exists!
        return false;
    }
}
