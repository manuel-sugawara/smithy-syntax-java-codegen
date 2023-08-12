package com.squareup.javapoet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BlockStatementSpec implements SyntaxNode {
    private final List<SyntaxNode> nodes;

    public BlockStatementSpec(Builder builder) {
        this.nodes = List.copyOf(builder.nodes);
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public void emit(CodeWriter writer) {
        writer.emit("{\n");
        writer.indent();
        for (var stmt : nodes) {
            stmt.emit(writer);
        }
        writer.unindent();
        writer.emit("}\n");
    }

    @Override
    public void emitInline(CodeWriter writer) {
        writer.emit("{\n");
        writer.indent();
        for (var stmt : nodes) {
            stmt.emit(writer);
        }
        writer.unindent();
        writer.emit("} ");
    }

    public Collection<? extends SyntaxNode> nodes() {
        return this.nodes;
    }

    public static final class Builder {
        private final List<SyntaxNode> nodes = new ArrayList<>();

        public Builder() {
        }

        public Builder addStatements(Collection<SyntaxNode> statements) {
            this.nodes.addAll(statements);
            return this;
        }

        public BlockStatementSpec build() {
            return new BlockStatementSpec(this);
        }
    }
}
