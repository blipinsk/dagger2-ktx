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
package com.bartoszlipinski.dagger2ktx

import com.squareup.kotlinpoet.*

class KotlinCodeGenerator {

    companion object {
        const val FUN_CREATE = "create"
        const val PARAM_CLAZZ = "clazz"
        private val CLASS_NAME_KCLASS = ClassName.bestGuess("kotlin.reflect.KClass")
        private val ANNOTATION_SUPPRESS_UNUSED = AnnotationSpec
                .builder(Suppress::class)
                .addMember("%S", "UNUSED_PARAMETER")
                .build()
    }

    fun generateCreateFun(component: ComponentCandidate): FunSpec {
        val componentInterface = component.getComponentInterface()
        return FunSpec.builder(FUN_CREATE)
                .returns(componentInterface)
                .receiver(Dagger::class)
                .addParameter(ParameterSpec
                        .builder(PARAM_CLAZZ,
                                ParameterizedTypeName.get(CLASS_NAME_KCLASS, componentInterface))
                        .addAnnotation(ANNOTATION_SUPPRESS_UNUSED)
                        .build()
                )
                .addStatement("return %T.create()",
                        ClassName.bestGuess(component.getQualifiedGeneratedName()))
                .build()
    }

}
