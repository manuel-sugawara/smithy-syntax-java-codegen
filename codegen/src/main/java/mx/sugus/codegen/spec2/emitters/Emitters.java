package mx.sugus.codegen.spec2.emitters;

import java.util.Collection;
import java.util.function.Consumer;
import mx.sugus.codegen.writer.CodegenWriter;

public final class Emitters {

    private Emitters() {
    }

    public static CodeEmitter format(String format, Object[] args) {
        return StatementEmitter.create(format, args);
    }

    public static CodeEmitter formatComment(String format, Object[] args) {
        return FormatCommentEmitter.create(format, args);
    }

    public static CodeEmitter formatInline(String format, Object[] args) {
        return FormatInlineCodeEmitter.create(format, args);
    }

    public static CodeEmitter literal(String value) {
        return LiteralStatementEmitter.create(value);
    }

    public static CodeEmitter literalComment(String value) {
        return LiteralCommentEmitter.create(value);
    }

    public static CodeEmitter literalInline(String value) {
        return LiteralInlineCodeEmitter.create(value);
    }

    public static CodeEmitter direct(Consumer<CodegenWriter> consumer) {
        return DirectEmitter.create(consumer);
    }

    public static CodeEmitter emptyInline() {
        return LiteralInlineCodeEmitter.empty();
    }

    public static void emitJoining(CodegenWriter writer, Collection<? extends CodeEmitter> emitters, String separator,
                                   String prefix,
                                   String suffix) {
        writer.writeInlineWithNoFormatting(prefix);
        var isFirst = true;
        for (var emitter : emitters) {
            if (!isFirst) {
                writer.writeInlineWithNoFormatting(separator);
            }
            emitter.emit(writer);
            isFirst = false;
        }
        writer.writeInlineWithNoFormatting(suffix);
    }
}
