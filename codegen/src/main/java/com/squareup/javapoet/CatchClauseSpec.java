package com.squareup.javapoet;

public class CatchClauseSpec implements SyntaxNode {
    public static Builder builder(SyntaxNode catchParameter) {
        return new Builder(catchParameter);
    }

    @Override
    public void emit(CodeWriter writer) {
        throw new UnsupportedOperationException();
    }

    public static final class Builder extends AbstractBlockBuilder<Builder, CatchClauseSpec> {
        private SyntaxNode catchParameter;

        public Builder(SyntaxNode catchParameter) {
            this.catchParameter = catchParameter;
        }

        @Override
        public CatchClauseSpec build() {
            throw new UnsupportedOperationException();
        }
    }
}
