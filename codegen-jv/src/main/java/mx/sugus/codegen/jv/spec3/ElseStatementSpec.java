package mx.sugus.codegen.jv.spec3;

import mx.sugus.codegen.jv.spec3.syntax.BlockStatement;
import mx.sugus.codegen.jv.spec3.syntax.IfStatementSyntax;

public class ElseStatementSpec extends AbstractBlock<ElseStatementSpec, IfStatementSyntax> {
    private final IfStatementSpec ifStatementSpec;

    public ElseStatementSpec(IfStatementSpec ifStatementSpec) {
        this.ifStatementSpec = ifStatementSpec;
    }

    @Override
    public IfStatementSyntax build() {
        var elseBody = BlockStatement.builder()
                                     .addStatements(contents)
                                     .build();

        return this.ifStatementSpec.elseStatement(elseBody).build();
    }
}
