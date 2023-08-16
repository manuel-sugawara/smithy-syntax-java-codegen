package mx.sugus.codegen.generators;

import static mx.sugus.codegen.SymbolConstants.aggregateType;
import static mx.sugus.codegen.SymbolConstants.concreteClassFor;
import static mx.sugus.codegen.SymbolConstants.isAggregate;
import static mx.sugus.codegen.util.PoetUtils.toTypeName;

import java.util.NoSuchElementException;
import javax.lang.model.element.Modifier;
import mx.sugus.codegen.util.Naming;
import mx.sugus.codegen.util.PoetUtils;
import mx.sugus.codegen.writer.CodegenWriter;
import mx.sugus.javapoet.AnnotationSpec;
import mx.sugus.javapoet.ClassName;
import mx.sugus.javapoet.FieldSpec;
import mx.sugus.javapoet.MethodSpec;
import mx.sugus.javapoet.TypeName;
import mx.sugus.javapoet.TypeSpec;
import software.amazon.smithy.codegen.core.Symbol;
import software.amazon.smithy.codegen.core.SymbolProvider;
import software.amazon.smithy.codegen.core.WriterDelegator;
import software.amazon.smithy.model.Model;
import software.amazon.smithy.model.shapes.MemberShape;
import software.amazon.smithy.model.shapes.UnionShape;

public record UnionGenerator(
    Model model,
    Symbol symbol,
    UnionShape shape,
    SymbolProvider symbolProvider,
    WriterDelegator<CodegenWriter> delegator
) {

    public void generate() {
        var spec = generateSpec();
        delegator.useShapeWriter(shape, w -> PoetUtils.emit(w, spec, symbol.getNamespace()));
    }

    public TypeSpec generateSpec() {
        var classBuilder = TypeSpec.classBuilder(symbol.getName())
                                   .addAnnotation(AnnotationSpec
                                                      .builder(ClassName.get("software.amazon.awssdk.annotations", "Generated"))
                                                      .addMember("value", "mx.sugus.smithy.java:codegen")
                                                      .build())
                                   .addModifiers(Modifier.PUBLIC, Modifier.FINAL);
        addFields(classBuilder);
        addConstructor(classBuilder);
        addAccessors(classBuilder);
        addTagAccessor(classBuilder);
        classBuilder.addType(generateMembersEnum());
        classBuilder.addType(Common.generateBuilderInterface(shape, symbolProvider));
        classBuilder.addType(generateBuilderClass());
        return classBuilder.build();
    }

    private void addFields(TypeSpec.Builder builder) {
        builder.addField(FieldSpec.builder(ClassName.get("", "Tag"), "memberTag")
                                  .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                                  .build())
               .addField(FieldSpec.builder(TypeName.OBJECT, "value")
                                  .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                                  .build());
    }

    private void addConstructor(TypeSpec.Builder builder) {
        builder.addMethod(MethodSpec.constructorBuilder()
                                    .addModifiers(Modifier.PRIVATE)
                                    .addParameter(ClassName.get("", "Builder"), "builder")
                                    .addStatement("this.memberTag = builder.memberTag")
                                    .addStatement("this.value = builder.value")
                                    .build());
    }

    private void addAccessors(TypeSpec.Builder builder) {
        for (var member : shape.members()) {
            builder.addMethod(generateAccessorForMember(member));
        }
    }

    MethodSpec generateAccessorForMember(MemberShape member) {
        var type = symbolProvider.toSymbol(member);
        var builder = Common.generateStubForClassAccessor(member, symbolProvider);
        var tagValue = "Tag." + Naming.screamCase(member.getMemberName());
        builder.ifStatement("memberTag == " + tagValue,
                            ifBody -> ifBody.addStatement("return ($T) value", toTypeName(type)));
        builder.addStatement("throw new $T(String.format($S, $L, this.memberTag))",
                             NoSuchElementException.class,
                             "Member of type %s was not set, instead the value is tagged with %s", tagValue);
        return builder.build();
    }

    private void addTagAccessor(TypeSpec.Builder builder) {
        builder.addMethod(MethodSpec.methodBuilder("memberTag")
                                    .addModifiers(Modifier.PUBLIC)
                                    .returns(ClassName.get("", "Tag"))
                                    .addStatement("return memberTag")
                                    .build());
    }

    TypeSpec generateMembersEnum() {
        var b = TypeSpec.enumBuilder("Tag");
        shape.getAllMembers().forEach((name, value) -> {
            //b.addEnumConstant(Naming.screamCase(name), name);
        });
        b.addField(EnumGenerator.generateValueField());
        b.addMethod(EnumGenerator.generateConstructor());
        b.addMethod(EnumGenerator.generateToString());
        return b.build();
    }

    TypeSpec generateBuilderClass() {
        var builderClass = TypeSpec.classBuilder("BuilderImpl")
                                   .addSuperinterface(ClassName.get("", "Builder"))
                                   .addModifiers(Modifier.PUBLIC, Modifier.FINAL);
        generateBuilderFields(builderClass);
        builderClass.addMethod(generateBuilderConstructor());
        builderClass.addMethod(generateBuilderCopyConstructor());
        for (var member : shape.members()) {
            generateBuilderSettersForMember(member, builderClass);
        }
        builderClass.addMethod(generateBuildMethod());
        return builderClass.build();
    }

    void generateBuilderFields(TypeSpec.Builder builderClass) {
        builderClass.addField(FieldSpec.builder(ClassName.get("", "Tag"), "memberTag")
                                       .addModifiers(Modifier.PRIVATE)
                                       .build());
        builderClass.addField(FieldSpec.builder(TypeName.OBJECT, "value")
                                       .addModifiers(Modifier.PRIVATE)
                                       .build());
    }

    MethodSpec generateBuilderConstructor() {
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

    MethodSpec generateBuilderCopyConstructor() {
        var builder = MethodSpec.constructorBuilder()
                                .addModifiers(Modifier.PRIVATE)
                                .addParameter(toTypeName(symbol), "that");
        builder.addStatement("this.memberTag = that.memberTag");
        builder.addStatement("this.value = that.value");
        return builder.build();
    }

    void generateBuilderSettersForMember(MemberShape member, TypeSpec.Builder builderClass) {
        var type = symbolProvider.toSymbol(member);
        var name = symbolProvider.toMemberName(member);
        switch (aggregateType(type)) {
            case MAP -> {
                builderClass.addMethod(stubForMemberSetter(name, type)
                                           .addStatement("this.memberTag = Tag.$L", Naming.screamCase(member.getMemberName()))
                                           .addStatement("this.value = $1L", name)
                                           .addStatement("return this")
                                           .build());
            }
            case LIST, SET -> {
                builderClass.addMethod(stubForMemberSetter(name, type)
                                           .addStatement("this.memberTag = Tag.$L", Naming.screamCase(member.getMemberName()))
                                           .addStatement("this.value = $1L", name)
                                           .build());

            }
            default -> builderClass.addMethod(stubForMemberSetter(name, type)
                                                  .addStatement("this.memberTag = Tag.$L",
                                                                Naming.screamCase(member.getMemberName()))
                                                  .addStatement("this.value = $1L", name)
                                                  .addStatement("return this")
                                                  .build());

        }
    }

    MethodSpec.Builder stubForMemberSetter(String name, Symbol type) {
        return baseStubForMemberSetter(name)
            .addParameter(toTypeName(type), name)
            .addJavadoc("Sets the value for the member $L", name);
    }

    MethodSpec.Builder baseStubForMemberSetter(String name) {
        return MethodSpec.methodBuilder(name)
                         .addModifiers(Modifier.PUBLIC)
                         .returns(ClassName.get("", "Builder"));
    }

    MethodSpec generateBuildMethod() {
        return MethodSpec.methodBuilder("build")
                         .addModifiers(Modifier.PUBLIC)
                         .returns(toTypeName(symbol))
                         .addStatement("return new $T(this)", toTypeName(symbol))
                         .build();
    }

}
