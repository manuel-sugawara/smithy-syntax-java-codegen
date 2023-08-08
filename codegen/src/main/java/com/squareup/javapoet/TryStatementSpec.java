package com.squareup.javapoet;

public class TryStatementSpec implements SyntaxNode {
    public static TryStatementSpec.Builder builder(SyntaxNode resources) {
        return new Builder(resources);
    }

    public static TryStatementSpec.Builder builder() {
        return new Builder(null);
    }

    @Override
    public void emit(CodeWriter writer) {
        throw new UnsupportedOperationException();
    }

    public static final class Builder extends AbstractBlockBuilder<Builder, TryStatementSpec> {
        private SyntaxNode resources;

        public Builder(SyntaxNode resources) {
            this.resources = resources;
        }

        public CatchClauseSpec.Builder addCatch(SyntaxNode catchParameter) {
            return CatchClauseSpec.builder(catchParameter);
        }

        @Override
        public TryStatementSpec build() {
            return null;
        }

        public FinallyClauseSpec.Builder addFinally() {
            return FinallyClauseSpec.builder();
        }
    }
}
