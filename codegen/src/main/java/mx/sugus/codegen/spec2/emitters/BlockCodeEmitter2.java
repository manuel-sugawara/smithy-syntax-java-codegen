package mx.sugus.codegen.spec2.emitters;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import mx.sugus.codegen.writer.CodegenWriter;

public class BlockCodeEmitter2<B extends BlockCodeEmitter2.Builder<B, T>, T extends BlockCodeEmitter2<B, T>>
    extends AbstractCodeEmitter {
    protected final CodeEmitter prefix;
    protected final List<CodeEmitter> contents;
    protected final boolean hasNext;

    protected BlockCodeEmitter2(Builder<B, T> builder) {
        this.prefix = builder.prefix;
        this.contents = List.copyOf(builder.contents);
        this.hasNext = builder.hasNext;
    }

    public static <B extends BlockCodeEmitter2.Builder<B, T>, T extends BlockCodeEmitter2<B, T>> Builder<B, T> builder() {
        return new Builder<>();
    }

    @Override
    public void emit(CodegenWriter writer) {
        prefix().emit(writer);
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

    public CodeEmitter prefix() {
        return prefix;
    }

    @SuppressWarnings({"unchecked","rawtypes"})
    public static class Builder<B extends Builder<B, T>, T extends BlockCodeEmitter2> {
        protected CodeEmitter prefix;
        protected Deque<BlockCodeEmitter2.Builder<?, ?>> state = new ArrayDeque<>();
        protected List<CodeEmitter> contents = new ArrayList<>();
        protected boolean hasNext = false;

        protected Builder(CodeEmitter prefix) {
            this.prefix = prefix;
            this.state.push(this);
        }

        protected Builder() {
            this.prefix = LiteralInlineCodeEmitter.EMPTY;
            this.state.push(this);
        }

        public B prefix(CodeEmitter prefix) {
            this.prefix = prefix;
            return (B) this;
        }

        protected B setHasNext() {
            this.hasNext = true;
            return (B) this;
        }

        public B addStatement(CodeEmitter content) {
            contents.add(content);
            return (B) this;
        }

        public B addStatement(String line) {
            assert state.peekFirst() != null;
            state.peekFirst().addStatement(Emitters.literal(line));
            return (B) this;
        }

        public B addStatement(String format, Object... params) {
            assert state.peekFirst() != null;
            state.peekFirst().addStatement(Emitters.format(format, params));
            return (B) this;
        }

        public B startControlFlow(String content) {
            var blockBuilder = builder();
            blockBuilder.prefix(Emitters.literalInline(content));
            state.push(blockBuilder);
            return (B) this;
        }

        public B startControlFlow(String format, Object... args) {
            var blockBuilder = builder();
            blockBuilder.prefix(Emitters.formatInline(format, args));
            state.push(blockBuilder);
            return (B) this;
        }

        public B nextControlFlow(String content) {
            var blockBuilder = builder();
            blockBuilder.prefix(Emitters.literalInline(content));
            var previous = state.pop();
            assert state.peek() != null;
            state.peek().addStatement(previous.setHasNext().build());
            state.push(blockBuilder);
            return (B) this;
        }

        public B nextControlFlow(String format, Object... args) {
            if (state.size() <= 1) {
                throw new IllegalStateException("nextControlFlow requires a previous call to startControlFlow");
            }
            var blockBuilder = builder();
            blockBuilder.prefix(Emitters.format(format, args));
            var previous = state.pop();
            state.peek().addStatement(previous.setHasNext().build());
            state.push(blockBuilder);
            return (B) this;
        }

        public B endControlFlow() {
            if (state.size() <= 1) {
                throw new IllegalStateException("nextControlFlow requires a previous call to startControlFlow");
            }
            var previous = state.pop();
            assert state.peek() != null;
            state.peek().addStatement(previous.build());
            return (B) this;
        }

        public B ifStatement(String condition, Consumer<Builder<B, T>> body) {
            startControlFlow("if (" + Objects.requireNonNull(condition) + ")");
            body.accept(this);
            endControlFlow();
            return (B) this;
        }

        public B ifStatement(String condition, Consumer<Builder<B, T>> body, Consumer<Builder<B, T>> elseBody) {
            startControlFlow("if (" + Objects.requireNonNull(condition) + ")");
            body.accept(this);
            nextControlFlow("else");
            elseBody.accept(this);
            endControlFlow();
            return (B) this;
        }

        public T build() {
            return (T) new BlockCodeEmitter2(this);
        }
    }
}
