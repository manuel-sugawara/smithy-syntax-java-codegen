package mx.sugus.codegen.integration.core;

import mx.sugus.codegen.writer.CodegenWriter;
import mx.sugus.codegen.writer.sections.ClassSection;
import software.amazon.smithy.model.traits.DocumentationTrait;
import software.amazon.smithy.utils.CodeInterceptor;

class ClassJavaDocInterceptor implements CodeInterceptor.Prepender<ClassSection, CodegenWriter> {

    @Override
    public void prepend(CodegenWriter writer, ClassSection classSection) {
        classSection.classShape().getTrait(DocumentationTrait.class).ifPresent(trait -> {
            writer.addJavadoc(trait.getValue());
        });
    }

    @Override
    public Class<ClassSection> sectionType() {
        return ClassSection.class;
    }
}
