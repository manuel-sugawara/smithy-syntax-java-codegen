package mx.sugus.codegen.jv.spec3.syntax;

import java.util.Objects;
import mx.sugus.codegen.jv.writer.CodegenWriter;

public final class ForStatement implements SyntaxNode {
    private final SyntaxNode initializer;
    private final SyntaxNode statement;

    ForStatement(Builder builder) {
        this.initializer = Objects.requireNonNull(builder.initializer);
        this.statement = Objects.requireNonNull(builder.statement);
    }

    public static Builder builder(SyntaxNode initializer) {
        return new Builder()
            .initializer(initializer);
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public Kind kind() {
        return Kind.ForStatement;
    }

    @Override
    public <R> R accept(SyntaxVisitor<R> visitor) {
        return visitor.visitForStatement(this);
    }

    @Override
    public void emit(CodegenWriter writer) {
        writer.writeInlineWithNoFormatting("for (");
        initializer.emitInline(writer);
        writer.writeInlineWithNoFormatting(") ");
        statement.emit(writer);
    }

    public Builder toBuilder() {
        return new Builder(this);
    }

    @Override
    public String toString() {
        var writer = new CodegenWriter("<none>");
        emit(writer);
        return writer.toString();
    }

    public SyntaxNode getInitializer() {
        return initializer;
    }

    public SyntaxNode getStatement() {
        return statement;
    }

    public static class Builder {
        private SyntaxNode initializer;
        private SyntaxNode statement;

        Builder() {
        }

        public Builder(ForStatement forStatement) {
            this.initializer = forStatement.initializer;
            this.statement = forStatement.statement;
        }

        public Builder initializer(SyntaxNode initializer) {
            this.initializer = initializer;
            return this;
        }

        public Builder statement(SyntaxNode statement) {
            this.statement = statement;
            return this;
        }

        public ForStatement build() {
            return new ForStatement(this);
        }
    }
}

