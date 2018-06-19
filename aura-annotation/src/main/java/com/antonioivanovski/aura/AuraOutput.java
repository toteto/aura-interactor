package com.antonioivanovski.aura;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation that causes AuraOutput to be generated for the annotated output interface.
 * <p>
 * By default, output methods will be invoked on foreground with {@link AuraExecutor#runForeground(Runnable)}
 * <p>
 * Use {@link AuraOff} for managing how the output methods are invoked.
 *
 * @see AuraOff
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface AuraOutput {

}
