package mx.sugus.codegen.spec3;

import java.util.Arrays;
import javax.lang.model.element.Modifier;
import mx.sugus.codegen.SymbolConstants;
import mx.sugus.codegen.spec3.syntax.LiteralStatement;
import mx.sugus.codegen.spec3.syntax.MethodSyntax;
import mx.sugus.codegen.spec3.syntax.MethodBodySyntax;
import mx.sugus.codegen.spec3.syntax.RewriteVisitor;
import mx.sugus.codegen.spec3.syntax.SyntaxNode;
import org.junit.jupiter.api.Test;

class RewriteVisitorTest {

    @Test
    public void test0() {
        var method = MethodSyntax.builder("toString")
                                 .addModifiers(Arrays.asList(Modifier.PUBLIC, Modifier.FINAL))
                                 .returnType(SymbolConstants.toSymbol(String.class))
                                 .body(MethodBodySyntax.builder()
                                                 .addStatement(LiteralStatement.create("return String.valueOf(this)"))
                                                 .build())
                                 .build();
        var rewriter = new RewriteVisitor() {
            @Override
            public SyntaxNode visitMethod(MethodSyntax m) {
                m = (MethodSyntax) super.visitMethod(m);
                if ("toString".equals(m.getName())) {
                    return m.toBuilder()
                            .body(MethodBodySyntax
                                      .builder()
                                      .addStatement(LiteralStatement.create("return super.toString()"))
                                      .build())
                            .build();
                }
                return m;
            }
        };

        var newMethod = rewriter.visit(method);
        System.out.printf("================= prev:\n%s\n"
                          + "=================  new:\n%s\n",
                          method.toString(), newMethod.toString());
    }

}