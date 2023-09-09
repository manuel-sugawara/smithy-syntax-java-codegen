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
    private final Map<ShapeType, Set<ShapeBaseTask<?>>> inits;
    private final Map<Identifier, Set<ShapeBaseTask<?>>> interceptors;
    private final Map<Class<?>, Set<ShapeBaseTask<?>>> serializers;

    BaseModuleConfig(Builder builder) {
        var inits = new LinkedHashMap<ShapeType, Set<ShapeBaseTask<?>>>();
        for (var kvp : builder.inits.entrySet()) {
            inits.put(kvp.getKey(), Collections.unmodifiableSet(new LinkedHashSet<ShapeBaseTask<?>>(kvp.getValue())));
        }
        this.inits = Collections.unmodifiableMap(inits);

        var interceptors = new LinkedHashMap<Identifier, Set<ShapeBaseTask<?>>>();
        for (var kvp : builder.interceptors.entrySet()) {
            interceptors.put(kvp.getKey(), Collections.unmodifiableSet(new LinkedHashSet<ShapeBaseTask<?>>(kvp.getValue())));
        }
        this.interceptors = Collections.unmodifiableMap(interceptors);

        var serializers = new LinkedHashMap<Class<?>, Set<ShapeBaseTask<?>>>();
        for (var kvp : builder.serializers.entrySet()) {
            serializers.put(kvp.getKey(), Collections.unmodifiableSet(new LinkedHashSet<>(kvp.getValue())));
        }
        this.serializers = Collections.unmodifiableMap(serializers);
    }

    public static Builder builder() {
        return new Builder();
    }

    public Collection<ShapeBaseTask<?>> inits(Shape shape) {
        return inits.getOrDefault(shape.getType(), Collections.emptySet());
    }

    public <T> Collection<ShapeBaseTask<T>> interceptors(ShapeBaseTask<T> task) {
        return interceptors.getOrDefault(task.taskId(), Collections.emptySet())
                           .stream()
                           // This might hide bugs, at lest a log entry will be adequate
                           .filter(x -> x.outputs().equals(task.outputs()))
                           .map(x -> (ShapeBaseTask<T>) x)
                           .collect(Collectors.toList());
    }

    public <T> Collection<ShapeBaseTask<T>> serializers(
        JavaShapeDirective directive,
        ShapeBaseTask<T> task
    ) {
        return serializers.getOrDefault(task.outputs(), Collections.emptySet())
                          .stream()
                          .map(x -> (ShapeBaseTask<T>) x)
                          .collect(Collectors.toSet());
    }

    public static class Builder {
        private Map<ShapeType, Set<ShapeBaseTask<?>>> inits = new LinkedHashMap<>();
        private Map<Identifier, Set<ShapeBaseTask<?>>> interceptors = new LinkedHashMap<>();
        private Map<Class<?>, Set<ShapeBaseTask<?>>> serializers = new LinkedHashMap<>();

        public Builder() {
        }

        public Builder addInit(ShapeType type, ShapeTask task) {
            inits.computeIfAbsent(type, t -> new LinkedHashSet<>())
                 .add(task);
            return this;
        }

        public Builder addInit(ShapeBaseTask<?> task) {
            inits.computeIfAbsent(task.type(), t -> new LinkedHashSet<>())
                 .add(task);
            return this;
        }

        public Builder putInterceptor(Identifier interceptsProducer, ShapeBaseTask<?> interceptor) {
            interceptors.computeIfAbsent(interceptsProducer, t -> new LinkedHashSet<>())
                        .add(interceptor);
            return this;
        }

        public Builder addSerializer(ShapeSerializer<?> serializer) {
            serializers.computeIfAbsent(serializer.outputs(), t -> new LinkedHashSet<>())
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
