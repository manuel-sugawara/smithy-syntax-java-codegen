package mx.sugus.codegen.spec.emitters;

import java.util.ArrayList;
import java.util.List;
import mx.sugus.codegen.writer.CodegenWriter;

public class BlockCodeEmitter implements CodeEmitter {
    private final CodeEmitter prefix;
    private final List<CodeEmitter> contents;
    private final boolean hasNext;

    private BlockCodeEmitter(Builder builder) {
        this.prefix = builder.prefix;
        this.contents = builder.contents;
        this.hasNext = builder.hasNext;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public void emit(CodegenWriter writer) {
        prefix.emit(writer);
        writer.write(" {")
              .indent();
        for (var emitter : contents) {
            emitter.emit(writer);
        }
        if (hasNext) {
            writer.dedent().writeInlineWithNoFormatting("} ");
        } else {
            writer.dedent().writeWithNoFormatting("}");
        }
    }

    public static class Builder {
        private CodeEmitter prefix;
        private List<CodeEmitter> contents = new ArrayList<>();
        private boolean hasNext = false;

        Builder() {
        }

        public Builder prefix(CodeEmitter prefix) {
            this.prefix = prefix;
            return this;
        }

        public Builder addContent(CodeEmitter content) {
            this.contents.add(content);
            return this;
        }

        public Builder hasNext() {
            this.hasNext = true;
            return this;
        }

        public BlockCodeEmitter build() {
            return new BlockCodeEmitter(this);
        }
    }
}
