package com.squareup.javapoet;

public final class FinallyClauseSpec implements SyntaxNode {
    private final BlockStatementSpec body;

    private FinallyClauseSpec(Builder builder) {
        this.body = builder.toBlockStatement();
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public void emit(CodeWriter writer) {
        writer.emit("finally ");
        body.emit(writer);
    }

    public static final class Builder extends AbstractBlockBuilder<Builder, FinallyClauseSpec> {

        @Override
        public FinallyClauseSpec build() {
            return new FinallyClauseSpec(this);
        }
    }
}
