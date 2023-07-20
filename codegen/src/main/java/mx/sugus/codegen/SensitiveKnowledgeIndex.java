package mx.sugus.codegen;

import java.util.HashSet;
import java.util.Set;
import software.amazon.smithy.model.Model;
import software.amazon.smithy.model.knowledge.KnowledgeIndex;
import software.amazon.smithy.model.shapes.BigDecimalShape;
import software.amazon.smithy.model.shapes.BigIntegerShape;
import software.amazon.smithy.model.shapes.BlobShape;
import software.amazon.smithy.model.shapes.BooleanShape;
import software.amazon.smithy.model.shapes.ByteShape;
import software.amazon.smithy.model.shapes.DocumentShape;
import software.amazon.smithy.model.shapes.DoubleShape;
import software.amazon.smithy.model.shapes.FloatShape;
import software.amazon.smithy.model.shapes.IntegerShape;
import software.amazon.smithy.model.shapes.ListShape;
import software.amazon.smithy.model.shapes.LongShape;
import software.amazon.smithy.model.shapes.MapShape;
import software.amazon.smithy.model.shapes.MemberShape;
import software.amazon.smithy.model.shapes.OperationShape;
import software.amazon.smithy.model.shapes.ResourceShape;
import software.amazon.smithy.model.shapes.ServiceShape;
import software.amazon.smithy.model.shapes.Shape;
import software.amazon.smithy.model.shapes.ShapeId;
import software.amazon.smithy.model.shapes.ShapeVisitor;
import software.amazon.smithy.model.shapes.ShortShape;
import software.amazon.smithy.model.shapes.StringShape;
import software.amazon.smithy.model.shapes.StructureShape;
import software.amazon.smithy.model.shapes.TimestampShape;
import software.amazon.smithy.model.shapes.ToShapeId;
import software.amazon.smithy.model.shapes.UnionShape;
import software.amazon.smithy.model.traits.SensitiveTrait;

public class SensitiveKnowledgeIndex implements KnowledgeIndex {

    private final Set<ShapeId> sensitiveShapes = new HashSet<>();
    SensitiveKnowledgeIndex(Model model) {
        var visitor = new ComputeSensitive(model);
        for (var shapeId : model.getShapeIds()) {
            var result = model.expectShape(shapeId).accept(visitor);
            if (result) {
                sensitiveShapes.add(shapeId);
            }
        }
    }

    public static SensitiveKnowledgeIndex of(Model model) {
        return model.getKnowledge(SensitiveKnowledgeIndex.class, SensitiveKnowledgeIndex::new);
    }

    public boolean isSensitive(ToShapeId toShapeId) {
        return sensitiveShapes.contains(toShapeId.toShapeId());
    }

    static class ComputeSensitive implements ShapeVisitor<Boolean> {
        private final Model model;

        ComputeSensitive(Model model) {
            this.model = model;
        }

        private static boolean hasSensitiveTrait(Shape shape) {
            return shape.hasTrait(SensitiveTrait.class);
        }
        @Override
        public Boolean blobShape(BlobShape shape) {
            return hasSensitiveTrait(shape);
        }

        @Override
        public Boolean booleanShape(BooleanShape shape) {
            return hasSensitiveTrait(shape);
        }

        @Override
        public Boolean listShape(ListShape shape) {
            return hasSensitiveTrait(shape) || shape.getMember().accept(this);
        }

        @Override
        public Boolean mapShape(MapShape shape) {
            return hasSensitiveTrait(shape)
                   || shape.getKey().accept(this)
                   || shape.getValue().accept(this);
        }

        @Override
        public Boolean byteShape(ByteShape shape) {
            return hasSensitiveTrait(shape);
        }

        @Override
        public Boolean shortShape(ShortShape shape) {
            return hasSensitiveTrait(shape);
        }

        @Override
        public Boolean integerShape(IntegerShape shape) {
            return hasSensitiveTrait(shape);
        }

        @Override
        public Boolean longShape(LongShape shape) {
            return hasSensitiveTrait(shape);
        }

        @Override
        public Boolean floatShape(FloatShape shape) {
            return hasSensitiveTrait(shape);
        }

        @Override
        public Boolean documentShape(DocumentShape shape) {
            return hasSensitiveTrait(shape);
        }

        @Override
        public Boolean doubleShape(DoubleShape shape) {
            return hasSensitiveTrait(shape);
        }

        @Override
        public Boolean bigIntegerShape(BigIntegerShape shape) {
            return hasSensitiveTrait(shape);
        }

        @Override
        public Boolean bigDecimalShape(BigDecimalShape shape) {
            return hasSensitiveTrait(shape);
        }

        @Override
        public Boolean operationShape(OperationShape shape) {
            return false;
        }

        @Override
        public Boolean resourceShape(ResourceShape shape) {
            return false;
        }

        @Override
        public Boolean serviceShape(ServiceShape shape) {
            return false;
        }

        @Override
        public Boolean stringShape(StringShape shape) {
            return hasSensitiveTrait(shape);
        }

        @Override
        public Boolean structureShape(StructureShape shape) {
            // We stop at the member level.
            return false;
        }

        @Override
        public Boolean unionShape(UnionShape shape) {
            // We stop at the member level.
            return false;
        }

        @Override
        public Boolean memberShape(MemberShape shape) {
            return hasSensitiveTrait(shape) || model.expectShape(shape.getTarget()).accept(this);
        }

        @Override
        public Boolean timestampShape(TimestampShape shape) {
            return hasSensitiveTrait(shape);
        }
    }
}
