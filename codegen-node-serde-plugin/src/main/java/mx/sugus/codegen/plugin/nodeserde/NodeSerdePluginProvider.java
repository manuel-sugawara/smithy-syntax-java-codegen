package mx.sugus.codegen.plugin.nodeserde;

import mx.sugus.codegen.plugin.Identifier;
import mx.sugus.codegen.plugin.PluginProvider;
import mx.sugus.codegen.plugin.SmithyGeneratorPlugin;
import software.amazon.smithy.model.node.ObjectNode;

public class NodeSerdePluginProvider implements PluginProvider {
    public static final Identifier ID = Identifier.of(NodeSerdePluginProvider.class);

    @Override
    public Identifier name() {
        return ID;
    }

    @Override
    public SmithyGeneratorPlugin build(ObjectNode config) {
        return new NodeSerdePlugin(config);
    }
}
