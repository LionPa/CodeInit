package io.lionpa.codeInit.register;

import java.lang.reflect.InvocationTargetException;

public interface IRegister {
    void run(Register register, Class<?> clazz) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException;
}
