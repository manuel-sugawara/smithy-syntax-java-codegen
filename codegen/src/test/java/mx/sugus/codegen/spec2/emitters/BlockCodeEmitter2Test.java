package mx.sugus.codegen.spec2.emitters;

import org.junit.jupiter.api.Test;

class BlockCodeEmitter2Test {

    @Test
    public void test0() {
        var builder = BlockCodeEmitter2.builder();
        builder.ifStatement("isA || isB", body -> {
            body.ifStatement("isA", isATrue -> {
                isATrue.addStatement("System.out.printf($S, isA)", "is A is true: %s")
                       .addStatement("return false");
            }, isAFalse -> {
                isAFalse.addStatement("System.out.printf($S, isA)", "NOPE, is A is false: %s");
            });
            body.addStatement("System.out.printf($S, isA)", "is B is true: %s");
            body.addStatement("return true");
        });

        System.out.printf("result: %s\n", builder.build().toString());
    }
}