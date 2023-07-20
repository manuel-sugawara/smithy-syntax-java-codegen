package mx.sugus.codegen.integration.core;

import mx.sugus.codegen.SmithyCodegenPlugin;
import mx.sugus.codegen.writer.CodegenWriter;
import mx.sugus.codegen.writer.sections.ClassSection;
import software.amazon.smithy.codegen.core.Symbol;
import software.amazon.smithy.utils.CodeInterceptor;

import javax.annotation.processing.Generated;

class GeneratedAnnotationInterceptor implements CodeInterceptor.Appender<ClassSection, CodegenWriter> {
    private final Symbol generatedAnnotationSymbol = Symbol.builder()
        .name(Generated.class.getSimpleName())
        .namespace(Generated.class.getPackageName(), ".")
        .build();

    @Override
    public void append(CodegenWriter writer, ClassSection methodSection) {
        writer.addImport(generatedAnnotationSymbol, generatedAnnotationSymbol.getName());
        writer.write("@$T($S)", generatedAnnotationSymbol, SmithyCodegenPlugin.class.getName());
    }

    @Override
    public Class<ClassSection> sectionType() {
        return ClassSection.class;
    }

}
