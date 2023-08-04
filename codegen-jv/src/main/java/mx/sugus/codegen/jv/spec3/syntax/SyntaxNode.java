package mx.sugus.codegen.jv.spec3.syntax;

import java.util.Collections;
import java.util.List;

public interface SyntaxNode extends CodeEmitter {

    Kind kind();

    <R> R accept(SyntaxVisitor<R> visitor);

    default List<SyntaxNode> children() {
        return Collections.emptyList();
    }

    enum Kind {
        LiteralExpression,
        FormatExpression, LiteralStatement, FormatStatement, AbstractBlock, ForStatement, IfStatement, BlockStatement, ClassField,

    }
}
