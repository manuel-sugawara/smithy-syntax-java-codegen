package mx.sugus.codegen.spec2.emitters;

import java.util.Objects;
import mx.sugus.codegen.writer.CodegenWriter;

public final class LiteralInlineCodeEmitter extends AbstractCodeEmitter {
    public static final LiteralInlineCodeEmitter EMPTY = new LiteralInlineCodeEmitter("");
    private final String value;

    private LiteralInlineCodeEmitter(String value) {
        this.value = Objects.requireNonNull(value);
    }

    public static CodeEmitter create(String value) {
        return new LiteralInlineCodeEmitter(value);
    }

    public static CodeEmitter empty() {
        return EMPTY;
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LiteralInlineCodeEmitter)) {
            return false;
        }
        LiteralInlineCodeEmitter that = (LiteralInlineCodeEmitter) o;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public void emit(CodegenWriter writer) {
        writer.writeInlineWithNoFormatting(value);
    }
}
