package mx.sugus.codegen.integration.core;

import mx.sugus.codegen.JavaCodegenContext;
import mx.sugus.codegen.integration.JavaCodegenIntegration;
import mx.sugus.codegen.writer.CodegenWriter;
import software.amazon.smithy.utils.CodeInterceptor;
import software.amazon.smithy.utils.CodeSection;

import java.util.List;

public class CoreIntegration implements JavaCodegenIntegration {
    private static final String INTEGRATION_NAME = "Core";

    @Override
    public String name() {
        return INTEGRATION_NAME;
    }

    @Override
    public List<? extends CodeInterceptor<? extends CodeSection, CodegenWriter>> interceptors(JavaCodegenContext codegenContext) {
        return List.of(
            new ClassJavaDocInterceptor(),
            new MemberJavaDocInterceptor(),
            new GeneratedAnnotationInterceptor()
        );
    }

}