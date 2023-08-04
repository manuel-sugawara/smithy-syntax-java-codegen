package mx.sugus.codegen.jv.spec3.syntax;

import mx.sugus.codegen.jv.writer.CodegenWriter;

public interface CodeEmitter {
    void emit(CodegenWriter writer);

    default void emitInline(CodegenWriter writer) {
        this.emit(writer);
    }
}
