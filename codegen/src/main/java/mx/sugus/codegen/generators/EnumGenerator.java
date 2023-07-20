package mx.sugus.codegen.generators;

import static mx.sugus.codegen.JavaSymbolProvider.mapStringToV;
import static mx.sugus.codegen.SymbolConstants.fromClassName;

import javax.lang.model.element.Modifier;
import mx.sugus.codegen.JavaSymbolProvider;
import mx.sugus.codegen.spec.AnnotationSpec;
import mx.sugus.codegen.spec.FieldSpec;
import mx.sugus.codegen.spec.MethodSpec;
import mx.sugus.codegen.spec.ParameterSpec;
import mx.sugus.codegen.spec.TypeSpec;
import mx.sugus.codegen.spec.emitters.Emitters;
import mx.sugus.codegen.util.Naming;
import mx.sugus.codegen.writer.CodegenWriter;
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
        return FieldSpec.builder(JavaSymbolProvider.STRING, "value")
                        .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                        .build();
    }

    static MethodSpec generateConstructor() {
        return MethodSpec.constructorBuilder()
                         .addModifiers(Modifier.PRIVATE)
                         .addParameter(JavaSymbolProvider.STRING, "value")
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
                                           .builder(fromClassName("software.amazon.awssdk.annotations.Generated"))
                                           .addValue("mx.sugus.smithy.java:codegen")
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
        delegator.useShapeWriter(shape, spec::emit);
    }

    void generateValueMapField(TypeSpec.EnumBuilder builder) {
        if (shape.getEnumValues().size() > USE_MAP_THRESHOLD) {
            builder.addField(FieldSpec.builder(mapStringToV(symbol), "VALUE_MAP")
                                      .addModifiers(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
                                      .initializer("$1T.uniqueIndex($2T.class, $2T::toString)", ENUM_UTILS, symbol)
                                      .build());
        }
    }

    void generateConstants(TypeSpec.EnumBuilder enumBuilder) {
        shape.getEnumValues().forEach((name, value) -> {
            enumBuilder.addEnumConstant(Naming.screamCase(name), value);
        });
        enumBuilder.addEnumConstant("UNKNOWN_TO_SDK_VERSION", Emitters.literalInline("(null)"));
    }

    MethodSpec generateFromValueUsingMap() {
        var b = MethodSpec.methodBuilder("fromValue")
                          .addJavadoc("Use this in place of valueOf to convert the raw string returned by the service into the "
                                      + "enum value.")
                          .returns(symbol)
                          .addModifiers(Modifier.STATIC, Modifier.PUBLIC)
                          .addParameter(ParameterSpec.builder(String.class, "value")
                                                     .addJavadoc("The string literal to convert to $T", symbol)
                                                     .build());

        if (shape.getEnumValues().size() > USE_MAP_THRESHOLD) {
            return b.ifStatement("value == null", ifBody -> ifBody.addStatement("return null"))
                    .addStatement("return VALUE_MAP.getOrDefault(value, UNKNOWN_TO_SDK_VERSION)")
                    .build();
        }
        b.startControlFlow("switch(value)");
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
        b.endControlFlow();
        return b.build();
    }
}
