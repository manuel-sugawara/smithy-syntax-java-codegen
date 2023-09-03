package mx.sugus.codegen.plugin;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class ClassPathPluginLoader implements PluginLoader {
    @Override
    public LoadResult loadPlugins(Set<Identifier> ids) {
        var plugins = new HashMap<Identifier, SmithyGeneratorPlugin>();
        for (Identifier identifier : ids) {
            SmithyGeneratorPlugin plugin = tryLoadPlugin(identifier);
            if (plugin != null) {
                var name = plugin.name();
                plugins.put(Identifier.of(name.packageName(), name.simpleName()), plugin);
            }
        }
        var unresolved = new HashSet<>(ids);
        for (var id : ids) {
            if (plugins.containsKey(id)) {
                unresolved.remove(id);
            }
        }
        return new LoadResult(unresolved, plugins);
    }

    private SmithyGeneratorPlugin tryLoadPlugin(Identifier value) {
        try {
            Class<?> clazz = Class.forName(value.namespace() + "." + value.name());
            if (SmithyGeneratorPlugin.class.isAssignableFrom(clazz)) {
                return (SmithyGeneratorPlugin) clazz.getDeclaredConstructor().newInstance();
            }
        } catch (Exception e) {

        }
        return null;
    }
}
