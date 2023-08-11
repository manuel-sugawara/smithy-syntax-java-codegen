package com.squareup.javapoet;

public class ElseStatementSpec implements SyntaxNode {

    private final BlockStatementSpec body;

    private ElseStatementSpec(BlockStatementSpec body) {
        this.body = body;
    }

    public static Builder builder(IfStatementSpec.Builder ifStatement) {
        return new Builder(ifStatement);
    }

    @Override
    public void emit(CodeWriter writer) {
        writer.emit("else ");
        body.emit(writer);
    }

    @Override
    public String toString() {
        var out = new StringBuilder();
        var writer = new CodeWriter(out);
        this.emit(writer);
        return out.toString();
    }

    public static final class Builder extends AbstractBlockBuilder<Builder, IfStatementSpec> {
        private IfStatementSpec.Builder ifStatement;

        Builder(IfStatementSpec.Builder ifStatement) {
            this.ifStatement = ifStatement;
        }

        @Override
        public IfStatementSpec build() {
            ifStatement.addElse(new ElseStatementSpec(this.toBlockStatement()));
            return ifStatement.build();
        }
    }
}
