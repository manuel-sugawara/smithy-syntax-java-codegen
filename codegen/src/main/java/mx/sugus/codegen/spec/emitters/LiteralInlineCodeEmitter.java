package mx.sugus.codegen.spec.emitters;

import mx.sugus.codegen.writer.CodegenWriter;

final class LiteralInlineCodeEmitter implements CodeEmitter {
    public static final LiteralInlineCodeEmitter EMPTY = new LiteralInlineCodeEmitter("");
    private final String value;

    private LiteralInlineCodeEmitter(String value) {
        this.value = value;
    }

    public static CodeEmitter create(String value) {
        return new LiteralInlineCodeEmitter(value);
    }

    public static CodeEmitter empty() {
        return EMPTY;
    }

    @Override
    public void emit(CodegenWriter writer) {
        writer.writeInlineWithNoFormatting(value);
    }
}
