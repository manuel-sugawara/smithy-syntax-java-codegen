package mx.sugus.codegen.plugin.syntax;

import java.util.Collection;
import java.util.List;
import javax.lang.model.element.Modifier;
import mx.sugus.codegen.plugin.BaseModuleConfig;
import mx.sugus.codegen.plugin.Identifier;
import mx.sugus.codegen.plugin.JavaShapeDirective;
import mx.sugus.codegen.plugin.ShapeTask;
import mx.sugus.codegen.plugin.ShapeTaskInterceptor;
import mx.sugus.codegen.plugin.SmithyGeneratorPlugin;
import mx.sugus.codegen.plugin.TypeSpecResult;
import mx.sugus.javapoet.ClassName;
import mx.sugus.javapoet.MethodSpec;
import mx.sugus.javapoet.ParameterizedTypeName;
import mx.sugus.javapoet.TypeVariableName;
import mx.sugus.syntax.java.InterfaceTrait;
import software.amazon.smithy.model.node.ObjectNode;
import software.amazon.smithy.model.shapes.ShapeType;

public class SyntaxPlugin implements SmithyGeneratorPlugin {

    private final ObjectNode config;
    private final String syntaxNode = "SyntaxNode";

    public SyntaxPlugin(ObjectNode node) {
        this.config = node;
    }

    BaseModuleConfig.Builder newBaseConfig() {
        return BaseModuleConfig
            .builder()
            .addInit(ShapeType.SERVICE,
                     ShapeTask.builder(TypeSpecResult.class)
                              .type(ShapeType.SERVICE)
                              .taskId(Identifier.of("mx.sugus.codegen.plugin.syntax", "SyntaxVisitor"))
                              .handler(this::syntaxVisitor)
                              .build())
            .addInit(ShapeType.SERVICE,
                     ShapeTask.builder(TypeSpecResult.class)
                              .type(ShapeType.SERVICE)
                              .taskId(Identifier.of("mx.sugus.codegen.plugin.syntax", "SyntaxVisitor"))
                              .handler(this::syntaxRewriteVisitor)
                              .build())
            .addInterceptor(
                ShapeTaskInterceptor.builder(TypeSpecResult.class)
                                    .taskId(Identifier.of("mx.sugus.codegen.plugin.data", "Default"))
                                    .handler(this::syntaxInterceptor)
                                    .build());
    }

    @Override
    public ClassName name() {
        return ClassName.get("mx.sugus.codegen.plugin.syntax", "SyntaxPluginProvider");
    }

    @Override
    public Collection<ClassName> requires() {
        var pluginId = Identifier.of("mx.sugus.codegen.plugin.data#DataPluginProvider");
        return List.of(ClassName.get(pluginId.namespace(), pluginId.name()));
    }

    @Override
    public BaseModuleConfig merge(BaseModuleConfig config) {
        return newBaseConfig().merge(config).build();
    }

    @Override
    public BaseModuleConfig merge(ObjectNode node, BaseModuleConfig config) {
        return newBaseConfig().merge(config).build();
    }

    private TypeSpecResult syntaxInterceptor(JavaShapeDirective directive, TypeSpecResult result) {
        var shapeIds = GenerateVisitor.shapesImplementing(syntaxNode(), directive.model());
        var shape = directive.shape();
        if (shapeIds.contains(shape.getId()) && !shape.hasTrait(InterfaceTrait.class)) {
            var visitor = ParameterizedTypeName.get(ClassName.bestGuess(syntaxNode() + "Visitor"), TypeVariableName.get("T"));
            var name = directive.symbolProvider().toShapeJavaName(shape);
            var spec = result.spec().toBuilder()
                             .addMethod(
                                 MethodSpec.methodBuilder("accept")
                                           .addAnnotation(Override.class)
                                           .addModifiers(Modifier.PUBLIC)
                                           .returns(TypeVariableName.get("T", Object.class))
                                           .addTypeVariable(TypeVariableName.get("T"))
                                           .addParameter(visitor, "visitor")
                                           .addStatement("return visitor.visit" + name + "(this)")
                                           .build())
                             .build();

            return result.toBuilder()
                         .spec(spec)
                         .build();
        }
        if (shape.hasTrait(InterfaceTrait.class) && shape.getId().getName().equals(syntaxNode)) {
            var visitor = ParameterizedTypeName.get(ClassName.bestGuess(syntaxNode() + "Visitor"), TypeVariableName.get("T"));
            var spec = result.spec().toBuilder()
                             .addMethod(MethodSpec.methodBuilder("accept")
                                                  .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                                                  .returns(TypeVariableName.get("T", Object.class))
                                                  .addTypeVariable(TypeVariableName.get("T"))
                                                  .addParameter(visitor, "visitor")
                                                  .build())
                             .build();
            return result.toBuilder()
                         .spec(spec)
                         .build();
        }
        return result;
    }

    private TypeSpecResult syntaxVisitor(JavaShapeDirective directive) {
        var spec = new GenerateVisitor(syntaxNode).generate(directive);
        return TypeSpecResult.builder().spec(spec).build();
    }

    private TypeSpecResult syntaxRewriteVisitor(JavaShapeDirective directive) {
        var spec = new GenerateRewriteVisitor(syntaxNode).generate(directive);
        return TypeSpecResult.builder().spec(spec).build();
    }

    private String syntaxNode() {
        return syntaxNode;
    }
}
