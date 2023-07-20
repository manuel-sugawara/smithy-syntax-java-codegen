package mx.sugus.codegen.writer;


import org.junit.jupiter.api.Test;

public class CodegenWriterTest {

    @Test
    public void foobar() {
        var writer = new CodegenWriter("com.example");
        writer.openJavaBlock("class Foobar", () -> {
            writer.separator();
                                 writer.addJavadoc("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore "
                                                   + "et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut "
                                                   + "aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse "
                                                   + "cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in "
                                                   + "culpa qui officia deserunt mollit anim id est laborum.");
            writer.emptyJavaBlock("public Foobar()");
                             });
        System.out.printf("=========\n%s\n", writer);
    }

    @Test
    public void foobar2() {
        var writer = new CodegenWriter("com.example");
        writer.openJavaBlock("class Foobar", () -> {
            writer.separator();
            writer.addJavadoc("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore "
                              + "et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut "
                              + "aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse "
                              + "cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in "
                              + "culpa qui officia deserunt mollit anim id est laborum.");
            writer.emptyJavaBlock("public Foobar()");
        });
        System.out.printf("=========\n%s\n", writer);
    }

}
