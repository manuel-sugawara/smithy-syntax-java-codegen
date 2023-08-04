package mx.sugus.codegen.jv.spec3;

import mx.sugus.codegen.jv.spec3.syntax.BlockStatement;
import mx.sugus.codegen.jv.spec3.syntax.IfStatement;
import mx.sugus.codegen.jv.spec3.syntax.SyntaxNode;

public final class IfStatementBuilder extends AbstractBlock<IfStatementBuilder, IfStatement> {
    private final SyntaxNode condition;
    private SyntaxNode elseStatement;

    IfStatementBuilder(SyntaxNode condition) {
        this.condition = condition;
    }

    IfStatementBuilder elseStatement(SyntaxNode elseStatement) {
        this.elseStatement = elseStatement;
        return this;
    }

    @Override
    public IfStatement build() {
        var ifBody = BlockStatement.builder()
                                   .addStatements(contents)
                                   .build();
        return IfStatement.builder(condition)
                          .statement(ifBody)
                          .elseStatement(elseStatement)
                          .build();
    }
}
