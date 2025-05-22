package io.lionpa.codeInit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value = ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RunOn {
    RunOnType on() default RunOnType.START;
    int priority() default -1;
    String before() default "";
    String after() default "";
}
