package mx.sugus.codegen.jv.spec3;

import mx.sugus.codegen.jv.spec3.syntax.BlockStatement;
import mx.sugus.codegen.jv.spec3.syntax.IfStatement;

public final class ElseStatementBuilder extends AbstractBlock<ElseStatementBuilder, IfStatement> {
    private final IfStatementBuilder ifStatementBuilder;

    ElseStatementBuilder(IfStatementBuilder ifStatementBuilder) {
        this.ifStatementBuilder = ifStatementBuilder;
    }

    @Override
    public IfStatement build() {
        var elseBody = BlockStatement.builder()
                                     .addStatements(contents)
                                     .build();
        return this.ifStatementBuilder.elseStatement(elseBody).build();
    }
}
