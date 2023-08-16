package mx.sugus.codegen.generators;

import static mx.sugus.codegen.util.PoetUtils.toTypeName;

import javax.lang.model.element.Modifier;
import mx.sugus.codegen.JavaSymbolProvider;
import mx.sugus.codegen.util.Naming;
import mx.sugus.codegen.util.PoetUtils;
import mx.sugus.codegen.writer.CodegenWriter;
import mx.sugus.javapoet.AnnotationSpec;
import mx.sugus.javapoet.ClassName;
import mx.sugus.javapoet.FieldSpec;
import mx.sugus.javapoet.MethodSpec;
import mx.sugus.javapoet.TypeSpec;
import software.amazon.smithy.codegen.core.Symbol;
import software.amazon.smithy.codegen.core.SymbolProvider;
import software.amazon.smithy.codegen.core.WriterDelegator;
import software.amazon.smithy.model.Model;
import software.amazon.smithy.model.shapes.IntEnumShape;

public record IntEnumGenerator(
    Model model,
    Symbol symbol,
    IntEnumShape shape,
    SymbolProvider symbolProvider,
    WriterDelegator<CodegenWriter> delegator
) {

    public void generate() {
        var spec = generateType();
        delegator.useShapeWriter(shape, w -> PoetUtils.emit(w, spec, symbol.getNamespace()));
    }

    TypeSpec generateType() {
        var b = TypeSpec.classBuilder(symbol.getName())
                        .addAnnotation(AnnotationSpec
                                           .builder(ClassName.get("software.amazon.awssdk.annotations", "Generated"))
                                           .addMember("value", "mx.sugus.smithy.java:codegen")
                                           .build())
                        .addModifiers(Modifier.PUBLIC, Modifier.FINAL);
        generateConstants(b);
        b.addMethod(generateConstructor());
        return b.build();
    }

    FieldSpec generateValueField() {
        return FieldSpec.builder(toTypeName(JavaSymbolProvider.STRING), "value")
                        .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                        .build();
    }

    void generateConstants(TypeSpec.Builder b) {
        shape.getEnumValues().forEach((name, value) -> {
            b.addField(FieldSpec.builder(int.class, Naming.screamCase(name))
                                .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                                .initializer("$L", value)
                                .build());
        });
    }

    MethodSpec generateConstructor() {
        return MethodSpec.constructorBuilder()
                         .addModifiers(Modifier.PRIVATE)
                         .build();
    }
}
