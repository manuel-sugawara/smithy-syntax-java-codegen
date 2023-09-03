package mx.sugus.codegen.plugin;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public interface PluginLoader {

    LoadResult loadPlugins(Set<Identifier> identifiers);

    default LoadResult loadAll(Set<Identifier> identifiers) {
        var result = loadPlugins(identifiers);
        var allRequired = result.resolved.keySet()
                                         .stream()
                                         .map(result.resolved::get)
                                         .map(SmithyGeneratorPlugin::requires)
                                         .flatMap(Collection::stream)
                                         .map(x -> Identifier.of(x.packageName(), x.simpleName()))
                                         .collect(Collectors.toSet());
        while (true) {
            result = loadPlugins(allRequired);
            var newAllRequired = result.resolved.keySet()
                                                .stream()
                                                .map(result.resolved::get)
                                                .map(SmithyGeneratorPlugin::requires)
                                                .flatMap(Collection::stream)
                                                .map(x -> Identifier.of(x.packageName(), x.simpleName()))
                                                .collect(Collectors.toSet());
            if (newAllRequired.size() == allRequired.size()) {
                break;
            }
            if (!result.isFullyResolved()) {
                break;
            }
            allRequired = newAllRequired;
        }
        return result;
    }

    class LoadResult {
        private final Set<Identifier> unresolved;
        private final Map<Identifier, SmithyGeneratorPlugin> resolved;

        public LoadResult(Set<Identifier> unresolved, Map<Identifier, SmithyGeneratorPlugin> result) {
            this.unresolved = unresolved;
            this.resolved = result;
        }

        public boolean isFullyResolved() {
            return unresolved.isEmpty();
        }

        public Set<Identifier> unresolved() {
            return unresolved;
        }

        public Map<Identifier, SmithyGeneratorPlugin> resolved() {
            return resolved;
        }
    }
}