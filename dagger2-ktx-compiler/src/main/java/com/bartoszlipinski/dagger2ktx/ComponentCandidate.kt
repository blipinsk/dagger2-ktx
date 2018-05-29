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

import com.bartoszlipinski.dagger2ktx.utils.getPackage
import com.squareup.kotlinpoet.ClassName
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement

class ComponentCandidate(private val component: Element) {

    companion object {
        const val DAGGER_PREFIX = "Dagger"
    }

    val packageName: String = component.getPackage()

    val generatedName by lazy { DAGGER_PREFIX + nestedName }

    val nestedName by lazy {
        var element: TypeElement = component as TypeElement
        var name = component.getSimpleName().toString()
        while (element.enclosingElement is TypeElement) {
            element = element.enclosingElement as TypeElement
            name = element.simpleName.toString() + "_" + name
        }
        name
    }

    fun getComponentInterface() = ClassName.bestGuess((component as TypeElement).qualifiedName.toString())

    fun getQualifiedGeneratedName() = "$packageName.$generatedName"

}