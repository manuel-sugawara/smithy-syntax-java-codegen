package com.squareup.javapoet;

import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

class StatementTest {

    @Test
    public void test0() {
        Statement stmt = Statement.of("var foo = Math.min(x, y)");
        System.out.printf("===========================\n%s\n", stmt.toString());
    }

    @Test
    public void test1() {
        Statement stmt = Statement.builder()
                                  .addCodeLn("var result = numbers")
                                  .addCodeLn(".stream()")
                                  .addCodeLn(".map(x -> x * 2)")
                                  .addCodeLn(".filter(x -> (x % 2) == 0)")
                                  .addCode(".collect($T.toList())", Collectors.class)
                                  .build();
        System.out.printf("===========================\n%s\n", stmt.toString());
    }
}