package mx.sugus.codegen.jv.spec3;

import java.util.ArrayList;
import java.util.List;
import mx.sugus.codegen.jv.spec3.syntax.SyntaxNode;
import mx.sugus.codegen.jv.spec3.syntax.TryStatement;

public class TryStatementSpec extends AbstractBlock<TryStatementSpec, TryStatement> {

    private final List<CatchClauseSpec> catchClauseSpecs = new ArrayList<>();
    private SyntaxNode resources;
    private FinallyClauseSpec finallyClauseSpec;

    TryStatementSpec() {
    }

    TryStatementSpec(SyntaxNode resources) {
        this.resources = resources;
    }

    public CatchClauseSpec addCatch(SyntaxNode catchParameter) {
        var catchClause = new CatchClauseSpec(catchParameter);
        catchClauseSpecs.add(catchClause);
        return catchClause;
    }

    public FinallyClauseSpec addFinally() {
        if (finallyClauseSpec != null) {
            throw new IllegalStateException("beginFinally has been already called");
        }
        this.finallyClauseSpec = new FinallyClauseSpec();
        return finallyClauseSpec;
    }


    @Override
    public TryStatement build() {
        var catchClauses = catchClauseSpecs.stream().map(CatchClauseSpec::build).toList();
        var finallyClause = finallyClauseSpec != null ? finallyClauseSpec.build() : null;
        var catchBody = toBlockStatement();

        return TryStatement.builder()
                           .resources(resources)
                           .tryBody(catchBody)
                           .catchClauses(catchClauses)
                           .finallyClause(finallyClause)
                           .build();
    }
}
