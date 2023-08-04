package mx.sugus.codegen.jv.spec3;

import java.util.ArrayList;
import java.util.List;
import mx.sugus.codegen.jv.spec3.syntax.SyntaxNode;
import mx.sugus.codegen.jv.spec3.syntax.TryStatement;

public final class TryStatementBuilder extends AbstractBlock<TryStatementBuilder, TryStatement> {

    private final List<CatchClauseBuilder> catchClauseBuilders = new ArrayList<>();
    private SyntaxNode resources;
    private FinallyClauseBuilder finallyClauseBuilder;

    TryStatementBuilder() {
    }

    TryStatementBuilder(SyntaxNode resources) {
        this.resources = resources;
    }

    public CatchClauseBuilder addCatch(SyntaxNode catchParameter) {
        var catchClause = new CatchClauseBuilder(catchParameter);
        catchClauseBuilders.add(catchClause);
        return catchClause;
    }

    public FinallyClauseBuilder addFinally() {
        if (finallyClauseBuilder != null) {
            throw new IllegalStateException("beginFinally has been already called");
        }
        this.finallyClauseBuilder = new FinallyClauseBuilder();
        return finallyClauseBuilder;
    }


    @Override
    public TryStatement build() {
        var catchClauses = catchClauseBuilders.stream().map(CatchClauseBuilder::build).toList();
        var finallyClause = finallyClauseBuilder != null ? finallyClauseBuilder.build() : null;
        var catchBody = toBlockStatement();
        return TryStatement.builder()
                           .resources(resources)
                           .tryBody(catchBody)
                           .catchClauses(catchClauses)
                           .finallyClause(finallyClause)
                           .build();
    }
}
