package mx.sugus.codegen;

import software.amazon.smithy.model.node.ObjectNode;
import software.amazon.smithy.model.node.StringNode;
import software.amazon.smithy.model.shapes.ShapeId;

public record JavaCodegenSettings(
    ShapeId service,
    String shortName,
    String packageName,
    String packageVersion
) {
    public static JavaCodegenSettings from(ObjectNode node) {
        return new JavaCodegenSettings(
            node.expectStringMember("service").expectShapeId(),
            node.expectStringMember("shortName").asStringNode().map(StringNode::getValue).orElse(null),
            node.expectStringMember("package").getValue(),
            node.expectStringMember("packageVersion").getValue()
        );
    }

    public String serviceName() {
        if (shortName != null) {
            return shortName;
        }
        return service.getName();
    }

    public String[] packageParts() {
        return packageName.split("\\.");
    }
}
