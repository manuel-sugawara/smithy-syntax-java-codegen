package mx.sugus.codegen.spec2.emitters;

import java.util.Objects;
import java.util.function.Consumer;
import mx.sugus.codegen.writer.CodegenWriter;

public final class DirectEmitter extends AbstractCodeEmitter {
    private final Consumer<CodegenWriter> emitter;

    private DirectEmitter(Consumer<CodegenWriter> emitter) {
        this.emitter = Objects.requireNonNull(emitter);
    }

    public static CodeEmitter create(Consumer<CodegenWriter> emitter) {
        return new DirectEmitter(emitter);
    }

    @Override
    public void emit(CodegenWriter writer) {
        emitter.accept(writer);
    }
}
