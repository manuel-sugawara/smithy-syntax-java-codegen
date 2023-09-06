package mx.sugus.codegen.generators2;

import static mx.sugus.codegen.util.PoetUtils.toTypeName;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.lang.model.element.Modifier;
import mx.sugus.codegen.JavaSymbolProviderImpl;
import mx.sugus.codegen.plugin.JavaShapeDirective;
import mx.sugus.codegen.util.Naming;
import mx.sugus.javapoet.ClassName;
import mx.sugus.javapoet.FieldSpec;
import mx.sugus.javapoet.MethodSpec;
import mx.sugus.javapoet.ParameterSpec;
import mx.sugus.javapoet.TypeSpec;
import software.amazon.smithy.model.shapes.MemberShape;

public class BaseEnumData implements DirectedStructure {
    @Override
    public ClassName className(JavaShapeDirective state) {
        var symbolProvider = state.symbolProvider();
        return symbolProvider.toClassName(state.shape());
    }

    @Override
    public TypeSpec.Builder typeSpec(JavaShapeDirective state) {
        var symbolProvider = state.symbolProvider();
        var name = symbolProvider.toShapeJavaName(state.shape());
        return TypeSpec.enumBuilder(name.toString())
                       .addModifiers(Modifier.PUBLIC);
    }

    @Override
    public Map<String, TypeSpec> enumConstants(JavaShapeDirective state) {
        var result = new LinkedHashMap<String, TypeSpec>();
        var shape = state.shape().asEnumShape().orElseThrow(() -> new IllegalArgumentException("expected enum"));
        shape.getEnumValues().forEach((name, value) -> {
            var enumConstant = Naming.screamCase(name);
            result.put(enumConstant, TypeSpec.anonymousClassBuilder("$S", name).build());
        });
        result.put("UNKNOWN_TO_VERSION", TypeSpec.anonymousClassBuilder("$L", "null").build());
        return result;
    }

    @Override
    public List<FieldSpec> fieldsFor(JavaShapeDirective state, MemberShape member) {
        return Collections.emptyList();
    }

    @Override
    public List<FieldSpec> extraFields(JavaShapeDirective state) {
        return List.of(FieldSpec.builder(String.class, "value")
                                .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                                .build());
    }

    @Override
    public List<MethodSpec> constructors(JavaShapeDirective state) {
        return List.of(MethodSpec.constructorBuilder()
                                 .addModifiers(Modifier.PRIVATE)
                                 .addParameter(toTypeName(JavaSymbolProviderImpl.STRING), "value")
                                 .addStatement("this.value = value")
                                 .build());
    }

    @Override
    public List<MethodSpec> methodsFor(JavaShapeDirective state, MemberShape member) {
        return Collections.emptyList();
    }

    @Override
    public List<MethodSpec> extraMethods(JavaShapeDirective state) {
        return List.of(MethodSpec.methodBuilder("toString")
                                 .returns(String.class)
                                 .addModifiers(Modifier.PUBLIC)
                                 .addAnnotation(Override.class)
                                 .addStatement("return String.valueOf(value)")
                                 .build(),
                       generateFromValueUsingMap(state));
    }

    MethodSpec generateFromValueUsingMap(JavaShapeDirective state) {
        var shape = state.shape().asEnumShape().orElseThrow(() -> new IllegalArgumentException("expected enum"));
        var symbol = state.symbol();
        var b = MethodSpec.methodBuilder("fromValue")
                          .addJavadoc("Use this in place of valueOf to convert the raw string returned by the service into the "
                                      + "enum value.")
                          .returns(toTypeName(symbol))
                          .addModifiers(Modifier.STATIC, Modifier.PUBLIC)
                          .addParameter(ParameterSpec.builder(String.class, "value")
                                                     .addJavadoc("The string literal to convert to $T", toTypeName(symbol))
                                                     .build());

        b.beginControlFlow("switch(value)");
        shape.getEnumValues().forEach((name, value) -> {
            var enumConstant = Naming.screamCase(name);
            b.addCode("case $S:\n", value);
            b.addStatement("$>return $N$<", enumConstant);
        });

        b.addCode("default:\n");
        b.addStatement("$>return $N$<", "UNKNOWN_TO_VERSION");
        b.endControlFlow();
        return b.build();
    }
}
