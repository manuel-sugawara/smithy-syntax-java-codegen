package mx.sugus.codegen.jv.spec3;

import java.util.Arrays;
import javax.lang.model.element.Modifier;
import mx.sugus.codegen.jv.SymbolConstants;
import mx.sugus.codegen.jv.spec3.syntax.LiteralStatement;
import mx.sugus.codegen.jv.spec3.syntax.Method;
import mx.sugus.codegen.jv.spec3.syntax.MethodBody;
import mx.sugus.codegen.jv.spec3.syntax.RewriteVisitor;
import mx.sugus.codegen.jv.spec3.syntax.SyntaxNode;
import org.junit.jupiter.api.Test;

class RewriteVisitorTest {

    @Test
    public void test0() {
        var method = Method.builder("toString")
                           .addModifiers(Arrays.asList(Modifier.PUBLIC, Modifier.FINAL))
                           .returnType(SymbolConstants.toSymbol(String.class))
                           .body(MethodBody.builder()
                                                 .addStatement(LiteralStatement.create("return String.valueOf(this)"))
                                                 .build())
                           .build();
        var rewriter = new RewriteVisitor() {
            @Override
            public SyntaxNode visitMethod(Method m) {
                m = (Method) super.visitMethod(m);
                if ("toString".equals(m.getName())) {
                    return m.toBuilder()
                            .body(MethodBody
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