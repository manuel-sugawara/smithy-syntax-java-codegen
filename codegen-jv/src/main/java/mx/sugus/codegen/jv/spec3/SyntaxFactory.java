package mx.sugus.codegen.jv.spec3;

import java.util.Objects;
import mx.sugus.codegen.jv.spec3.syntax.FormatExpression;
import mx.sugus.codegen.jv.spec3.syntax.FormatStatement;
import mx.sugus.codegen.jv.spec3.syntax.LiteralExpression;
import mx.sugus.codegen.jv.spec3.syntax.LiteralStatement;

public final class SyntaxFactory {

    public static LiteralExpression expr(String value) {
        return LiteralExpression.create(value);
    }

    public static FormatExpression expr(String format, Objects... args) {
        return FormatExpression.create(format, args);
    }

    public static LiteralStatement stmt(String value) {
        return LiteralStatement.create(value);
    }

    public static FormatStatement stmt(String format, Objects... args) {
        return FormatStatement.create(format, args);
    }

    public static MethodBuilder methodBuilder(String name) {
        return new MethodBuilder(name);
    }

    public static ClassBuilder classBuilder(String name) {
        return new ClassBuilder(name);
    }
}
