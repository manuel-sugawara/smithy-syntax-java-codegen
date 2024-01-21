package mx.sugus.util;

import java.util.function.Function;
import java.util.function.Supplier;

public class DefaultBuilderReference<P, T> implements BuilderReference<P, T> {
    private final Supplier<T> emptyTransient;
    private final Function<T, T> transientCopyConstructor;
    private final Function<T, P> transientToPersistent;
    private final Function<P, T> persistentToTransient;
    private final Function<T, T> clearTransient;

    private final Supplier<P> emptyPersistent;
    private P asPersistent;
    private T asTransient;

    DefaultBuilderReference(
        Supplier<T> emptyTransient,
        Function<T, T> transientCopyConstructor,
        Function<T, P> transientToPersistent,
        Function<P, T> persistentToTransient,
        Function<T, T> clearTransient,
        Supplier<P> emptyPersistent,
        P asPersistent
    ) {
        this.emptyTransient = emptyTransient;
        this.transientCopyConstructor = transientCopyConstructor;
        this.transientToPersistent = transientToPersistent;
        this.persistentToTransient = persistentToTransient;
        this.clearTransient = clearTransient;
        this.emptyPersistent = emptyPersistent;
        this.asPersistent = asPersistent;
    }

    @Override
    public P asPersistent() {
        if (asPersistent == null) {
            if (asTransient == null) {
                return emptyPersistent.get();
            }
            asPersistent = transientToPersistent.apply(transientCopyConstructor.apply(asTransient));
            asTransient = null;
        }
        return asPersistent;
    }

    @Override
    public T asTransient() {
        if (asTransient == null) {
            if (asPersistent == null) {
                asTransient = emptyTransient.get();
                return asTransient;
            }
            asTransient = persistentToTransient.apply(asPersistent);
            asPersistent = null;
        }
        return asTransient;
    }

    @Override
    public void clear() {
        if (asTransient != null) {
            asTransient = clearTransient.apply(asTransient);
        }
    }
}
