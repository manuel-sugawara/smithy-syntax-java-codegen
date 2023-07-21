package mx.sugus.codegen.spec2.emitters;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import mx.sugus.codegen.writer.CodegenWriter;

public final class StatementEmitter extends AbstractCodeEmitter {

    private final String format;
    private final Object[] args;

    private StatementEmitter(String format, Object[] args) {
        this.format = Objects.requireNonNull(format);
        this.args = Objects.requireNonNull(args);
    }

    public static CodeEmitter create(String format, Object[] args) {
        return new StatementEmitter(format, args);
    }

    public String format() {
        return format;
    }

    public List<Object> args() {
        return Arrays.asList(args);
    }

    @Override
    public void emit(CodegenWriter writer) {
        writer.writeInline(format, args)
              .write(";");
    }
}
