package mx.sugus.codegen.plugin.syntax;

import mx.sugus.codegen.plugin.Identifier;
import mx.sugus.codegen.plugin.PluginProvider;
import mx.sugus.codegen.plugin.SmithyGeneratorPlugin;
import software.amazon.smithy.model.node.ObjectNode;

public class SyntaxPluginProvider implements PluginProvider {
    public static final Identifier ID = Identifier.of(SyntaxPluginProvider.class);

    @Override
    public Identifier name() {
        return ID;
    }

    @Override
    public SmithyGeneratorPlugin build(ObjectNode config) {
        return new SyntaxPlugin(config);
    }
}
