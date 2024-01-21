package mx.sugus.codegen.plugin.syntax;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import javax.lang.model.element.Modifier;
import mx.sugus.codegen.plugin.AbstractShapeTask;
import mx.sugus.codegen.plugin.JavaShapeDirective;
import mx.sugus.codegen.plugin.TypeSpecResult;
import mx.sugus.javapoet.ClassName;
import mx.sugus.javapoet.MethodSpec;
import mx.sugus.javapoet.ParameterizedTypeName;
import mx.sugus.javapoet.TypeSpec;
import mx.sugus.syntax.java.InterfaceTrait;
import software.amazon.smithy.model.shapes.MemberShape;
import software.amazon.smithy.model.shapes.Shape;
import software.amazon.smithy.model.shapes.ShapeId;
import software.amazon.smithy.model.shapes.ShapeType;
import software.amazon.smithy.model.shapes.StructureShape;

public class GenerateRewriteVisitor extends AbstractShapeTask<TypeSpecResult> {
    private final String syntaxNode;

    public GenerateRewriteVisitor(String syntaxNode) {
        super(TypeSpecResult.class, ShapeType.SERVICE);
        this.syntaxNode = syntaxNode;
    }

    private String syntaxNode() {
        return syntaxNode;
    }

    public TypeSpec.Builder typeSpec(JavaShapeDirective directive) {
        var syntaxNodeClass = directive.toClass(syntaxNode());
        var rewriteVisitorClass = ClassName.get(syntaxNodeClass.packageName(), syntaxNodeClass.simpleName() + "RewriteVisitor");
        var visitorClass = ClassName.get(syntaxNodeClass.packageName(), syntaxNodeClass.simpleName() + "Visitor");
        return TypeSpec.classBuilder(rewriteVisitorClass)
                       .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                       .addSuperinterface(ParameterizedTypeName.get(visitorClass,
                                                                    directive.toClass(syntaxNode)));
    }

    @Override
    public TypeSpecResult produce(JavaShapeDirective directive) {
        var builder = typeSpec(directive);
        var shapeIds = GenerateVisitor.shapesImplementing(syntaxNode, directive.model());
        for (var shape : directive.model().getStructureShapes()) {
            if (shapeIds.contains(shape.getId())) {
                if (!shape.hasTrait(InterfaceTrait.class)) {
                    builder.addMethod(visitForStructure(directive, shape)
                                          .addAnnotation(Override.class)
                                          .addModifiers(Modifier.PUBLIC)
                                          .returns(directive.toClass(syntaxNode))
                                          .build());
                }
            }
        }
        return TypeSpecResult.builder().spec(builder.build()).build();
    }

    MethodSpec.Builder visitForStructure(JavaShapeDirective directive, StructureShape shape) {
        var name = shape.getId().getName();
        var symbolProvider = directive.symbolProvider();
        var type = symbolProvider.toTypeName(shape);
        var syntaxNodeClass = directive.toClass(syntaxNode);
        var builder = MethodSpec.methodBuilder("visit" + name)
                                .addModifiers(Modifier.PUBLIC)
                                .returns(syntaxNodeClass)
                                .addParameter(type, "node");

        var shapeIds = GenerateVisitor.shapesImplementing(syntaxNode, directive.model());
        if (!hasMemberNodes(directive, shapeIds, shape)) {
            return builder.addStatement("return node");
        }
        builder.addStatement("$T.Builder builder = null", type);
        for (var member : shape.members()) {
            if (shapeIds.contains(member.getTarget())) {
                addSingleSyntaxNode(directive, member, builder);
            }
            if (isCollectionOfSyntaxNode(shapeIds, directive, member)) {
                addCollectionOfSyntaxNode(directive, member, builder);
            }
        }
        builder.beginControlFlow("if (builder != null)")
               .addStatement("return builder.build()")
               .endControlFlow();
        builder.addStatement("return node");
        return builder;
    }

    private void addCollectionOfSyntaxNode(JavaShapeDirective state, MemberShape member, MethodSpec.Builder builder) {
        var symbolProvider = state.symbolProvider();
        var memberName = symbolProvider.toMemberJavaName(member);
        var memberSymbol = symbolProvider.toSymbol(member);
        var memberType = symbolProvider.toTypeName(member);
        var memberCamelName = memberName.asCamelCase();
        var memberInnerTypeShape = memberInnerType(state, member);
        var memberInnerType = symbolProvider.toTypeName(memberInnerTypeShape);
        builder.addStatement("$T $L = node.$L()", memberType, memberName, memberName);
        builder.addStatement("$T new$L = null", memberType, memberCamelName);
        builder.beginControlFlow("for (int idx = 0; idx < $L.size(); idx++)", memberName);
        builder.addStatement("$T value = $L.get(idx)", memberInnerType, memberName);
        builder.addStatement("$1T newValue = ($1T) value.accept(this)", memberInnerType);
        builder.beginControlFlow("if (!$T.equals(value, newValue))", Objects.class);
        builder.beginControlFlow("if (new$L == null)", memberCamelName);
        builder.addStatement("new$L = new $T<>()", memberCamelName, symbolProvider.concreteClassFor2(memberSymbol));
        builder.addStatement("new$L.addAll($L.subList(0, idx))", memberCamelName, memberName);
        builder.endControlFlow();
        builder.addStatement("value = newValue");
        builder.endControlFlow();

        builder.beginControlFlow("if (new$L != null)", memberCamelName);
        builder.addStatement("new$L.add(value)", memberCamelName);
        builder.endControlFlow();
        builder.endControlFlow();

        builder.beginControlFlow("if (new$1L != null)", memberCamelName);
        builder.beginControlFlow("if (builder == null)")
               .addStatement("builder = node.toBuilder()")
               .endControlFlow();
        builder.addStatement("builder.$L(new$L)", memberName, memberCamelName);
        builder.endControlFlow();
    }

    private Shape memberInnerType(JavaShapeDirective state, MemberShape member) {
        var targetId = member.getTarget();
        var target = state.model().expectShape(targetId);
        if (target.isListShape()) {
            var listShape = target.asListShape().get();
            var listMemberTarget = listShape.getMember().getTarget();
            return state.model().expectShape(listMemberTarget);
        }
        throw new NoSuchElementException();
    }

    private boolean isCollectionOfSyntaxNode(Set<ShapeId> shapeIds, JavaShapeDirective state, MemberShape member) {
        var targetId = member.getTarget();
        var target = state.model().expectShape(targetId);
        if (target.isListShape()) {
            var listShape = target.asListShape().get();
            var listMemberTarget = listShape.getMember().getTarget();
            if (shapeIds.contains(listMemberTarget)) {
                return true;
            }
            // do we have a list of SyntaxNodes?
            if (listMemberTarget.getName().equals(syntaxNode)) {
                return true;
            }
        }
        return false;
    }

    void addSingleSyntaxNode(JavaShapeDirective state, MemberShape member, MethodSpec.Builder builder) {
        var symbolProvider = state.symbolProvider();
        var memberName = symbolProvider.toMemberJavaName(member);
        var memberType = symbolProvider.toTypeName(member);
        var memberCamelName = memberName.asCamelCase();
        builder.addStatement("$T old$L = node.$L()", memberType, memberCamelName, memberName);
        builder.addStatement("$T new$L = ($T) old$L.accept(this)", memberType, memberCamelName, memberType,
                             memberCamelName);
        builder.beginControlFlow("if (!$1T.equals(old$2L, new$2L))", Objects.class, memberCamelName);
        builder.beginControlFlow("if (builder == null)")
               .addStatement("builder = node.toBuilder()")
               .endControlFlow();
        builder.addStatement("builder.$L(new$L)", memberName, memberCamelName);
        builder.endControlFlow();
    }

    boolean hasMemberNodes(JavaShapeDirective state, Set<ShapeId> shapeIds, StructureShape shape) {
        for (var member : shape.members()) {
            if (shapeIds.contains(member.getTarget())) {
                return true;
            }
            if (isCollectionOfSyntaxNode(shapeIds, state, member)) {
                return true;
            }
        }
        return false;
    }
}