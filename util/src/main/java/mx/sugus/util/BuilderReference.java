package mx.sugus.util;

public interface BuilderReference<P, T> {

    P asPersistent();

    T asTransient();

    void clear();
}
