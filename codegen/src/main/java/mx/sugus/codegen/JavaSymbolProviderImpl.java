package mx.sugus.codegen;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import mx.sugus.codegen.util.Naming;
import mx.sugus.codegen.util.PathUtil;
import mx.sugus.javapoet.ClassName;
import mx.sugus.syntax.java.JavaTrait;
import software.amazon.smithy.codegen.core.ReservedWords;
import software.amazon.smithy.codegen.core.ReservedWordsBuilder;
import software.amazon.smithy.codegen.core.Symbol;
import software.amazon.smithy.codegen.core.directed.CreateSymbolProviderDirective;
import software.amazon.smithy.model.Model;
import software.amazon.smithy.model.shapes.BigDecimalShape;
import software.amazon.smithy.model.shapes.BigIntegerShape;
import software.amazon.smithy.model.shapes.BlobShape;
import software.amazon.smithy.model.shapes.BooleanShape;
import software.amazon.smithy.model.shapes.ByteShape;
import software.amazon.smithy.model.shapes.DocumentShape;
import software.amazon.smithy.model.shapes.DoubleShape;
import software.amazon.smithy.model.shapes.EnumShape;
import software.amazon.smithy.model.shapes.FloatShape;
import software.amazon.smithy.model.shapes.IntEnumShape;
import software.amazon.smithy.model.shapes.IntegerShape;
import software.amazon.smithy.model.shapes.ListShape;
import software.amazon.smithy.model.shapes.LongShape;
import software.amazon.smithy.model.shapes.MapShape;
import software.amazon.smithy.model.shapes.MemberShape;
import software.amazon.smithy.model.shapes.OperationShape;
import software.amazon.smithy.model.shapes.ResourceShape;
import software.amazon.smithy.model.shapes.ServiceShape;
import software.amazon.smithy.model.shapes.Shape;
import software.amazon.smithy.model.shapes.ShapeVisitor;
import software.amazon.smithy.model.shapes.ShortShape;
import software.amazon.smithy.model.shapes.StringShape;
import software.amazon.smithy.model.shapes.StructureShape;
import software.amazon.smithy.model.shapes.TimestampShape;
import software.amazon.smithy.model.shapes.UnionShape;
import software.amazon.smithy.model.traits.ErrorTrait;
import software.amazon.smithy.model.traits.UniqueItemsTrait;
import software.amazon.smithy.utils.StringUtils;

public class JavaSymbolProviderImpl implements JavaSymbolProvider, ShapeVisitor<Symbol> {
    public static final Symbol BOOLEAN = fromClass(Boolean.class);
    public static final Symbol BYTE = fromClass(Byte.class);
    public static final Symbol SHORT = fromClass(Short.class);
    public static final Symbol INTEGER = fromClass(Integer.class);
    public static final Symbol LONG = fromClass(Long.class);
    public static final Symbol FLOAT = fromClass(Float.class);
    public static final Symbol DOUBLE = fromClass(Double.class);
    public static final Symbol BIG_INTEGER = fromClass(BigInteger.class);
    public static final Symbol BIG_DECIMAL = fromClass(BigDecimal.class);
    public static final Symbol STRING = fromClass(String.class);

    private final JavaCodegenSettings settings;
    private final Model model;
    private final ServiceShape service;
    private final ReservedWords escaper;

    private JavaSymbolProviderImpl(Model model, JavaCodegenSettings settings) {
        this.settings = settings;
        this.model = model;
        this.service = model.expectShape(settings.service(), ServiceShape.class);
        var serviceName = settings.serviceName();
        this.escaper = new ReservedWordsBuilder()
            .loadWords(Objects.requireNonNull(JavaSymbolProviderImpl.class.getResource("java-reserved-words.txt")),
                       keyword -> StringUtils.uncapitalize(serviceName) + keyword)
            .loadWords(Objects.requireNonNull(JavaSymbolProviderImpl.class.getResource("java-system-type-names.txt")),
                       keyword -> serviceName + keyword)
            .build();
    }

    public static Symbol mapOf(Symbol key, Symbol value) {
        return builderFor(Map.class)
            .addReference(key)
            .addReference(value)
            .putProperty(SymbolConstants.AGGREGATE_TYPE, SymbolConstants.AggregateType.MAP)
            .build();
    }

    public static Symbol mapStringToV(Symbol value) {
        return builderFor(Map.class)
            .addReference(STRING)
            .addReference(value)
            .putProperty(SymbolConstants.AGGREGATE_TYPE, SymbolConstants.AggregateType.MAP)
            .build();
    }

    public static Symbol listOf(Symbol value) {
        return builderFor(List.class)
            .addReference(value)
            .putProperty(SymbolConstants.AGGREGATE_TYPE, SymbolConstants.AggregateType.LIST)
            .build();
    }

    public static Symbol setOf(Symbol value) {
        return builderFor(Set.class)
            .addReference(value)
            .putProperty(SymbolConstants.AGGREGATE_TYPE, SymbolConstants.AggregateType.SET)
            .build();
    }

    public static JavaSymbolProvider create(CreateSymbolProviderDirective<JavaCodegenSettings> directive) {
        return new JavaSymbolProviderImpl(directive.model(), directive.settings());
    }

    private static Symbol.Builder createSymbolBuilder(String typeName, String namespace) {
        return createSymbolBuilder(typeName).namespace(namespace, ".");
    }

    private static Symbol.Builder createSymbolBuilder(String typeName) {
        return Symbol.builder().name(typeName);
    }

    private static Symbol fromClass(Class<?> clazz) {
        return Symbol.builder()
                     .name(clazz.getSimpleName())
                     .namespace(clazz.getPackageName(), ".")
                     .putProperty(ClassName.class.getName(), ClassName.get(clazz))
                     .build();
    }

    private static Symbol.Builder builderFor(Class<?> clazz) {
        return Symbol.builder()
                     .name(clazz.getSimpleName())
                     .namespace(clazz.getPackageName(), ".");
    }

    @Override
    public Symbol toSymbol(Shape shape) {
        return shape.accept(this);
    }

    @Override
    public String toMemberName(MemberShape shape) {
        var container = model.expectShape(shape.getContainer());
        if (container.isEnumShape() || container.isIntEnumShape()) {
            return escaper.escape(Naming.screamCase(shape.getMemberName()));
        }
        return memberName(shape);
    }

    // --- Visitor ---

    @Override
    public Symbol blobShape(BlobShape blobShape) {
        // FIXME, verify what the AWS Java SDK does.
        // We need a supporting package.
        return fromClass(ByteBuffer.class);
    }

    @Override
    public Symbol booleanShape(BooleanShape booleanShape) {
        return BOOLEAN;
    }

    @Override
    public Symbol listShape(ListShape listShape) {
        if (listShape.hasTrait(UniqueItemsTrait.class)) {
            return setOf(listShape.getMember().accept(this));
        }
        return listOf(listShape.getMember().accept(this));
    }

    @Override
    public Symbol mapShape(MapShape mapShape) {
        return mapOf(mapShape.getKey().accept(this), mapShape.getValue().accept(this));
    }

    @Override
    public Symbol documentShape(DocumentShape documentShape) {
        return null;
    }

    @Override
    public Symbol byteShape(ByteShape byteShape) {
        return BYTE;
    }

    @Override
    public Symbol shortShape(ShortShape shortShape) {
        return SHORT;
    }

    @Override
    public Symbol integerShape(IntegerShape integerShape) {
        return INTEGER;
    }

    @Override
    public Symbol longShape(LongShape longShape) {
        return LONG;
    }

    @Override
    public Symbol floatShape(FloatShape floatShape) {
        return FLOAT;
    }

    @Override
    public Symbol doubleShape(DoubleShape doubleShape) {
        return DOUBLE;
    }

    @Override
    public Symbol bigIntegerShape(BigIntegerShape bigIntegerShape) {
        return BIG_INTEGER;
    }

    @Override
    public Symbol bigDecimalShape(BigDecimalShape bigDecimalShape) {
        return BIG_DECIMAL;
    }

    @Override
    public Symbol operationShape(OperationShape operationShape) {
        return Symbol.builder()
                     .name(shapeName(operationShape))
                     .namespace(settings.packageName(), ".")
                     .build();
    }

    @Override
    public Symbol resourceShape(ResourceShape resourceShape) {
        return null;
    }

    @Override
    public Symbol serviceShape(ServiceShape serviceShape) {
        return null;
    }

    @Override
    public Symbol stringShape(StringShape stringShape) {
        return STRING;
    }

    @Override
    public Symbol structureShape(StructureShape structureShape) {
        if (structureShape.hasTrait(JavaTrait.class)) {
            var className = structureShape.getTrait(JavaTrait.class).map(JavaTrait::getValue).orElse("");
            return SymbolConstants.fromClassName(className);
        }
        var name = shapeName(structureShape);
        var builder = Symbol.builder()
                            .name(name)
                            .namespace(settings.packageName(), ".")
                            .definitionFile(shapeClassPath(structureShape));
        if (structureShape.hasTrait(ErrorTrait.class)) {
            builder.putProperty("extends", fromClass(RuntimeException.class));
        }
        return builder.build();
    }

    @Override
    public Symbol unionShape(UnionShape shape) {
        var name = shapeName(shape);
        return createSymbolBuilder(name, settings.packageName())
            .definitionFile(shapeClassPath(shape))
            .build();
    }

    @Override
    public Symbol memberShape(MemberShape memberShape) {
        return toSymbol(model.expectShape(memberShape.getTarget()));
    }

    @Override
    public Symbol timestampShape(TimestampShape timestampShape) {
        return fromClass(Instant.class);
    }

    @Override
    public Symbol enumShape(EnumShape shape) {
        var name = shapeName(shape);
        return createSymbolBuilder(name, settings.packageName())
            .definitionFile(shapeClassPath(shape))
            .build();
    }

    @Override
    public Symbol intEnumShape(IntEnumShape shape) {
        var name = shapeName(shape);
        return createSymbolBuilder(name, settings.packageName())
            .definitionFile(shapeClassPath(shape))
            .build();
    }

    private String memberName(MemberShape shape) {
        var name = Naming.pascalCase(shape.getMemberName());
        return escaper.escape(name);
    }

    private String shapeName(Shape shape) {
        var name = StringUtils.capitalize(shape.getId().getName(service));
        return escaper.escape(name);
    }

    private String shapeClassPath(Shape shape) {
        //return PathUtil.from(PathUtil.from(settings.packageParts()), "model", shapeName(shape) + ".java");
        //TODO the package should be inside "model", how do we pass that down?
        return PathUtil.from(PathUtil.from(settings.packageParts()), shapeName(shape) + ".java");
    }
}
