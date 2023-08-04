package mx.sugus.codegen.spec3;

import mx.sugus.codegen.spec3.syntax.FinallyClause;

public class FinallyClauseSpec extends AbstractBlock<FinallyClauseSpec, FinallyClause> {

    @Override
    public FinallyClause build() {
        var finallyBody = toBlockStatement();
        return FinallyClause.builder()
                            .body(finallyBody)
                            .build();
    }
}
