package com.vaha.server.common.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface Task {

  Type value() default Type.PUSH;

  enum Type {
    PUSH, PULL, CRON
  }
}
