package mx.sugus.codegen.generators;

import static mx.sugus.codegen.SymbolConstants.fromClassName;

import javax.lang.model.element.Modifier;
import mx.sugus.codegen.JavaSymbolProvider;
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
        delegator.useShapeWriter(shape, spec::emit);
    }

    TypeSpec generateType() {
        var b = TypeSpec.classBuilder(symbol.getName())
                        .addAnnotation(AnnotationSpec
                                           .builder(fromClassName("software.amazon.awssdk.annotations.Generated"))
                                           .addValue("mx.sugus.smithy.java:codegen")
                                           .build())
                        .addModifiers(Modifier.PUBLIC, Modifier.FINAL);
        generateConstants(b);
        b.addMethod(generateConstructor());
        return b.build();
    }

    FieldSpec generateValueField() {
        return FieldSpec.builder(JavaSymbolProvider.STRING, "value")
                        .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                        .build();
    }

    void generateConstants(TypeSpec.ClassBuilder b) {
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
