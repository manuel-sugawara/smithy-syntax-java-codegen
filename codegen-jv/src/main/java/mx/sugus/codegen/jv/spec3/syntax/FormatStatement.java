package mx.sugus.codegen.jv.spec3.syntax;

import java.util.Arrays;
import java.util.List;
import mx.sugus.codegen.jv.writer.CodegenWriter;

public final class FormatStatement implements SyntaxNode {
    private final String format;
    private final Object[] args;

    FormatStatement(String format, Object[] args) {
        this.format = format;
        this.args = args;
    }

    public static FormatStatement create(String format, Object[] args) {
        return new FormatStatement(format, args);
    }

    @Override
    public void emit(CodegenWriter writer) {
        writer.writeInline(format, args);
        writer.writeWithNoFormatting(";");
    }

    @Override
    public Kind kind() {
        return Kind.FormatStatement;
    }

    public String getFormat() {
        return format;
    }

    public List<Object> getArgs() {
        return Arrays.asList(args);
    }

    @Override
    public <R> R accept(SyntaxVisitor<R> visitor) {
        return visitor.visitFormatStatement(this);
    }
}
