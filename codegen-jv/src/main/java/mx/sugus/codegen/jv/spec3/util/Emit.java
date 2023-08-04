package mx.sugus.codegen.jv.spec3.util;

import java.util.Collection;
import mx.sugus.codegen.jv.spec3.syntax.SyntaxNode;
import mx.sugus.codegen.jv.writer.CodegenWriter;

public final class Emit {
    public static void emitJoining(
        CodegenWriter writer, Collection<? extends SyntaxNode> emitters,
        String separator,
        String prefix,
        String suffix
    ) {
        writer.writeInlineWithNoFormatting(prefix);
        var isFirst = true;
        for (var emitter : emitters) {
            if (!isFirst) {
                writer.writeInlineWithNoFormatting(separator);
            }
            emitter.emit(writer);
            isFirst = false;
        }
        writer.writeInlineWithNoFormatting(suffix);
    }

}
