package mx.sugus.codegen.plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import software.amazon.smithy.model.node.ObjectNode;

public class ComposedPluginLoader implements PluginLoader {
    private final PluginLoader left;
    private final PluginLoader right;

    public ComposedPluginLoader(PluginLoader left, PluginLoader right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public LoadResult loadPlugins(Set<Identifier> ids, Map<Identifier, ObjectNode> pluginConfig) {
        var leftResult = left.loadPlugins(ids, pluginConfig);
        if (leftResult.isFullyResolved()) {
            return leftResult;
        }
        var rightResult = right.loadPlugins(leftResult.unresolved(), pluginConfig);
        var plugins = new HashMap<Identifier, SmithyGeneratorPlugin>(leftResult.resolved());
        plugins.putAll(rightResult.resolved());
        return new LoadResult(rightResult.unresolved(), plugins);
    }
}
