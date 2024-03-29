package mx.sugus.codegen.plugin.nodeserde;

import java.util.LinkedHashSet;
import java.util.Set;
import javax.lang.model.element.Modifier;
import mx.sugus.codegen.plugin.AbstractShapeTask;
import mx.sugus.codegen.plugin.JavaShapeDirective;
import mx.sugus.codegen.plugin.TypeSpecResult;
import mx.sugus.javapoet.ClassName;
import mx.sugus.javapoet.MethodSpec;
import mx.sugus.javapoet.ParameterizedTypeName;
import mx.sugus.javapoet.TypeSpec;
import mx.sugus.javapoet.TypeVariableName;
import mx.sugus.syntax.java.InterfaceTrait;
import mx.sugus.syntax.java.IsaTrait;
import software.amazon.smithy.model.Model;
import software.amazon.smithy.model.knowledge.TopDownIndex;
import software.amazon.smithy.model.shapes.ShapeId;
import software.amazon.smithy.model.shapes.ShapeType;
import software.amazon.smithy.model.shapes.StructureShape;

public class GenerateVisitor extends AbstractShapeTask<TypeSpecResult> {
    private final String syntaxNode;

    public GenerateVisitor(String syntaxNode) {
        super(TypeSpecResult.class, ShapeType.SERVICE);
        this.syntaxNode = syntaxNode;
    }

    @Override
    public TypeSpecResult produce(JavaShapeDirective directive) {
        return  TypeSpecResult.builder().spec(generate(directive)).build();
    }

    static boolean filterSyntaxNodes(String syntaxNode, Model model, StructureShape shape) {
        while (true) {
            if (shape.hasTrait(IsaTrait.class)) {
                var isaValue = shape.getTrait(IsaTrait.class).map(IsaTrait::getValue).orElse("");
                if (isaValue.equals(syntaxNode)) {
                    return true;
                }
                if (!isaValue.contains("#")) {
                    isaValue = shape.getId().getNamespace() + "#" + isaValue;
                }
                var parent = model.expectShape(ShapeId.from(isaValue));
                shape = parent.asStructureShape().orElseThrow();
                continue;
            }
            return false;
        }
    }

    static boolean filterIsaOfSyntaxNode(Set<ShapeId> roots, StructureShape shape) {
        if (shape.hasTrait(IsaTrait.class)) {
            var isaValue = shape.getTrait(IsaTrait.class).map(IsaTrait::getValue).orElse("");
            if (!isaValue.contains("#")) {
                isaValue = shape.getId().getNamespace() + "#" + isaValue;
            }
            return roots.contains(ShapeId.from(isaValue));
        }
        return false;
    }

    public static Set<ShapeId> shapesImplementing(ShapeId syntaxNodeId, Model model) {
        var syntaxNode = syntaxNodeId.getName();
        var shapeIds = shapesImplementing(syntaxNode, model);
        shapeIds.add(syntaxNodeId);
        return shapeIds;
    }

    public static Set<ShapeId> shapesImplementing(String syntaxNode, Model model) {
        var shapeIds = new LinkedHashSet<ShapeId>();
        for (var shape : model.getStructureShapes()) {
            if (filterSyntaxNodes(syntaxNode, model, shape)) {
                shapeIds.add(shape.getId());
            }
        }
        var done = false;
        var lastSize = shapeIds.size();
        while (!done) {
            for (var shape : model.getStructureShapes()) {
                if (filterIsaOfSyntaxNode(shapeIds, shape)) {
                    shapeIds.add(shape.getId());
                }
            }
            if (shapeIds.size() > lastSize) {
                lastSize = shapeIds.size();
            } else {
                done = true;
            }
        }
        return shapeIds;
    }

    public static MethodSpec.Builder visitForStructure(JavaShapeDirective state, StructureShape shape) {
        var name = shape.getId().getName();
        var symbolProvider = state.symbolProvider();
        var type = symbolProvider.toTypeName(shape);
        var builder = MethodSpec.methodBuilder("visit" + name)
                                .addModifiers(Modifier.PUBLIC)
                                .returns(TypeVariableName.get("T"))
                                .addParameter(type, "node");
        return builder;
    }

    public TypeSpec.Builder typeSpec(JavaShapeDirective directive) {
        return TypeSpec.interfaceBuilder(visitorClass(directive))
                       .addModifiers(Modifier.PUBLIC)
                       .addTypeVariable(TypeVariableName.get("T"));
    }

    private ClassName visitorClass(JavaShapeDirective directive) {
        var syntaxNodeClass = directive.toClass(syntaxNode);
        return ClassName.get(syntaxNodeClass.packageName(), syntaxNodeClass.simpleName() + "Visitor");
    }

    public TypeSpec generate(JavaShapeDirective state) {
        var builder = typeSpec(state);
        var shapeIds = shapesImplementing(syntaxNode, state.model());
        for (var shape : state.model().getStructureShapes()) {
            if (shapeIds.contains(shape.getId())) {
                if (!shape.hasTrait(InterfaceTrait.class)) {
                    builder.addMethod(visitForStructure(state, shape)
                                          .addModifiers(Modifier.ABSTRACT)
                                          .build());
                }
            }
        }
        builder.addType(defaultVisitor(state));
        return builder.build();
    }

    TypeSpec defaultVisitor(JavaShapeDirective directive) {
        var builder = TypeSpec.classBuilder("Default")
                              .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.ABSTRACT)
                              .addSuperinterface(ParameterizedTypeName.get(visitorClass(directive),
                                                                           TypeVariableName.get("T")))
                              .addTypeVariable(TypeVariableName.get("T"));

        builder.addMethod(MethodSpec.methodBuilder("getDefault")
                                    .addModifiers(Modifier.ABSTRACT, Modifier.PUBLIC)
                                    .addParameter(directive.toClass(syntaxNode), "node")
                                    .returns(TypeVariableName.get("T"))
                                    .build());
        var shapeIds = shapesImplementing(syntaxNode, directive.model());
        for (var shape : directive.model().getStructureShapes()) {
            if (shapeIds.contains(shape.getId())) {
                if (!shape.hasTrait(InterfaceTrait.class)) {
                    builder.addMethod(visitForStructure(directive, shape)
                                          .addAnnotation(Override.class)
                                          .addStatement("return getDefault(node)")
                                          .build());
                }
            }
        }
        return builder.build();
    }
}
