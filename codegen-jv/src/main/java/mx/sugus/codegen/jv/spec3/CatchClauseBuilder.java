package mx.sugus.codegen.jv.spec3;

import mx.sugus.codegen.jv.spec3.syntax.CatchClause;
import mx.sugus.codegen.jv.spec3.syntax.SyntaxNode;

public final class CatchClauseBuilder extends AbstractBlock<CatchClauseBuilder, CatchClause> {
    private final SyntaxNode catchParameter;

    CatchClauseBuilder(SyntaxNode catchParameter) {
        this.catchParameter = catchParameter;
    }

    @Override
    public CatchClause build() {
        var catchBody = toBlockStatement();
        return CatchClause.builder()
                          .parameter(catchParameter)
                          .body(catchBody)
                          .build();
    }
}
