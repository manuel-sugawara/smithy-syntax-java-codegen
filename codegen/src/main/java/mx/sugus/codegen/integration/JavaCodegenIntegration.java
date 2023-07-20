package mx.sugus.codegen.integration;

import mx.sugus.codegen.JavaCodegenContext;
import mx.sugus.codegen.JavaCodegenSettings;
import mx.sugus.codegen.writer.CodegenWriter;
import software.amazon.smithy.codegen.core.SmithyIntegration;

public interface JavaCodegenIntegration extends SmithyIntegration<JavaCodegenSettings, CodegenWriter, JavaCodegenContext> {
}