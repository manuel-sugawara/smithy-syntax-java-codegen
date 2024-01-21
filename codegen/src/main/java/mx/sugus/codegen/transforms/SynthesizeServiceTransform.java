package mx.sugus.codegen.transforms;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import mx.sugus.syntax.java.CodegenIgnoreTrait;
import software.amazon.smithy.model.Model;
import software.amazon.smithy.model.SourceLocation;
import software.amazon.smithy.model.shapes.OperationShape;
import software.amazon.smithy.model.shapes.ServiceShape;
import software.amazon.smithy.model.shapes.Shape;
import software.amazon.smithy.model.shapes.ShapeId;
import software.amazon.smithy.model.shapes.ShapeVisitor;
import software.amazon.smithy.model.shapes.StructureShape;

public class SynthesizeServiceTransform {

    public static Model transform(Model model, String serviceId) {
        return new SynthesizeServiceTransform().transformModel(model, serviceId);
    }

    public Model transformModel(Model model, String serviceId) {
        var serviceShapeId = ShapeId.from(serviceId);
        if (model.getShape(serviceShapeId).isPresent()) {
            return model;
        }
        var builder = model.toBuilder();
        var roots = findRoots(model, serviceShapeId);
        var container = buildContainerShape(model, serviceShapeId, roots);
        var output = buildOutputShape(model, serviceShapeId);
        var operation = buildOperationShape(model, serviceShapeId, container.toShapeId(), output.toShapeId());
        var service = buildServiceShape(model, serviceShapeId, operation);
        builder.addShape(container);
        builder.addShape(output);
        builder.addShape(operation);
        builder.addShape(service);
        return builder.build();
    }

    private ServiceShape buildServiceShape(Model model, ShapeId serviceShapeId, OperationShape operation) {
        var builder = ServiceShape.builder();
        builder.source(SourceLocation.NONE)
               .addTrait(new CodegenIgnoreTrait())
               .id(serviceShapeId)
               .addOperation(operation.toShapeId());
        return builder.build();
    }

    private OperationShape buildOperationShape(Model model, ShapeId serviceShapeId, ShapeId input, ShapeId output) {
        var opContainerId = ShapeId.fromParts(serviceShapeId.getNamespace(), serviceShapeId.getName() + "OpContainer");
        var idx = 0;
        while (model.getShape(opContainerId).isPresent()) {
            opContainerId = ShapeId.fromParts(serviceShapeId.getNamespace(), serviceShapeId.getName() + "OpContainer" + idx);
            idx++;
        }

        return OperationShape.builder()
                             .input(input)
                             .output(output)
                             .id(opContainerId)
                             .addTrait(new CodegenIgnoreTrait())
                             .build();
    }

    private StructureShape buildContainerShape(Model model, ShapeId serviceShapeId, Set<ShapeId> roots) {
        var builder = StructureShape.builder()
                                    .addTrait(new CodegenIgnoreTrait());
        var containerId = ShapeId.fromParts(serviceShapeId.getNamespace(), serviceShapeId.getName() + "Container");
        var idx = 0;
        while (model.getShape(containerId).isPresent()) {
            containerId = ShapeId.fromParts(serviceShapeId.getNamespace(), serviceShapeId.getName() + "Container" + idx);
            idx++;
        }
        builder.id(containerId);
        var memberIdx = 0;
        for (var root : roots) {
            var memberName = "member" + memberIdx;
            builder.addMember(memberName, root);
            memberIdx += 1;
        }
        return builder.build();
    }

    private StructureShape buildOutputShape(Model model, ShapeId serviceShapeId) {
        var builder = StructureShape.builder()
                                    .addTrait(new CodegenIgnoreTrait());
        var containerId = ShapeId.fromParts(serviceShapeId.getNamespace(), serviceShapeId.getName() + "ContainerOutput");
        var idx = 0;
        while (model.getShape(containerId).isPresent()) {
            containerId = ShapeId.fromParts(serviceShapeId.getNamespace(), serviceShapeId.getName() + "ContainerOutput" + idx);
            idx++;
        }
        builder.id(containerId);
        return builder.build();
    }

    private Set<ShapeId> findRoots(Model model, ShapeId serviceShapeId) {
        var computeReferencedVisitor = new FindReferencedVisitor(model);
        Map<ShapeId, Set<ShapeId>> referenced = new HashMap<>();
        var serviceNamespace = serviceShapeId.getNamespace();
        for (var shape : model.getStructureShapes()) {
            var shapeId = shape.toShapeId();
            if (serviceNamespace.equals(shapeId.getNamespace())) {
                referenced.put(shapeId, shape.accept(computeReferencedVisitor));
            }
        }
        Map<ShapeId, Set<ShapeId>> reversed = new HashMap<>();
        for (var kvp : referenced.entrySet()) {
            var sourceId = kvp.getKey();
            for (var shapeId : kvp.getValue()) {
                reversed.computeIfAbsent(shapeId, k -> new HashSet<>()).add(sourceId);
            }
            reversed.putIfAbsent(sourceId, Collections.emptySet());
        }
        return reversed.entrySet()
                       .stream()
                       .filter(kvp -> kvp.getValue().isEmpty())
                       .map(Map.Entry::getKey)
                       .collect(Collectors.toSet());
    }

    static class FindReferencedVisitor extends ShapeVisitor.Default<Set<ShapeId>> {
        private final Model model;

        FindReferencedVisitor(Model model) {
            this.model = model;
        }

        @Override
        protected Set<ShapeId> getDefault(Shape shape) {
            return Collections.emptySet();
        }

        @Override
        public Set<ShapeId> structureShape(StructureShape shape) {
            return shape.getAllMembers().values()
                        .stream()
                        .map(s -> model.getShape(s.getTarget()).map(target -> target.accept(this)).orElse(null))
                        .filter(Objects::nonNull)
                        .flatMap(Collection::stream)
                        .collect(Collectors.toSet());
        }
    }
}
