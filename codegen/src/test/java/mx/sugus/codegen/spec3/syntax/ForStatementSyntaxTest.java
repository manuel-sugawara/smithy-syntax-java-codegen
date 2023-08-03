package mx.sugus.codegen.spec3.syntax;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.junit.jupiter.api.Test;

class ForStatementSyntaxTest {

    @Test
    public void testBasicSingleStatement() {
        var stmt = ForStatementSyntax.builder()
                                     .initializer(LiteralExpression.create("var x = 0; x < 10; ++x"))
                                     .statement(LiteralStatement.create("System.out.println(fooList.get(x))"))
                                     .build();

        assertThat(stmt.toString(), equalTo(""" 
                                                for (var x = 0; x < 10; ++x) System.out.println(fooList.get(x));
                                                 """));
    }


    @Test
    public void testBasicBlockStatement() {
        var stmt = ForStatementSyntax.builder()
                                     .initializer(LiteralExpression.create("var x = 0; x < 10; ++x"))
                                     .statement(BlockStatement.builder()
                                                              .addStatement(LiteralStatement.create("System.out.println(fooList"
                                                                                                    + ".get(x))"))
                                                              .build())
                                     .build();

        assertThat(stmt.toString(), equalTo(""" 
                                                for (var x = 0; x < 10; ++x) {
                                                    System.out.println(fooList.get(x));
                                                }
                                                 """));
    }

    @Test
    public void testEnhancedSingleStatement() {
        var stmt = ForStatementSyntax.builder()
                                     .initializer(LiteralExpression.create("var foo : fooList"))
                                     .statement(LiteralStatement.create("System.out.println(foo)"))
                                     .build();

        assertThat(stmt.toString(), equalTo(""" 
                                                for (var foo : fooList) System.out.println(foo);
                                                 """));
    }


    @Test
    public void testEnhancedBlockStatement() {
        var stmt = ForStatementSyntax.builder()
                                     .initializer(LiteralExpression.create("var foo : fooList"))
                                     .statement(BlockStatement.builder()
                                                              .addStatement(LiteralStatement.create("System.out.println(foo)"))
                                                              .build())
                                     .build();

        assertThat(stmt.toString(), equalTo(""" 
                                                for (var foo : fooList) {
                                                    System.out.println(foo);
                                                }
                                                 """));
    }
}