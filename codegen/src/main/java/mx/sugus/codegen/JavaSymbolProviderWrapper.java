package mx.sugus.codegen;

import java.util.Objects;
import mx.sugus.codegen.util.Name;
import software.amazon.smithy.codegen.core.Symbol;
import software.amazon.smithy.codegen.core.SymbolProvider;
import software.amazon.smithy.model.shapes.MemberShape;
import software.amazon.smithy.model.shapes.Shape;

public class JavaSymbolProviderWrapper implements JavaSymbolProvider {
    private final SymbolProvider symbolProvider;

    public JavaSymbolProviderWrapper(SymbolProvider symbolProvider) {
        this.symbolProvider = Objects.requireNonNull(symbolProvider, "symbolProvider");
    }

    @Override
    public Symbol toSymbol(Shape shape) {
        return symbolProvider.toSymbol(shape);
    }

    @Override
    public String toMemberName(MemberShape shape) {
        return symbolProvider.toMemberName(shape);
    }
}

