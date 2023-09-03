package mx.sugus.codegen.util;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class SimpleNameTest {

    @Test
    public void test0() {
        var name = SimpleName.of(SimpleName.NameCasing.PASCAL, "foo_bar_baz");
        System.out.printf("==========================\n%s\n", name);
    }

    @Test
    public void test1() {
        var name = SimpleName.of(SimpleName.NameCasing.PASCAL, "case");
        System.out.printf("==========================\n%s\n", name.withPrefix("a"));
    }

    @Test
    public void test2() {
        var name = SimpleName.of(SimpleName.NameCasing.PASCAL, "cases");
        System.out.printf("==========================\n%s\n", name.toSingularSpelling().withPrefix("add"));
    }

    @Test
    public void test3() {
        var name = SimpleName.of(SimpleName.NameCasing.PASCAL, "case");
        System.out.printf("==========================\n%s\n",
                          name.toSingularSpelling()
                              .toPluralSpelling()
                              .withPrefix("add"));
    }
}