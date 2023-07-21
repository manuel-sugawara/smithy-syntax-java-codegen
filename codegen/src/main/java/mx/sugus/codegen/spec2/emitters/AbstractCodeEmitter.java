package mx.sugus.codegen.spec2.emitters;

import mx.sugus.codegen.writer.CodegenWriter;

public abstract class AbstractCodeEmitter implements CodeEmitter {

    @Override
    public String toString() {
        var writer = new CodegenWriter("");
        emit(writer);
        return writer.toString();
    }
}
