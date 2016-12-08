package ru.sberned.kafkalogback;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.contrib.json.classic.JsonLayout;
import ch.qos.logback.core.util.ContextUtil;

import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;

/**
 * Created by empatuk on 05/12/2016.
 */
public class CustomJsonLayout extends JsonLayout {
    public static final String LINE_NUMBER = "line_number";
    public static final String CLASS_NAME = "class_name";
    public static final String METHOD_NAME = "method_name";
    public static final String FILE_NAME = "file_name";
    public static final String HOST = "host";

    private boolean includeLineNumber = true;
    private boolean includeClassName = true;
    private boolean includeMethodName = true;
    private boolean includeHost = true;
    private boolean includeFileName = true;
    // fields splitted by |, where left is a key and right is a value
    private List<String> additionalFields;

    @Override
    protected void addCustomDataToJsonMap(Map<String, Object> map, ILoggingEvent event) {
        add(HOST, includeHost, getHostName(), map);
        if (event.hasCallerData()) {
            StackTraceElement callerData = event.getCallerData()[0];
            addLine(LINE_NUMBER, includeLineNumber, callerData.getLineNumber(), map);
            add(CLASS_NAME, includeClassName, callerData.getClassName(), map);
            add(METHOD_NAME, includeMethodName, callerData.getMethodName(), map);
            add(FILE_NAME, includeFileName, callerData.getFileName(), map);
        }

        if (additionalFields != null) {
            additionalFields.forEach(field -> {
                String[] p = field.split("\\|");
                if (p.length == 2) {
                    add(p[0], true, p[1], map);
                } else {
                    System.out.println("Unable to parse property string: " + field);
                }
            });
        }
    }

    private String getHostName() {
        try {
            return ContextUtil.getLocalHostName();
        } catch (UnknownHostException|SocketException e) {
            return "undefined";
        }
    }

    private void addLine(String fieldName, boolean field, int value, Map<String, Object> map) {
        if (field) {
            map.put(fieldName, value);
        }
    }

    public void setIncludeLineNumber(boolean includeLineNumber) {
        this.includeLineNumber = includeLineNumber;
    }

    public void setIncludeClassName(boolean includeClassName) {
        this.includeClassName = includeClassName;
    }

    public void setIncludeMethodName(boolean includeMethodName) {
        this.includeMethodName = includeMethodName;
    }

    public void setIncludeHost(boolean includeHost) {
        this.includeHost = includeHost;
    }

    public void setAdditionalFields(List<String> additionalFields) {
        this.additionalFields = additionalFields;
    }

    public void setIncludeFileName(boolean includeFileName) {
        this.includeFileName = includeFileName;
    }
}
