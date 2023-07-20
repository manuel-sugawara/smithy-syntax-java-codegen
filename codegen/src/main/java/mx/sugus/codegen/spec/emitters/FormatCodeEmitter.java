package mx.sugus.codegen.spec.emitters;

import mx.sugus.codegen.writer.CodegenWriter;

final class FormatCodeEmitter implements CodeEmitter {

    private final String format;
    private final Object[] args;

    private FormatCodeEmitter(String format, Object[] args) {
        this.format = format;
        this.args = args;
    }

    public static CodeEmitter create(String format, Object[] args) {
        return new FormatCodeEmitter(format, args);
    }

    @Override
    public void emit(CodegenWriter writer) {
        writer.writeInline(format, args)
              .write(";");
    }
}
