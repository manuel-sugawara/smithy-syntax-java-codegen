package mx.sugus.codegen.generators;

import static mx.sugus.codegen.SymbolConstants.aggregateType;
import static mx.sugus.codegen.SymbolConstants.concreteClassFor;
import static mx.sugus.codegen.SymbolConstants.fromClassName;
import static mx.sugus.codegen.SymbolConstants.isAggregate;

import java.util.NoSuchElementException;
import javax.lang.model.element.Modifier;
import mx.sugus.codegen.spec.AnnotationSpec;
import mx.sugus.codegen.spec.FieldSpec;
import mx.sugus.codegen.spec.MethodSpec;
import mx.sugus.codegen.spec.TypeSpec;
import mx.sugus.codegen.util.Naming;
import mx.sugus.codegen.writer.CodegenWriter;
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
        delegator.useShapeWriter(shape, spec::emit);
    }

    public TypeSpec generateSpec() {
        var classBuilder = TypeSpec.classBuilder(symbol.getName())
                                   .addAnnotation(AnnotationSpec
                                                      .builder(fromClassName("software.amazon.awssdk.annotations.Generated"))
                                                      .addValue("mx.sugus.smithy.java:codegen")
                                                      .build())
                                   .addModifiers(Modifier.PUBLIC, Modifier.FINAL);
        addFields(classBuilder);
        addConstructor(classBuilder);
        addAccessors(classBuilder);
        addTagAccessor(classBuilder);
        classBuilder.addInnerType(generateMembersEnum());
        classBuilder.addInnerType(Common.generateBuilderInterface(shape, symbolProvider));
        classBuilder.addInnerType(generateBuilderClass());
        return classBuilder.build();
    }

    private void addFields(TypeSpec.ClassBuilder builder) {
        builder.addField(FieldSpec.builder("Tag", "memberTag")
                                  .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                                  .build())
               .addField(FieldSpec.builder("Object", "value")
                                  .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                                  .build());
    }

    private void addConstructor(TypeSpec.ClassBuilder builder) {
        builder.addMethod(MethodSpec.constructorBuilder()
                                    .addModifiers(Modifier.PRIVATE)
                                    .addParameter("Builder", "builder")
                                    .addStatement("this.memberTag = builder.memberTag")
                                    .addStatement("this.value = builder.value")
                                    .build());
    }

    private void addAccessors(TypeSpec.ClassBuilder builder) {
        for (var member : shape.members()) {
            builder.addMethod(generateAccessorForMember(member));
        }
    }

    MethodSpec generateAccessorForMember(MemberShape member) {
        var type = symbolProvider.toSymbol(member);
        var builder = Common.generateStubForClassAccessor(member, symbolProvider);
        var tagValue = "Tag." + Naming.screamCase(member.getMemberName());
        builder.ifStatement("memberTag == " + tagValue,
                            ifBody -> ifBody.addStatement("return ($T) value", type));
        builder.addStatement("throw new $T(String.format($S, $L, this.memberTag))",
                             NoSuchElementException.class,
                             "Member of type %s was not set, instead the value is tagged with %s", tagValue);
        return builder.build();
    }

    private void addTagAccessor(TypeSpec.ClassBuilder builder) {
        builder.addMethod(MethodSpec.methodBuilder("memberTag")
                                    .addModifiers(Modifier.PUBLIC)
                                    .returns("Tag")
                                    .addStatement("return memberTag")
                                    .build());
    }

    TypeSpec generateMembersEnum() {
        var b = TypeSpec.enumBuilder("Tag");
        shape.getAllMembers().forEach((name, value) -> {
            b.addEnumConstant(Naming.screamCase(name), name);
        });
        b.addField(EnumGenerator.generateValueField());
        b.addMethod(EnumGenerator.generateConstructor());
        b.addMethod(EnumGenerator.generateToString());
        return b.build();
    }

    TypeSpec generateBuilderClass() {
        var builderClass = TypeSpec.classBuilder("BuilderImpl")
                                   .addSuperinterface("Builder")
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

    void generateBuilderFields(TypeSpec.ClassBuilder builderClass) {
        builderClass.addField(FieldSpec.builder(fromClassName("Tag"), "memberTag")
                                       .addModifiers(Modifier.PRIVATE)
                                       .build());
        builderClass.addField(FieldSpec.builder(fromClassName("Object"), "value")
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
                                .addParameter(symbol, "that");
        builder.addStatement("this.memberTag = that.memberTag");
        builder.addStatement("this.value = that.value");
        return builder.build();
    }

    void generateBuilderSettersForMember(MemberShape member, TypeSpec.ClassBuilder builderClass) {
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
            .addParameter(type, name)
            .addJavadoc("Sets the value for the member $L", name);
    }

    MethodSpec.Builder baseStubForMemberSetter(String name) {
        return MethodSpec.methodBuilder(name)
                         .addModifiers(Modifier.PUBLIC)
                         .returns("Builder");
    }

    MethodSpec generateBuildMethod() {
        return MethodSpec.methodBuilder("build")
                         .addModifiers(Modifier.PUBLIC)
                         .returns(symbol)
                         .addStatement("return new $T(this)", symbol)
                         .build();
    }

}
