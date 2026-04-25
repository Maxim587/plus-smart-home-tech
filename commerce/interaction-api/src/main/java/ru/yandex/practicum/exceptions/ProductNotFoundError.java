package ru.yandex.practicum.exceptions;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;


@Builder
@AllArgsConstructor
public class ProductNotFoundError {

    private Cause cause;
    private List<StackTraceItem> stackTrace;
    private String httpStatus;
    private String userMessage;
    private String message;
    private Suppressed suppressed;
    private String localizedMessage;

    @Data
    @Builder
    static class Cause {
        List<StackTraceItem> stackTrace;
        String message;
        String localizedMessage;
    }

    @Data
    @Builder
    static class StackTraceItem {
        String classLoaderName;
        String moduleName;
        String moduleVersion;
        String methodName;
        String fileName;
        int lineNumber;
        String className;
        boolean nativeMethod;
    }

    @Data
    @Builder
    static class Suppressed {
        List<StackTraceItem> stackTrace;
        String message;
        String localizedMessage;
    }
}



