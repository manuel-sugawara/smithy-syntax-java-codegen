package mx.sugus.codegen.spec2.emitters;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import mx.sugus.codegen.writer.CodegenWriter;

public class BlockCodeEmitter extends AbstractCodeEmitter {
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

    public CodeEmitter prefix() {
        return prefix;
    }

    public List<CodeEmitter> contents() {
        return contents;
    }

    public boolean hasNext() {
        return hasNext;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BlockCodeEmitter)) {
            return false;
        }
        BlockCodeEmitter that = (BlockCodeEmitter) o;
        return hasNext == that.hasNext && prefix.equals(that.prefix) && contents.equals(that.contents);
    }

    @Override
    public int hashCode() {
        return Objects.hash(prefix, contents, hasNext);
    }

    public static class Builder<B extends Builder> {
        private CodeEmitter prefix;
        private List<CodeEmitter> contents = new ArrayList<>();
        private boolean hasNext = false;

        Builder() {
        }

        public B prefix(CodeEmitter prefix) {
            this.prefix = prefix;
            return (B) this;
        }

        public B addContent(CodeEmitter content) {
            this.contents.add(content);
            return (B) this;
        }

        public B hasNext() {
            this.hasNext = true;
            return (B) this;
        }

        public BlockCodeEmitter build() {
            return new BlockCodeEmitter(this);
        }
    }
}
