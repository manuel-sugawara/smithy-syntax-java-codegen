package mx.sugus.codegen.plugin.data;

import static mx.sugus.codegen.util.PoetUtils.toClassName;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import javax.lang.model.element.Modifier;
import mx.sugus.codegen.NotNullOptionalityKnowledgeIndex;
import mx.sugus.codegen.SensitiveKnowledgeIndex;
import mx.sugus.codegen.SymbolConstants;
import mx.sugus.codegen.plugin.JavaShapeDirective;
import mx.sugus.javapoet.ClassName;
import mx.sugus.javapoet.FieldSpec;
import mx.sugus.javapoet.MethodSpec;
import mx.sugus.javapoet.ParameterSpec;
import mx.sugus.javapoet.ParameterizedTypeName;
import mx.sugus.javapoet.TypeName;
import mx.sugus.javapoet.TypeSpec;
import mx.sugus.syntax.java.ConstTrait;
import mx.sugus.syntax.java.IsaTrait;
import mx.sugus.util.CollectionBuilderReference;
import software.amazon.smithy.model.shapes.EnumShape;
import software.amazon.smithy.model.shapes.MemberShape;
import software.amazon.smithy.model.shapes.ShapeId;

public final class BaseStructureData implements DirectedStructure {

    private final BaseStructureDataBuilderKind builderType = BaseStructureDataBuilderKind.USE_REFERENCE;

    @Override
    public ClassName className(JavaShapeDirective state) {
        return toClassName(state.symbol());
    }

    @Override
    public TypeSpec.Builder typeSpec(JavaShapeDirective state) {
        var result = TypeSpec.classBuilder(state.symbol().getName())
                             .addModifiers(Modifier.PUBLIC, Modifier.FINAL);
        var shape = state.shape();
        if (shape.hasTrait(IsaTrait.class)) {
            var parent = state.parentClass(shape);
            result.addSuperinterface(parent);
        }
        return result;
    }

    @Override
    public List<FieldSpec> fieldsFor(JavaShapeDirective state, MemberShape member) {
        if (member.hasTrait(ConstTrait.class)) {
            return Collections.emptyList();
        }
        return List.of(fieldFor(state, member));
    }

    public FieldSpec fieldFor(JavaShapeDirective state, MemberShape member) {
        var symbolProvider = state.symbolProvider();
        var name = symbolProvider.toMemberName(member);
        var type = symbolProvider.toTypeName(member);
        return FieldSpec.builder(type, name)
                        .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                        .build();
    }

    @Override
    public List<MethodSpec> constructors(JavaShapeDirective state) {
        return List.of(constructorFromBuilder(state));
    }

    public MethodSpec constructorFromBuilder(JavaShapeDirective state) {
        var symbolProvider = state.symbolProvider();
        var builder = MethodSpec.constructorBuilder()
                                .addModifiers(Modifier.PRIVATE)
                                .addParameter(ParameterSpec.builder(builderClassName(), "builder").build());
        var optionalityIndex = NotNullOptionalityKnowledgeIndex.of(state.model());
        for (var member : state.shape().members()) {
            if (member.hasTrait(ConstTrait.class)) {
                continue;
            }
            var name = symbolProvider.toMemberName(member);
            var symbol = symbolProvider.toSymbol(member);
            var aggregateType = SymbolConstants.aggregateType(symbol);
            switch (aggregateType) {
                case LIST, SET, MAP -> memberValueFromBuilder(state, member, builder);
                default -> {
                    if (!optionalityIndex.isMemberNullable(member)) {
                        builder.addStatement("this.$L = $T.requireNonNull(builder.$L, $S)", name, Objects.class, name, name);
                    } else {
                        builder.addStatement("this.$1L = builder.$1L", name);
                    }
                }
            }
        }
        return builder.build();
    }

    @Override
    public List<MethodSpec> methodsFor(JavaShapeDirective state, MemberShape member) {
        if (member.hasTrait(ConstTrait.class)) {
            return List.of(constAccessor(state, member));
        }
        return List.of(accessor(state, member));
    }

    private void memberValueFromBuilder(JavaShapeDirective state, MemberShape member, MethodSpec.Builder builder) {
        var symbolProvider = state.symbolProvider();
        var name = symbolProvider.toMemberName(member);
        var symbol = symbolProvider.toSymbol(member);
        switch (builderType) {
            case PLAIN_USE_ONCE -> builder.addStatement("this.$1L = $2T.$3L(builder.$1L)",
                                                        name,
                                                        Collections.class,
                                                        SymbolConstants.toUnmodifiableCollection(symbol));
            case USE_REFERENCE -> builder.addStatement("this.$1L = builder.$1L.asPersistent()", name);
            default -> throw new IllegalArgumentException("builder type not supported: " + builderType);
        }
    }

    private MethodSpec accessor(JavaShapeDirective state, MemberShape member) {
        var symbolProvider = state.symbolProvider();
        var name = symbolProvider.toMemberName(member);
        var type = symbolProvider.toTypeName(member);
        return MethodSpec.methodBuilder(name)
                         .addModifiers(Modifier.PUBLIC)
                         .returns(type)
                         .addStatement("return this.$L", name)
                         .build();
    }

    private MethodSpec constAccessor(JavaShapeDirective state, MemberShape member) {
        var symbolProvider = state.symbolProvider();
        var name = symbolProvider.toMemberName(member);
        var type = symbolProvider.toTypeName(member);
        var builder = MethodSpec.methodBuilder(name)
                                .addModifiers(Modifier.PUBLIC)
                                .returns(type);
        var refId = member.getTrait(ConstTrait.class).map(ConstTrait::getValue).orElse("");
        var shapeId = ShapeId.from(refId);
        var constMember = state.model().expectShape(shapeId, MemberShape.class);
        var containingSymbol = state.model().expectShape(constMember.getContainer(), EnumShape.class);
        builder.addStatement("return $T.$L", symbolProvider.toClassName(containingSymbol),
                             symbolProvider.toMemberName(constMember));
        return builder.build();
    }

    public ClassName builderClassName() {
        return ClassName.bestGuess("Builder");
    }

    @Override
    public List<MethodSpec> extraMethods(JavaShapeDirective state) {
        return List.of(toBuilderMethod(state),
                       equalsMethod(state),
                       hashCodeMethod(state),
                       toStringMethod(state),
                       builderMethod(state));
    }

    public MethodSpec toBuilderMethod(JavaShapeDirective state) {
        var dataType = builderClassName();
        return MethodSpec.methodBuilder("toBuilder")
                         .addModifiers(Modifier.PUBLIC)
                         .returns(dataType)
                         .addStatement("return new $T(this)", dataType)
                         .build();
    }

    public MethodSpec equalsMethod(JavaShapeDirective state) {
        var symbolProvider = state.symbolProvider();
        var builder = MethodSpec.methodBuilder("equals")
                                .addAnnotation(Override.class)
                                .addModifiers(Modifier.PUBLIC)
                                .returns(boolean.class)
                                .addParameter(Object.class, "obj");

        builder.beginControlFlow("if (this == obj)")
               .addStatement("return true")
               .endControlFlow();

        builder.beginControlFlow("if (obj == null)")
               .addStatement("return false")
               .endControlFlow();

        var className = className(state);
        builder.beginControlFlow("if (!(obj instanceof $T))", className)
               .addStatement("return false")
               .endControlFlow();

        builder.addStatement("$1T other = ($1T) obj", className);
        builder.addCode("return ");
        var optionalityIndex = NotNullOptionalityKnowledgeIndex.of(state.model());
        var isFirst = true;
        for (var member : state.shape().members()) {
            if (member.hasTrait(ConstTrait.class)) {
                continue;
            }
            var name = symbolProvider.toMemberName(member);
            if (!isFirst) {
                builder.addCode("\n$> && ");
            }
            if (optionalityIndex.isMemberNullable(member)) {
                builder.addCode("$1T.equals(this.$2L, other.$2L)", Objects.class, name);
            } else {
                builder.addCode("this.$1L.equals(other.$1L)", name);
            }
            if (!isFirst) {
                builder.addCode("$<");
            }
            isFirst = false;
        }
        if (isFirst) {
            builder.addCode("true");
        }
        builder.addCode(";\n");
        return builder.build();
    }

    public MethodSpec hashCodeMethod(JavaShapeDirective state) {
        var symbolProvider = state.symbolProvider();
        var builder = MethodSpec.methodBuilder("hashCode")
                                .addAnnotation(Override.class)
                                .addModifiers(Modifier.PUBLIC)
                                .returns(int.class);

        builder.addStatement("int hashCode = 17");
        var optionalityIndex = NotNullOptionalityKnowledgeIndex.of(state.model());
        for (var member : state.shape().members()) {
            var name = symbolProvider.toMemberName(member);
            if (member.hasTrait(ConstTrait.class)) {
                builder.addStatement("hashCode = 31 * hashCode + $T.hashCode(this.$L())", Objects.class, name);
                continue;
            }
            if (optionalityIndex.isMemberNullable(member)) {
                builder.addStatement("hashCode = 31 * hashCode + ($1L != null ? $1L.hashCode() : 0)", name);
            } else {
                builder.addStatement("hashCode = 31 * hashCode + $L.hashCode()", name);
            }
        }
        builder.addStatement("return hashCode");
        return builder.build();
    }

    public MethodSpec toStringMethod(JavaShapeDirective state) {
        var symbolProvider = state.symbolProvider();
        var builder = MethodSpec.methodBuilder("toString")
                                .addAnnotation(Override.class)
                                .addModifiers(Modifier.PUBLIC)
                                .returns(String.class);
        var isFirst = true;
        var sensitiveIndex = SensitiveKnowledgeIndex.of(state.model());
        builder.addCode("return $S", state.shape().getId().getName() + "{");
        for (var member : state.shape().members()) {
            var literalName = member.getMemberName() + ": ";
            var name = symbolProvider.toMemberName(member);
            builder.addCode("\n$> + ");
            if (!isFirst) {
                literalName = ", " + literalName;
            }
            if (sensitiveIndex.isSensitive(member)) {
                literalName += "<*** REDACTED ***>";
                builder.addCode("$S", literalName);
            } else {
                if (member.hasTrait(ConstTrait.class)) {
                    builder.addCode("$S + $N()", literalName, name);
                } else {
                    builder.addCode("$S + $N", literalName, name);
                }
                builder.addCode("$<");
                isFirst = false;
            }
        }
        builder.addCode(" + $S;\n", "}");
        return builder.build();
    }

    public MethodSpec builderMethod(JavaShapeDirective state) {
        var dataType = builderClassName();
        return MethodSpec.methodBuilder("builder")
                         .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                         .returns(dataType)
                         .addStatement("return new $T()", dataType)
                         .build();
    }

    @Override
    public List<DirectedStructure> innerTypes(JavaShapeDirective state) {
        return List.of(new BuilderGenerator());
    }

    class BuilderGenerator implements DirectedStructure {
        @Override
        public ClassName className(JavaShapeDirective state) {
            return builderClassName();
        }

        @Override
        public TypeSpec.Builder typeSpec(JavaShapeDirective state) {
            return TypeSpec.classBuilder(builderClassName().simpleName())
                           .addModifiers(Modifier.STATIC, Modifier.PUBLIC, Modifier.FINAL);
        }

        @Override
        public List<FieldSpec> fieldsFor(JavaShapeDirective state, MemberShape member) {
            if (member.hasTrait(ConstTrait.class)) {
                return Collections.emptyList();
            }
            return List.of(fieldFor(state, member));
        }

        private FieldSpec fieldFor(JavaShapeDirective state, MemberShape member) {
            var symbolProvider = state.symbolProvider();
            var name = symbolProvider.toMemberName(member);
            var innerType = symbolProvider.toTypeName(member);
            var symbol = symbolProvider.toSymbol(member);
            var aggregateType = SymbolConstants.aggregateType(symbol);
            var finalType = switch (aggregateType) {
                case LIST, SET, MAP -> finalTypeForAggregate(state, innerType, member);
                default -> innerType;
            };
            var filed = FieldSpec.builder(finalType, name)
                                 .addModifiers(Modifier.PRIVATE);
            return filed.build();
        }

        private TypeName finalTypeForAggregate(JavaShapeDirective state, TypeName innerType, MemberShape member) {
            switch (BaseStructureData.this.builderType) {
                case USE_REFERENCE -> {
                    return ParameterizedTypeName.get(ClassName.get(CollectionBuilderReference.class), innerType);
                }
                default -> {
                    return innerType;
                }
            }
        }

        @Override
        public List<FieldSpec> extraFields(JavaShapeDirective state) {
            return List.of(FieldSpec.builder(TypeName.BOOLEAN, "_built")
                                    .addModifiers(Modifier.PRIVATE)
                                    .build());
        }

        @Override
        public List<MethodSpec> constructors(JavaShapeDirective state) {
            return List.of(constructor(state), constructorFromData(state));
        }

        public MethodSpec constructor(JavaShapeDirective state) {
            var symbolProvider = state.symbolProvider();
            var builder = MethodSpec.constructorBuilder();
            for (var member : state.shape().members()) {
                var symbol = symbolProvider.toSymbol(member);
                var aggregateType = SymbolConstants.aggregateType(symbol);
                switch (aggregateType) {
                    case LIST, MAP, SET -> setEmptyValue(state, member, builder);
                }
            }
            return builder.build();
        }

        private void setEmptyValue(JavaShapeDirective state, MemberShape member, MethodSpec.Builder builder) {
            var symbolProvider = state.symbolProvider();
            var symbol = symbolProvider.toSymbol(member);
            var name = symbolProvider.toMemberName(member);
            switch (BaseStructureData.this.builderType) {
                case USE_REFERENCE -> {
                    var aggregateType = SymbolConstants.aggregateType(symbol);
                    var emptyReferenceBuilder = symbolProvider.emptyReferenceBuilder(aggregateType);
                    builder.addStatement("this.$L = $T.$L()", name, CollectionBuilderReference.class, emptyReferenceBuilder);
                    ;
                }
                default -> {
                    var concreteType = symbolProvider.concreteClassFor2(symbol);
                    builder.addStatement("this.$L = new $T<>()", name, concreteType);
                }
            }
        }

        public MethodSpec constructorFromData(JavaShapeDirective state) {
            var symbolProvider = state.symbolProvider();
            var builder = MethodSpec.constructorBuilder()
                                    .addParameter(ParameterSpec.builder(BaseStructureData.this.className(state), "data").build());
            for (var member : state.shape().members()) {
                if (member.hasTrait(ConstTrait.class)) {
                    continue;
                }
                var name = symbolProvider.toMemberName(member);
                var symbol = symbolProvider.toSymbol(member);
                var aggregateType = SymbolConstants.aggregateType(symbol);
                switch (aggregateType) {
                    case LIST, MAP, SET -> setValueFromPersistent(state, member, builder);
                    default -> builder.addStatement("this.$1L = data.$1L", name);
                }
            }
            return builder.build();
        }

        private void setValueFromPersistent(JavaShapeDirective state, MemberShape member, MethodSpec.Builder builder) {
            var symbolProvider = state.symbolProvider();
            var symbol = symbolProvider.toSymbol(member);
            var name = symbolProvider.toMemberName(member);
            switch (BaseStructureData.this.builderType) {
                case USE_REFERENCE -> {
                    var aggregateType = SymbolConstants.aggregateType(symbol);
                    var init = symbolProvider.initReferenceBuilder(aggregateType);
                    builder.addStatement("this.$1L = $2T.$3L(data.$1L)", name, CollectionBuilderReference.class,
                                         init);
                }
                default -> builder.addStatement("this.$1L.addAll(data.$1L)", name);
            }
        }

        @Override
        public List<MethodSpec> methodsFor(JavaShapeDirective state, MemberShape member) {
            if (member.hasTrait(ConstTrait.class)) {
                return Collections.emptyList();
            }
            var symbol = state.symbolProvider().toSymbol(member);
            var aggregateType = SymbolConstants.aggregateType(symbol);
            if (aggregateType == SymbolConstants.AggregateType.NONE || aggregateType == SymbolConstants.AggregateType.MAP) {
                return List.of(setter(state, member));
            }
            return List.of(setter(state, member), adder(state, member));
        }

        private MethodSpec adder(JavaShapeDirective state, MemberShape member) {
            var symbolProvider = state.symbolProvider();
            var name = symbolProvider.toMemberJavaName(member);
            var builder = MethodSpec.methodBuilder("add" + name.toSingularSpelling().asCamelCase())
                                    .addModifiers(Modifier.PUBLIC)
                                    .returns(className(state));
            var symbol = symbolProvider.toSymbol(member);
            var aggregateType = SymbolConstants.aggregateType(symbol);
            switch (aggregateType) {
                case LIST, SET -> addValue(state, member, builder);
                default -> throw new IllegalArgumentException("cannot create adder for " + member);
            }
            return builder.addStatement("return this")
                          .build();
        }

        private void addValue(JavaShapeDirective state, MemberShape member, MethodSpec.Builder builder) {
            var symbolProvider = state.symbolProvider();
            var name = symbolProvider.toMemberJavaName(member);
            var symbol = symbolProvider.toSymbol(member);
            var paramName = name.toSingularSpelling().toString();
            builder.addParameter(SymbolConstants.typeParam(symbol), paramName);
            switch (BaseStructureData.this.builderType) {
                case USE_REFERENCE -> {
                    builder.addStatement("this.$L.asTransient().add($L)", name.toString(), paramName);
                }
                default -> builder.addStatement("this.$L.add($L)", name.toString(), paramName);
            }
        }

        public MethodSpec setter(JavaShapeDirective state, MemberShape member) {
            var symbolProvider = state.symbolProvider();
            var name = symbolProvider.toMemberName(member);
            var type = symbolProvider.toTypeName(member);
            var builder = MethodSpec.methodBuilder(name)
                                    .addModifiers(Modifier.PUBLIC)
                                    .addParameter(type, name)
                                    .returns(className(state));

            var symbol = symbolProvider.toSymbol(member);
            var aggregateType = SymbolConstants.aggregateType(symbol);
            switch (aggregateType) {
                case LIST, SET, MAP -> {
                    setValue(state, member, builder);
                }
                default -> {
                    builder.addStatement("this.$1L = $1L", name);
                }
            }
            return builder.addStatement("return this")
                          .build();
        }

        private void setValue(JavaShapeDirective state, MemberShape member, MethodSpec.Builder builder) {
            var symbolProvider = state.symbolProvider();
            var name = symbolProvider.toMemberJavaName(member);
            switch (BaseStructureData.this.builderType) {
                case USE_REFERENCE -> {
                    builder.addStatement("this.$L.clear()", name);
                    builder.addStatement("this.$1L.asTransient().addAll($1L)", name);
                }
                default -> {
                    builder.addStatement("this.$L.clear()", name);
                    builder.addStatement("this.$1L.addAll($1L)", name);
                }
            }
        }

        @Override
        public List<MethodSpec> extraMethods(JavaShapeDirective state) {
            return List.of(buildMethod(state));
        }

        public MethodSpec buildMethod(JavaShapeDirective state) {
            var symbolProvider = state.symbolProvider();
            var dataType = BaseStructureData.this.className(state);
            var builder = MethodSpec.methodBuilder("build")
                                    .addModifiers(Modifier.PUBLIC)
                                    .returns(dataType);

            switch (BaseStructureData.this.builderType) {
                case PLAIN_USE_ONCE -> {
                    builder.beginControlFlow("if (_built)")
                           .addStatement("throw new IllegalStateException($S)", "The builder has been already used")
                           .endControlFlow();
                    builder.addStatement("_built = true")
                           .addStatement("$1T result = new $1T(this)", dataType);
                    for (var member : state.shape().members()) {
                        if (member.hasTrait(ConstTrait.class)) {
                            continue;
                        }
                        var name = symbolProvider.toMemberName(member);
                        builder.addStatement("this.$L = null", name);
                    }
                    builder.addStatement("return result");
                }
                default -> {
                    builder.addStatement("return new $1T(this)", dataType);
                }
            }
            return builder.build();
        }
    }
}
