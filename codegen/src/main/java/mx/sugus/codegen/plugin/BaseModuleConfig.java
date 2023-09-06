package mx.sugus.codegen.plugin;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import software.amazon.smithy.model.shapes.Shape;
import software.amazon.smithy.model.shapes.ShapeType;

@SuppressWarnings("unchecked")
public class BaseModuleConfig {
    private final Map<ShapeType, Set<ShapeTask>> inits;
    private final Map<Identifier, Set<ShapeTaskInterceptor<?>>> interceptors;
    private final Map<Class<?>, Set<ShapeSerializer<?>>> serializers;

    BaseModuleConfig(Builder builder) {
        var inits = new LinkedHashMap<ShapeType, Set<ShapeTask>>();
        for (var kvp : builder.inits.entrySet()) {
            inits.put(kvp.getKey(), Collections.unmodifiableSet(new LinkedHashSet<>(kvp.getValue())));
        }
        this.inits = Collections.unmodifiableMap(inits);

        var interceptors = new LinkedHashMap<Identifier, Set<ShapeTaskInterceptor<?>>>();
        for (var kvp : builder.interceptors.entrySet()) {
            interceptors.put(kvp.getKey(), Collections.unmodifiableSet(new LinkedHashSet<>(kvp.getValue())));
        }
        this.interceptors = Collections.unmodifiableMap(interceptors);

        var serializers = new LinkedHashMap<Class<?>, Set<ShapeSerializer<?>>>();
        for (var kvp : builder.serializers.entrySet()) {
            serializers.put(kvp.getKey(), Collections.unmodifiableSet(new LinkedHashSet<>(kvp.getValue())));
        }
        this.serializers = Collections.unmodifiableMap(serializers);
    }

    public static Builder builder() {
        return new Builder();
    }

    public Collection<ShapeTask> inits(Shape shape) {
        return inits.getOrDefault(shape.getType(), Collections.emptySet());
    }

    public <T> Collection<ShapeTaskInterceptor<T>> interceptors(ShapeTask<T> task) {
        return interceptors.getOrDefault(task.taskId(), Collections.emptySet())
                           .stream()
                           .filter(x -> x.clazz() == task.clazz())
                           .map(x -> (ShapeTaskInterceptor<T>) x)
                           .collect(Collectors.toList());
    }

    public <T> Collection<ShapeSerializer<T>> serializers(
        JavaShapeDirective directive,
        ShapeTask<T> task
    ) {
        return serializers.getOrDefault(task.clazz(), Collections.emptySet())
                          .stream()
                          .map(x -> (ShapeSerializer<T>) x)
                          .collect(Collectors.toSet());
    }

    public static class Builder {
        private Map<ShapeType, Set<ShapeTask>> inits = new LinkedHashMap<>();
        private Map<Identifier, Set<ShapeTaskInterceptor<?>>> interceptors = new LinkedHashMap<>();
        private Map<Class<?>, Set<ShapeSerializer<?>>> serializers = new LinkedHashMap<>();

        public Builder() {
        }

        public Builder addInit(ShapeType type, ShapeTask task) {
            inits.computeIfAbsent(type, t -> new LinkedHashSet<>())
                 .add(task);
            return this;
        }

        public Builder addInterceptor(ShapeTaskInterceptor<?> interceptor) {
            interceptors.computeIfAbsent(interceptor.taskId(), t -> new LinkedHashSet<>())
                        .add(interceptor);
            return this;
        }

        public Builder addSerializer(ShapeSerializer<?> serializer) {
            serializers.computeIfAbsent(serializer.clazz(), t -> new LinkedHashSet<>())
                       .add(serializer);
            return this;
        }

        public Builder merge(BaseModuleConfig other) {
            other.inits.forEach((k, v) -> {
                inits.computeIfAbsent(k, t -> new LinkedHashSet<>())
                     .addAll(v);
            });

            other.interceptors.forEach((k, v) -> {
                interceptors.computeIfAbsent(k, t -> new LinkedHashSet<>())
                            .addAll(v);
            });

            other.serializers.forEach((k, v) -> {
                serializers.computeIfAbsent(k, t -> new LinkedHashSet<>())
                           .addAll(v);
            });
            return this;
        }

        public BaseModuleConfig build() {
            return new BaseModuleConfig(this);
        }
    }
}
