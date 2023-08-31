package mx.sugus.plugin;

import java.util.function.Function;
import software.amazon.smithy.codegen.core.SymbolProvider;

public class SymbolDecoratorContributor {

    public SymbolDecoratorContributor(Builder builder) {
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        public Function<SymbolProvider,SymbolProvider> contributor;

        public Builder contributor(Function<SymbolProvider,SymbolProvider> contributor) {
            this.contributor = contributor;
            return this;
        }

        public SymbolDecoratorContributor build() {
            return new SymbolDecoratorContributor(this);
        }
    }
}
