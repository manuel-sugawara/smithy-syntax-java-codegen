package mx.sugus.codegen.spec2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import javax.lang.model.element.Modifier;
import mx.sugus.codegen.SymbolConstants;
import mx.sugus.codegen.spec2.emitters.CodeEmitter;
import mx.sugus.codegen.spec2.emitters.Emitters;
import mx.sugus.codegen.util.Sets;
import mx.sugus.codegen.writer.CodegenWriter;
import software.amazon.smithy.codegen.core.Symbol;

public class TypeSpec implements CodeEmitter {
    protected final List<TypeSpec> innerTypes;
    private final TypeKind kind;
    private final Set<Modifier> modifiers;
    private final String name;
    private final List<AnnotationSpec> annotations;
    private final List<Symbol> implement;
    private final List<Symbol> extend;
    private final List<FieldSpec> fields;
    protected final List<EnumConstant> enumConstants;
    private final List<MethodSpec> methods;

    TypeSpec(Builder<?> builder) {
        this.kind = builder.kind;
        this.name = builder.name;
        this.modifiers = Collections.unmodifiableSet(new LinkedHashSet<>(builder.modifiers));
        this.annotations = List.copyOf(builder.annotations);
        this.extend = List.copyOf(builder.extend);
        this.implement = List.copyOf(builder.implement);
        this.fields = List.copyOf(builder.fields);
        this.enumConstants = List.copyOf(builder.enumConstants);
        this.methods = List.copyOf(builder.methods);
        this.innerTypes = List.copyOf(builder.innerTypes);
    }

    public static ClassBuilder classBuilder(String name) {
        return new ClassBuilder(name);
    }

    public static InterfaceBuilder interfaceBuilder(String name) {
        return new InterfaceBuilder(name);
    }

    public static EnumBuilder enumBuilder(String name) {
        return new EnumBuilder(name);
    }

    @Override
    public void emit(CodegenWriter writer) {
        // Type preface
        emitPreface(writer);

        // Enum constants
        emitEnumConstants(writer);

        // Fields
        emitFields(writer);

        // Constructors & Methods
        emitMethods(writer);

        // Inner types
        emitInnerTypes(writer);

        writer.dedent().write("}");
    }

    void emitPreface(CodegenWriter writer) {
        for (var annotation : annotations) {
            annotation.emit(writer);
            writer.writeWithNoFormatting("");
        }
        writer.writeInlineWithNoFormatting(SpecUtils.toString(this.modifiers))
              .writeInlineWithNoFormatting(" ")
              .writeInlineWithNoFormatting(kind.name().toLowerCase(Locale.US))
              .writeInlineWithNoFormatting(" ")
              .writeInlineWithNoFormatting(name);

        writeTypeExtensions(writer, "extends", extend);
        writeTypeExtensions(writer, "implements", implement);
        writer.write(" {").indent();
    }

    void emitEnumConstants(CodegenWriter writer) {
        if (enumConstants.isEmpty()) {
            return;
        }
        var isFirst = true;
        for (var constant : enumConstants) {
            if (!isFirst) {
                writer.writeWithNoFormatting(",");
                writer.write("");
            }
            writer.writeInline("$L", constant.name);
            constant.body.emit(writer);

            isFirst = false;
        }
        writer.writeWithNoFormatting(";");
        writer.write("");
    }

    void emitFields(CodegenWriter writer) {
        // static fields
        var hasStaticFields = false;
        for (var field : fields) {
            if (!field.isStatic()) {
                continue;
            }
            field.emit(writer);
            hasStaticFields = true;
        }
        if (hasStaticFields) {
            writer.writeWithNoFormatting("");
        }

        // non-static fields
        var hasNonStaticFields = false;
        for (var field : fields) {
            if (field.isStatic()) {
                continue;
            }
            field.emit(writer);
            hasNonStaticFields = true;
        }
        if (hasNonStaticFields) {
            writer.writeWithNoFormatting("");
        }
    }

    void emitMethods(CodegenWriter writer) {
        // Constructors
        for (var method : methods) {
            if (!method.isConstructor()) {
                continue;
            }
            method.emit(this.name, writer, kind);
            writer.writeWithNoFormatting("");
        }

        // Methods
        for (var method : methods) {
            if (method.isConstructor()) {
                continue;
            }
            method.emit(writer, kind);
            writer.writeWithNoFormatting("");
        }
    }

    void emitInnerTypes(CodegenWriter writer) {
        for (var innerType : innerTypes) {
            innerType.emit(writer);
            writer.writeWithNoFormatting("");
        }
    }

    private void writeTypeExtensions(CodegenWriter writer, String keyword, List<Symbol> types) {
        if (types.isEmpty()) {
            return;
        }
        writer.writeInlineWithNoFormatting(" ");
        writer.writeInlineWithNoFormatting(keyword);
        writer.writeInlineWithNoFormatting(" ");
        var isFirst = true;
        for (var symbol : types) {
            if (!isFirst) {
                writer.writeInlineWithNoFormatting(", ");
            }
            writer.writeInline("$T", symbol);
            isFirst = false;
        }
    }

    public enum TypeKind {
        CLASS(
            Sets.empty(),
            Sets.empty(),
            Sets.empty(),
            Sets.empty()),

        INTERFACE(
            Set.of(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL),
            Set.of(Modifier.PUBLIC, Modifier.ABSTRACT),
            Set.of(Modifier.PUBLIC, Modifier.STATIC),
            Set.of(Modifier.STATIC)),

        ENUM(
            Sets.empty(),
            Sets.empty(),
            Sets.empty(),
            Set.of(Modifier.STATIC)),

        ANNOTATION(
            Set.of(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL),
            Set.of(Modifier.PUBLIC, Modifier.ABSTRACT),
            Set.of(Modifier.PUBLIC, Modifier.STATIC),
            Set.of(Modifier.STATIC));

        final Set<Modifier> implicitFieldModifiers;
        final Set<Modifier> implicitMethodModifiers;
        final Set<Modifier> implicitTypeModifiers;
        final Set<Modifier> asMemberModifiers;

        public Set<Modifier> getImplicitFieldModifiers() {
            return implicitFieldModifiers;
        }

        public Set<Modifier> getImplicitMethodModifiers() {
            return implicitMethodModifiers;
        }

        public Set<Modifier> getImplicitTypeModifiers() {
            return implicitTypeModifiers;
        }

        public Set<Modifier> getAsMemberModifiers() {
            return asMemberModifiers;
        }

        TypeKind(Set<Modifier> implicitFieldModifiers,
                 Set<Modifier> implicitMethodModifiers,
                 Set<Modifier> implicitTypeModifiers,
                 Set<Modifier> asMemberModifiers) {
            this.implicitFieldModifiers = implicitFieldModifiers;
            this.implicitMethodModifiers = implicitMethodModifiers;
            this.implicitTypeModifiers = implicitTypeModifiers;
            this.asMemberModifiers = asMemberModifiers;
        }
    }

    @SuppressWarnings("unchecked")
    public static class Builder<B extends Builder<B>> {
        protected final TypeKind kind;
        protected final String name;
        protected final Set<Modifier> modifiers = new LinkedHashSet<>();
        protected final List<Symbol> implement = new ArrayList<>();
        protected final List<Symbol> extend = new ArrayList<>();
        protected final List<FieldSpec> fields = new ArrayList<>();
        protected final List<MethodSpec> methods = new ArrayList<>();
        protected final List<EnumConstant> enumConstants = new ArrayList<>();
        protected final List<TypeSpec> innerTypes = new ArrayList<>();
        private final List<AnnotationSpec> annotations = new ArrayList<>();


        Builder(TypeKind kind, String name) {
            this.kind = kind;
            this.name = Objects.requireNonNull(name);
        }

        public B addModifiers(Modifier... modifiers) {
            for (var modifier : modifiers) {
                this.modifiers.add(modifier);
            }
            return (B) this;
        }

        public B addAnnotation(Object type) {
            this.annotations.add(AnnotationSpec.builder(type).build());
            return (B) this;
        }

        public B addAnnotation(AnnotationSpec annotation) {
            this.annotations.add(annotation);
            return (B) this;
        }

        public B addField(FieldSpec field) {
            fields.add(field);
            return (B) this;
        }


        public B addMethod(MethodSpec method) {
            methods.add(method);
            return (B) this;
        }

        public B addInnerType(TypeSpec innerType) {
            innerTypes.add(innerType);
            return (B) this;
        }

        public TypeSpec build() {
            return new TypeSpec(this);
        }
    }

    public static class ClassBuilder extends Builder<ClassBuilder> {
        ClassBuilder(String name) {
            super(TypeKind.CLASS, name);
        }

        public ClassBuilder superclass(Object value) {
            this.extend.clear();
            this.extend.add(SymbolConstants.toSymbol(value));
            return this;
        }

        public ClassBuilder addSuperinterface(Object value) {
            this.implement.add(SymbolConstants.toSymbol(value));
            return this;
        }
    }

    public static class InterfaceBuilder extends Builder<InterfaceBuilder> {

        InterfaceBuilder(String name) {
            super(TypeKind.INTERFACE, name);
        }

        public InterfaceBuilder addSuperinterface(Object value) {
            this.extend.add(SymbolConstants.toSymbol(value));
            return this;
        }
    }

    public static class EnumBuilder extends Builder<EnumBuilder> {
        EnumBuilder(String name) {
            super(TypeKind.ENUM, name);
        }

        public EnumBuilder addEnumConstant(String name) {
            this.enumConstants.add(new EnumConstant(name, Emitters.emptyInline()));
            return this;
        }

        public EnumBuilder addEnumConstant(String name, String value) {
            this.enumConstants.add(new EnumConstant(name,
                                                    Emitters.formatInline("($S)", new Object[] {value})));
            return this;
        }

        public EnumBuilder addEnumConstant(String name, CodeEmitter value) {
            this.enumConstants.add(new EnumConstant(name, value));
            return this;
        }
    }

    record EnumConstant(String name, CodeEmitter body) {
    }

    public static class AnonymousClassBuilder extends Builder {
        AnonymousClassBuilder() {
            super(TypeKind.CLASS, "");
        }
    }
}
