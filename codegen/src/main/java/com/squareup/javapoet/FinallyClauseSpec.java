package com.squareup.javapoet;

public class FinallyClauseSpec implements SyntaxNode {
    public static Builder builder() {
        return new Builder();
    }

    @Override
    public void emit(CodeWriter writer) {
        throw new UnsupportedOperationException();
    }

    public static final class Builder extends AbstractBlockBuilder<Builder, FinallyClauseSpec> {

        @Override
        public FinallyClauseSpec build() {
            throw new UnsupportedOperationException();
        }
    }
}
