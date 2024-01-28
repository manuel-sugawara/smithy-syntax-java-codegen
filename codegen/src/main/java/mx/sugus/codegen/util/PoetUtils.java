package mx.sugus.codegen.util;

import java.io.IOException;
import mx.sugus.codegen.writer.CodegenWriter;
import mx.sugus.javapoet.ClassName;
import mx.sugus.javapoet.JavaFile;
import mx.sugus.javapoet.ParameterizedTypeName;
import mx.sugus.javapoet.TypeName;
import mx.sugus.javapoet.TypeSpec;
import mx.sugus.javapoet.WildcardTypeName;
import mx.sugus.syntax.java.IsaTrait;
import software.amazon.smithy.codegen.core.Symbol;
import software.amazon.smithy.codegen.core.SymbolReference;

public class PoetUtils {

    public static Symbol SUBTYPE_OF_OBJECT = Symbol
        .builder()
        .name(WildcardTypeName.class.getSimpleName())
        .namespace(WildcardTypeName.class.getPackageName(), ".")
        .putProperty(TypeName.class.getName(), WildcardTypeName.subtypeOf(Object.class))
        .build();

    public static TypeName toTypeName(Symbol s) {
        var baseClass = ClassName.get(s.getNamespace(), s.getName());
        var typeName = s.getProperty(TypeName.class.getName(), TypeName.class).orElse(null);
        if (typeName != null) {
            return typeName;
        }
        if (s.getReferences().isEmpty()) {
            return baseClass;
        }
        TypeName[] params =
            s.getReferences().stream().map(SymbolReference::getSymbol).map(PoetUtils::toTypeName).toArray(TypeName[]::new);
        return ParameterizedTypeName.get(baseClass, params);
    }

    public static ClassName toClassName(Symbol s) {
        return ClassName.get(s.getNamespace(), s.getName());
    }

    public static void emit(CodegenWriter w, TypeSpec spec, String ns) {
        w.writeInlineWithNoFormatting(generateClass(spec, ns));
    }

    private static String generateClass(TypeSpec spec, String ns) {
        StringBuilder output = new StringBuilder();
        try {
            buildJavaFile(spec, ns).writeTo(output);
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate class", e);
        }
        return output.toString();
    }

    public static JavaFile buildJavaFile(TypeSpec spec, String ns) {
        JavaFile.Builder builder = JavaFile.builder(ns, spec)
                                           .skipJavaLangImports(true)
                                           .indent("    ");
        //spec.staticImports().forEach(i -> i.memberNames().forEach(m -> builder.addStaticImport(i.className(), m)));
        return builder.build();
    }

}
