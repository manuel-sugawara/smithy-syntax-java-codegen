package mx.sugus.codegen.plugin.syntax;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import javax.lang.model.element.Modifier;
import mx.sugus.codegen.plugin.JavaShapeDirective;
import mx.sugus.javapoet.ClassName;
import mx.sugus.javapoet.MethodSpec;
import mx.sugus.javapoet.ParameterizedTypeName;
import mx.sugus.javapoet.TypeSpec;
import mx.sugus.syntax.java.InterfaceTrait;
import software.amazon.smithy.model.shapes.MemberShape;
import software.amazon.smithy.model.shapes.Shape;
import software.amazon.smithy.model.shapes.ShapeId;
import software.amazon.smithy.model.shapes.StructureShape;

public class GenerateRewriteVisitor {
    private final String syntaxNode;

    public GenerateRewriteVisitor(String syntaxNode) {
        this.syntaxNode = syntaxNode;
    }

    public TypeSpec.Builder typeSpec(JavaShapeDirective state) {
        return TypeSpec.classBuilder(syntaxNode + "RewriteVisitor")
                       .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                       .addSuperinterface(ParameterizedTypeName.get(ClassName.bestGuess(syntaxNode + "Visitor"),
                                                                    ClassName.bestGuess(syntaxNode)));
    }

    public TypeSpec generate(JavaShapeDirective state) {
        var builder = typeSpec(state);
        var shapeIds = mx.sugus.codegen.generators.internal.GenerateVisitor.shapesImplementing(syntaxNode, state.model());
        for (var shape : state.model().getStructureShapes()) {
            if (shapeIds.contains(shape.getId())) {
                if (!shape.hasTrait(InterfaceTrait.class)) {
                    builder.addMethod(visitForStructure(state, shape)
                                          .addAnnotation(Override.class)
                                          .addModifiers(Modifier.PUBLIC)
                                          .returns(ClassName.bestGuess(syntaxNode))
                                          .build());
                }
            }
        }
        return builder.build();
    }

    MethodSpec.Builder visitForStructure(JavaShapeDirective state, StructureShape shape) {
        var name = shape.getId().getName();
        var symbolProvider = state.symbolProvider();
        var type = symbolProvider.toTypeName(shape);
        var builder = MethodSpec.methodBuilder("visit" + name)
                                .addModifiers(Modifier.PUBLIC)
                                .returns(ClassName.bestGuess(syntaxNode))
                                .addParameter(type, "node");

        var shapeIds = GenerateVisitor.shapesImplementing(syntaxNode, state.model());
        if (!hasMemberNodes(state, shapeIds, shape)) {
            return builder.addStatement("return node");
        }
        builder.addStatement("$T.Builder builder = null", type);
        for (var member : shape.members()) {
            if (shapeIds.contains(member.getTarget())) {
                addSingleSyntaxNode(state, member, builder);
            }
            if (isCollectionOfSyntaxNode(shapeIds, state, member)) {
                addCollectionOfSyntaxNode(state, member, builder);
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
