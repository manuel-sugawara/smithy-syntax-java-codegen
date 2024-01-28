package mx.sugus.codegen;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import mx.sugus.syntax.java.InterfaceTrait;
import mx.sugus.syntax.java.IsaTrait;
import software.amazon.smithy.model.Model;
import software.amazon.smithy.model.knowledge.KnowledgeIndex;
import software.amazon.smithy.model.shapes.Shape;
import software.amazon.smithy.model.shapes.ShapeId;
import software.amazon.smithy.model.shapes.StructureShape;
import software.amazon.smithy.model.shapes.ToShapeId;
import software.amazon.smithy.model.traits.StringTrait;

public class IsaKnowledgeIndex implements KnowledgeIndex {

    private final Map<ShapeId, StructureShape> shapeIdToParent;

    IsaKnowledgeIndex(Model model) {
        shapeIdToParent = shapeIdToParent(model);
    }

    public static IsaKnowledgeIndex of(Model model) {
        return model.getKnowledge(IsaKnowledgeIndex.class, IsaKnowledgeIndex::new);
    }

    private static Map<ShapeId, StructureShape> shapeIdToParent(Model model) {
        Set<ShapeId> appliedTraits = model.getAppliedTraits();
        if (!appliedTraits.contains(IsaTrait.ID) || !appliedTraits.contains(InterfaceTrait.ID)) {
            return Collections.emptyMap();
        }
        Set<StructureShape> shapesIsaAnnotated = new TreeSet<>();
        Set<StructureShape> shapesInterfaceAnnotated = new TreeSet<>();
        for (StructureShape shape : model.getStructureShapes()) {
            if (shape.hasTrait(IsaTrait.class)) {
                shapesIsaAnnotated.add(shape);
            }
            if (shape.hasTrait(InterfaceTrait.class)) {
                shapesInterfaceAnnotated.add(shape);
            }
        }
        if (shapesInterfaceAnnotated.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<ShapeId, ShapeId> relations = shapesIsaAnnotated.stream()
                                                            .filter(s -> extractIsa(s) != null)
                                                            .collect(Collectors.toMap(Shape::toShapeId,
                                                                                      IsaKnowledgeIndex::extractIsa));

        if (relations.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<ShapeId, StructureShape> result = new HashMap<>();
        for (var kvp : relations.entrySet()) {
            var shapeId = kvp.getKey();
            var parentShape = findStructureShape(model, kvp.getValue());
            if (parentShape == null) {
                continue;
            }
            if (!shapesInterfaceAnnotated.contains(parentShape)) {
                continue;
            }

            result.put(shapeId, parentShape);
        }
        return result;
    }

    private static StructureShape findStructureShape(Model model, ShapeId shapeId) {
        return model.getShape(shapeId)
                    .map(shape -> shape.asStructureShape()
                                       .orElse(null))
                    .orElse(null);

    }

    private static ShapeId extractIsa(StructureShape shape) {
        String shapeId = shape.getTrait(IsaTrait.class).map(StringTrait::getValue).orElse(null);
        if (shapeId == null) {
            return null;
        }
        try {
            return ShapeId.from(shapeId);
        } catch (RuntimeException e) {
            return null;
        }
    }

    public StructureShape parent(ToShapeId toShapeId) {
        return shapeIdToParent.get(toShapeId.toShapeId());
    }


}
