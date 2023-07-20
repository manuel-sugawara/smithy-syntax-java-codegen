package mx.sugus.codegen.spec.emitters;

import mx.sugus.codegen.writer.CodegenWriter;

final class LiteralCodeEmitter implements CodeEmitter {
    private final String value;

    private LiteralCodeEmitter(String value) {
        this.value = value;
    }

    public static CodeEmitter create(String value) {
        return new LiteralCodeEmitter(value);
    }

    @Override
    public void emit(CodegenWriter writer) {
        writer.writeInlineWithNoFormatting(value)
              .write(";");
    }
}
