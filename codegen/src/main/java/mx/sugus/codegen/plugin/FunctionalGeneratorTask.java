package mx.sugus.codegen.plugin;

import java.util.List;
import java.util.function.Function;

public class FunctionalGeneratorTask<T, R> implements GeneratorTask<R> {
    private final Class<T> input;
    private final Class<R> output;
    private final Function<T, R> handler;

    public FunctionalGeneratorTask(Class<T> input, Class<R> output, Function<T, R> handler) {
        this.input = input;
        this.output = output;
        this.handler = handler;
    }

    public FunctionalGeneratorTask(Builder<T, R> builder) {
        this.input = builder.input;
        this.output = builder.output;
        this.handler = builder.handler;
    }

    @Override
    public List<Class<?>> input() {
        return List.of(input);
    }

    @Override
    public Class<R> output() {
        return output;
    }

    @Override
    public R invoke(Object[] args) {
        return handler.apply(input.cast(args[0]));
    }

    public Function<T, R> handler() {
        return handler;
    }

    public static class Builder<T, R> {

        private Class<T> input;
        private Class<R> output;
        private Function<T, R> handler;

        public Builder<T, R> input(Class<T> input) {
            this.input = input;
            return this;
        }

        public Builder<T, R> output(Class<R> output) {
            this.output = output;
            return this;
        }

        public Builder<T, R> handler(Function<T, R> handler) {
            this.handler = handler;
            return this;
        }

        public FunctionalGeneratorTask<T, R> build() {
            return new FunctionalGeneratorTask<>(this);
        }

    }

}
