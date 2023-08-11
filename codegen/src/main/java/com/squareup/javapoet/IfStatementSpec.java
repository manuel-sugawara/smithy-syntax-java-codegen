package com.squareup.javapoet;

public class IfStatementSpec implements SyntaxNode {
    private final SyntaxNode condition;
    private final BlockStatementSpec body;
    private final ElseStatementSpec elseStatement;

    private IfStatementSpec(Builder builder) {
        this.condition = builder.condition;
        this.body = builder.toBlockStatement();
        this.elseStatement = builder.elseStatement;
    }

    public static Builder builder(SyntaxNode condition) {
        return new Builder(condition);
    }

    @Override
    public void emit(CodeWriter writer) {
        writer.emit("if (");
        condition.emit(writer);
        writer.emit(")");
        if (elseStatement == null) {
            body.emit(writer);
        } else {
            body.emitInline(writer);
            elseStatement.emit(writer);
        }
    }

    @Override
    public String toString() {
        var out = new StringBuilder();
        var writer = new CodeWriter(out);
        this.emit(writer);
        return out.toString();
    }

    public static final class Builder extends AbstractBlockBuilder<Builder, IfStatementSpec> {
        private SyntaxNode condition;
        private ElseStatementSpec elseStatement;

        Builder(SyntaxNode condition) {
            this.condition = condition;
        }

        public Builder addElse(ElseStatementSpec elseStatement) {
            this.elseStatement = elseStatement;
            return this;
        }

        @Override
        public IfStatementSpec build() {
            return new IfStatementSpec(this);
        }
    }
}
