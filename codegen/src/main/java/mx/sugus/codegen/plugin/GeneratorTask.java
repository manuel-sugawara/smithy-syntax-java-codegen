package mx.sugus.codegen.plugin;

import java.util.List;

public interface GeneratorTask<T> {

    Class<T> output();
    T invoke(Object[] args);
}
