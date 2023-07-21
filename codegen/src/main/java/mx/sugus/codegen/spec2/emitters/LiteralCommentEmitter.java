package mx.sugus.codegen.spec2.emitters;

import java.util.Objects;
import mx.sugus.codegen.writer.CodegenWriter;

public final class LiteralCommentEmitter extends AbstractCodeEmitter {
    private final String value;

    private LiteralCommentEmitter(String value) {
        this.value = Objects.requireNonNull(value);
    }

    public static CodeEmitter create(String value) {
        return new LiteralCommentEmitter(value);
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LiteralCommentEmitter)) {
            return false;
        }
        LiteralCommentEmitter that = (LiteralCommentEmitter) o;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public void emit(CodegenWriter writer) {
        writer.writeWithNoFormatting(value);
    }
}
