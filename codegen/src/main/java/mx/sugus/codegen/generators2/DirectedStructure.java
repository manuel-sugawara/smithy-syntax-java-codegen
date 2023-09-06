package mx.sugus.codegen.generators2;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import mx.sugus.codegen.plugin.JavaShapeDirective;
import mx.sugus.javapoet.ClassName;
import mx.sugus.javapoet.FieldSpec;
import mx.sugus.javapoet.MethodSpec;
import mx.sugus.javapoet.TypeSpec;
import software.amazon.smithy.model.shapes.MemberShape;

public interface DirectedStructure extends DirectiveToTypeSpec {

    ClassName className(JavaShapeDirective state);

    TypeSpec.Builder typeSpec(JavaShapeDirective state);

    List<FieldSpec> fieldsFor(JavaShapeDirective state, MemberShape member);

    default List<FieldSpec> extraFields(JavaShapeDirective state) {
        return Collections.emptyList();
    }

    default Map<String, TypeSpec> enumConstants(JavaShapeDirective state) {
        return Collections.emptyMap();
    }

    List<MethodSpec> constructors(JavaShapeDirective state);

    List<MethodSpec> methodsFor(JavaShapeDirective state, MemberShape member);

    default List<MethodSpec> extraMethods(JavaShapeDirective state) {
        return Collections.emptyList();
    }

    default List<DirectedStructure> innerTypes(JavaShapeDirective state) {
        return Collections.emptyList();
    }

    @Override
    default TypeSpec build(JavaShapeDirective state) {
        var builder = typeSpec(state);
        enumConstants(state).forEach(builder::addEnumConstant);
        for (var member : state.shape().members()) {
            for (var field : fieldsFor(state, member)) {
                builder.addField(field);
            }
        }
        for (var field : extraFields(state)) {
            builder.addField(field);
        }

        for (var method : constructors(state)) {
            builder.addMethod(method);
        }
        for (var member : state.shape().members()) {
            for (var method : methodsFor(state, member)) {
                builder.addMethod(method);
            }
        }
        for (var method : extraMethods(state)) {
            builder.addMethod(method);
        }
        for (var inner : innerTypes(state)) {
            var innerType = inner.build(state);
            builder.addType(innerType);
        }
        return builder.build();
    }
}
