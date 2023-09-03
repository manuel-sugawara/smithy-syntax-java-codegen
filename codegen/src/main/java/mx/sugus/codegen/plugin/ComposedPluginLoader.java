package mx.sugus.codegen.plugin;

import java.util.HashMap;
import java.util.Set;

public class ComposedPluginLoader implements PluginLoader {
    private final PluginLoader left;
    private final PluginLoader right;

    public ComposedPluginLoader(PluginLoader left, PluginLoader right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public LoadResult loadPlugins(Set<Identifier> ids) {
        var result = left.loadPlugins(ids);
        var idToPlugin = new HashMap<>(result.resolved());
        if (result.isFullyResolved()) {
            return result;
        }
        var rightResult = right.loadPlugins(result.unresolved());

        return result;
    }
}
