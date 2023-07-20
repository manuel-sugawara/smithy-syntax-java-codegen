package mx.sugus.codegen.spec.emitters;

import java.util.Objects;
import java.util.function.Consumer;
import mx.sugus.codegen.writer.CodegenWriter;

public final class DirectEmitter implements CodeEmitter {
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
