package com.squareup.javapoet;

public class ElseStatementSpec implements SyntaxNode {

    public static Builder builder(IfStatementSpec.Builder ifStatement) {
        return new Builder(ifStatement);
    }

    @Override
    public void emit(CodeWriter writer) {
        throw new UnsupportedOperationException();
    }

    public static final class Builder extends AbstractBlockBuilder<Builder, IfStatementSpec> {
        private IfStatementSpec.Builder ifStatement;

        Builder(IfStatementSpec.Builder ifStatement) {
            this.ifStatement = ifStatement;
        }

        @Override
        public IfStatementSpec build() {
            throw new UnsupportedOperationException();
        }
    }
}
