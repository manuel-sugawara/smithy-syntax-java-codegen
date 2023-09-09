package mx.sugus.codegen.plugin;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import software.amazon.smithy.model.node.ObjectNode;

public interface PluginLoader {

    LoadResult loadPlugins(Set<Identifier> identifiers, Map<Identifier, ObjectNode> plugins);

    default LoadResult loadAll(Set<Identifier> identifiers, Map<Identifier, ObjectNode> pluginConfig) {
        var result = loadPlugins(identifiers, pluginConfig);
        var allRequired = result.resolved.keySet()
                                         .stream()
                                         .map(result.resolved::get)
                                         .map(SmithyGeneratorPlugin::requires)
                                         .flatMap(Collection::stream)
                                         .collect(Collectors.toSet());
        while (true) {
            System.out.printf("PREV loadPlugins:: -----------------------------\n current result: [%s]\n", result.resolved);
            result = loadPlugins(allRequired, pluginConfig).merge(result);
            System.out.printf(" NEW loadPlugins:: -----------------------------\n current result: [%s]\n", result.resolved);
            var newAllRequired = result.resolved.keySet()
                                                .stream()
                                                .map(result.resolved::get)
                                                .map(SmithyGeneratorPlugin::requires)
                                                .flatMap(Collection::stream)
                                                .collect(Collectors.toSet());
            //System.out.printf("NEW loadPlugins:: -----------------------------\n current result: [%s]", result.resolved);
            if (newAllRequired.size() == allRequired.size()) {
                break;
            }
            if (result.isFullyResolved()) {
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
            this.unresolved = new HashSet<>(unresolved);
            this.resolved = new HashMap<>(result);
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

        public LoadResult merge(LoadResult other) {
            var newResolved = new HashMap<>(resolved);
            newResolved.putAll(other.resolved);
            return new LoadResult(unresolved, newResolved);
        }
    }
}