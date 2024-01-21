package mx.sugus.util;

import java.util.function.Function;
import java.util.function.Supplier;
import mx.sugus.util.CollectionBuilderReference;
import mx.sugus.util.DefaultBuilderReference;

public class DefaultCollectionBuilderReference<T> extends DefaultBuilderReference<T, T> implements CollectionBuilderReference<T> {
    DefaultCollectionBuilderReference(Supplier<T> emptyTransient, Function<T, T> transientCopyConstructor,
                                      Function<T, T> transientToPersistent, Function<T, T> persistentToTransient, Function<T, T> clearTransient, Supplier<T> emptyPersistent, T asPersistent) {
        super(emptyTransient, transientCopyConstructor, transientToPersistent, persistentToTransient, clearTransient,
              emptyPersistent, asPersistent);
    }
}
