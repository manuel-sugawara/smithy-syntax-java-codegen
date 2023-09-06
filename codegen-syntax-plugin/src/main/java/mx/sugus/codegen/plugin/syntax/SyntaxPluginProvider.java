package mx.sugus.codegen.plugin.syntax;

import mx.sugus.codegen.plugin.Identifier;
import mx.sugus.codegen.plugin.PluginProvider;
import mx.sugus.codegen.plugin.SmithyGeneratorPlugin;
import software.amazon.smithy.model.node.ObjectNode;

public class SyntaxPluginProvider implements PluginProvider {
    @Override
    public Identifier name() {
        return Identifier.of("mx.sugus.codegen.plugin.syntax", "SyntaxPluginProvider");
    }

    @Override
    public SmithyGeneratorPlugin build(ObjectNode config) {
        return new SyntaxPlugin(config);
    }
}
