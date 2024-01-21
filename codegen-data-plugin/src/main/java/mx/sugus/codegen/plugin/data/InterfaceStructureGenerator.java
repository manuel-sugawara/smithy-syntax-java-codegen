package mx.sugus.codegen.plugin.data;

import static mx.sugus.codegen.util.PoetUtils.toClassName;

import java.util.Collections;
import java.util.List;
import javax.lang.model.element.Modifier;
import mx.sugus.codegen.plugin.JavaShapeDirective;
import mx.sugus.javapoet.ClassName;
import mx.sugus.javapoet.FieldSpec;
import mx.sugus.javapoet.MethodSpec;
import mx.sugus.javapoet.TypeSpec;
import mx.sugus.syntax.java.IsaTrait;
import software.amazon.smithy.model.shapes.MemberShape;

public class InterfaceStructureGenerator implements DirectedStructure {

    @Override
    public ClassName className(JavaShapeDirective state) {
        return toClassName(state.symbol());
    }

    @Override
    public TypeSpec.Builder typeSpec(JavaShapeDirective state) {
        var result = TypeSpec.interfaceBuilder(state.symbol().getName())
                             .addModifiers(Modifier.PUBLIC);
        var shape = state.shape();
        if (shape.hasTrait(IsaTrait.class)) {
            var parent = state.parentClass(shape);
            result.addSuperinterface(parent);
        }
        return result;
    }

    @Override
    public List<FieldSpec> fieldsFor(JavaShapeDirective state, MemberShape member) {
        return Collections.emptyList();
    }

    @Override
    public List<MethodSpec> constructors(JavaShapeDirective state) {
        return Collections.emptyList();
    }

    @Override
    public List<MethodSpec> methodsFor(JavaShapeDirective state, MemberShape member) {
        return List.of(accessor(state, member));
    }

    private MethodSpec accessor(JavaShapeDirective state, MemberShape member) {
        var symbolProvider = state.symbolProvider();
        var name = symbolProvider.toMemberName(member);
        var type = symbolProvider.toTypeName(member);
        return MethodSpec.methodBuilder(name)
                         .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                         .returns(type)
                         .build();
    }
}
