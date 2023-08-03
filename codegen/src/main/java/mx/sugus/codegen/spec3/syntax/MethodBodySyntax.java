package mx.sugus.codegen.spec3.syntax;

import java.util.ArrayList;
import java.util.List;
import mx.sugus.codegen.writer.CodegenWriter;

public final class MethodBodySyntax implements SyntaxNode {
    private final List<SyntaxNode> nodes;

    MethodBodySyntax(Builder builder) {
        this.nodes = List.copyOf(builder.nodes);
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public void emit(CodegenWriter writer) {
        writer.write(" {")
              .indent();
        for (var emitter : nodes) {
            emitter.emit(writer);
        }
        writer.dedent().writeWithNoFormatting("}");
    }

    @Override
    public Kind kind() {
        return null;
    }

    @Override
    public <R> R accept(SyntaxVisitor<R> visitor) {
        return visitor.visitMethodBody(this);
    }

    public Builder toBuilder() {
        return new Builder(this);
    }

    public static class Builder {
        private final List<SyntaxNode> nodes = new ArrayList<>();

        public Builder() {
        }

        public Builder(MethodBodySyntax methodBody) {
            this.nodes.addAll(methodBody.nodes);
        }

        public Builder addStatement(SyntaxNode statement) {
            this.nodes.add(statement);
            return this;
        }

        public Builder addStatements(List<SyntaxNode> statements) {
            this.nodes.addAll(statements);
            return this;
        }

        public MethodBodySyntax build() {
            return new MethodBodySyntax(this);
        }
    }
}