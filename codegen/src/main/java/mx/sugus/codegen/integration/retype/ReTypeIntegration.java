package mx.sugus.codegen.integration.retype;

import mx.sugus.codegen.JavaCodegenSettings;
import mx.sugus.codegen.integration.JavaCodegenIntegration;
import software.amazon.smithy.codegen.core.Symbol;
import software.amazon.smithy.codegen.core.SymbolProvider;
import software.amazon.smithy.model.Model;

import java.util.UUID;


public class ReTypeIntegration implements JavaCodegenIntegration {
    private static final String INTEGRATION_NAME = "Annotation-Integration";
    private static final String TEST_TRAIT_NAME = "example.weather#uuidTrait";

    @Override
    public String name() {
        return INTEGRATION_NAME;
    }

    @Override
    public SymbolProvider decorateSymbolProvider(Model model, JavaCodegenSettings settings, SymbolProvider symbolProvider) {
        return shape -> {
            if (shape.hasTrait(TEST_TRAIT_NAME)) {
                return getUUIDSymbol();
            } else if (shape.isMemberShape()) {
                var target = model.expectShape(shape.asMemberShape().get().getTarget());
                if (target.hasTrait(TEST_TRAIT_NAME)) {
                    return getUUIDSymbol();
                }
            }
            return symbolProvider.toSymbol(shape);
        };
    }

    private Symbol getUUIDSymbol() {
        return Symbol.builder()
            .name(UUID.class.getSimpleName())
            .namespace(UUID.class.getPackageName(), ".")
            .build();
    }
}
