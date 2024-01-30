package mx.sugus.codegen.plugin.nodeserde;

import mx.sugus.codegen.SymbolConstants;
import mx.sugus.codegen.plugin.JavaShapeDirective;
import mx.sugus.codegen.util.PoetUtils;
import mx.sugus.javapoet.CodeBlock;
import software.amazon.smithy.model.node.ArrayNode;
import software.amazon.smithy.model.node.Node;
import software.amazon.smithy.model.node.ObjectNode;
import software.amazon.smithy.model.shapes.BooleanShape;
import software.amazon.smithy.model.shapes.ByteShape;
import software.amazon.smithy.model.shapes.DocumentShape;
import software.amazon.smithy.model.shapes.DoubleShape;
import software.amazon.smithy.model.shapes.EnumShape;
import software.amazon.smithy.model.shapes.FloatShape;
import software.amazon.smithy.model.shapes.IntegerShape;
import software.amazon.smithy.model.shapes.ListShape;
import software.amazon.smithy.model.shapes.LongShape;
import software.amazon.smithy.model.shapes.MapShape;
import software.amazon.smithy.model.shapes.MemberShape;
import software.amazon.smithy.model.shapes.Shape;
import software.amazon.smithy.model.shapes.ShapeVisitor;
import software.amazon.smithy.model.shapes.ShortShape;
import software.amazon.smithy.model.shapes.StringShape;
import software.amazon.smithy.model.shapes.StructureShape;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

class NodeWriterCodegenVisitor extends ShapeVisitor.Default<Void> {
    private final JavaShapeDirective directive;
    private final CodeBlock.Builder builder;
    private final Deque<String> nodeScope = new ArrayDeque<>();
    private final Map<String, Integer> prefixToCounter = new HashMap<>();
    private MemberShape member;
    private final int nameIndex = 0;

    NodeWriterCodegenVisitor(JavaShapeDirective directive, CodeBlock.Builder builder) {
        this.directive = directive;
        this.builder = builder;
        pushSource("this");
    }


    private ObjectNode.Builder foo() {
        return ObjectNode.builder();

    }

    private void pushSource(String source) {
        nodeScope.push(source);
    }

    private String source() {
        return nodeScope.peekFirst();
    }

    private void popSource() {
        nodeScope.pop();
    }

    private String newName(String prefix) {
        int currentValue = prefixToCounter.getOrDefault(prefix, 0);
        prefixToCounter.put(prefix, currentValue + 1);
        String sufix;
        if (prefix.isEmpty()) {
            sufix = "this";
        } else {
            sufix = "NodeBuilder";
        }
        if (currentValue == 0) {
            return prefix + sufix;
        }
        return prefix + sufix + (currentValue + 1);
    }

    @Override
    protected Void getDefault(Shape shape) {
        throw new UnsupportedOperationException();
    }

    private Void codegenUsingFromNodeValue() {
        return codegenUsingFromNodeValue("");
    }

    private Void codegenUsingFromNodeValue(String convertToNative) {
        var name = directive.symbolProvider().toMemberJavaName(member);
        if (directive.symbolProvider().isMemberNullable(member)) {
            builder.add("$1L != null ? $2T.from($1L$3L) : $2T.nullNode()", source(),  Node.class, convertToNative);
        } else {
            builder.add("$T.from($L)", Node.class, source());
        }
        return null;
    }

    @Override
    public Void booleanShape(BooleanShape shape) {
        return codegenUsingFromNodeValue();
    }

    @Override
    public Void byteShape(ByteShape shape) {
        return codegenUsingFromNodeValue();
    }

    @Override
    public Void shortShape(ShortShape shape) {
        return codegenUsingFromNodeValue();

    }

    @Override
    public Void integerShape(IntegerShape shape) {
        return codegenUsingFromNodeValue();
    }

    @Override
    public Void longShape(LongShape shape) {
        return codegenUsingFromNodeValue();
    }

    @Override
    public Void floatShape(FloatShape shape) {
        return codegenUsingFromNodeValue();
    }

    @Override
    public Void doubleShape(DoubleShape shape) {
        return codegenUsingFromNodeValue();
    }

    @Override
    public Void stringShape(StringShape shape) {
        return codegenUsingFromNodeValue();
    }

    @Override
    public Void enumShape(EnumShape shape) {
        return codegenUsingFromNodeValue(".toString()");
    }

    @Override
    public Void listShape(ListShape shape) {
        var symbol = directive.symbolProvider().toSymbol(member);
        var name = directive.symbolProvider().toMemberJavaName(member);
        var builderName = name + "NodeBuilder";
        builder.addStatement("$1T.Builder $2L = $1T.builder()", ArrayNode.class, builderName);
        var itemName = newName("item");
        builder.beginControlFlow("for ($T $L : $L)", SymbolConstants.typeParam(symbol), itemName, source());
        builder.add("$L.withValue(", builderName);
        var target = directive.model().expectShape(shape.getMember().getTarget());
        pushSource(itemName);
        target.accept(this);
        popSource();
        builder.add(")");
        builder.addStatement("");
        builder.endControlFlow();
        builder.addStatement("builder.withMember($S, $L.build())", member.getMemberName(), builderName);
        return null;
    }

    @Override
    public Void mapShape(MapShape shape) {
        var name = directive.symbolProvider().toMemberJavaName(member);
        var builderName = name + "NodeBuilder";
        builder.addStatement("$1T.Builder $2L = $1T.builder()", ObjectNode.class, builderName);
        var itemName = newName("item");
        var valueShapeId = directive.symbolProvider().toTypeName(shape.getValue());
        builder.beginControlFlow("for ($T<String, $T> $L : $L.entrySet())",
                Map.Entry.class, valueShapeId,
                itemName, source());
        builder.add("$L.withMember($L.getKey(), ", builderName, itemName);
        var target = directive.model().expectShape(shape.getValue().getTarget());
        pushSource(itemName + ".getValue()");
        target.accept(this);
        popSource();
        builder.add(")");
        builder.addStatement("");
        builder.endControlFlow();
        builder.addStatement("builder.withMember($S, $L.build())", member.getMemberName(), builderName);
        return null;
    }

    @Override
    public Void structureShape(StructureShape shape) {
        var symbolProvider = directive.symbolProvider();
        var shapeSymbol = symbolProvider.toSymbol(shape);
        PoetUtils.toClassName(shapeSymbol);

        builder.add("$1L != null ? $1L.toNode() : $2T.nullNode()", source(), Node.class);
        return null;
    }


    @Override
    public Void memberShape(MemberShape shape) {
        this.member = shape;
        var symbol = directive.symbolProvider().toSymbol(shape);
        var targetId = shape.getTarget();
        var target = directive.model().expectShape(targetId);
        var aggregateType = SymbolConstants.aggregateType(symbol);
        if (aggregateType == SymbolConstants.AggregateType.NONE) {
            var name = directive.symbolProvider().toMemberName(shape);
            builder.add("builder.withMember($S, ", name);
        }
        String source = source();
        String javaName = directive.symbolProvider().toMemberName(shape);
        pushSource(source + "." + javaName + "()");
        target.accept(this);
        popSource();
        if (aggregateType == SymbolConstants.AggregateType.NONE) {
            builder.addStatement(")");
        }
        return null;
    }

    @Override
    public Void documentShape(DocumentShape shape) {
        return null;
    }
}
