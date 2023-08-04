package mx.sugus.codegen.jv.spec3;

import mx.sugus.codegen.jv.spec3.syntax.BlockStatement;
import mx.sugus.codegen.jv.spec3.syntax.ForStatement;
import mx.sugus.codegen.jv.spec3.syntax.SyntaxNode;

public final class ForStatementBuilder extends AbstractBlock<ForStatementBuilder, ForStatement> {

    private final SyntaxNode initializer;

    ForStatementBuilder(SyntaxNode initializer) {
        this.initializer = initializer;
    }

    @Override
    public ForStatement build() {
        var forBody = BlockStatement.builder()
                                    .addStatements(contents)
                                    .build();

        return ForStatement.builder()
                           .initializer(initializer)
                           .statement(forBody)
                           .build();
    }
}
