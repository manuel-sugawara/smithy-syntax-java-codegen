package mx.sugus.codegen.plugin.data;

import java.util.Collection;
import java.util.Collections;
import mx.sugus.codegen.plugin.BaseModuleConfig;
import mx.sugus.codegen.plugin.DefaultTransformModelTask;
import mx.sugus.codegen.plugin.Identifier;
import mx.sugus.codegen.plugin.PluginProvider;
import mx.sugus.codegen.plugin.SmithyGeneratorPlugin;
import mx.sugus.codegen.transforms.FlattenInterfaceMembers;
import mx.sugus.codegen.transforms.SynthesizeServiceTransform;
import software.amazon.smithy.model.node.ObjectNode;

public class FlattenInterfaceMembersPluginProvider implements PluginProvider {
    public static final Identifier ID = Identifier.of(FlattenInterfaceMembersPluginProvider.class);

    public FlattenInterfaceMembersPluginProvider() {
    }

    public static BaseModuleConfig newBaseConfig() {
        return BaseModuleConfig
            .builder()
            .addTransformer(DefaultTransformModelTask.builder()
                                                     .taskId(Identifier.of(SynthesizeServiceTransform.class))
                                                     .transform(FlattenInterfaceMembers::transform)
                                                     .build())
            .build();
    }

    @Override
    public Identifier name() {
        return ID;
    }

    @Override
    public PrepareForShapeCodegenPluginGenerator build(ObjectNode config) {
        return new PrepareForShapeCodegenPluginGenerator();
    }

    static class PrepareForShapeCodegenPluginGenerator implements SmithyGeneratorPlugin {

        @Override
        public Identifier name() {
            return ID;
        }

        @Override
        public Collection<Identifier> requires() {
            return Collections.emptyList();
        }

        @Override
        public BaseModuleConfig merge(BaseModuleConfig config) {
            return BaseModuleConfig.builder()
                                   .merge(newBaseConfig())
                                   .merge(config)
                                   .build();
        }

        @Override
        public BaseModuleConfig merge(ObjectNode node, BaseModuleConfig config) {
            return BaseModuleConfig.builder()
                                   .merge(newBaseConfig())
                                   .merge(config)
                                   .build();
        }
    }
}
