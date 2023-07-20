package mx.sugus.codegen;

import mx.sugus.codegen.integration.JavaCodegenIntegration;
import mx.sugus.codegen.writer.CodegenWriter;
import software.amazon.smithy.build.PluginContext;
import software.amazon.smithy.build.SmithyBuildPlugin;
import software.amazon.smithy.codegen.core.directed.CodegenDirector;

public class SmithyCodegenPlugin implements SmithyBuildPlugin {
    private final CodegenDirector<CodegenWriter, JavaCodegenIntegration, JavaCodegenContext, JavaCodegenSettings> runner = new CodegenDirector<>();

    @Override
    public String getName() {
        return "demo-codegen";
    }

    @Override
    public void execute(PluginContext pluginContext) {
        JavaCodegenSettings javaCodegenSettings = JavaCodegenSettings.from(pluginContext.getSettings());

        runner.directedCodegen(new JavaDirectedCodegen());
        runner.integrationClass(JavaCodegenIntegration.class);
        runner.fileManifest(pluginContext.getFileManifest());
        runner.model(pluginContext.getModel());
        runner.settings(javaCodegenSettings);
        runner.service(javaCodegenSettings.service());
        runner.performDefaultCodegenTransforms();
        runner.changeStringEnumsToEnumShapes(true);
        runner.createDedicatedInputsAndOutputs();
        runner.run();
    }
}
