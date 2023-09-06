package mx.sugus.codegen.plugin;

import java.util.List;

public interface GeneratorTask<T> {
    List<Class<?>> input();
    Class<T> output();
    T invoke(Object[] args);
}
