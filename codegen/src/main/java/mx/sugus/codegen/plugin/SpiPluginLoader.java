package mx.sugus.codegen.plugin;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;
import software.amazon.smithy.model.node.ObjectNode;

public class SpiPluginLoader implements PluginLoader {

    @Override
    public LoadResult loadPlugins(Set<Identifier> ids, Map<Identifier, ObjectNode> pluginConfig) {
        var resolved = Container.LOADED;
        var unresolved = new HashSet<>(ids);
        var plugins = new HashMap<Identifier, SmithyGeneratorPlugin>();
        for (var id : ids) {
            if (resolved.containsKey(id)) {
                plugins.put(id, resolved.get(id).build(pluginConfig.get(id)));
                unresolved.remove(id);
            }
        }
        return new LoadResult(unresolved, plugins);
    }

    static class Container {
        // Saving the plugins here means that we cannot longer "dynamically"
        // load new plugins added to the classpath. For now, it seems like a
        // good compromise between loading/resolving performance and functionality.
        static Map<Identifier, PluginProvider> LOADED = loadPlugins();

        static Map<Identifier, PluginProvider> loadPlugins() {
            var plugins = new HashMap<Identifier, PluginProvider>();
            for (var plugin : ServiceLoader.load(PluginProvider.class,
                                                 SmithyGeneratorPlugin.class.getClassLoader())) {
                plugins.put(plugin.name(), plugin);
            }
            return Collections.unmodifiableMap(plugins);
        }
    }

}
