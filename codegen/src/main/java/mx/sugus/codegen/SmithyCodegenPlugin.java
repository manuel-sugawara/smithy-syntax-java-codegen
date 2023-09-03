package mx.sugus.codegen;

import mx.sugus.codegen.integration.JavaCodegenIntegration;
import mx.sugus.codegen.plugin.BaseModule;
import mx.sugus.codegen.plugin.ClassPathPluginLoader;
import mx.sugus.codegen.plugin.ComposedPluginLoader;
import mx.sugus.codegen.plugin.DefaultBaseModuleConfig;
import mx.sugus.codegen.plugin.PluginLoader;
import mx.sugus.codegen.plugin.SmithyGenerator;
import mx.sugus.codegen.plugin.SpiPluginLoader;
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
        var settingsNode = context.getSettings();
        JavaCodegenSettings settings = JavaCodegenSettings.from(settingsNode);
        //runner.directedCodegen(new JavaDirectedCodegen());
        runner.directedCodegen(new SmithyGenerator(
            new BaseModule(DefaultBaseModuleConfig.buildDependants(pluginLoader(), settingsNode))));
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

    private PluginLoader pluginLoader() {
        return new ComposedPluginLoader(new ClassPathPluginLoader(), new SpiPluginLoader());
    }
}
