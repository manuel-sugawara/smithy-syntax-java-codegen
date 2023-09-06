package mx.sugus.codegen.generators;

import static mx.sugus.codegen.JavaSymbolProviderImpl.mapStringToV;
import static mx.sugus.codegen.SymbolConstants.fromClassName;
import static mx.sugus.codegen.util.PoetUtils.toTypeName;

import javax.lang.model.element.Modifier;
import mx.sugus.codegen.JavaSymbolProviderImpl;
import mx.sugus.codegen.util.Naming;
import mx.sugus.codegen.util.PoetUtils;
import mx.sugus.codegen.writer.CodegenWriter;
import mx.sugus.javapoet.FieldSpec;
import mx.sugus.javapoet.MethodSpec;
import mx.sugus.javapoet.ParameterSpec;
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
        return FieldSpec.builder(String.class, "value")
                        .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                        .build();
    }

    static MethodSpec generateConstructor() {
        return MethodSpec.constructorBuilder()
                         .addModifiers(Modifier.PRIVATE)
                         .addParameter(toTypeName(JavaSymbolProviderImpl.STRING), "value")
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
                        .addModifiers(Modifier.PUBLIC);
        generateConstants(b);
        generateValueMapField(b);
        b.addField(generateValueField());
        b.addMethod(generateConstructor());
        b.addMethod(generateFromValueUsingMap());
        b.addMethod(generateToString());

        var spec = b.build();
        delegator.useShapeWriter(shape, w -> PoetUtils.emit(w, spec, symbol.getNamespace()));
    }

    void generateValueMapField(TypeSpec.Builder builder) {
        if (shape.getEnumValues().size() > USE_MAP_THRESHOLD) {
            builder.addField(FieldSpec.builder(toTypeName(mapStringToV(symbol)), "VALUE_MAP")
                                      .addModifiers(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
                                      .initializer("$1T.uniqueIndex($2T.class, $2T::toString)", toTypeName(ENUM_UTILS),
                                                   toTypeName(symbol))
                                      .build());
        }
    }

    void generateConstants(TypeSpec.Builder enumBuilder) {
        shape.getEnumValues().forEach((name, value) -> {
            var enumConstant = Naming.screamCase(name);
            enumBuilder.addEnumConstant(enumConstant, TypeSpec.anonymousClassBuilder("$S", name).build());
        });
        enumBuilder.addEnumConstant("UNKNOWN_TO_SDK_VERSION", TypeSpec.anonymousClassBuilder("$L", "null").build()); //, Emitters
    }

    MethodSpec generateFromValueUsingMap() {
        var b = MethodSpec.methodBuilder("fromValue")
                          .addJavadoc("Use this in place of valueOf to convert the raw string returned by the service into the "
                                      + "enum value.")
                          .returns(toTypeName(symbol))
                          .addModifiers(Modifier.STATIC, Modifier.PUBLIC)
                          .addParameter(ParameterSpec.builder(String.class, "value")
                                                     .addJavadoc("The string literal to convert to $T", toTypeName(symbol))
                                                     .build());

        if (shape.getEnumValues().size() > USE_MAP_THRESHOLD) {
            return b.ifStatement("value == null", ifBody -> ifBody.addStatement("return null"))
                    .addStatement("return VALUE_MAP.getOrDefault(value, UNKNOWN_TO_SDK_VERSION)")
                    .build();
        }
        b.beginControlFlow("switch(value)");
        shape.getEnumValues().forEach((name, value) -> {
            var enumConstant = Naming.screamCase(name);
            b.addCode("case $S:\n", value);
            b.addStatement("$>return $N$<", enumConstant);
        });

        b.addCode("default:\n");
        b.addStatement("$>return $N$<", "UNKNOWN_TO_SDK_VERSION");
        b.endControlFlow();
        return b.build();
    }
}
