package jnr.posix.junit;

import jnr.ffi.Platform;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Enables the annotated test class or method only when
 * {@link Platform#getNativePlatform()}'s {@link Platform.OS} matches one of
 * the given values, unlike JUnit's own {@code @EnabledOnOs} which compares
 * against JUnit's {@code OS} enum derived from {@code os.name}.
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(EnabledOnJnrOsCondition.class)
public @interface EnabledOnJnrOs {
    Platform.OS[] value();
}
