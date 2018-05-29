/*
 * Copyright 2017 Bartosz Lipinski
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

import com.squareup.javapoet.ClassName;

import java.io.File;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

public class Utils {

    private static final String ANNOTATION_KOTLIN_METADATA = "kotlin.Metadata";

    private final Types typeUtils;
    private final Elements elementUtils;
    private final Filer javaFiler;
    private final KotlinFiler kotlinFiler;

    private static Utils instance = null;

    public static void initialize(ProcessingEnvironment pe) {
        instance = null;
        getInstance(pe);
    }

    public synchronized static Utils getInstance() {
        return getInstance(null);
    }

    public synchronized static Utils getInstance(ProcessingEnvironment pe) {
        if (instance == null) {
            instance = new Utils(pe);
        }
        return instance;
    }

    private Utils(ProcessingEnvironment processingEnv) {
        typeUtils = processingEnv.getTypeUtils();
        elementUtils = processingEnv.getElementUtils();
        javaFiler = processingEnv.getFiler();
        kotlinFiler = KotlinFiler.with(processingEnv);
    }

    public static Types types() {
        return getInstance().typeUtils;
    }

    public static Elements elements() {
        return getInstance().elementUtils;
    }

    public static Filer javaFiler() {
        return getInstance().javaFiler;
    }

    public static File kotlinFiler() {
        return getInstance().kotlinFiler.newFile();
    }

    public static boolean isPresentOnClasspath(ClassName className) {
        return elements().getTypeElement(className.toString()) != null;
    }

    public static Set<String> getAnnotations(Element element) {
        Set<String> set = new LinkedHashSet<>();
        List<? extends AnnotationMirror> annotations = element.getAnnotationMirrors();
        for (AnnotationMirror annotation : annotations) {
            set.add(annotation.getAnnotationType().asElement().toString());
        }
        return Collections.unmodifiableSet(set);
    }

    public static boolean isKotlinClass(Element classElement) {
        return Utils.getAnnotations(classElement).contains(ANNOTATION_KOTLIN_METADATA);
    }

    public static final class KotlinFiler {
        public static final String KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated";
        private final String kaptKotlinGeneratedDir;

        private KotlinFiler(ProcessingEnvironment processingEnv) {
            kaptKotlinGeneratedDir = processingEnv.getOptions().get(KAPT_KOTLIN_GENERATED_OPTION_NAME);
            if (kaptKotlinGeneratedDir == null) {
                Logger.warning("Can't find the target directory for generated Kotlin files.");
            }
        }

        public static KotlinFiler with(ProcessingEnvironment processingEnv) {
            return new KotlinFiler(processingEnv);
        }

        public File newFile() {
            if (kaptKotlinGeneratedDir == null) {
                throw new IllegalStateException("Can't generate Kotlin files.");
            }
            return new File(kaptKotlinGeneratedDir);
        }
    }
}
