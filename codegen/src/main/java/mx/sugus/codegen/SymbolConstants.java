package mx.sugus.codegen;

import mx.sugus.javapoet.ClassName;
import mx.sugus.javapoet.TypeName;
import software.amazon.smithy.codegen.core.CodegenException;
import software.amazon.smithy.codegen.core.Symbol;

public class SymbolConstants {

    public static final String AGGREGATE_TYPE = "::aggregateType";

    public static boolean isAggregate(Symbol symbol) {
        return symbol.getProperty(AGGREGATE_TYPE, AggregateType.class)
                     .map(t -> t != AggregateType.NONE)
                     .orElse(false);
    }

    public static AggregateType aggregateType(Symbol symbol) {
        return symbol.getProperty(AGGREGATE_TYPE, AggregateType.class)
                     .orElse(AggregateType.NONE);
    }

    public static Symbol concreteClassFor(Symbol symbol) {
        return
            switch (aggregateType(symbol)) {
                case MAP -> {
                    var builder = Symbol.builder();
                    yield builder.name("LinkedHashMap")
                                 .namespace("java.util", ".")
                                 .build();
                }
                case LIST -> {
                    var builder = Symbol.builder();
                    yield builder.name("ArrayList")
                                 .namespace("java.util", ".")
                                 .build();
                }
                case SET -> {
                    var builder = Symbol.builder();
                    yield builder.name("LinkedHashSet")
                                 .namespace("java.util", ".")
                                 .build();
                }
                default -> symbol;
            };
    }

    public static String toUnmodifiableCollection(Symbol type) {
        return
            switch (aggregateType(type)) {
                case LIST -> "unmodifiableList";
                case SET -> "unmodifiableSet";
                case MAP -> "unmodifiableMap";
                default -> throw new CodegenException("unknownCollection: " + aggregateType(type));
            };
    }


    public static Symbol fromClass(Class<?> clazz) {
        return Symbol.builder()
                     .name(clazz.getSimpleName())
                     .namespace(clazz.getPackageName(), ".")
                     .build();
    }

    public static Symbol fromClassName(String className) {
        var lastDot = className.lastIndexOf('.');
        if (lastDot == -1) {
            return Symbol.builder()
                         .name(className)
                         .build();
        }
        // XXX substring might fail here if the given string is "foo.bar."
        return Symbol.builder()
                     .name(className.substring(lastDot + 1))
                     .namespace(className.substring(0, lastDot), ".")
                     .build();
    }

    public static Symbol toSymbol(Object arg) {
        if (arg == null) {
            throw new NullPointerException("arg");
        }
        if (arg instanceof Symbol s) {
            return s;
        }
        if (arg instanceof Class c) {
            return fromClass(c);
        }
        if (arg instanceof String s) {
            return fromClassName(s);
        }
        throw new IllegalArgumentException("cannot convert " + arg.getClass().getSimpleName() + " to Symbol");
    }

    public static TypeName typeParam(Symbol symbol) {
        var ref = symbol.getReferences().get(0).getSymbol();
        return ClassName.bestGuess(ref.getFullName());
    }


    public enum AggregateType {
        NONE, LIST, MAP, SET
    }
}
