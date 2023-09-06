package mx.sugus.codegen.plugin;

import java.util.List;
import java.util.function.BiFunction;

public class BiFunctionalGeneratorTask<T, U, R> implements GeneratorTask<R> {

    private final Class<T> leftInput;
    private final Class<U> rightInput;
    private final Class<R> output;

    private final BiFunction<T, U, R> handler;

    public BiFunctionalGeneratorTask(Class<T> leftInput, Class<U> rightInput, Class<R> output, BiFunction<T, U, R> handler) {
        this.leftInput = leftInput;
        this.rightInput = rightInput;
        this.output = output;
        this.handler = handler;
    }

    @Override
    public List<Class<?>> input() {
        return List.of(leftInput, rightInput);
    }

    @Override
    public Class<R> output() {
        return output;
    }

    @Override
    public R invoke(Object[] args) {
        return handler.apply(leftInput.cast(args[0]), rightInput.cast(args[1]));
    }

    public BiFunction<T, U, R> handler() {
        return handler;
    }
}
