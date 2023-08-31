package mx.sugus.codegen.generators;

import static mx.sugus.codegen.SymbolConstants.aggregateType;
import static mx.sugus.codegen.SymbolConstants.concreteClassFor;
import static mx.sugus.codegen.SymbolConstants.isAggregate;
import static mx.sugus.codegen.util.PoetUtils.toTypeName;

import java.util.Collections;
import javax.lang.model.element.Modifier;
import mx.sugus.codegen.JavaSymbolProvider;
import mx.sugus.codegen.generators.internal.GenerateVisitor;
import mx.sugus.codegen.util.PoetUtils;
import mx.sugus.codegen.writer.CodegenWriter;
import mx.sugus.javapoet.AnnotationSpec;
import mx.sugus.javapoet.ClassName;
import mx.sugus.javapoet.FieldSpec;
import mx.sugus.javapoet.MethodSpec;
import mx.sugus.javapoet.ParameterizedTypeName;
import mx.sugus.javapoet.TypeSpec;
import mx.sugus.javapoet.TypeVariableName;
import mx.sugus.syntax.java.InterfaceTrait;
import mx.sugus.syntax.java.JavaTrait;
import software.amazon.smithy.codegen.core.CodegenException;
import software.amazon.smithy.codegen.core.Symbol;
import software.amazon.smithy.codegen.core.WriterDelegator;
import software.amazon.smithy.model.Model;
import software.amazon.smithy.model.shapes.ShapeId;
import software.amazon.smithy.model.shapes.StructureShape;

public record StructureGenerator(
    Model model,
    Symbol symbol,
    StructureShape shape,
    JavaSymbolProvider symbolProvider,
    WriterDelegator<CodegenWriter> delegator
) {

    // BaseStructureData
    public void generate() {
        var spec = generateDirectedSpec();
        var syntaxNode = "SyntaxNode";
        var syntaxNodeId = ShapeId.fromParts(shape.getId().toShapeId().getNamespace(), syntaxNode);
        if (spec != null) {
            var shapeIds = GenerateVisitor.shapesImplementing(syntaxNodeId, model);
            if (shapeIds.contains(shape.getId()) && !shape.hasTrait(InterfaceTrait.class)) {
                var visitor = ParameterizedTypeName.get(ClassName.bestGuess(syntaxNode + "Visitor"), TypeVariableName.get("T"));
                spec = spec.toBuilder()
                           .addMethod(
                               MethodSpec.methodBuilder("accept")
                                         .addModifiers(Modifier.PUBLIC)
                                         .returns(TypeVariableName.get("T", Object.class))
                                         .addTypeVariable(TypeVariableName.get("T"))
                                         .addParameter(visitor, "visitor")
                                         .addStatement("return visitor.visit" + shape.getId().getName() + "(this)")
                                         .build())
                           .build();
            }
            if (shape.getId().getName().equals(syntaxNode)) {
                var visitor = ParameterizedTypeName.get(ClassName.bestGuess(syntaxNode + "Visitor"), TypeVariableName.get("T"));
                spec = spec.toBuilder()
                           .addMethod(MethodSpec.methodBuilder("accept")
                                                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                                                .returns(TypeVariableName.get("T", Object.class))
                                                .addTypeVariable(TypeVariableName.get("T"))
                                                .addParameter(visitor, "visitor")
                                                .build())
                    .build();

            }
            var finalSpec = spec;
            delegator.useShapeWriter(shape, w -> PoetUtils.emit(w, finalSpec, symbol.getNamespace()));
        }
    }

     TypeSpec generateDirectedSpec() {
        if (shape().hasTrait(JavaTrait.class)) {
            return null;
        }
        if (shape().hasTrait(InterfaceTrait.class)) {
            return new InterfaceStructureGenerator().build(this);
        }
        return new BaseStructureData(StructureGeneratorConfig.builder().build()).build(this);
    }
}
