package mx.sugus.syntax.java;


import mx.sugus.javapoet.ClassName;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import org.junit.jupiter.api.Test;

class FormatterNodeBaseTest {

    @Test
    public void test0() {
        var expected =
                StatementFormatter.builder()
                        .addPart(FormatterTypeName.builder()
                                .value(ClassName.get(String.class))
                                .build())
                        .addPart(FormatterLiteral.builder()
                                .value(" value = ")
                                .build())
                        .addPart(FormatterString.builder()
                                .value("Hello world!")
                                .build())
                        .build();
        assertThat(FormatterNodeBase.formatStatement("$T value = $S", String.class, "Hello world!"),
                equalTo(expected));
    }

    @Test
    public void test1() {
        var expected =
                StatementFormatter.builder()
                        .addPart(FormatterTypeName.builder()
                                .value(ClassName.get(java.lang.String.class))
                                .build())
                        .addPart(FormatterLiteral.builder()
                                .value(" value = new ")
                                .build())
                        .addPart(FormatterTypeName.builder()
                                .value(ClassName.get(java.lang.String.class))
                                .build())
                        .addPart(FormatterLiteral.builder()
                                .value("(")
                                .build())
                        .addPart(FormatterString.builder()
                                .value("Hello world!")
                                .build())
                        .addPart(FormatterLiteral.builder()
                                .value(")").
                                build())
                        .build();
        assertThat(FormatterNodeBase.formatStatement("$1T value = new $1T($2S)", String.class, "Hello world!"),
                equalTo(expected));

    }
}