package mx.sugus.codegen.integration.core;

import mx.sugus.codegen.writer.CodegenWriter;
import mx.sugus.codegen.writer.sections.FieldSection;
import software.amazon.smithy.model.traits.DocumentationTrait;
import software.amazon.smithy.utils.CodeInterceptor;

class MemberJavaDocInterceptor implements CodeInterceptor.Prepender<FieldSection, CodegenWriter> {

    @Override
    public Class<FieldSection> sectionType() {
        return FieldSection.class;
    }

    @Override
    public void prepend(CodegenWriter writer, FieldSection enumVariantSection) {
        enumVariantSection.shape().getTrait(DocumentationTrait.class).ifPresent(trait -> {
            writer.addComment(trait.getValue());
        });
    }
}
