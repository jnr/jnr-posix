package jnr.posix.junit;

import jnr.ffi.Platform;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Disables the annotated test class or method when
 * {@link Platform#getNativePlatform()}'s {@link Platform.OS} matches one of
 * the given values, unlike JUnit's own {@code @DisabledOnOs} which compares
 * against JUnit's {@code OS} enum derived from {@code os.name}.
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(DisabledOnJnrOsCondition.class)
public @interface DisabledOnJnrOs {
    Platform.OS[] value();
}
