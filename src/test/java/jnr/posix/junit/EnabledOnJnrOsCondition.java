package jnr.posix.junit;

import jnr.ffi.Platform;
import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.util.Arrays;

import static org.junit.platform.commons.support.AnnotationSupport.findAnnotation;

public class EnabledOnJnrOsCondition implements ExecutionCondition {
    @Override
    public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext context) {
        return findAnnotation(context.getElement(), EnabledOnJnrOs.class)
                .map(annotation -> {
                    Platform.OS currentOs = Platform.getNativePlatform().getOS();
                    boolean enabled = Arrays.asList(annotation.value()).contains(currentOs);
                    return enabled
                            ? ConditionEvaluationResult.enabled("Enabled on jnr OS: " + currentOs)
                            : ConditionEvaluationResult.disabled(
                                    "Disabled on jnr OS: " + currentOs + ", required one of " + Arrays.toString(annotation.value()));
                })
                .orElseGet(() -> ConditionEvaluationResult.enabled("@EnabledOnJnrOs is not present"));
    }
}
