package mx.sugus.codegen.plugin.data;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import software.amazon.smithy.model.Model;
import software.amazon.smithy.model.neighbor.Relationship;
import software.amazon.smithy.model.neighbor.RelationshipDirection;
import software.amazon.smithy.model.neighbor.Walker;
import software.amazon.smithy.model.shapes.MemberShape;
import software.amazon.smithy.model.shapes.ShapeId;
import software.amazon.smithy.model.shapes.ShapeType;
import software.amazon.smithy.model.shapes.StructureShape;

public class Something {
    private final String namespace;

    public Something(String namespace) {
        this.namespace = Objects.requireNonNull(namespace, "namespace");
    }

    public Model transform(Model model) {
        var roots = findRoots(model);
        return model.toBuilder()
            .addShape(createContainer(model, roots))
            .build();
    }

    public Set<ShapeId> findRoots(Model model) {
        var allRoots = new HashSet<ShapeId>();
        for (var shape : model.toSet()) {
            var type = shape.getType();
            if (type == ShapeType.STRUCTURE || type == ShapeType.ENUM || type == ShapeType.INT_ENUM || type == ShapeType.UNION) {
                allRoots.add(shape.getId());
            }
        }
        Predicate<Relationship> filter = rel -> rel.getRelationshipType().getDirection() == RelationshipDirection.DIRECTED;
        var graph = new HashMap<ShapeId, Set<ShapeId>>();
        var dequeue = new ArrayDeque<ShapeId>();
        dequeue.addAll(allRoots);
        var included = new HashSet<ShapeId>();
        var walker = new Walker(model);
        while (!dequeue.isEmpty()) {
            var shapeId = dequeue.removeFirst();
            var neighbors = graph.computeIfAbsent(shapeId, (x) -> new HashSet<>());
            for (var related : walker.walkShapes(model.expectShape(shapeId), filter)) {
                var relatedId = related.getId();
                if (relatedId.equals(shapeId)) {
                    continue;
                }
                neighbors.add(relatedId);
                if (!included.contains(relatedId)) {
                    included.add(relatedId);
                    dequeue.addLast(relatedId);
                }
            }
        }
        var inverse = new HashMap<ShapeId, Set<ShapeId>>();
        graph.forEach((k, v) -> {
            inverse.computeIfAbsent(k, (x) -> new HashSet<>());
            for (var shapeId : v) {
                inverse.computeIfAbsent(shapeId, (x) -> new HashSet<>())
                       .add(k);
            }
        });
        return inverse.entrySet().stream().filter(kvp -> kvp.getValue().isEmpty()).map(Map.Entry::getKey)
                      .collect(Collectors.toSet());
    }

    private StructureShape createContainer(Model model, Set<ShapeId> roots) {
        var containerId = ShapeId.from(namespace + "#Container123");
        var builder = StructureShape.builder()
                                    .id(containerId);
        var idx = 0;
        for (var shapeId : roots) {
            idx++;
            model.expectShape(shapeId);
            builder.addMember(MemberShape.builder()
                                         .id(containerId.withMember("member" + idx))
                                         .target(shapeId)
                                         .build());
        }
        return builder.build();
    }
}
