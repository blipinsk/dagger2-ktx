/*
 * Copyright 2016 Bartosz Lipinski
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bartoszlipinski.dagger2ktx.utils;

import java.util.Arrays;
import java.util.List;

import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.Diagnostic;

public class Logger {
    private static boolean DEBUG = false;

    private static Logger instance = null;

    private final Messager messager;
    private final boolean logToSystemOut;

    public static void initialize(ProcessingEnvironment pe) {
        instance = null;
        getInstance(pe);
    }

    public synchronized static Logger getInstance() {
        return getInstance(null);
    }

    public synchronized static Logger getInstance(ProcessingEnvironment pe) {
        if (instance == null) {
            instance = new Logger(pe);
        }
        return instance;
    }

    private Logger(ProcessingEnvironment pe) {
        logToSystemOut = isJUnitTest();
        messager = pe.getMessager();
    }

    private boolean isJUnitTest() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        List<StackTraceElement> list = Arrays.asList(stackTrace);
        for (StackTraceElement element : list) {
            if (element.getClassName().startsWith("org.junit.")) {
                return true;
            }
        }
        return false;
    }

    public static void debug(String message) {
        if (DEBUG) {
            log(message);
        }
    }

    public static void log(String message) {
        if (getInstance().logToSystemOut) {
            System.out.println("LOG: " + message);
        }
        getInstance().messager.printMessage(Diagnostic.Kind.NOTE, message);
    }

    public static void error(String message) {
        if (getInstance().logToSystemOut) {
            System.out.println("ERROR: " + message);
        }
        getInstance().messager.printMessage(Diagnostic.Kind.ERROR, message);
//            throw new RuntimeException("Dagger2Ktx compiler failed...");
    }

    public static void warning(String message) {
        if (getInstance().logToSystemOut) {
            System.out.println("WARNING: " + message);
        }
        getInstance().messager.printMessage(Diagnostic.Kind.WARNING, message);
    }

}
