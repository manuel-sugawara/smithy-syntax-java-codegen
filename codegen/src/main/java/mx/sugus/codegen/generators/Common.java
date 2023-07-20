package mx.sugus.codegen.generators;

import static mx.sugus.codegen.SymbolConstants.aggregateType;
import static mx.sugus.codegen.SymbolConstants.concreteClassFor;
import static mx.sugus.codegen.SymbolConstants.fromClassName;
import static mx.sugus.codegen.SymbolConstants.isAggregate;

import javax.lang.model.element.Modifier;
import mx.sugus.codegen.SensitiveKnowledgeIndex;
import mx.sugus.codegen.spec.FieldSpec;
import mx.sugus.codegen.spec.MethodSpec;
import mx.sugus.codegen.spec.TypeSpec;
import mx.sugus.codegen.spec.emitters.DirectEmitter;
import software.amazon.smithy.codegen.core.Symbol;
import software.amazon.smithy.codegen.core.SymbolProvider;
import software.amazon.smithy.model.Model;
import software.amazon.smithy.model.shapes.MemberShape;
import software.amazon.smithy.model.shapes.Shape;
import software.amazon.smithy.model.traits.DocumentationTrait;
import software.amazon.smithy.utils.StringUtils;

public final class Common {

    static boolean isMemberSensitive(Model model, MemberShape member) {
        return SensitiveKnowledgeIndex.of(model).isSensitive(member);
    }

    public static MethodSpec.Builder generateStubForClassAccessor(MemberShape member, SymbolProvider symbolProvider) {
        var name = symbolProvider.toMemberName(member);
        var type = symbolProvider.toSymbol(member);
        return MethodSpec.methodBuilder(name)
                         .addModifiers(Modifier.PUBLIC)
                         .returns(type)
                         .addJavadoc(member.getTrait(DocumentationTrait.class)
                                           .map(DocumentationTrait::getValue)
                                           .orElse(null));
    }

    public static MethodSpec generateToStringMethod(Model model, Shape shape, SymbolProvider symbolProvider) {
        MethodSpec.Builder builder = MethodSpec.methodBuilder("toString")
                                               .returns(String.class)
                                               .addModifiers(Modifier.PUBLIC)
                                               .addAnnotation(Override.class);

        builder.addCodeEmitter(DirectEmitter.create(w -> {
            w.write("return $T.builder($S)",
                    fromClassName("software.amazon.awssdk.utils.ToString"),
                    shape.getId().getName());
            for (var member : shape.members()) {
                var name = symbolProvider.toMemberName(member);
                if (isMemberSensitive(model, member)) {
                    w.indent()
                     .write(".add($S, $L() == null ? null : $S)",
                            member.getMemberName(), name, "*** Sensitive Data Redacted ***")
                     .dedent();
                } else {
                    w.indent()
                     .write(".add($S, $L())", member.getMemberName(), name)
                     .dedent();
                }
            }
            w.indent()
             .write(".build();")
             .dedent();
        }));

        return builder.build();
    }

    public static TypeSpec generateBuilderInterface(Shape shape, SymbolProvider symbolProvider) {
        var symbol = symbolProvider.toSymbol(shape);
        var interfaceBuilder = TypeSpec.interfaceBuilder("Builder")
                                       .addModifiers(Modifier.PUBLIC);
        addBuilderInterfaceFluentSetters(interfaceBuilder, shape, symbolProvider);
        interfaceBuilder.addMethod(generateBuilderInterfaceBuildMethod(symbol));
        return interfaceBuilder.build();
    }

    static void addBuilderInterfaceFluentSetters(TypeSpec.InterfaceBuilder interfaceBuilder, Shape shape,
                                                 SymbolProvider symbolProvider) {
        for (var member : shape.members()) {
            var type = symbolProvider.toSymbol(member);
            var name = symbolProvider.toMemberName(member);
            interfaceBuilder.addMethod(stubForBuilderFluentMemberSetter(name, type)
                                           .build());
        }
    }

    static MethodSpec.Builder stubForBuilderFluentMemberSetter(String name, Symbol type) {
        return baseStubForBuilderFluentMemberSetter(name)
            .addParameter(type, name)
            .addJavadoc("Sets the value for the member $L", name);
    }

    static MethodSpec.Builder baseStubForBuilderFluentMemberSetter(String name) {
        return MethodSpec.methodBuilder(name)
                         .addModifiers(Modifier.PUBLIC)
                         .returns("Builder");
    }

    static MethodSpec generateBuilderInterfaceBuildMethod(Symbol symbol) {
        return MethodSpec.methodBuilder("build")
                         .addModifiers(Modifier.ABSTRACT)
                         .returns(symbol)
                         .addJavadoc("Creates a new instance of {@code $T}", symbol)
                         .build();
    }

    // -- Builders
    public static MethodSpec generateBuildMethod(Symbol buildable) {
        return MethodSpec.methodBuilder("build")
                         .addModifiers(Modifier.PUBLIC)
                         .returns(buildable)
                         .addStatement("return new $T(this)", buildable)
                         .build();
    }

    public static MethodSpec generateBuilderMethod() {
        return generateBuilderMethod(Symbol.builder()
                                           .name("Builder")
                                           .build(),
                                     Symbol.builder()
                                           .name("BuilderImpl")
                                           .build());
    }

    public static MethodSpec generateToBuilderMethod() {
        return generateToBuilderMethod(Symbol.builder()
                                             .name("Builder")
                                             .build(),
                                       Symbol.builder()
                                             .name("BuilderImpl")
                                             .build());

    }

    public static MethodSpec generateToBuilderMethod(Symbol builder, Symbol builderImpl) {
        return MethodSpec.methodBuilder("toBuilder")
                         .addModifiers(Modifier.PUBLIC)
                         .returns(builder)
                         .addStatement("return new $T(this)", builderImpl)
                         .build();
    }

    public static MethodSpec generateBuilderMethod(Symbol builder, Symbol builderImpl) {
        return MethodSpec.methodBuilder("builder")
                         .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                         .returns(builder)
                         .addStatement("return new $T()", builderImpl)
                         .build();
    }

    public static TypeSpec generateBuilderClass(Shape shape, SymbolProvider symbolProvider) {
        var builderClass = TypeSpec.classBuilder("BuilderImpl")
                                   .addSuperinterface("Builder")
                                   .addModifiers(Modifier.PUBLIC, Modifier.FINAL);
        var symbol = symbolProvider.toSymbol(shape);
        addBuilderFields(builderClass, shape, symbolProvider);
        builderClass.addMethod(generateBuilderConstructor(shape, symbolProvider));
        builderClass.addMethod(generateBuilderCopyConstructor(shape, symbol, symbolProvider));
        for (var member : shape.members()) {
            addBuilderSettersForMember(builderClass, member, symbolProvider);
            addBuilderPojoMethodsForMember(builderClass, member, symbolProvider);
        }
        builderClass.addMethod(generateBuildMethod(symbol));
        return builderClass.build();
    }

    static void addBuilderFields(TypeSpec.ClassBuilder builderClass, Shape shape, SymbolProvider symbolProvider) {
        for (var member : shape.members()) {
            var type = symbolProvider.toSymbol(member);
            var name = symbolProvider.toMemberName(member);
            builderClass.addField(FieldSpec.builder(type, name)
                                           .addModifiers(Modifier.PRIVATE)
                                           .build());
        }
    }

    static MethodSpec generateBuilderConstructor(Shape shape, SymbolProvider symbolProvider) {
        MethodSpec.Builder builder = MethodSpec.constructorBuilder()
                                               .addModifiers(Modifier.PRIVATE);

        for (var member : shape.members()) {
            var symbol = symbolProvider.toSymbol(member);
            if (isAggregate(symbol)) {
                var name = symbolProvider.toMemberName(member);
                builder.addStatement("this.$L = new $T<>()", name, concreteClassFor(symbol));
            }
        }
        return builder.build();
    }

    static MethodSpec generateBuilderCopyConstructor(Shape shape, Symbol symbol, SymbolProvider symbolProvider) {
        var builder = MethodSpec.constructorBuilder()
                                .addModifiers(Modifier.PRIVATE)
                                .addParameter(symbol, "that");
        for (var member : shape.members()) {
            var name = symbolProvider.toMemberName(member);
            var type = symbolProvider.toSymbol(member);
            if (isAggregate(type)) {
                builder.addStatement("this.$1L = new $2T<>(that.$1L)",
                                     name, concreteClassFor(type));
            } else {
                builder.addStatement("this.$1L = that.$1L", name);
            }
        }
        return builder.build();
    }

    static void addBuilderSettersForMember(
        TypeSpec.ClassBuilder builderClass,
        MemberShape member,
        SymbolProvider symbolProvider
    ) {
        var type = symbolProvider.toSymbol(member);
        var name = symbolProvider.toMemberName(member);
        switch (aggregateType(type)) {
            case MAP -> {
                builderClass.addMethod(stubForBuilderFluentMemberSetter(name, type)
                                           .addAnnotation(Override.class)
                                           .addStatement("this.$L.clear()", name)
                                           .addStatement("this.$1L.putAll($1L)", name)
                                           .build());
            }
            case LIST, SET -> {
                builderClass.addMethod(stubForBuilderFluentMemberSetter(name, type)
                                           .addAnnotation(Override.class)
                                           .addStatement("this.$L.clear()", name)
                                           .addStatement("this.$1L.addAll($1L)", name)
                                           .build());

            }
            default -> builderClass.addMethod(stubForBuilderFluentMemberSetter(name, type)
                                                  .addAnnotation(Override.class)
                                                  .addStatement("this.$1L = $1L", name)
                                                  .build());

        }
    }

    static void addBuilderPojoMethodsForMember(
        TypeSpec.ClassBuilder builder,
        MemberShape member,
        SymbolProvider symbolProvider
    ) {
        var type = symbolProvider.toSymbol(member);
        var name = symbolProvider.toMemberName(member);
        var cname = StringUtils.capitalize(name);

        builder.addMethod(MethodSpec.methodBuilder("get" + cname)
                                    .addModifiers(Modifier.PUBLIC)
                                    .returns(type)
                                    .addStatement("return $L", name)
                                    .build());

        builder.addMethod(MethodSpec.methodBuilder("set" + cname)
                                    .addModifiers(Modifier.PUBLIC)
                                    .addParameter(type, name)
                                    .returns(void.class)
                                    .addStatement("this.$1L = $1L", name)
                                    .build());

    }
}
