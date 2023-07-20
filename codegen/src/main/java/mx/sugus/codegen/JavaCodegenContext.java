package mx.sugus.codegen;

import java.util.List;
import mx.sugus.codegen.integration.JavaCodegenIntegration;
import mx.sugus.codegen.writer.CodegenWriter;
import software.amazon.smithy.build.FileManifest;
import software.amazon.smithy.codegen.core.CodegenContext;
import software.amazon.smithy.codegen.core.SymbolProvider;
import software.amazon.smithy.codegen.core.WriterDelegator;
import software.amazon.smithy.codegen.core.directed.CreateContextDirective;
import software.amazon.smithy.model.Model;

public final class JavaCodegenContext
    implements CodegenContext<JavaCodegenSettings, CodegenWriter, JavaCodegenIntegration> {

    private final Model model;
    private final JavaCodegenSettings settings;
    private final SymbolProvider symbolProvider;
    private final FileManifest fileManifest;
    private final WriterDelegator<CodegenWriter> writerDelegator;
    private final List<JavaCodegenIntegration> integrations;

    JavaCodegenContext(
        Model model,
        JavaCodegenSettings settings,
        SymbolProvider symbolProvider,
        FileManifest fileManifest,
        List<JavaCodegenIntegration> integrations
    ) {
        this.model = model;
        this.settings = settings;
        this.symbolProvider = symbolProvider;
        this.fileManifest = fileManifest;
        this.writerDelegator = new WriterDelegator<>(fileManifest, symbolProvider,
                                                     (filename, namespace) -> new CodegenWriter(namespace));
        this.integrations = integrations;
    }

    public static JavaCodegenContext fromContextDirective(
        CreateContextDirective<JavaCodegenSettings, JavaCodegenIntegration> createContextDirective
    ) {
        return new JavaCodegenContext(
            createContextDirective.model(),
            createContextDirective.settings(),
            createContextDirective.symbolProvider(),
            createContextDirective.fileManifest(),
            createContextDirective.integrations()
        );
    }

    @Override
    public Model model() {
        return model;
    }

    @Override
    public JavaCodegenSettings settings() {
        return settings;
    }

    @Override
    public SymbolProvider symbolProvider() {
        return symbolProvider;
    }

    @Override
    public FileManifest fileManifest() {
        return fileManifest;
    }

    @Override
    public WriterDelegator<CodegenWriter> writerDelegator() {
        return writerDelegator;
    }

    @Override
    public List<JavaCodegenIntegration> integrations() {
        return integrations;
    }
}
