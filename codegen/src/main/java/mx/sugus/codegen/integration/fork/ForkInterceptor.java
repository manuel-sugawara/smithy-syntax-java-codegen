package mx.sugus.codegen.integration.fork;

import mx.sugus.codegen.writer.CodegenWriter;
import mx.sugus.codegen.writer.sections.MethodSection;
import software.amazon.smithy.utils.CodeInterceptor;

class ForkInterceptor implements CodeInterceptor.Appender<MethodSection, CodegenWriter> {
    private static final String TEST_TRAIT_NAME = "example.weather#forkable";

    @Override
    public void append(CodegenWriter writer, MethodSection methodSection) {
        var shape = methodSection.shape();
        if (shape.hasTrait(TEST_TRAIT_NAME)) {
            writer.openBlock("public String fork() {", "}", () -> {
                writer.write("return $S;", "FORK");
            });
        }
    }

    @Override
    public Class<MethodSection> sectionType() {
        return MethodSection.class;
    }
}
