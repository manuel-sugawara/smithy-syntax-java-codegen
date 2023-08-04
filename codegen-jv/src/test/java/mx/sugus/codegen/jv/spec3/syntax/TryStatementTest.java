package mx.sugus.codegen.jv.spec3.syntax;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.junit.jupiter.api.Test;

class TryStatementTest {

    @Test
    public void testJustCatch() {
        var stmt =
            TryStatement
                .builder()
                .tryBody(BlockStatement
                             .builder()
                             .addStatement(LiteralStatement
                                               .create("content = Files.readString(path, encoding)"))
                             .build())
                .catchClause(CatchClause
                                 .builder()
                                 .parameter(LiteralExpression.create("IOException e"))
                                 .body(BlockStatement
                                           .builder()
                                           .addStatement(LiteralStatement
                                                             .create("throw new RuntimeException(e)"))
                                           .build())
                                 .build())
                .build();

        assertThat(stmt.toString(), equalTo("""
                                                try {
                                                    content = Files.readString(path, encoding);
                                                } catch (IOException e) {
                                                    throw new RuntimeException(e);
                                                }\s
                                                """));
    }


    @Test
    public void testJustFinally() {
        var stmt =
            TryStatement
                .builder()
                .tryBody(BlockStatement
                             .builder()
                             .addStatement(LiteralStatement
                                               .create("content = inputStream.readAllBytes()"))
                             .build())
                .finallyClause(FinallyClause
                                   .builder()
                                   .body(BlockStatement
                                             .builder()
                                             .addStatement(LiteralStatement
                                                               .create("inputStream.close()"))
                                             .build())
                                   .build())
                .build();

        assertThat(stmt.toString(), equalTo("""
                                                try {
                                                    content = inputStream.readAllBytes();
                                                } finally {
                                                    inputStream.close();
                                                }
                                                 """));
    }

    @Test
    public void testCatchFinally() {
        var stmt =
            TryStatement
                .builder()
                .tryBody(BlockStatement
                             .builder()
                             .addStatement(LiteralStatement
                                               .create("content = inputStream.readAllBytes()"))
                             .build())
                .catchClause(CatchClause
                                 .builder()
                                 .parameter(LiteralExpression.create("IOException e"))
                                 .body(BlockStatement
                                           .builder()
                                           .addStatement(LiteralStatement
                                                             .create("throw new RuntimeException(e)"))
                                           .build())
                                 .build())
                .finallyClause(FinallyClause
                                   .builder()
                                   .body(BlockStatement
                                             .builder()
                                             .addStatement(LiteralStatement
                                                               .create("inputStream.close()"))
                                             .build())
                                   .build())
                .build();


        assertThat(stmt.toString(), equalTo("""
                                                try {
                                                    content = inputStream.readAllBytes();
                                                } catch (IOException e) {
                                                    throw new RuntimeException(e);
                                                } finally {
                                                    inputStream.close();
                                                }
                                                """));
    }

}