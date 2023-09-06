package mx.sugus.codegen.plugin;

import mx.sugus.javapoet.ClassName;
import software.amazon.smithy.model.node.ObjectNode;

public interface PluginProvider {
    Identifier name();
    SmithyGeneratorPlugin build(ObjectNode config);
}
