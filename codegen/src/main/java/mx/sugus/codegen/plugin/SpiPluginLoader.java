package mx.sugus.codegen.plugin;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;

public class SpiPluginLoader implements PluginLoader {

    @Override
    public LoadResult loadPlugins(Set<Identifier> ids) {
        var plugins = Container.LOADED;
        var unresolved = new HashSet<>(ids);
        for (var id : ids) {
            if (plugins.containsKey(id)) {
                unresolved.remove(id);
            }
        }
        return new LoadResult(unresolved, plugins);
    }

    static class Container {
        // Saving the plugins here means that we cannot longer "dynamically"
        // load new plugins added to the classpath. For now, it seems like a
        // good compromise between loading/resolving performance and functionality.
        static Map<Identifier, SmithyGeneratorPlugin> LOADED = loadPlugins();

        static Map<Identifier, SmithyGeneratorPlugin> loadPlugins() {
            var plugins = new HashMap<Identifier, SmithyGeneratorPlugin>();
            for (var plugin : ServiceLoader.load(SmithyGeneratorPlugin.class, SmithyGeneratorPlugin.class.getClassLoader())) {
                var name = plugin.name();
                plugins.put(Identifier.of(name.packageName(), name.simpleName()), plugin);
            }
            return Collections.unmodifiableMap(plugins);
        }
    }

}
