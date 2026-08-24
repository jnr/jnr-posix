package jnr.posix.junit;

import jnr.ffi.Platform;
import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.util.Arrays;

import static org.junit.platform.commons.support.AnnotationSupport.findAnnotation;

class DisabledOnJnrOsCondition implements ExecutionCondition {
    @Override
    public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext context) {
        return findAnnotation(context.getElement(), DisabledOnJnrOs.class)
                .map(annotation -> {
                    Platform.OS currentOs = Platform.getNativePlatform().getOS();
                    boolean disabled = Arrays.asList(annotation.value()).contains(currentOs);
                    return disabled
                            ? ConditionEvaluationResult.disabled(
                                    "Disabled on jnr OS: " + currentOs + ", excluded " + Arrays.toString(annotation.value()))
                            : ConditionEvaluationResult.enabled("Enabled on jnr OS: " + currentOs);
                })
                .orElseGet(() -> ConditionEvaluationResult.enabled("@DisabledOnJnrOs is not present"));
    }
}
