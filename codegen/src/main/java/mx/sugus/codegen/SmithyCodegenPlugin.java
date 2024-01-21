package mx.sugus.codegen;

import java.util.ArrayList;
import java.util.ServiceLoader;
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
import software.amazon.smithy.model.Model;

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
        var baseModule = new BaseModule(DefaultBaseModuleConfig.buildDependants(pluginLoader(), settingsNode));
        runner.directedCodegen(new SmithyGenerator(baseModule));
        runner.model(baseModule.earlyPreprocessModel(context.getModel()));
        runner.integrationFinder(() -> integrationFinder(baseModule));
        runner.integrationClass(JavaCodegenIntegration.class);
        runner.fileManifest(context.getFileManifest());
        runner.settings(settings);
        runner.service(settings.service());
        runner.performDefaultCodegenTransforms();
        runner.changeStringEnumsToEnumShapes(true);
        runner.createDedicatedInputsAndOutputs("Request", "Response");
        runner.run();
    }

    private Iterable<JavaCodegenIntegration> integrationFinder(BaseModule module) {
        var result = new ArrayList<JavaCodegenIntegration>();
        result.add(new ModelTransformIntegration(module));
        for (var integration : ServiceLoader.load(JavaCodegenIntegration.class, this.getClass().getClassLoader())) {
            result.add(integration);
        }
        return result;
    }

    private PluginLoader pluginLoader() {
        return new ComposedPluginLoader(new ClassPathPluginLoader(), new SpiPluginLoader());
    }

    static class ModelTransformIntegration implements JavaCodegenIntegration {
        private final BaseModule baseModule;

        ModelTransformIntegration(BaseModule baseModule) {
            this.baseModule = baseModule;
        }

        @Override
        public Model preprocessModel(Model model, JavaCodegenSettings settings) {
            return baseModule.preprocessModel(model, settings);
        }
    }
}
