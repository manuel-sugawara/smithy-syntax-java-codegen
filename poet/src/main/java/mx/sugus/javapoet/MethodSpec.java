/*
 * Copyright (C) 2015 Square, Inc.
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
package mx.sugus.javapoet;

import static mx.sugus.javapoet.Util.checkArgument;
import static mx.sugus.javapoet.Util.checkNotNull;
import static mx.sugus.javapoet.Util.checkState;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Modifier;

/**
 * A generated constructor or method declaration.
 */
public final class MethodSpec implements SyntaxNode {
    static final String CONSTRUCTOR = "<init>";

    public final String name;
    public final CodeBlock javadoc;
    public final List<AnnotationSpec> annotations;
    public final Set<Modifier> modifiers;
    public final List<TypeVariableName> typeVariables;
    public final TypeName returnType;
    public final List<ParameterSpec> parameters;
    public final boolean varargs;
    public final List<TypeName> exceptions;
    public final CodeBlock code;
    public final CodeBlock defaultValue;
    public final BlockStatementSpec body;


    private MethodSpec(Builder builder) {
        CodeBlock code = builder.code.build();
        checkArgument(code.isEmpty() || !builder.modifiers.contains(Modifier.ABSTRACT),
                      "abstract method %s cannot have code", builder.name);
        checkArgument(!builder.varargs || lastParameterIsArray(builder.parameters),
                      "last parameter of varargs method %s must be an array", builder.name);

        this.name = checkNotNull(builder.name, "name == null");
        this.javadoc = builder.javadoc.build();
        this.annotations = Util.immutableList(builder.annotations);
        this.modifiers = Util.immutableSet(builder.modifiers);
        this.typeVariables = Util.immutableList(builder.typeVariables);
        this.returnType = builder.returnType;
        this.parameters = Util.immutableList(builder.parameters);
        this.varargs = builder.varargs;
        this.exceptions = Util.immutableList(builder.exceptions);
        this.defaultValue = builder.defaultValue;
        this.code = code;
        this.body = builder.toBlockStatement();
    }

    public static Builder methodBuilder(String name) {
        return new Builder(name);
    }

    public static Builder constructorBuilder() {
        return new Builder(CONSTRUCTOR);
    }

    private boolean lastParameterIsArray(List<ParameterSpec> parameters) {
        return !parameters.isEmpty()
               && TypeName.asArray(parameters.get(parameters.size() - 1).type) != null;
    }

    void emit(CodeWriter codeWriter, String enclosingName, Set<Modifier> implicitModifiers) {
        codeWriter.emitJavadoc(javadocWithParameters());
        codeWriter.emitAnnotations(annotations, false);
        codeWriter.emitModifiers(modifiers, implicitModifiers);

        if (!typeVariables.isEmpty()) {
            codeWriter.emitTypeVariables(typeVariables);
            codeWriter.emit(" ");
        }

        if (isConstructor()) {
            codeWriter.emit("$L($Z", enclosingName);
        } else {
            codeWriter.emit("$T $L($Z", returnType, name);
        }

        boolean firstParameter = true;
        for (Iterator<ParameterSpec> i = parameters.iterator(); i.hasNext(); ) {
            ParameterSpec parameter = i.next();
            if (!firstParameter) {
                codeWriter.emit(",").emitWrappingSpace();
            }
            parameter.emit(codeWriter, !i.hasNext() && varargs);
            firstParameter = false;
        }

        codeWriter.emit(")");

        if (!exceptions.isEmpty()) {
            codeWriter.emitWrappingSpace().emit("throws");
            boolean firstException = true;
            for (TypeName exception : exceptions) {
                if (!firstException) {
                    codeWriter.emit(",");
                }
                codeWriter.emitWrappingSpace().emit("$T", exception);
                firstException = false;
            }
        }

        if (hasModifier(Modifier.ABSTRACT)) {
            codeWriter.emit(";\n");
        } else if (hasModifier(Modifier.NATIVE)) {
            // Code is allowed to support stuff like GWT JSNI.
            codeWriter.emit(code);
            codeWriter.emit(";\n");
        } else {
            codeWriter.emit(" ");
            body.emit(codeWriter);
        }
        codeWriter.popTypeVariables(typeVariables);
    }

    private CodeBlock javadocWithParameters() {
        CodeBlock.Builder builder = javadoc.toBuilder();
        boolean emitTagNewline = true;
        for (ParameterSpec parameterSpec : parameters) {
            if (!parameterSpec.javadoc.isEmpty()) {
                // Emit a new line before @param section only if the method javadoc is present.
                if (emitTagNewline && !javadoc.isEmpty()) {
                    builder.add("\n");
                }
                emitTagNewline = false;
                builder.add("@param $L $L", parameterSpec.name, parameterSpec.javadoc);
            }
        }
        return builder.build();
    }

    public boolean hasModifier(Modifier modifier) {
        return modifiers.contains(modifier);
    }

    public boolean isConstructor() {
        return name.equals(CONSTRUCTOR);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (getClass() != o.getClass()) {
            return false;
        }
        return toString().equals(o.toString());
    }

    @Override
    public int hashCode() {
        // XXX
        return toString().hashCode();
    }

    @Override
    public String toString() {
        StringBuilder out = new StringBuilder();
        CodeWriter codeWriter = new CodeWriter(out);
        emit(codeWriter, "Constructor", Collections.emptySet());
        return out.toString();
    }

    public Builder toBuilder() {
        Builder builder = new Builder(name);
        builder.javadoc.add(javadoc);
        builder.annotations.addAll(annotations);
        builder.modifiers.addAll(modifiers);
        builder.typeVariables.addAll(typeVariables);
        builder.returnType = returnType;
        builder.parameters.addAll(parameters);
        builder.exceptions.addAll(exceptions);
        builder.code.add(code);
        builder.varargs = varargs;
        builder.contents.addAll(body.nodes());
        return builder;
    }

    @Override
    public void emit(CodeWriter writer) {
        throw new UnsupportedOperationException();
    }

    public static final class Builder extends AbstractBlockBuilder<Builder, MethodSpec> {
        private final List<TypeVariableName> typeVariables = new ArrayList<>();
        private final List<AnnotationSpec> annotations = new ArrayList<>();
        private final List<Modifier> modifiers = new ArrayList<>();
        private final List<ParameterSpec> parameters = new ArrayList<>();
        private final CodeBlock.Builder javadoc = CodeBlock.builder();
        private final Set<TypeName> exceptions = new LinkedHashSet<>();
        private final CodeBlock.Builder code = CodeBlock.builder();
        private String name;
        private TypeName returnType;
        private boolean varargs;
        private CodeBlock defaultValue;

        private Builder(String name) {
            setName(name);
        }

        public Builder setName(String name) {
            checkNotNull(name, "name == null");
            checkArgument(name.equals(CONSTRUCTOR) || SourceVersion.isName(name),
                          "not a valid name: %s", name);
            this.name = name;
            this.returnType = name.equals(CONSTRUCTOR) ? null : TypeName.VOID;
            return this;
        }

        public Builder addJavadoc(String format, Object... args) {
            javadoc.add(format, args);
            return this;
        }

        public Builder addJavadoc(CodeBlock block) {
            javadoc.add(block);
            return this;
        }

        public Builder addAnnotations(Iterable<AnnotationSpec> annotationSpecs) {
            checkArgument(annotationSpecs != null, "annotationSpecs == null");
            for (AnnotationSpec annotationSpec : annotationSpecs) {
                this.annotations.add(annotationSpec);
            }
            return this;
        }

        public Builder addAnnotation(AnnotationSpec annotationSpec) {
            this.annotations.add(annotationSpec);
            return this;
        }

        public Builder addAnnotation(ClassName annotation) {
            this.annotations.add(AnnotationSpec.builder(annotation).build());
            return this;
        }

        public Builder addAnnotation(Class<?> annotation) {
            return addAnnotation(ClassName.get(annotation));
        }

        public Builder addModifiers(Modifier... modifiers) {
            checkNotNull(modifiers, "modifiers == null");
            Collections.addAll(this.modifiers, modifiers);
            return this;
        }

        public Builder addModifiers(Iterable<Modifier> modifiers) {
            checkNotNull(modifiers, "modifiers == null");
            for (Modifier modifier : modifiers) {
                this.modifiers.add(modifier);
            }
            return this;
        }

        public Builder addTypeVariables(Iterable<TypeVariableName> typeVariables) {
            checkArgument(typeVariables != null, "typeVariables == null");
            for (TypeVariableName typeVariable : typeVariables) {
                this.typeVariables.add(typeVariable);
            }
            return this;
        }

        public Builder addTypeVariable(TypeVariableName typeVariable) {
            typeVariables.add(typeVariable);
            return this;
        }

        public Builder returns(TypeName returnType) {
            checkState(!name.equals(CONSTRUCTOR), "constructor cannot have return type.");
            this.returnType = returnType;
            return this;
        }

        public Builder returns(Type returnType) {
            return returns(TypeName.get(returnType));
        }

        public Builder addParameters(Iterable<ParameterSpec> parameterSpecs) {
            checkArgument(parameterSpecs != null, "parameterSpecs == null");
            for (ParameterSpec parameterSpec : parameterSpecs) {
                this.parameters.add(parameterSpec);
            }
            return this;
        }

        public Builder addParameter(ParameterSpec parameterSpec) {
            this.parameters.add(parameterSpec);
            return this;
        }

        public Builder addParameter(TypeName type, String name, Modifier... modifiers) {
            return addParameter(ParameterSpec.builder(type, name, modifiers).build());
        }

        public Builder addParameter(Type type, String name, Modifier... modifiers) {
            return addParameter(TypeName.get(type), name, modifiers);
        }

        public Builder varargs() {
            return varargs(true);
        }

        public Builder varargs(boolean varargs) {
            this.varargs = varargs;
            return this;
        }

        public Builder addExceptions(Iterable<? extends TypeName> exceptions) {
            checkArgument(exceptions != null, "exceptions == null");
            for (TypeName exception : exceptions) {
                this.exceptions.add(exception);
            }
            return this;
        }

        public Builder addException(TypeName exception) {
            this.exceptions.add(exception);
            return this;
        }

        public Builder addException(Type exception) {
            return addException(TypeName.get(exception));
        }

        public MethodSpec build() {
            return new MethodSpec(this);
        }
    }
}
