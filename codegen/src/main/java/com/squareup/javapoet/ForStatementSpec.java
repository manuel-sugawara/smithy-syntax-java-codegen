package com.squareup.javapoet;

public class ForStatementSpec implements SyntaxNode{
    @Override
    public void emit(CodeWriter writer) {
        throw new UnsupportedOperationException();
    }

    public static Builder builder(SyntaxNode initializer) {
        return new Builder(initializer);
    }

    public static final class Builder extends AbstractBlockBuilder<Builder, ForStatementSpec> {
        private SyntaxNode initializer;

        Builder(SyntaxNode initializer) {
            this.initializer = initializer;
        }

        @Override
        public ForStatementSpec build() {
            throw new UnsupportedOperationException();
        }
    }
}
