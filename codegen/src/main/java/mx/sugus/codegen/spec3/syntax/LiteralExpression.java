package mx.sugus.codegen.spec3.syntax;

import mx.sugus.codegen.writer.CodegenWriter;

public final class LiteralExpression implements SyntaxNode {
    private final String expression;

    LiteralExpression(String expression) {
        this.expression = expression;
    }

    public static LiteralExpression create(String expression) {
        return new LiteralExpression(expression);
    }

    @Override
    public void emit(CodegenWriter writer) {
        writer.writeInlineWithNoFormatting(expression);
    }

    @Override
    public Kind kind() {
        return Kind.LiteralExpression;
    }

    @Override
    public <R> R accept(SyntaxVisitor<R> visitor) {
        return visitor.visitLiteralExpression(this);
    }
}
