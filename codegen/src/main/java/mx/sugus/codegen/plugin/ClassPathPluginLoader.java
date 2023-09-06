package mx.sugus.codegen.plugin;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import software.amazon.smithy.build.SmithyBuildException;
import software.amazon.smithy.model.node.ObjectNode;

public class ClassPathPluginLoader implements PluginLoader {
    @Override
    public LoadResult loadPlugins(Set<Identifier> ids, Map<Identifier, ObjectNode> pluginConfigs) {
        var plugins = new HashMap<Identifier, SmithyGeneratorPlugin>();
        for (Identifier identifier : ids) {
            SmithyGeneratorPlugin plugin = tryLoadPlugin(identifier, pluginConfigs);
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
        System.out.printf("ClassPathPluginLoader: unresolved: %s\n"
                          + "ClassPathPluginLoader: resolved: %s\n", unresolved, plugins);
        return new LoadResult(unresolved, plugins);
    }

    private SmithyGeneratorPlugin tryLoadPlugin(Identifier value, Map<Identifier, ObjectNode> pluginConfigs) {
        var className = value.namespace() + "." + value.name();
        try {
            Class<?> clazz = Class.forName(className);
            if (SmithyGeneratorPlugin.class.isAssignableFrom(clazz)) {
                return (SmithyGeneratorPlugin) clazz.getDeclaredConstructor().newInstance();
            } else if (PluginProvider.class.isAssignableFrom(clazz)) {
                var generator = (PluginProvider) clazz.getDeclaredConstructor().newInstance();

                return generator.build(pluginConfigs.get(value));
            } else {
                throw new SmithyBuildException("Unable to load plugin with class `"
                                               + className
                                               + "`, is not assignable from: "
                                               + clazz.getCanonicalName());
            }
        } catch (Exception e) {
            throw new SmithyBuildException("Unable to load plugin with class `" + className + "`: " + e.getMessage(), e);
        }
        //return null;
    }
}
