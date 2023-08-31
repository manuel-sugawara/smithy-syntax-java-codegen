package mx.sugus.codegen.generators;

import java.util.Collections;
import java.util.List;
import mx.sugus.javapoet.FieldSpec;
import mx.sugus.javapoet.MethodSpec;
import mx.sugus.javapoet.TypeSpec;
import mx.sugus.javapoet.ClassName;
import software.amazon.smithy.model.shapes.MemberShape;

public interface DirectedStructure {

    ClassName className(StructureGenerator state);

    TypeSpec.Builder typeSpec(StructureGenerator state);

    List<FieldSpec> fieldsFor(StructureGenerator state, MemberShape member);

    default List<FieldSpec> extraFields(StructureGenerator state) {
        return Collections.emptyList();
    }

    List<MethodSpec> constructors(StructureGenerator state);

    List<MethodSpec> methodsFor(StructureGenerator state, MemberShape member);

    default List<MethodSpec> extraMethods(StructureGenerator state) {
        return Collections.emptyList();
    }

    default List<DirectedStructure> innerTypes(StructureGenerator state) {
        return Collections.emptyList();
    }

    default TypeSpec build(StructureGenerator state) {
        var builder = typeSpec(state);
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
