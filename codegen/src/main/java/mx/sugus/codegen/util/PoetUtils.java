package mx.sugus.codegen.util;

import java.io.IOException;
import mx.sugus.codegen.writer.CodegenWriter;
import mx.sugus.javapoet.ClassName;
import mx.sugus.javapoet.JavaFile;
import mx.sugus.javapoet.TypeSpec;
import software.amazon.smithy.codegen.core.Symbol;

public class PoetUtils {

    public static ClassName toClassName(Symbol s) {
        return ClassName.get(s.getNamespace(), s.getName());
    }

    public static void emit(CodegenWriter w, TypeSpec spec) {
        w.writeInlineWithNoFormatting(generateClass(spec));
    }

    private static String generateClass(TypeSpec spec) {
        StringBuilder output = new StringBuilder();
        try {
            buildJavaFile(spec).writeTo(output);
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate class", e);
        }
        return output.toString();
    }

    public static JavaFile buildJavaFile(TypeSpec spec) {
        JavaFile.Builder builder = JavaFile.builder("foobar" , spec).skipJavaLangImports(true);
        //spec.staticImports().forEach(i -> i.memberNames().forEach(m -> builder.addStaticImport(i.className(), m)));
        return builder.build();
    }
}
