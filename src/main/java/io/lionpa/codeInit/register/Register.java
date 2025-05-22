package io.lionpa.codeInit.register;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Register {
    String type() default "";
    String data() default "";
}
