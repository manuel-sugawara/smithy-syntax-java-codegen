package mx.sugus.codegen.jv.spec3.syntax;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import mx.sugus.codegen.jv.writer.CodegenWriter;

public final class CodeSection implements SyntaxNode {
    private final String name;
    private final List<SyntaxNode> children;
    CodeSection(Builder builder) {
        this.name = Objects.requireNonNull(builder.name);
        this.children = List.copyOf(builder.children);
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getName() {
        return name;
    }

    @Override
    public List<SyntaxNode> children() {
        return children;
    }

    @Override
    public void emit(CodegenWriter writer) {
        for (var node : children) {
            node.emit(writer);
        }
    }

    @Override
    public Kind kind() {
        return null;
    }

    @Override
    public <R> R accept(SyntaxVisitor<R> visitor) {
        return visitor.visitCodeSection(this);
    }

    public Builder toBuilder() {
        return new Builder(this);
    }

    public static class Builder {
        private final List<SyntaxNode> children = new ArrayList<>();
        private String name;

        public Builder() {
        }

        public Builder(CodeSection codeSection) {
            this.name = codeSection.name;
            this.children.addAll(codeSection.children);
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder addStatement(SyntaxNode statement) {
            this.children.add(statement);
            return this;
        }

        public Builder addStatements(Collection<SyntaxNode> statements) {
            this.children.clear();
            this.children.addAll(statements);
            return this;
        }

        public CodeSection build() {
            return new CodeSection(this);
        }
    }
}
