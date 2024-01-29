package mx.sugus.codegen.plugin.nodeserde;

import java.util.Collection;
import java.util.List;
import mx.sugus.codegen.plugin.BaseModuleConfig;
import mx.sugus.codegen.plugin.Identifier;
import mx.sugus.codegen.plugin.SmithyGeneratorPlugin;
import mx.sugus.codegen.plugin.data.DataPluginProvider;
import mx.sugus.codegen.plugin.data.StructureGeneratorData;
import software.amazon.smithy.model.node.ObjectNode;

public class NodeSerdePlugin implements SmithyGeneratorPlugin {
    private final ObjectNode config;

    public NodeSerdePlugin(ObjectNode node) {
        this.config = node;
    }

    BaseModuleConfig.Builder newBaseConfig() {
        return BaseModuleConfig
            .builder()
            .putInterceptor(StructureGeneratorData.ID,
                            new NodeSerdeInterceptor());
    }

    @Override
    public Identifier name() {
        return NodeSerdePluginProvider.ID;
    }

    @Override
    public Collection<Identifier> requires() {
        return List.of(DataPluginProvider.ID);
    }

    @Override
    public BaseModuleConfig merge(BaseModuleConfig config) {
        return newBaseConfig().merge(config).build();
    }

    @Override
    public BaseModuleConfig merge(ObjectNode node, BaseModuleConfig config) {
        return newBaseConfig().merge(config).build();
    }

}
