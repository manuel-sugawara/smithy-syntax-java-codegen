package mx.sugus.plugin;

import java.util.function.Consumer;
import mx.sugus.javapoet.AbstractBlockBuilder;

public class InterceptorContributor {

    public InterceptorContributor(Builder builder) {
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Class<?> section;
        private Consumer<AbstractBlockBuilder<?,?>> interceptor;

        public Builder section(Class<?> section) {
            this.section = section;
            return this;
        }

        public Builder interceptor(Consumer<AbstractBlockBuilder<?,?>> interceptor) {
            this.interceptor = interceptor;
            return this;
        }

        public InterceptorContributor build() {
            return new InterceptorContributor(this);
        }
    }
}
