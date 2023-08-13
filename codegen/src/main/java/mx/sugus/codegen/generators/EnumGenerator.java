package mx.sugus.codegen.generators;

import static mx.sugus.codegen.JavaSymbolProvider.mapStringToV;
import static mx.sugus.codegen.SymbolConstants.fromClassName;
import static mx.sugus.codegen.util.PoetUtils.toClassName;

import javax.lang.model.element.Modifier;
import mx.sugus.codegen.JavaSymbolProvider;
import mx.sugus.codegen.util.Naming;
import mx.sugus.codegen.util.PoetUtils;
import mx.sugus.codegen.writer.CodegenWriter;
import mx.sugus.javapoet.AnnotationSpec;
import mx.sugus.javapoet.FieldSpec;
import mx.sugus.javapoet.MethodSpec;
import mx.sugus.javapoet.ParameterSpec;
import mx.sugus.javapoet.TypeName;
import mx.sugus.javapoet.TypeSpec;
import software.amazon.smithy.codegen.core.Symbol;
import software.amazon.smithy.codegen.core.SymbolProvider;
import software.amazon.smithy.codegen.core.WriterDelegator;
import software.amazon.smithy.model.Model;
import software.amazon.smithy.model.shapes.EnumShape;

public record EnumGenerator(
    Model model,
    Symbol symbol,
    EnumShape shape,
    SymbolProvider symbolProvider,
    WriterDelegator<CodegenWriter> delegator
) {

    private static final int USE_MAP_THRESHOLD = 8;
    static Symbol ENUM_UTILS = fromClassName("software.amazon.awssdk.utils.internal.EnumUtils");

    static FieldSpec generateValueField() {
        return FieldSpec.builder(TypeName.BOOLEAN, "value")
                        .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                        .build();
    }

    static MethodSpec generateConstructor() {
        return MethodSpec.constructorBuilder()
                         .addModifiers(Modifier.PRIVATE)
                         .addParameter(toClassName(JavaSymbolProvider.STRING), "value")
                         .addStatement("this.value = value")
                         .build();
    }

    static MethodSpec generateToString() {
        return MethodSpec.methodBuilder("toString")
                         .returns(String.class)
                         .addModifiers(Modifier.PUBLIC)
                         .addAnnotation(Override.class)
                         .addStatement("return String.valueOf(value)")
                         .build();
    }

    public void generate() {
        var b = TypeSpec.enumBuilder(symbol.getName())
                        .addAnnotation(AnnotationSpec
                                           .builder(toClassName(fromClassName("software.amazon.awssdk.annotations.Generated")))
                                           .addMember("value", "mx.sugus.smithy.java:codegen")
                                           .build())
                        .addModifiers(Modifier.PUBLIC);
        shape.getEnumValues().size();
        generateConstants(b);
        generateValueMapField(b);
        b.addField(generateValueField());
        b.addMethod(generateConstructor());
        b.addMethod(generateFromValueUsingMap());
        b.addMethod(generateToString());

        var spec = b.build();
        delegator.useShapeWriter(shape, w -> PoetUtils.emit(w, spec));
    }

    void generateValueMapField(TypeSpec.Builder builder) {
        if (shape.getEnumValues().size() > USE_MAP_THRESHOLD) {
            builder.addField(FieldSpec.builder(toClassName(mapStringToV(symbol)), "VALUE_MAP")
                                      .addModifiers(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
                                      .initializer("$1T.uniqueIndex($2T.class, $2T::toString)", toClassName(ENUM_UTILS),
                                                   toClassName(symbol))
                                      .build());
        }
    }

    void generateConstants(TypeSpec.Builder enumBuilder) {
        shape.getEnumValues().forEach((name, value) -> {
            enumBuilder.addEnumConstant(Naming.screamCase(name));
        });
        enumBuilder.addEnumConstant("UNKNOWN_TO_SDK_VERSION"); //, Emitters.literalInline("(null)"));
    }

    MethodSpec generateFromValueUsingMap() {
        var b = MethodSpec.methodBuilder("fromValue")
                          .addJavadoc("Use this in place of valueOf to convert the raw string returned by the service into the "
                                      + "enum value.")
                          .returns(toClassName(symbol))
                          .addModifiers(Modifier.STATIC, Modifier.PUBLIC)
                          .addParameter(ParameterSpec.builder(String.class, "value")
                                                     .addJavadoc("The string literal to convert to $T", toClassName(symbol))
                                                     .build());

        if (shape.getEnumValues().size() > USE_MAP_THRESHOLD) {
            return b.ifStatement("value == null", ifBody -> ifBody.addStatement("return null"))
                    .addStatement("return VALUE_MAP.getOrDefault(value, UNKNOWN_TO_SDK_VERSION)")
                    .build();
        }
        b.beginControlFlow("switch(value)");
        /*
        shape.getEnumValues().forEach((name, value) -> {
            b.addCodeEmitter(Emitters.direct(w -> {
                w.write("case $S:", value)
                 .indent()
                 .write("return $L;", Naming.screamCase(name))
                 .dedent();
            }));
        });
        b.addCodeEmitter(w -> w.writeWithNoFormatting("default:")
                               .indent()
                               .writeWithNoFormatting("return UNKNOWN_TO_SDK_VERSION;")
                               .dedent());

         */
        b.endControlFlow();
        return b.build();
    }
}
