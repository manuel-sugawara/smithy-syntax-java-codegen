package mx.sugus.codegen.plugin.data;

import java.util.Collection;
import java.util.Collections;
import mx.sugus.codegen.plugin.BaseModuleConfig;
import mx.sugus.codegen.plugin.Identifier;
import mx.sugus.codegen.plugin.PluginProvider;
import mx.sugus.codegen.plugin.SmithyGeneratorPlugin;
import software.amazon.smithy.model.node.ObjectNode;

public class DataPluginProvider implements PluginProvider {
    public static final Identifier ID = Identifier.of(DataPluginProvider.class);
    private final ObjectNode config;

    public DataPluginProvider(ObjectNode node) {
        this.config = node;
    }

    public DataPluginProvider() {
        this.config = null;
    }

    public static BaseModuleConfig newBaseConfig() {
        return BaseModuleConfig
            .builder()
            .addInit(new StructureGeneratorData())
            .addInit(new StructureGeneratorEnum())
            .build();
    }

    @Override
    public Identifier name() {
        return ID;
    }

    @Override
    public SmithyGeneratorPlugin build(ObjectNode config) {
        return new DataPlugin();
    }

    static class DataPlugin implements SmithyGeneratorPlugin {

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
