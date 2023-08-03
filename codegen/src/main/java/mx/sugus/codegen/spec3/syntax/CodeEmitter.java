package mx.sugus.codegen.spec3.syntax;

import mx.sugus.codegen.writer.CodegenWriter;

public interface CodeEmitter {
    void emit(CodegenWriter writer);

    default void emitInline(CodegenWriter writer) {
        this.emit(writer);
    }
}
