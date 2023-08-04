package mx.sugus.codegen.jv.spec3.syntax;

import java.util.Objects;
import mx.sugus.codegen.jv.writer.CodegenWriter;

public final class IfStatementSyntax implements SyntaxNode {
    private final SyntaxNode condition;
    private final SyntaxNode statement;
    private final SyntaxNode elseStatement;

    IfStatementSyntax(Builder builder) {
        this.condition = Objects.requireNonNull(builder.condition, "condition");
        this.statement = Objects.requireNonNull(builder.statement, "statement");
        this.elseStatement = builder.elseStatement;
    }

    public static Builder builder(SyntaxNode condition) {
        return new Builder(condition);
    }

    @Override
    public void emit(CodegenWriter writer) {
        writer.writeInlineWithNoFormatting("if (");
        condition.emit(writer);
        writer.writeInlineWithNoFormatting(") ");
        if (elseStatement == null) {
            statement.emit(writer);
        } else {
            statement.emitInline(writer);
            writer.writeInlineWithNoFormatting("else ");
            elseStatement.emit(writer);
        }
    }

    @Override
    public Kind kind() {
        return Kind.IfStatement;
    }

    @Override
    public <R> R accept(SyntaxVisitor<R> visitor) {
        return visitor.visitIfStatement(this);
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

    public SyntaxNode getCondition() {
        return condition;
    }

    public SyntaxNode getStatement() {
        return statement;
    }

    public SyntaxNode getElseStatement() {
        return elseStatement;
    }

    public static class Builder {
        private SyntaxNode condition;
        private SyntaxNode statement;
        private SyntaxNode elseStatement;

        Builder(SyntaxNode condition) {
            this.condition = condition;
        }

        Builder(IfStatementSyntax statement) {
            this.condition = statement.condition;
            this.statement = statement.statement;
            this.elseStatement = statement.elseStatement;
        }

        public Builder condition(SyntaxNode condition) {
            this.condition = condition;
            return this;
        }

        public Builder statement(SyntaxNode statement) {
            this.statement = statement;
            return this;
        }

        public Builder elseStatement(SyntaxNode elseStatement) {
            this.elseStatement = elseStatement;
            return this;
        }

        public IfStatementSyntax build() {
            return new IfStatementSyntax(this);
        }
    }
}
