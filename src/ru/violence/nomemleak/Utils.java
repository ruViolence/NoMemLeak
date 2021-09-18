package ru.violence.nomemleak;

import lombok.experimental.UtilityClass;

import java.lang.reflect.Field;

@UtilityClass
public class Utils {
    public Field getFieldAccessible(Class<?> clazz, String fieldName) throws NoSuchFieldException {
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field;
    }
}
