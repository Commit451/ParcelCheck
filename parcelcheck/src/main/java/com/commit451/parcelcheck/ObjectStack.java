package com.commit451.parcelcheck;

import java.util.Stack;

/**
 * A stack which keeps up with objects, to prevent infinite loops
 */
public class ObjectStack extends Stack<Class> {

    public boolean canAdd(Class clazz) {
        int count = 0;
        for (Class prevClass : this) {
            if (prevClass.equals(clazz)) {
                count++;
            }
        }
        return count < 2;
    }
}
