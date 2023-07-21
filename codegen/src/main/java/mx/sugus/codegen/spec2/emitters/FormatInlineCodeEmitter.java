package mx.sugus.codegen.spec2.emitters;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import mx.sugus.codegen.writer.CodegenWriter;

public final class FormatInlineCodeEmitter extends AbstractCodeEmitter {
    private final String format;
    private final Object[] args;

    private FormatInlineCodeEmitter(String format, Object[] args) {
        this.format = Objects.requireNonNull(format);
        this.args = Objects.requireNonNull(args);
    }

    public static CodeEmitter create(String format, Object[] args) {
        return new FormatInlineCodeEmitter(format, args);
    }

    public String format() {
        return format;
    }

    public List<Object> args() {
        return Arrays.asList(args);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FormatInlineCodeEmitter)) {
            return false;
        }
        FormatInlineCodeEmitter that = (FormatInlineCodeEmitter) o;
        return format.equals(that.format) && Arrays.equals(args, that.args);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(format);
        result = 31 * result + Arrays.hashCode(args);
        return result;
    }

    @Override
    public void emit(CodegenWriter writer) {
        writer.writeInline(format, args);
    }

}
