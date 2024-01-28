package mx.sugus.codegen.transforms;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import mx.sugus.syntax.java.InterfaceTrait;
import mx.sugus.syntax.java.IsaTrait;
import software.amazon.smithy.model.Model;
import software.amazon.smithy.model.shapes.Shape;
import software.amazon.smithy.model.shapes.ShapeId;
import software.amazon.smithy.model.shapes.StructureShape;
import software.amazon.smithy.model.traits.StringTrait;
import software.amazon.smithy.model.transform.ModelTransformer;

public class FlattenInterfaceMembers {

    public static Model transform(Model model) {
        return new FlattenInterfaceMembers().transform(ModelTransformer.create(), model);
    }

    public Model transform(ModelTransformer transformer, Model model) {
        Set<Shape> replacements = findReplacements(model);
        if (replacements.isEmpty()) {
            return model;
        }
        return transformer.replaceShapes(model, replacements);
    }

    private Set<Shape> findReplacements(Model model) {
        Set<ShapeId> appliedTraits = model.getAppliedTraits();
        if (!appliedTraits.contains(IsaTrait.ID) || !appliedTraits.contains(InterfaceTrait.ID)) {
            return Collections.emptySet();
        }
        Set<StructureShape> shapesIsaAnnotated = new TreeSet<>();
        Set<StructureShape> shapesInterfaceAnnotated = new TreeSet<>();
        for (StructureShape shape : model.getStructureShapes()) {
            if (shape.hasTrait(IsaTrait.class)) {
                shapesIsaAnnotated.add(shape);
            }
            if (shape.hasTrait(InterfaceTrait.class)) {
                // We only record the structure if it has members
                if (!shape.members().isEmpty()) {
                    shapesInterfaceAnnotated.add(shape);
                }
            }
        }
        if (shapesInterfaceAnnotated.isEmpty()) {
            return Collections.emptySet();
        }
        Map<ShapeId, List<StructureShape>> isaValueToStructures =
            shapesIsaAnnotated.stream()
                              .filter(s -> extractIsa(s) != null)
                              .collect(Collectors.groupingBy(this::extractIsa));

        if (isaValueToStructures.isEmpty()) {
            return Collections.emptySet();
        }
        Set<Shape> result = new TreeSet<>();
        for (var kvp : isaValueToStructures.entrySet()) {
            var shapeId = kvp.getKey();
            var parentShape = findStructureShape(model, shapeId);
            if (parentShape == null) {
                continue;
            }
            if (!shapesInterfaceAnnotated.contains(parentShape)) {
                continue;
            }

            kvp.getValue()
               .stream()
               .map(x -> mergeParentFields(parentShape, x))
               .filter(Objects::nonNull)
               .forEach(result::add);
        }

        return result;
    }

    private StructureShape findStructureShape(Model model, ShapeId shapeId) {
        return model.getShape(shapeId)
                    .map(shape -> shape.asStructureShape()
                                       .orElse(null))
                    .orElse(null);

    }

    private StructureShape mergeParentFields(StructureShape parent, StructureShape child) {
        var missing = new HashSet<>(parent.getMemberNames());
        for (var childMemberName : child.getMemberNames()) {
            missing.remove(childMemberName);
        }
        if (missing.isEmpty()) {
            return null;
        }
        var builder = child.toBuilder();
        for (var fieldName : missing) {
            var parentField = parent.getMember(fieldName).orElseThrow();
            builder.addMember(parentField
                                  .toBuilder()
                                  .id(child.toShapeId().withMember(fieldName))
                                  .build());
        }
        return builder.build();
    }

    private ShapeId extractIsa(StructureShape shape) {
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
}
