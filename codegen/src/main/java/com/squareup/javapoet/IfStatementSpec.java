package com.squareup.javapoet;

public class IfStatementSpec implements SyntaxNode {
    private IfStatementSpec() {
    }

    public static AbstractBlockBuilder<Builder, IfStatementSpec> builder(SyntaxNode condition) {
        return new Builder(condition);
    }

    @Override
    public void emit(CodeWriter writer) {
        throw new UnsupportedOperationException();
    }

    public static final class Builder extends AbstractBlockBuilder<Builder, IfStatementSpec> {
        private SyntaxNode condition;

        Builder(SyntaxNode condition) {
            this.condition = condition;
        }

        @Override
        public IfStatementSpec build() {
            return null;
        }
    }
}
