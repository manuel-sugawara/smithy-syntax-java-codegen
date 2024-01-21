package mx.sugus.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface CollectionBuilderReference<T> extends BuilderReference<T, T> {

    /**
     * Creates a builder reference to an unordered map.
     *
     * @param <K> Type of key of the map.
     * @param <V> Type of value of the map.
     * @return Returns the created map.
     */
    static <K, V> CollectionBuilderReference<Map<K, V>> forUnorderedMap() {
        return new DefaultCollectionBuilderReference<>(HashMap::new,
                                                       HashMap::new,
                                                       Collections::unmodifiableMap,
                                                       HashMap::new,
                                                       x -> {
                                                           x.clear();
                                                           return x;
                                                       },
                                                       Collections::emptyMap,
                                                       null);
    }

    /**
     * Creates a builder reference to an unordered map borrowing from the given argument.
     *
     * @param <K> Type of key of the map.
     * @param <V> Type of value of the map.
     * @return Returns the created map.
     */
    static <K, V> CollectionBuilderReference<Map<K, V>> fromPersistentUnorderedMap(Map<K, V> borrowed) {
        return new DefaultCollectionBuilderReference<>(HashMap::new,
                                                       HashMap::new,
                                                       Collections::unmodifiableMap,
                                                       HashMap::new,
                                                       x -> {
                                                           x.clear();
                                                           return x;
                                                       },
                                                       Collections::emptyMap,
                                                       borrowed);
    }

    /**
     * Creates a builder reference to a ordered map.
     *
     * @param <K> Type of key of the map.
     * @param <V> Type of value of the map.
     * @return Returns the created map.
     */
    static <K, V> CollectionBuilderReference<Map<K, V>> forOrderedMap() {
        return new DefaultCollectionBuilderReference<>(LinkedHashMap::new,
                                                       LinkedHashMap::new,
                                                       Collections::unmodifiableMap,
                                                       LinkedHashMap::new,
                                                       x -> {
                                                           x.clear();
                                                           return x;
                                                       },
                                                       Collections::emptyMap,
                                                       null);
    }

    /**
     * Creates a builder reference to an ordered map borrowing from the given map.
     *
     * @param <K> Type of key of the map.
     * @param <V> Type of value of the map.
     * @return Returns the created map.
     */
    static <K, V> CollectionBuilderReference<Map<K, V>> fromPersistentOrderedMap(Map<K, V> borrowed) {
        return new DefaultCollectionBuilderReference<>(LinkedHashMap::new,
                                                       LinkedHashMap::new,
                                                       Collections::unmodifiableMap,
                                                       LinkedHashMap::new,
                                                       x -> {
                                                           x.clear();
                                                           return x;
                                                       },
                                                       Collections::emptyMap,
                                                       borrowed);

    }

    /**
     * Creates a builder reference to a list.
     *
     * @param <T> Type of value in the list.
     * @return Returns the created list.
     */
    static <T> CollectionBuilderReference<List<T>> forList() {
        return new DefaultCollectionBuilderReference<>(ArrayList::new,
                                                       ArrayList::new,
                                                       Collections::unmodifiableList,
                                                       ArrayList::new,
                                                       x -> {
                                                           x.clear();
                                                           return x;
                                                       },
                                                       Collections::emptyList,
                                                       null);
    }

    /**
     * Creates a builder reference to a list.
     *
     * @param <T> Type of value in the list.
     * @return Returns the created list.
     */
    static <T> CollectionBuilderReference<List<T>> fromPersistentList(List<T> borrowed) {
        return new DefaultCollectionBuilderReference<>(ArrayList::new,
                                                       ArrayList::new,
                                                       Collections::unmodifiableList,
                                                       ArrayList::new,
                                                       x -> {
                                                           x.clear();
                                                           return x;
                                                       },
                                                       Collections::emptyList,
                                                       borrowed);
    }

    /**
     * Creates a builder reference to an unordered set.
     *
     * @param <T> Type of value in the set.
     * @return Returns the created set.
     */
    static <T> CollectionBuilderReference<Set<T>> forUnorderedSet() {
        return new DefaultCollectionBuilderReference<>(HashSet::new,
                                                       HashSet::new,
                                                       Collections::unmodifiableSet,
                                                       HashSet::new,
                                                       x -> {
                                                           x.clear();
                                                           return x;
                                                       },
                                                       Collections::emptySet,
                                                       null);
    }

    /**
     * Creates a builder reference to an unordered set.
     *
     * @param <T> Type of value in the set.
     * @return Returns the created set.
     */
    static <T> CollectionBuilderReference<Set<T>> fromPersistentUnorderedSet(Set<T> borrowed) {
        return new DefaultCollectionBuilderReference<>(HashSet::new,
                                                       HashSet::new,
                                                       Collections::unmodifiableSet,
                                                       HashSet::new,
                                                       x -> {
                                                           x.clear();
                                                           return x;
                                                       },
                                                       Collections::emptySet,
                                                       borrowed);
    }

    /**
     * Creates a builder reference to an ordered set.
     *
     * @param <T> Type of value in the set.
     * @return Returns the created set.
     */
    static <T> CollectionBuilderReference<Set<T>> forOrderedSet() {
        return new DefaultCollectionBuilderReference<>(LinkedHashSet::new,
                                                       LinkedHashSet::new,
                                                       Collections::unmodifiableSet,
                                                       LinkedHashSet::new,
                                                       x -> {
                                                           x.clear();
                                                           return x;
                                                       },
                                                       Collections::emptySet,
                                                       null);
    }

    /**
     * Creates a builder reference to an ordered set.
     *
     * @param <T> Type of value in the set.
     * @return Returns the created set.
     */
    static <T> CollectionBuilderReference<Set<T>> fromPersistentOrderedSet(Set<T> borrowed) {
        return new DefaultCollectionBuilderReference<>(LinkedHashSet::new,
                                                       LinkedHashSet::new,
                                                       Collections::unmodifiableSet,
                                                       LinkedHashSet::new,
                                                       (x) -> {
                                                           x.clear();
                                                           return x;
                                                       },
                                                       Collections::emptySet,
                                                       borrowed);
    }


}
