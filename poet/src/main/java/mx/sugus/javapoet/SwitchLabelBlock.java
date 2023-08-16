package mx.sugus.javapoet;

import java.util.List;
import java.util.Objects;

public class SwitchLabelBlock implements SyntaxNode {
    private final SyntaxNode label;
    private final List<SyntaxNode> nodes;

    private SwitchLabelBlock(Builder builder) {
        this.label = builder.label;
        this.nodes = List.copyOf(builder.contents);
    }

    public static Builder builder(SyntaxNode label) {
        return new Builder(label);
    }

    @Override
    public void emit(CodeWriter writer) {
        if (label != null) {
            writer.emit("case ");
            label.emit(writer);
        } else {
            writer.emit("default");
        }
        writer.emit(":\n");
        writer.indent();
        for (var node : nodes) {
            node.emit(writer);
        }
        writer.unindent();
    }

    @Override
    public String toString() {
        var out = new StringBuilder();
        var writer = new CodeWriter(out);
        this.emit(writer);
        return out.toString();
    }

    public static class Builder extends AbstractBlockBuilder<Builder, SwitchLabelBlock> {
        private SyntaxNode label;

        public Builder(SyntaxNode label) {
            this.label = label;
        }

        @Override
        public SwitchLabelBlock build() {
            return new SwitchLabelBlock(this);
        }
    }
}
