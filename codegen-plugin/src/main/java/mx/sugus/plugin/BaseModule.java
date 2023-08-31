package mx.sugus.plugin;

import java.util.function.Function;

public class BaseModule {

    public ClassRewriterContribution contributeArchetypeRewriter() {
        return ClassRewriterContribution
            .builder()
            .rewritter(x -> x)
            .archetype(CodegenArchetype.STRUCTURE)
            .ordering()
            .build();
    }

    public InterceptorContributor contributeHelloWorld() {
        return InterceptorContributor
            .builder()
            .section(String.class)
            .interceptor(b -> {
                b.addStatement("$T.out.printf($S)", System.class, "Hello class section");
            })
            .build();
    }

    public SymbolDecoratorContributor contributeSymbolDecorator() {
        return SymbolDecoratorContributor.builder()
            .contributor(Function.identity())
            .build();
    }
}
