package mx.sugus.codegen.plugin;

import java.util.Objects;
import software.amazon.smithy.model.shapes.ShapeId;

public record Identifier(String namespace, String name) {

    public static Identifier of(String value) {
        ShapeId shapeId = ShapeId.from(value);
        return new Identifier(shapeId.getNamespace(), shapeId.getName());
    }

    public static Identifier of(String namespace, String simpleName) {
        return new Identifier(namespace, simpleName);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Identifier)) {
            return false;
        }
        Identifier that = (Identifier) o;
        return namespace.equals(that.namespace) && name.equals(that.name);
    }

}
