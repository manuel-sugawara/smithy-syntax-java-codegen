package mx.sugus.codegen.plugin.syntax;

import java.util.Collection;
import java.util.List;
import mx.sugus.codegen.plugin.BaseModuleConfig;
import mx.sugus.codegen.plugin.Identifier;
import mx.sugus.codegen.plugin.SmithyGeneratorPlugin;
import mx.sugus.codegen.plugin.data.DataPluginProvider;
import mx.sugus.codegen.plugin.data.StructureGeneratorData;
import software.amazon.smithy.model.node.ObjectNode;

public class SyntaxPlugin implements SmithyGeneratorPlugin {
    private final ObjectNode config;
    // 🙀  the value is hardcoded here 🙀
    private final String syntaxNode = "mx.sugus.syntax.java#SyntaxNode";

    public SyntaxPlugin(ObjectNode node) {
        this.config = node;
    }

    BaseModuleConfig.Builder newBaseConfig() {
        return BaseModuleConfig
            .builder()
            .addInit(new GenerateVisitor(syntaxNode))
            .addInit(new GenerateRewriteVisitor(syntaxNode))
            .putInterceptor(StructureGeneratorData.ID,
                            new SyntaxInterceptor(syntaxNode));
    }

    @Override
    public Identifier name() {
        return SyntaxPluginProvider.ID;
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
