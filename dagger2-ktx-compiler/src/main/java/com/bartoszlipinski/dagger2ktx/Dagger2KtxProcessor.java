/*
 * Copyright 2018 Bartosz Lipinski
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
package com.bartoszlipinski.dagger2ktx;

import com.bartoszlipinski.dagger2ktx.utils.Logger;
import com.bartoszlipinski.dagger2ktx.utils.Utils;
import com.squareup.kotlinpoet.FileSpec;
import dagger.Component;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

import static com.bartoszlipinski.dagger2ktx.utils.Utils.kotlinFiler;
import static java.util.Collections.singleton;
import static javax.lang.model.SourceVersion.latestSupported;

public class Dagger2KtxProcessor extends AbstractProcessor {

    private static final String FILE_GENERATION_COMMENT = "Generated code from dagger2-ktx compiler. Do not modify!";
    private static final String PACKAGE_DAGGER = "com.bartoszlipinski.dagger2ktx";
    private static final String KTX_SUFFIX = "$$Ktx";

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        Logger.initialize(processingEnv);
        Utils.initialize(processingEnv);
    }

    @Override
    public Set<String> getSupportedOptions() {
        return singleton(Utils.KotlinFiler.KAPT_KOTLIN_GENERATED_OPTION_NAME);
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return singleton(Component.class.getCanonicalName());
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnv) {
        try {
            Set<? extends Element> annotatedElements =
                    roundEnv.getElementsAnnotatedWith(Component.class);
            Set<ComponentCandidate> candidates = annotatedElements
                    .stream()
                    .map(ComponentCandidate::new)
                    .collect(Collectors.toSet());
            generateExtensionBridge(candidates);
        } catch (IOException e) {
            Logger.error("Error while creating a file: " + e.getMessage());
        }
        return false; //not claiming the annotation
    }

    private void generateExtensionBridge(Set<ComponentCandidate> candidates) throws IOException {
        KotlinCodeGenerator generator = new KotlinCodeGenerator();
        for (ComponentCandidate candidate : candidates) {
            FileSpec.builder(PACKAGE_DAGGER, candidate.getGeneratedName() + KTX_SUFFIX)
                    .addComment(FILE_GENERATION_COMMENT)
                    .addFunction(generator.generateCreateFun(candidate))
                    .build()
                    .writeTo(kotlinFiler());
        }
    }
}