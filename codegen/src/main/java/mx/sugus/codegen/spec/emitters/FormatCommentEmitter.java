package mx.sugus.codegen.spec.emitters;

import mx.sugus.codegen.writer.CodegenWriter;

final class FormatCommentEmitter implements CodeEmitter {

    private final String format;
    private final Object[] args;

    private FormatCommentEmitter(String format, Object[] args) {
        this.format = format;
        this.args = args;
    }

    public static CodeEmitter create(String format, Object[] args) {
        return new FormatCommentEmitter(format, args);
    }

    @Override
    public void emit(CodegenWriter writer) {
        writer.write(format, args);
    }
}
