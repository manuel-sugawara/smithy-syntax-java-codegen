package mx.sugus.codegen.generators;

import static mx.sugus.codegen.SymbolConstants.aggregateType;
import static mx.sugus.codegen.SymbolConstants.concreteClassFor;
import static mx.sugus.codegen.SymbolConstants.fromClassName;
import static mx.sugus.codegen.SymbolConstants.isAggregate;

import java.util.Collections;
import javax.lang.model.element.Modifier;
import mx.sugus.codegen.spec.AnnotationSpec;
import mx.sugus.codegen.spec.FieldSpec;
import mx.sugus.codegen.spec.MethodSpec;
import mx.sugus.codegen.spec.TypeSpec;
import mx.sugus.codegen.writer.CodegenWriter;
import software.amazon.smithy.codegen.core.CodegenException;
import software.amazon.smithy.codegen.core.Symbol;
import software.amazon.smithy.codegen.core.SymbolProvider;
import software.amazon.smithy.codegen.core.WriterDelegator;
import software.amazon.smithy.model.Model;
import software.amazon.smithy.model.shapes.StructureShape;

public record StructureGenerator(
    Model model,
    Symbol symbol,
    StructureShape shape,
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
        generateFields(classBuilder);
        classBuilder.addMethod(generateConstructor());
        addAccessors(classBuilder);
        classBuilder.addMethod(Common.generateBuilderMethod());
        classBuilder.addMethod(Common.generateToBuilderMethod());
        classBuilder.addMethod(Common.generateToStringMethod(model, shape, symbolProvider));
        classBuilder.addInnerType(Common.generateBuilderInterface(shape, symbolProvider));
        classBuilder.addInnerType(Common.generateBuilderClass(shape, symbolProvider));
        return classBuilder.build();
    }

    MethodSpec generateConstructor() {
        MethodSpec.Builder builder = MethodSpec.constructorBuilder()
                                               .addParameter("Builder", "builder")
                                               .addModifiers(Modifier.PRIVATE);
        for (var member : shape.members()) {
            var name = symbolProvider.toMemberName(member);
            var type = symbolProvider.toSymbol(member);
            if (isAggregate(type)) {
                var toUnmodifiable = toUnmodifiableCollection(type);
                builder.addStatement("this.$1L = $2T.$3L(new $4T<>(builder.$1L))",
                                     name, Collections.class, toUnmodifiable, concreteClassFor(type));
            } else {
                builder.addStatement("this.$1L = builder.$1L", name);
            }
        }
        return builder.build();
    }

    void generateFields(TypeSpec.Builder<TypeSpec.ClassBuilder> builder) {
        for (var member : shape.members()) {
            var name = symbolProvider.toMemberName(member);
            var type = symbolProvider.toSymbol(member);
            builder.addField(FieldSpec.builder(type, name)
                                      .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                                      .build());
        }
    }

    void addAccessors(TypeSpec.Builder<TypeSpec.ClassBuilder> builder) {
        for (var member : shape.members()) {
            var name = symbolProvider.toMemberName(member);
            builder.addMethod(Common.generateStubForClassAccessor(member, symbolProvider)
                                    .addStatement("return $L", name)
                                    .build());
        }
    }

    String toUnmodifiableCollection(Symbol type) {
        return
            switch (aggregateType(type)) {
                case LIST, SET -> "unmodifiableList";
                case MAP -> "unmodifiableMap";
                default -> throw new CodegenException("unknownCollection: " + aggregateType(symbol));
            };
    }

}
