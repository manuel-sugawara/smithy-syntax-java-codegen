package mx.sugus.codegen.generators;

import static mx.sugus.codegen.util.PoetUtils.toClassName;

import java.util.Collections;
import java.util.List;
import javax.lang.model.element.Modifier;
import mx.sugus.javapoet.ClassName;
import mx.sugus.javapoet.FieldSpec;
import mx.sugus.javapoet.MethodSpec;
import mx.sugus.javapoet.TypeSpec;
import mx.sugus.syntax.java.IsaTrait;
import software.amazon.smithy.model.shapes.MemberShape;
import software.amazon.smithy.model.traits.StringTrait;

public class InterfaceStructureGenerator implements DirectedStructure {
    @Override
    public ClassName className(StructureGenerator state) {
        return toClassName(state.symbol());
    }

    @Override
    public TypeSpec.Builder typeSpec(StructureGenerator state) {
        var result = TypeSpec.interfaceBuilder(state.symbol().getName())
                       .addModifiers(Modifier.PUBLIC);
        var shape = state.shape();
        if (shape.hasTrait(IsaTrait.class)) {
            var parent = shape.getTrait(IsaTrait.class).map(StringTrait::getValue).orElse("");
            result.addSuperinterface(ClassName.bestGuess(parent));
        }
        return result;
    }

    @Override
    public List<FieldSpec> fieldsFor(StructureGenerator state, MemberShape member) {
        return Collections.emptyList();
    }

    @Override
    public List<MethodSpec> constructors(StructureGenerator state) {
        return Collections.emptyList();
    }

    @Override
    public List<MethodSpec> methodsFor(StructureGenerator state, MemberShape member) {
        return Collections.emptyList();
    }
}
