package com.squareup.javapoet;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class TryStatementSpec implements SyntaxNode {
    private final SyntaxNode resources;
    private final BlockStatementSpec body;
    private final List<CatchClauseSpec> catchClauses;
    private final FinallyClauseSpec finallyClause;

    private TryStatementSpec(Builder builder) {
        if (builder.finallyClauseBuilder == null && builder.catchClauseBuilders.isEmpty()) {
            throw new IllegalArgumentException("try without catch clauses or finally clause");
        }
        this.resources = builder.resources;
        this.body = builder.toBlockStatement();
        this.catchClauses = builder.catchClauseBuilders.stream().map(CatchClauseSpec.Builder::build).toList();
        this.finallyClause = builder.finallyClauseBuilder != null ? builder.finallyClauseBuilder.build() : null;
    }

    public static TryStatementSpec.Builder builder(SyntaxNode resources) {
        return new Builder(Objects.requireNonNull(resources));
    }

    public static TryStatementSpec.Builder builder() {
        return new Builder(null);
    }

    @Override
    public void emit(CodeWriter writer) {
        writer.emit("try ");
        if (resources != null) {
            writer.emit("(");
            resources.emit(writer);
            writer.emit(") ");
        }
        body.emitInline(writer);
        if (!catchClauses.isEmpty()) {
            var lastIndex = catchClauses.size() - 1;
            for (var idx = 0; idx < lastIndex; ++idx) {
                catchClauses.get(idx).emitInline(writer);
            }
            var last = catchClauses.get(lastIndex);
            if (finallyClause != null) {
                last.emitInline(writer);
            } else {
                last.emit(writer);
            }
        }
        if (finallyClause != null) {
            finallyClause.emit(writer);
        }
    }

    public static final class Builder extends AbstractBlockBuilder<Builder, TryStatementSpec> {
        private final List<CatchClauseSpec.Builder> catchClauseBuilders = new ArrayList<>();
        private SyntaxNode resources;
        private FinallyClauseSpec.Builder finallyClauseBuilder;

        public Builder(SyntaxNode resources) {
            this.resources = resources;
        }

        public CatchClauseSpec.Builder addCatch(SyntaxNode catchParameter) {
            var builder = CatchClauseSpec.builder(catchParameter);
            catchClauseBuilders.add(builder);
            return builder;
        }

        public FinallyClauseSpec.Builder addFinally() {
            if (finallyClauseBuilder != null) {
                throw new IllegalStateException("finally clause was previously added");
            }
            var builder = FinallyClauseSpec.builder();
            finallyClauseBuilder = builder;
            return builder;
        }

        @Override
        public TryStatementSpec build() {
            return new TryStatementSpec(this);
        }

    }
}
