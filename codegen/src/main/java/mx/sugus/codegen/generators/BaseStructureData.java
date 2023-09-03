package mx.sugus.codegen.generators;

import static mx.sugus.codegen.util.PoetUtils.toClassName;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import javax.lang.model.element.Modifier;
import mx.sugus.codegen.SymbolConstants;
import mx.sugus.javapoet.ClassName;
import mx.sugus.javapoet.FieldSpec;
import mx.sugus.javapoet.MethodSpec;
import mx.sugus.javapoet.ParameterSpec;
import mx.sugus.javapoet.TypeName;
import mx.sugus.javapoet.TypeSpec;
import mx.sugus.syntax.java.ConstTrait;
import mx.sugus.syntax.java.IsaTrait;
import software.amazon.smithy.model.shapes.EnumShape;
import software.amazon.smithy.model.shapes.MemberShape;
import software.amazon.smithy.model.shapes.ShapeId;
import software.amazon.smithy.model.traits.StringTrait;

public class BaseStructureData implements DirectedStructure {
    private final StructureGeneratorConfig config;

    public BaseStructureData(StructureGeneratorConfig config) {
        this.config = config;
    }

    @Override
    public ClassName className(StructureGenerator state) {
        return toClassName(state.symbol());
    }

    @Override
    public TypeSpec.Builder typeSpec(StructureGenerator state) {
        var result = TypeSpec.classBuilder(state.symbol().getName())
                             .addModifiers(Modifier.PUBLIC, Modifier.FINAL);
        var shape = state.shape();
        if (shape.hasTrait(IsaTrait.class)) {
            var parent = shape.getTrait(IsaTrait.class).map(StringTrait::getValue).orElse("");
            result.addSuperinterface(ClassName.bestGuess(parent));
        }
        return result;
    }

    @Override
    public List<FieldSpec> fieldsFor(StructureGenerator state, MemberShape member) {
        if (member.hasTrait(ConstTrait.class)) {
            return Collections.emptyList();
        }
        return List.of(fieldFor(state, member));
    }

    public FieldSpec fieldFor(StructureGenerator state, MemberShape member) {
        var symbolProvider = state.symbolProvider();
        var name = symbolProvider.toMemberName(member);
        var type = symbolProvider.toTypeName(member);
        return FieldSpec.builder(type, name)
                        .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                        .build();
    }

    @Override
    public List<MethodSpec> constructors(StructureGenerator state) {
        return List.of(constructorFromBuilder(state));
    }

    public MethodSpec constructorFromBuilder(StructureGenerator state) {
        var symbolProvider = state.symbolProvider();
        var builder = MethodSpec.constructorBuilder()
                                .addModifiers(Modifier.PRIVATE)
                                .addParameter(ParameterSpec.builder(builderClassName(), "builder").build());
        for (var member : state.shape().members()) {
            if (member.hasTrait(ConstTrait.class)) {
                continue;
            }
            var name = symbolProvider.toMemberName(member);
            var symbol = symbolProvider.toSymbol(member);
            var aggregateType = SymbolConstants.aggregateType(symbol);
            switch (aggregateType) {
                case LIST, SET -> {
                    builder.addStatement("this.$1L = $2T.$3L(builder.$1L)",
                                         name,
                                         Collections.class,
                                         SymbolConstants.toUnmodifiableCollection(symbol));
                }
                default -> {
                    builder.addStatement("this.$1L = builder.$1L", name);
                }
            }
        }
        return builder.build();
    }

    @Override
    public List<MethodSpec> methodsFor(StructureGenerator state, MemberShape member) {
        if (member.hasTrait(ConstTrait.class)) {
            return List.of(constAccessor(state, member));
        }
        return List.of(accessor(state, member));
    }

    public MethodSpec accessor(StructureGenerator state, MemberShape member) {
        var symbolProvider = state.symbolProvider();
        var name = symbolProvider.toMemberName(member);
        var type = symbolProvider.toTypeName(member);
        return MethodSpec.methodBuilder(name)
                         .addModifiers(Modifier.PUBLIC)
                         .returns(type)
                         .addStatement("return this.$L", name)
                         .build();
    }

    public MethodSpec constAccessor(StructureGenerator state, MemberShape member) {
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
    public List<MethodSpec> extraMethods(StructureGenerator state) {
        return List.of(toBuilderMethod(state),
                       equalsMethod(state),
                       hashCodeMethod(state),
                       toStringMethod(state),
                       builderMethod(state));
    }

    public MethodSpec toBuilderMethod(StructureGenerator state) {
        var dataType = builderClassName();
        return MethodSpec.methodBuilder("toBuilder")
                         .addModifiers(Modifier.PUBLIC)
                         .returns(dataType)
                         .addStatement("return new $T(this)", dataType)
                         .build();
    }

    public MethodSpec equalsMethod(StructureGenerator state) {
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
        var isFirst = true;
        for (var member : state.shape().members()) {
            if (member.hasTrait(ConstTrait.class)) {
                continue;
            }
            var name = symbolProvider.toMemberName(member);
            if (!isFirst) {
                builder.addCode("\n$> && ");
            }
            builder.addCode("$1T.equals(this.$2L, other.$2L)", Objects.class, name);
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

    public MethodSpec hashCodeMethod(StructureGenerator state) {
        var symbolProvider = state.symbolProvider();
        var builder = MethodSpec.methodBuilder("hashCode")
                                .addAnnotation(Override.class)
                                .addModifiers(Modifier.PUBLIC)
                                .returns(int.class);

        builder.addStatement("int hashCode = 17");
        for (var member : state.shape().members()) {
            var name = symbolProvider.toMemberName(member);
            if (member.hasTrait(ConstTrait.class)) {
                builder.addStatement("hashCode = 31 * hashCode + $T.hashCode(this.$L())", Objects.class, name);
            } else {
                builder.addStatement("hashCode = 31 * hashCode + $T.hashCode(this.$L)", Objects.class, name);
            }
        }
        builder.addStatement("return hashCode");
        return builder.build();
    }

    public MethodSpec toStringMethod(StructureGenerator state) {
        var symbolProvider = state.symbolProvider();
        var builder = MethodSpec.methodBuilder("toString")
                                .addAnnotation(Override.class)
                                .addModifiers(Modifier.PUBLIC)
                                .returns(String.class);
        var isFirst = true;
        builder.addCode("return $S", state.shape().getId().getName() + "{");
        for (var member : state.shape().members()) {
            var literalName = member.getMemberName() + ": ";
            var name = symbolProvider.toMemberName(member);
            builder.addCode("\n$> + ");
            if (!isFirst) {
                literalName = ", " + literalName;
            }
            builder.addCode("$S + String.valueOf($N())", literalName, name);
            builder.addCode("$<");
            isFirst = false;
        }
        builder.addCode(" + $S;\n", "}");
        return builder.build();
    }

    public MethodSpec builderMethod(StructureGenerator state) {
        var dataType = builderClassName();
        return MethodSpec.methodBuilder("builder")
                         .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                         .returns(dataType)
                         .addStatement("return new $T()", dataType)
                         .build();
    }

    @Override
    public List<DirectedStructure> innerTypes(StructureGenerator state) {
        return List.of(new BuilderGenerator());
    }

    class BuilderGenerator implements DirectedStructure {
        @Override
        public ClassName className(StructureGenerator state) {
            return builderClassName();
        }

        @Override
        public TypeSpec.Builder typeSpec(StructureGenerator state) {
            return TypeSpec.classBuilder(builderClassName().simpleName())
                           .addModifiers(Modifier.STATIC, Modifier.PUBLIC, Modifier.FINAL);
        }

        @Override
        public List<FieldSpec> fieldsFor(StructureGenerator state, MemberShape member) {
            if (member.hasTrait(ConstTrait.class)) {
                return Collections.emptyList();
            }
            return List.of(fieldFor(state, member));
        }

        public FieldSpec fieldFor(StructureGenerator state, MemberShape member) {
            var symbolProvider = state.symbolProvider();
            var name = symbolProvider.toMemberName(member);
            var type = symbolProvider.toTypeName(member);
            var filed = FieldSpec.builder(type, name)
                                 .addModifiers(Modifier.PRIVATE);
            var symbol = symbolProvider.toSymbol(member);
            var aggregateType = SymbolConstants.aggregateType(symbol);
            switch (aggregateType) {
                case LIST, MAP, SET -> {
                    filed.initializer("new $T<>()", symbolProvider.concreteClassFor2(symbol));
                }
            }
            return filed.build();
        }

        @Override
        public List<FieldSpec> extraFields(StructureGenerator state) {
            return List.of(FieldSpec.builder(TypeName.BOOLEAN, "_built")
                                    .addModifiers(Modifier.PRIVATE)
                                    .build());
        }

        @Override
        public List<MethodSpec> constructors(StructureGenerator state) {
            return List.of(constructor(state), constructorFromData(state));
        }

        public MethodSpec constructor(StructureGenerator state) {
            return MethodSpec.constructorBuilder().build();
        }

        public MethodSpec constructorFromData(StructureGenerator state) {
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
                    case LIST, MAP, SET -> {
                        builder.addStatement("this.$1L.addAll(data.$1L)", name);
                    }
                    default -> {
                        builder.addStatement("this.$1L = data.$1L", name);
                    }
                }
            }
            return builder.build();
        }

        @Override
        public List<MethodSpec> methodsFor(StructureGenerator state, MemberShape member) {
            if (member.hasTrait(ConstTrait.class)) {
                return Collections.emptyList();
            }
            var symbol = state.symbolProvider().toSymbol(member);
            var aggregateType = SymbolConstants.aggregateType(symbol);
            if (aggregateType == SymbolConstants.AggregateType.NONE) {
                return List.of(setter(state, member));
            }
            return List.of(setter(state, member), adder(state, member));
        }

        private MethodSpec adder(StructureGenerator state, MemberShape member) {
            var symbolProvider = state.symbolProvider();
            var name = symbolProvider.toMemberJavaName(member);
            var builder = MethodSpec.methodBuilder("add" + name.toSingularSpelling().asCamelCase())
                                    .addModifiers(Modifier.PUBLIC)
                                    .returns(className(state));

            var symbol = symbolProvider.toSymbol(member);
            var aggregateType = SymbolConstants.aggregateType(symbol);
            switch (aggregateType) {
                case LIST, SET -> {
                    var paramName = name.toSingularSpelling().toString();
                    builder.addParameter(SymbolConstants.typeParam(symbol), paramName);
                    builder.addStatement("this.$L.add($L)", name.toString(), paramName);
                }
                default -> {
                    builder.addStatement("this.$1L = $1L", name);
                }
            }
            return builder.addStatement("return this")
                          .build();
        }

        public MethodSpec setter(StructureGenerator state, MemberShape member) {
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
                case LIST, SET -> {
                    builder.addStatement("this.$L.clear()", name);
                    builder.addStatement("this.$1L.addAll($1L)", name);
                }
                default -> {
                    builder.addStatement("this.$1L = $1L", name);
                }
            }
            return builder.addStatement("return this")
                          .build();
        }

        @Override
        public List<MethodSpec> extraMethods(StructureGenerator state) {
            return List.of(buildMethod(state));
        }

        public MethodSpec buildMethod(StructureGenerator state) {
            var symbolProvider = state.symbolProvider();
            var dataType = BaseStructureData.this.className(state);
            var builder = MethodSpec.methodBuilder("build")
                                    .addModifiers(Modifier.PUBLIC)
                                    .returns(dataType);
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
            return builder.addStatement("return result").build();
        }
    }
}
