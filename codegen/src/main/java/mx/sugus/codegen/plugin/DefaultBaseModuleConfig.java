package mx.sugus.codegen.plugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import mx.sugus.codegen.util.PoetUtils;
import software.amazon.smithy.model.node.ObjectNode;

public final class DefaultBaseModuleConfig {

    private DefaultBaseModuleConfig() {
    }

    public static BaseModuleConfig newBaseConfig() {
        return BaseModuleConfig
            .builder()
            .addSerializer(
                ShapeSerializer
                    .builder(TypeSpecResult.class)
                    .identifier(Identifier.of("mx.sugus.codegen.plugin", "TypeSpecResult"))
                    .handler(DefaultBaseModuleConfig::serializeType)
                    .build())
            .build();
    }


    public static void serializeType(JavaShapeDirective directive, TypeSpecResult result) {
        var ns = directive.settings().packageName();
        if (result.namespace() != null) {
            ns = ns + "." +
                 result.namespace();
        }
        var namespace = ns;
        if (result.spec() != null) {
            var file = ns.replace(".", "/") + "/" + result.spec().name + ".java";
            directive.context()
                     .writerDelegator()
                     .useFileWriter(file, w -> PoetUtils.emit(w, result.spec(), namespace));
        }
    }

    public static BaseModuleConfig buildDependants(PluginLoader loader, ObjectNode basePluginConfig) {
        var pluginsEnabled = pluginsEnabled(basePluginConfig);
        var pluginsLoaded = loader.loadAll(pluginsEnabled.keySet(), pluginsEnabled);
        if (!pluginsLoaded.isFullyResolved()) {
            throw new RuntimeException("unresolved plugins: " + pluginsLoaded.unresolved());
        }
        var config = newBaseConfig();
        var pluginsResolved = sortPlugins(pluginsEnabled, pluginsLoaded.resolved());
        for (var plugin : pluginsResolved) {
            config = plugin.merge(config);
        }
        return config;
    }

    private static List<SmithyGeneratorPlugin> sortPlugins(
        Map<Identifier, ObjectNode> pluginsEnabled,
        Map<Identifier, SmithyGeneratorPlugin> pluginsLoaded
    ) {
        // Adapted from https://keithschwarz.com/interesting/code/?dir=topological-sort
        Graph graph = buildGraph(pluginsEnabled, pluginsLoaded);

        /* Maintain two structures - a set of visited nodes (so that once we've
         * added a node to the list, we don't label it again), and a list of
         * nodes that actually holds the topological ordering.
         */
        List<Identifier> result = new ArrayList<>();
        Set<Identifier> visited = new HashSet<>();

        /* We'll also maintain a third set consisting of all nodes that have
         * been fully expanded.  If the graph contains a cycle, then we can
         * detect this by noting that a node has been explored but not fully
         * expanded.
         */
        Set<Identifier> expanded = new HashSet<>();

        /* Fire off a DFS from each node in the graph. */
        graph.outGraph.keySet().stream().sorted(Comparator.comparing(Identifier::toString))
                      .forEach(node -> {
                          explore(node, graph, result, visited, expanded);
                      });
        /* Hand back the resulting ordering. */
        return result.stream().map(pluginsLoaded::get).collect(Collectors.toList());
    }

    public static void explore(Identifier node, Graph graph, List<Identifier> ordering, Set<Identifier> visited,
                               Set<Identifier> expanded) {
        /* Check whether we've been here before.  If so, we should stop the
         * search.
         */
        if (visited.contains(node)) {
            /* There are two cases to consider.  First, if this node has
             * already been expanded, then it's already been assigned a
             * position in the final topological sort, and we don't need to
             * explore it again.  However, if it hasn't been expanded, it means
             * that we've just found a node that is currently being explored,
             * and therefore is part of a cycle.  In that case, we should
             * report an error.
             */
            if (expanded.contains(node)) {
                return;
            }
            throw new IllegalArgumentException("Graph contains a cycle.");
        }
        /* Mark that we've been here */
        visited.add(node);
        /* Recursively explore all the node's predecessors. */
        for (var predecessor : graph.outGraph.getOrDefault(node, Collections.emptySet())) {
            explore(predecessor, graph, ordering, visited, expanded);
        }
        /* Having explored all the node's predecessors, we can now add this
         * node to the sorted ordering.
         */
        ordering.add(node);
        /* Similarly, mark that this node is done being expanded. */
        expanded.add(node);
    }

    private static Graph buildGraph(
        Map<Identifier, ObjectNode> pluginsEnabled,
        Map<Identifier, SmithyGeneratorPlugin> pluginsLoaded
    ) {
        var allPlugins = pluginsEnabled.keySet()
                                       .stream()
                                       .map(pluginsLoaded::get)
                                       .filter(Objects::nonNull)
                                       .map(SmithyGeneratorPlugin::requires)
                                       .flatMap(Collection::stream)
                                       .collect(Collectors.toSet());
        allPlugins.addAll(pluginsEnabled.keySet());
        while (true) {
            var newAllPlugins = allPlugins
                .stream()
                .map(pluginsLoaded::get)
                .filter(Objects::nonNull)
                .map(SmithyGeneratorPlugin::requires)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
            newAllPlugins.addAll(allPlugins);
            if (newAllPlugins.size() == allPlugins.size()) {
                break;
            }
            allPlugins = newAllPlugins;
        }
        Map<Identifier, Set<Identifier>> inverseGraph = new HashMap<>();
        for (var pluginId : allPlugins) {
            var plugin = pluginsLoaded.get(pluginId);
            if (plugin == null) {
                throw new RuntimeException("Cannot find the plugin with id: " + pluginId);
            }
            Set<Identifier> requires = plugin.requires()
                                             .stream()
                                             .collect(Collectors.toSet());
            inverseGraph.computeIfAbsent(pluginId, (x) -> new HashSet<>())
                        .addAll(requires);
        }
        Map<Identifier, Set<Identifier>> graph = new HashMap<>();
        inverseGraph.forEach((k, v) -> {
            if (v.isEmpty()) {
                graph.computeIfAbsent(k, (x) -> new HashSet<>());
            }
            for (var vertex : v) {
                graph.computeIfAbsent(vertex, (x) -> new HashSet<>())
                     .add(k);
            }
        });
        System.out.printf("=======>> inverse graph: %s\n=======>>        graph: %s\n", graph, inverseGraph);
        return new Graph(graph, inverseGraph);
    }

    static Map<Identifier, ObjectNode> pluginsEnabled(ObjectNode node) {
        var pluginsNode = node.getObjectMember("plugins").orElse(null);
        if (pluginsNode != null) {
            var pluginsEnabled = new HashMap<Identifier, ObjectNode>();
            pluginsNode.getMembers().forEach((k, v) -> {
                pluginsEnabled.put(Identifier.of(k.getValue()), v.expectObjectNode());
            });
            return pluginsEnabled;
        }
        return Collections.emptyMap();
    }

    record Graph(Map<Identifier, Set<Identifier>> outGraph, Map<Identifier, Set<Identifier>> inGraph) {

    }

}
