package mx.sugus.codegen.plugin.nodeserde;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.lang.model.element.Modifier;
import mx.sugus.codegen.SymbolConstants;
import mx.sugus.codegen.plugin.AbstractShapeTask;
import mx.sugus.codegen.plugin.JavaShapeDirective;
import mx.sugus.codegen.plugin.TypeSpecResult;
import mx.sugus.codegen.util.PoetUtils;
import mx.sugus.javapoet.CodeBlock;
import mx.sugus.javapoet.MethodSpec;
import mx.sugus.syntax.java.ConstTrait;
import mx.sugus.syntax.java.InterfaceTrait;
import software.amazon.smithy.model.node.Node;
import software.amazon.smithy.model.node.ObjectNode;
import software.amazon.smithy.model.node.StringNode;
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
import software.amazon.smithy.model.shapes.ShapeType;
import software.amazon.smithy.model.shapes.ShapeVisitor;
import software.amazon.smithy.model.shapes.ShortShape;
import software.amazon.smithy.model.shapes.StringShape;
import software.amazon.smithy.model.shapes.StructureShape;

public class NodeSerdeInterceptor extends AbstractShapeTask<TypeSpecResult> {


    NodeSerdeInterceptor() {
        super(TypeSpecResult.class, ShapeType.STRUCTURE);
    }


    @Override
    public TypeSpecResult transform(JavaShapeDirective directive, TypeSpecResult result) {
        var method = toNodeMethod(directive);
        if (directive.shape().hasTrait(InterfaceTrait.class)) {
            var spec = result.spec().toBuilder()
                             .addMethod(method.toBuilder().addModifiers(Modifier.ABSTRACT).build())
                             .build();
            return result.toBuilder()
                         .spec(spec)
                         .build();
        } else {
            var spec = result.spec().toBuilder()
                             .addMethod(method)
                             .addMethod(fromNodeMethod(directive))
                             .build();
            return result.toBuilder()
                         .spec(spec)
                         .build();
        }
    }

    private MethodSpec toNodeMethod(JavaShapeDirective directive) {
        var builder = MethodSpec.methodBuilder("toNode")
                                .addModifiers(Modifier.PUBLIC)
                                .returns(Node.class);
        builder.addStatement("return null");
        return builder.build();
    }

    private void toNodeMethodAddEmitMember(JavaShapeDirective directive, MemberShape member, MethodSpec.Builder builder) {

    }

    private MethodSpec fromNodeMethod(JavaShapeDirective directive) {
        var className = PoetUtils.toClassName(directive.symbol());
        var builder = MethodSpec.methodBuilder("fromNode")
                                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                                .addParameter(Node.class, "node")
                                .returns(className);
        builder.addStatement("$T.Builder builder = builder()", className);
        builder.addStatement("$T objectNode = node.expectObjectNode()", ObjectNode.class);
        for (var member : directive.shape().members()) {
            if (member.hasTrait(ConstTrait.class)) {
                continue;
            }
            var codeBlockBuilder = CodeBlock.builder();
            var renderer = new NodeReaderCodegenVisitor(directive, codeBlockBuilder);
            member.accept(renderer);
            builder.addStatement(codeBlockBuilder.build());
        }

        builder.addStatement("return builder.build()");

        return builder.build();
    }

    private class NodeReaderCodegenVisitor extends ShapeVisitor.Default<Void> {
        private final JavaShapeDirective directive;
        private final CodeBlock.Builder builder;
        private final Deque<String> nodeScope = new ArrayDeque<>();
        private final Map<String, Integer> prefixToCounter = new HashMap<>();
        private MemberShape member;
        private int nameIndex = 0;

        private NodeReaderCodegenVisitor(JavaShapeDirective directive, CodeBlock.Builder builder) {
            this.directive = directive;
            this.builder = builder;
            pushSource("objectNode");
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
                sufix = "node";
            } else {
                sufix = "Node";
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

        @Override
        public Void booleanShape(BooleanShape shape) {
            builder.add("$L.expectBooleanNode()", source());
            return null;
        }

        @Override
        public Void listShape(ListShape shape) {
            var name = directive.symbolProvider().toMemberJavaName(member);
            builder.add("$L.getArrayMember($S, arrayNodes -> {", source(), name);
            builder.add("\n$>");
            builder.add("for (Node node : arrayNodes) {");
            builder.add("\n$>");
            builder.add("builder.add$L(", name.toSingularSpelling().asCamelCase());
            var target = directive.model().expectShape(shape.getMember().getTarget());
            pushSource("node");
            target.accept(this);
            popSource();
            builder.add(");");
            builder.add("$<\n");
            builder.add("}");
            builder.add("$<");
            builder.add("})");
            return null;
        }

        @Override
        public Void mapShape(MapShape shape) {
            var name = directive.symbolProvider().toMemberJavaName(member);
            builder.add("$L.getObjectMember($S, mapNode -> {", source(), name);
            builder.add("\n$>");
            builder.add("for ($T<$T, $T> member : mapNode.getMembers().entrySet()) {",
                        Map.Entry.class, StringNode.class, Node.class);
            builder.add("\n$>");
            builder.add("$T node = member.getValue();\n", Node.class);
            builder.add("builder.put$L(getKey().getValue(), ", name.toSingularSpelling().asCamelCase());
            var target = directive.model().expectShape(shape.getValue().getTarget());
            pushSource("node");
            target.accept(this);
            popSource();
            builder.add(");");
            builder.add("$<\n");
            builder.add("}");
            builder.add("$<");
            builder.add("})");
            return null;
        }

        @Override
        public Void byteShape(ByteShape shape) {
            builder.add("$L.expectNumberNode().getValue().byteValue()", source());
            return null;
        }

        private ObjectNode getObjectNode() {
            return null;
        }

        private Optional<ObjectNode> getOptionalObjectNode() {
            return Optional.ofNullable(getObjectNode());
        }

        @Override
        public Void enumShape(EnumShape shape) {
            var enumClass = directive.toClass(shape.toShapeId());
            builder.add("$T.fromValue($L.expectStringNode().getValue())", enumClass, source());
            return null;
        }

        @Override
        public Void shortShape(ShortShape shape) {
            // getOptionalObjectNode().map(x -> x.g)
            // getObjectNode().getMember("foo").map(x -> x.expectNumberNode().getValue()).ifPresent();
            builder.add("$L.expectNumberNode().getValue().shortValue()", source());
            return null;
        }

        @Override
        public Void integerShape(IntegerShape shape) {
            builder.add("$L.expectNumberNode().getValue().intValue()", source());
            return null;
        }

        @Override
        public Void longShape(LongShape shape) {
            builder.add("$L.expectNumberNode().getValue().longValue()", source());
            return null;
        }

        @Override
        public Void floatShape(FloatShape shape) {
            builder.add("$L.expectNumberNode().getValue().floatValue()", source());
            return null;
        }

        @Override
        public Void doubleShape(DoubleShape shape) {
            builder.add("$L.expectNumberNode().getValue().doubleValue()", source());
            return null;
        }

        @Override
        public Void stringShape(StringShape shape) {
            builder.add("$L.expectStringNode().getValue()", source());
            return null;
        }

        @Override
        public Void structureShape(StructureShape shape) {
            var symbolProvider = directive.symbolProvider();
            var shapeSymbol = symbolProvider.toSymbol(shape);
            PoetUtils.toClassName(shapeSymbol);
            builder.add("$T.fromNode($L)", PoetUtils.toClassName(shapeSymbol), source());
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
                var nodeName = newName(name);
                builder.add("$L.getMember($S).map($L -> ", source(), name, nodeName);
                pushSource(nodeName);
            }
            target.accept(this);
            if (aggregateType == SymbolConstants.AggregateType.NONE) {
                var name = directive.symbolProvider().toMemberName(shape);
                builder.add(").ifPresent(value -> builder.$L(value))", name);
                popSource();
            } else {
                builder.addStatement("");
            }
            builder.addStatement("");
            return null;
        }

        @Override
        public Void documentShape(DocumentShape shape) {
            return null;
        }
    }
}
