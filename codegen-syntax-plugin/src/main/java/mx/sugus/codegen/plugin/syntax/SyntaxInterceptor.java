package mx.sugus.codegen.plugin.syntax;

import javax.lang.model.element.Modifier;
import mx.sugus.codegen.plugin.AbstractShapeTask;
import mx.sugus.codegen.plugin.JavaShapeDirective;
import mx.sugus.codegen.plugin.TypeSpecResult;
import mx.sugus.javapoet.ClassName;
import mx.sugus.javapoet.MethodSpec;
import mx.sugus.javapoet.ParameterizedTypeName;
import mx.sugus.javapoet.TypeVariableName;
import mx.sugus.syntax.java.InterfaceTrait;
import software.amazon.smithy.model.shapes.ShapeType;

public class SyntaxInterceptor extends AbstractShapeTask<TypeSpecResult> {

    private final String syntaxNode;

    SyntaxInterceptor(String syntaxNode) {
        super(TypeSpecResult.class, ShapeType.STRUCTURE);
        this.syntaxNode = syntaxNode;
    }

    public String syntaxNode() {
        return syntaxNode;
    }

    @Override
    public TypeSpecResult transform(JavaShapeDirective directive, TypeSpecResult result) {
        var shapeIds = GenerateVisitor.shapesImplementing(syntaxNode(), directive.model());
        var shape = directive.shape();
        if (shapeIds.contains(shape.getId()) && !shape.hasTrait(InterfaceTrait.class)) {
            var syntaxNodeClass = directive.toClass(syntaxNode());
            var visitorClass = ClassName.get(syntaxNodeClass.packageName(), syntaxNodeClass.simpleName() + "Visitor");
            var visitor = ParameterizedTypeName.get(visitorClass, TypeVariableName.get("VisitorR"));
            var name = directive.symbolProvider().toShapeJavaName(shape);
            var spec = result.spec().toBuilder()
                             .addMethod(
                                 MethodSpec.methodBuilder("accept")
                                           .addAnnotation(Override.class)
                                           .addModifiers(Modifier.PUBLIC)
                                           .returns(TypeVariableName.get("VisitorR", Object.class))
                                           .addTypeVariable(TypeVariableName.get("VisitorR"))
                                           .addParameter(visitor, "visitor")
                                           .addStatement("return visitor.visit$L(this)", name)
                                           .build())
                             .build();

            return result.toBuilder()
                         .spec(spec)
                         .build();
        }
        if (shape.hasTrait(InterfaceTrait.class) && shape.getId().toString().equals(syntaxNode)) {
            var syntaxNodeClass = directive.toClass(syntaxNode());
            var visitorClass = ClassName.get(syntaxNodeClass.packageName(), syntaxNodeClass.simpleName() + "Visitor");
            var visitor = ParameterizedTypeName.get(visitorClass, TypeVariableName.get("VisitorR"));
            var spec = result.spec().toBuilder()
                             .addMethod(MethodSpec.methodBuilder("accept")
                                                  .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                                                  .returns(TypeVariableName.get("VisitorR", Object.class))
                                                  .addTypeVariable(TypeVariableName.get("VisitorR"))
                                                  .addParameter(visitor, "visitor")
                                                  .build())
                             .build();
            return result.toBuilder()
                         .spec(spec)
                         .build();
        }
        return result;
    }
}
