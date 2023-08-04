package mx.sugus.codegen.jv.spec3.syntax;

import java.util.Arrays;
import java.util.List;
import mx.sugus.codegen.jv.writer.CodegenWriter;

public final class FormatExpression implements SyntaxNode {
    private final String format;
    private final Object[] args;

    FormatExpression(String format, Object[] args) {
        this.format = format;
        this.args = args;
    }

    public static FormatExpression create(String format, Object... args) {
        return new FormatExpression(format, args);
    }

    @Override
    public void emit(CodegenWriter writer) {
        writer.writeInline(format, args);
    }

    @Override
    public Kind kind() {
        return Kind.FormatExpression;
    }

    public String getFormat() {
        return format;
    }

    public List<Object> getArgs() {
        return Arrays.asList(args);
    }

    @Override
    public <R> R accept(SyntaxVisitor<R> visitor) {
        return visitor.visitFormatExpression(this);
    }
}
