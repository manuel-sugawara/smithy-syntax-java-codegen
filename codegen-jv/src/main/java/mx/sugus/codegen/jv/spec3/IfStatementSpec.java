package mx.sugus.codegen.jv.spec3;

import mx.sugus.codegen.jv.spec3.syntax.BlockStatement;
import mx.sugus.codegen.jv.spec3.syntax.IfStatementSyntax;
import mx.sugus.codegen.jv.spec3.syntax.SyntaxNode;

public class IfStatementSpec extends AbstractBlock<IfStatementSpec, IfStatementSyntax> {
    private final SyntaxNode condition;
    private SyntaxNode elseStatement;

    public IfStatementSpec(SyntaxNode condition) {
        this.condition = condition;
    }

    public IfStatementSpec elseStatement(SyntaxNode elseStatement) {
        this.elseStatement = elseStatement;
        return this;
    }

    @Override
    public IfStatementSyntax build() {
        var ifBody = BlockStatement.builder()
                                   .addStatements(contents)
                                   .build();
        return IfStatementSyntax.builder(condition)
                                .statement(ifBody)
                                .elseStatement(elseStatement)
                                .build();
    }
}
