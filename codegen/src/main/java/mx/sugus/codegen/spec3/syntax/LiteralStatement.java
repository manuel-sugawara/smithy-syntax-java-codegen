package mx.sugus.codegen.spec3.syntax;

import mx.sugus.codegen.writer.CodegenWriter;

public final class LiteralStatement implements SyntaxNode {
    private final String statement;

    LiteralStatement(String statement) {
        this.statement = statement;
    }

    public static LiteralStatement create(String statement) {
        return new LiteralStatement(statement);
    }

    public String getStatement() {
        return statement;
    }

    @Override
    public void emit(CodegenWriter writer) {
        writer.writeInlineWithNoFormatting(statement);
        writer.writeWithNoFormatting(";");
    }

    @Override
    public void emitInline(CodegenWriter writer) {
        writer.writeInlineWithNoFormatting(statement);
        writer.writeInlineWithNoFormatting("; ");
    }

    @Override
    public Kind kind() {
        return Kind.LiteralStatement;
    }

    @Override
    public <R> R accept(SyntaxVisitor<R> visitor) {
        return visitor.visitLiteralStatement(this);
    }
}
