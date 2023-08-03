package mx.sugus.codegen.spec3.syntax;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import mx.sugus.codegen.writer.CodegenWriter;

public class ClassBody implements SyntaxNode {
    public final List<ClassField> fields;
    public final List<MethodSyntax> methods;

    public ClassBody(Builder builder) {
        this.methods = List.copyOf(builder.methods);
        this.fields = List.copyOf(builder.fields);
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public void emit(CodegenWriter writer) {
        writer.write(" {").indent();
        for (var field : fields) {
            field.emit(writer);
        }
        for (var method : methods) {
            method.emit(writer);
        }
        writer.dedent().writeWithNoFormatting("}");
    }

    @Override
    public Kind kind() {
        return null;
    }

    @Override
    public <R> R accept(SyntaxVisitor<R> visitor) {
        return visitor.visitClassBody(this);
    }

    public List<ClassField> getFields() {
        return fields;
    }

    public List<MethodSyntax> getMethods() {
        return methods;
    }

    public Builder toBuilder() {
        return new Builder(this);
    }

    public static class Builder {
        public final List<ClassField> fields = new ArrayList<>();
        private final List<MethodSyntax> methods = new ArrayList<>();

        Builder() {
        }

        Builder(ClassBody classBody) {
            this.fields.addAll(classBody.fields);
            this.methods.addAll(classBody.methods);
        }

        public Builder addMethod(MethodSyntax method) {
            this.methods.add(method);
            return this;
        }

        public Builder addField(ClassField field) {
            this.fields.add(field);
            return this;
        }

        public Builder addFields(Collection<ClassField> fields) {
            this.fields.clear();
            this.fields.addAll(fields);
            return this;
        }

        public Builder addMethods(List<MethodSyntax> methods) {
            this.methods.clear();
            this.methods.addAll(methods);
            return this;
        }

        public ClassBody build() {
            return new ClassBody(this);
        }
    }
}
