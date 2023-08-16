package mx.sugus.javapoet;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class SwitchStatement implements SyntaxNode {

    private final SyntaxNode expression;
    private final List<SwitchLabelBlock> labels;
    private SwitchStatement(Builder builder) {
        this.expression = Objects.requireNonNull(builder.expression, "expression");
        labels = new ArrayList<>(builder.contents.size());
        for (var node : builder.contents) {
            if (node instanceof SwitchLabelBlock label) {
                labels.add(label);
            } else {
                throw new IllegalStateException("switch statements can only have labels, got instead: "
                                                + node.getClass().getName() + ", with: " + node.toString());
            }
        }
    }

    public static Builder builder(SyntaxNode expression) {
        return new Builder(expression);
    }

    @Override
    public void emit(CodeWriter writer) {
        writer.emit("switch (");
        expression.emit(writer);
        writer.emit(") {\n");
        writer.indent();
        for (var node : labels) {
            node.emit(writer);
        }
        writer.unindent();
        writer.emit("}\n");
    }

    @Override
    public String toString() {
        var out = new StringBuilder();
        var writer = new CodeWriter(out);
        this.emit(writer);
        return out.toString();
    }

    public static final class Builder extends AbstractBlockBuilder<Builder, SwitchStatement> {
        private SyntaxNode expression;
        Builder(SyntaxNode expression) {
            this.expression = expression;
        }

        @Override
        public SwitchStatement build() {
            return new SwitchStatement(this);
        }
    }
}
