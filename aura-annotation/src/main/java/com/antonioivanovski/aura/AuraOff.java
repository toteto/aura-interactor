package com.antonioivanovski.aura;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation that causes the output method to not be wrapped and execute on
 * {@link AuraExecutor#runForeground(Runnable)}.
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
public @interface AuraOff {

}
