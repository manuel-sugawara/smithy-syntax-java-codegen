package mx.sugus.syntax.java;

public interface SyntaxNode {
    <T> T accept(SyntaxNodeVisitor<T> visitor);
}
