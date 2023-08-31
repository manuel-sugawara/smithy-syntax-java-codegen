package mx.sugus.codegen;

import mx.sugus.codegen.integration.JavaCodegenIntegration;
import mx.sugus.codegen.writer.CodegenWriter;
import software.amazon.smithy.build.PluginContext;
import software.amazon.smithy.build.SmithyBuildPlugin;
import software.amazon.smithy.codegen.core.directed.CodegenDirector;

public class SmithyCodegenPlugin implements SmithyBuildPlugin {
    private final CodegenDirector<CodegenWriter, JavaCodegenIntegration, JavaCodegenContext, JavaCodegenSettings> runner =
        new CodegenDirector<>();

    @Override
    public String getName() {
        return "smithy-java-codegen";
    }

    @Override
    public void execute(PluginContext context) {
        JavaCodegenSettings settings = JavaCodegenSettings.from(context.getSettings());
        runner.directedCodegen(new JavaDirectedCodegen());
        runner.integrationClass(JavaCodegenIntegration.class);
        runner.fileManifest(context.getFileManifest());
        runner.model(context.getModel());
        runner.settings(settings);
        runner.service(settings.service());
        runner.performDefaultCodegenTransforms();
        runner.changeStringEnumsToEnumShapes(true);
        runner.createDedicatedInputsAndOutputs("Request", "Response");
        runner.run();
    }
}
