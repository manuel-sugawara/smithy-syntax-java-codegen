package mx.sugus.codegen.spec.emitters;

import java.util.Objects;
import mx.sugus.codegen.writer.CodegenWriter;

final class FormatInlineCodeEmitter implements CodeEmitter {
    private final String format;
    private final Object[] args;

    private FormatInlineCodeEmitter(String format, Object[] args) {
        this.format = Objects.requireNonNull(format);
        this.args = Objects.requireNonNull(args);
    }

    public static CodeEmitter create(String format, Object[] args) {
        return new FormatInlineCodeEmitter(format, args);
    }

    @Override
    public void emit(CodegenWriter writer) {
        writer.writeInline(format, args);
    }

}
