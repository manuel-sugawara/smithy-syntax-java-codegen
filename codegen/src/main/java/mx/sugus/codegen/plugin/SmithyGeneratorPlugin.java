package mx.sugus.codegen.plugin;

import java.util.Collection;
import mx.sugus.javapoet.ClassName;
import software.amazon.smithy.model.node.ObjectNode;

public interface SmithyGeneratorPlugin {
    Identifier name();

    Collection<Identifier> requires();

    BaseModuleConfig merge(BaseModuleConfig config);

    BaseModuleConfig merge(ObjectNode node, BaseModuleConfig config);
}
