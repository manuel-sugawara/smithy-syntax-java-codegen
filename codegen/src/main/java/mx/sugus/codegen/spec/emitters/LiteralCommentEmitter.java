package mx.sugus.codegen.spec.emitters;

import java.util.Objects;
import mx.sugus.codegen.writer.CodegenWriter;

public class LiteralCommentEmitter implements CodeEmitter {
    private final String value;

    private LiteralCommentEmitter(String value) {
        this.value = Objects.requireNonNull(value);
    }

    public static CodeEmitter create(String value) {
        return new LiteralCommentEmitter(value);
    }

    @Override
    public void emit(CodegenWriter writer) {
        writer.writeWithNoFormatting(value);
    }
}
