package mx.sugus.codegen.spec3;

import mx.sugus.codegen.spec3.syntax.BlockStatement;
import mx.sugus.codegen.spec3.syntax.ForStatementSyntax;
import mx.sugus.codegen.spec3.syntax.SyntaxNode;

public class ForStatementSpec extends AbstractBlock<ForStatementSpec, ForStatementSyntax> {

    private final SyntaxNode initializer;

    ForStatementSpec(SyntaxNode initializer) {
        this.initializer = initializer;
    }

    @Override
    public ForStatementSyntax build() {
        var forBody = BlockStatement.builder()
                                    .addStatements(contents)
                                    .build();

        return ForStatementSyntax.builder()
                                 .initializer(initializer)
                                 .statement(forBody)
                                 .build();
    }
}
