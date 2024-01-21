package mx.sugus.syntax.java;

public interface FormatterNode {
    SyntaxFormatterNodeKind kind();
    Object value();
}
