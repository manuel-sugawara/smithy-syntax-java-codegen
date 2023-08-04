package mx.sugus.codegen.jv.spec3.syntax;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import mx.sugus.codegen.jv.writer.CodegenWriter;

public final class BlockStatement implements SyntaxNode {
    private final List<SyntaxNode> children;

    BlockStatement(Builder builder) {
        this.children = List.copyOf(builder.children);
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public List<SyntaxNode> children() {
        return children;
    }

    @Override
    public void emit(CodegenWriter writer) {
        writer.write("{").indent();
        for (var emitter : children) {
            emitter.emit(writer);
        }
        writer.dedent().writeWithNoFormatting("}");
    }

    @Override
    public void emitInline(CodegenWriter writer) {
        writer.write("{").indent();
        for (var emitter : children) {
            emitter.emit(writer);
        }
        writer.dedent().writeInlineWithNoFormatting("} ");
    }

    @Override
    public Kind kind() {
        return Kind.BlockStatement;
    }

    @Override
    public <R> R accept(SyntaxVisitor<R> visitor) {
        return visitor.visitBlockStatement(this);
    }

    public Builder toBuilder() {
        return new Builder().addStatements(children);
    }

    public static class Builder {
        private final List<SyntaxNode> children = new ArrayList<>();

        public Builder addStatement(SyntaxNode statement) {
            this.children.add(statement);
            return this;
        }

        public Builder addStatements(Collection<SyntaxNode> statements) {
            this.children.clear();
            this.children.addAll(statements);
            return this;
        }

        public BlockStatement build() {
            return new BlockStatement(this);
        }
    }
}
