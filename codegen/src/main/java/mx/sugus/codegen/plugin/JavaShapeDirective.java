package mx.sugus.codegen.plugin;

import java.util.Objects;
import mx.sugus.codegen.JavaCodegenContext;
import mx.sugus.codegen.JavaCodegenSettings;
import mx.sugus.codegen.JavaSymbolProvider;
import mx.sugus.codegen.OptionalityKnowledgeIndex;
import mx.sugus.javapoet.ClassName;
import mx.sugus.syntax.java.IsaTrait;
import software.amazon.smithy.codegen.core.Symbol;
import software.amazon.smithy.model.Model;
import software.amazon.smithy.model.shapes.Shape;
import software.amazon.smithy.model.shapes.ShapeId;
import software.amazon.smithy.model.traits.StringTrait;

public class JavaShapeDirective {
    private final Model model;
    private final Symbol symbol;
    private final Shape shape;
    private final JavaSymbolProvider symbolProvider;
    private final JavaCodegenContext context;
    private final JavaCodegenSettings settings;

    JavaShapeDirective(Builder builder) {
        this.model = Objects.requireNonNull(builder.model, "model");
        this.symbol = builder.symbol; // Objects.requireNonNull(builder.symbol, "symbol");
        this.shape = Objects.requireNonNull(builder.shape, "shape");
        this.symbolProvider = Objects.requireNonNull(builder.symbolProvider, "symbolProvider");
        this.context = Objects.requireNonNull(builder.context, "context");
        this.settings = Objects.requireNonNull(builder.settings, "settings");
    }

    public static Builder builder() {
        return new Builder();
    }

    public Model model() {
        return model;
    }

    public Symbol symbol() {
        return symbol;
    }

    public Shape shape() {
        return shape;
    }

    public JavaSymbolProvider symbolProvider() {
        return symbolProvider;
    }

    public JavaCodegenContext context() {
        return context;
    }

    public JavaCodegenSettings settings() {
        return settings;
    }

    public ClassName parentClass(Shape shape) {
        var parent = shape.getTrait(IsaTrait.class).map(StringTrait::getValue).orElse("");
        ClassName parentClassName;
        if (parent.contains("#")) {
            var asShapeId = ShapeId.from(parent);
            var symbolProvider = symbolProvider();
            var parentShape = model().expectShape(asShapeId);
            parentClassName = symbolProvider.toClassName(parentShape);
        } else {
            parentClassName = ClassName.bestGuess(parent);
        }
        return parentClassName;
    }

    public ClassName toClass(ShapeId shapeId) {
        var symbolProvider = symbolProvider();
        var shape = model().expectShape(shapeId);
        return symbolProvider.toClassName(shape);
    }

    public ClassName toClass(String unparsedShapeId) {
        var shapeId = ShapeId.from(unparsedShapeId);
        var symbolProvider = symbolProvider();
        var shape = model().expectShape(shapeId);
        return symbolProvider.toClassName(shape);
    }

    public static class Builder {
        private Model model;
        private Symbol symbol;
        private Shape shape;
        private JavaSymbolProvider symbolProvider;
        private JavaCodegenContext context;
        private JavaCodegenSettings settings;
        private OptionalityKnowledgeIndex optionalityKnowledgeIndex;

        public Builder model(Model model) {
            this.model = model;
            return this;
        }

        public Builder symbol(Symbol symbol) {
            this.symbol = symbol;
            return this;
        }

        public Builder shape(Shape shape) {
            this.shape = shape;
            return this;
        }

        public Builder symbolProvider(JavaSymbolProvider symbolProvider) {
            this.symbolProvider = symbolProvider;
            return this;
        }

        public Builder context(JavaCodegenContext context) {
            this.context = context;
            return this;
        }

        public Builder settings(JavaCodegenSettings settings) {
            this.settings = settings;
            return this;
        }

        // private OptionalityKnowledgeIndex optionalityKnowledgeIndex;
        public Builder optionalityKnowledgeIndex(OptionalityKnowledgeIndex optionalityKnowledgeIndex) {
            this.optionalityKnowledgeIndex = optionalityKnowledgeIndex;
            return this;
        }

        public JavaShapeDirective build() {
            return new JavaShapeDirective(this);
        }
    }
}
