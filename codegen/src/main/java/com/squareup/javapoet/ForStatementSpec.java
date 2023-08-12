package com.squareup.javapoet;

import java.util.Objects;

public final class ForStatementSpec implements SyntaxNode {
    private final SyntaxNode initializer;
    private final BlockStatementSpec body;

    ForStatementSpec(Builder builder) {
        this.initializer = Objects.requireNonNull(builder.initializer);
        this.body = builder.toBlockStatement();
    }

    public static Builder builder(SyntaxNode initializer) {
        return new Builder(initializer);
    }

    @Override
    public void emit(CodeWriter writer) {
        writer.emit("for (");
        initializer.emit(writer);
        writer.emit(") ");
        body.emit(writer);
    }

    public static final class Builder extends AbstractBlockBuilder<Builder, ForStatementSpec> {
        private SyntaxNode initializer;

        Builder(SyntaxNode initializer) {
            this.initializer = Objects.requireNonNull(initializer);
        }

        @Override
        public ForStatementSpec build() {
            return new ForStatementSpec(this);
        }
    }
}
