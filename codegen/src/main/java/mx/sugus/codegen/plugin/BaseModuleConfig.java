package mx.sugus.codegen.plugin;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import software.amazon.smithy.model.shapes.Shape;
import software.amazon.smithy.model.shapes.ShapeType;

public class BaseModuleConfig {
    private final Map<ShapeType, Set<ShapeTask>> inits;
    private final Map<Identifier, Set<ShapeTaskInterceptor>> interceptors;
    private final Map<Identifier, Set<ShapeSerializer>> serializers;

    BaseModuleConfig(Builder builder) {
        var inits = new LinkedHashMap<ShapeType, Set<ShapeTask>>();
        for (var kvp : builder.inits.entrySet()) {
            inits.put(kvp.getKey(), Collections.unmodifiableSet(new LinkedHashSet<>(kvp.getValue())));
        }
        this.inits = Collections.unmodifiableMap(inits);

        var interceptors = new LinkedHashMap<Identifier, Set<ShapeTaskInterceptor>>();
        for (var kvp : builder.interceptors.entrySet()) {
            interceptors.put(kvp.getKey(), Collections.unmodifiableSet(new LinkedHashSet<>(kvp.getValue())));
        }
        this.interceptors = Collections.unmodifiableMap(interceptors);

        var serializers = new LinkedHashMap<Identifier, Set<ShapeSerializer>>();
        for (var kvp : builder.serializers.entrySet()) {
            serializers.put(kvp.getKey(), Collections.unmodifiableSet(new LinkedHashSet<>(kvp.getValue())));
        }
        this.serializers = Collections.unmodifiableMap(serializers);
    }

    public Collection<ShapeTask> inits(Shape shape) {
        return Collections.emptyList();
    }

    public Collection<ShapeTaskInterceptor> interceptors(ShapeTask task) {
        return Collections.emptyList();
    }

    public Collection<ShapeSerializer> serializers(
        JavaShapeDirective directive,
        ShapeTask task
    ) {
        return Collections.emptyList();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Map<ShapeType, Set<ShapeTask>> inits = new LinkedHashMap<>();
        private Map<Identifier, Set<ShapeTaskInterceptor>> interceptors = new LinkedHashMap<>();
        private Map<Identifier, Set<ShapeSerializer>> serializers = new LinkedHashMap<>();

        public Builder() {
        }

        public Builder addInit(ShapeType type, ShapeTask task) {
            inits.computeIfAbsent(type, t -> new LinkedHashSet<>())
                 .add(task);
            return this;
        }

        public Builder addInterceptor(Identifier id, ShapeTaskInterceptor interceptor) {
            interceptors.computeIfAbsent(id, t -> new LinkedHashSet<>())
                        .add(interceptor);
            return this;
        }

        public Builder addSerializer(Identifier id, ShapeSerializer serializer) {
            serializers.computeIfAbsent(id, t -> new LinkedHashSet<>())
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
