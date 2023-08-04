package mx.sugus.codegen.jv.spec3.syntax;

import java.util.Objects;
import mx.sugus.codegen.jv.writer.CodegenWriter;
import software.amazon.smithy.codegen.core.Symbol;

public final class ParameterSyntax implements SyntaxNode {
    private final String name;
    private final Symbol type;

    public ParameterSyntax(Builder builder) {
        this.name = Objects.requireNonNull(builder.name);
        this.type = Objects.requireNonNull(builder.type);
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getName() {
        return name;
    }

    public Symbol getType() {
        return type;
    }

    @Override
    public void emit(CodegenWriter writer) {
        writer.writeInline("$T $L", type, name);
    }

    @Override
    public Kind kind() {
        return null;
    }

    @Override
    public <R> R accept(SyntaxVisitor<R> visitor) {
        return visitor.visitParameter(this);
    }

    public static class Builder {
        private String name;
        private Symbol type;

        public Builder() {
        }

        public Builder type(Symbol type) {
            this.type = type;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public ParameterSyntax build() {
            return new ParameterSyntax(this);
        }
    }
}
