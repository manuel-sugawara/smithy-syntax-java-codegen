package mx.sugus.plugin;

public class BaseModule {

    ClassRewriterContribution contributeArchetypeRewriter(ClassRewriter rewriter) {
        var c = ClassRewriterContribution.builder()
            .rewritter(x -> x)
            .archetype(CodegenArchetype.STRUCTURE)
            .ordering()
            .build();

        return c;
    }
}
