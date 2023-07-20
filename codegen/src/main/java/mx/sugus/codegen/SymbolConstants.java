package mx.sugus.codegen;

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

    public static Symbol implFor3(Symbol symbol) {
        return
            switch (aggregateType(symbol)) {
                case MAP -> {
                    var builder = Symbol.builder();
                    for (var reference : symbol.getReferences()) {
                        builder.addReference(reference.getSymbol());
                    }
                    yield builder.name("HashMap")
                                 .namespace("java.util", ".")
                                 .build();
                }
                case LIST, SET -> {
                    var builder = Symbol.builder();
                    for (var reference : symbol.getReferences()) {
                        builder.addReference(reference.getSymbol());
                    }
                    yield builder.name("ArrayList")
                                 .namespace("java.util", ".")
                                 .build();
                }
                default -> symbol;
            };
    }

    public static Symbol concreteClassFor(Symbol symbol) {
        return
            switch (aggregateType(symbol)) {
                case MAP -> {
                    var builder = Symbol.builder();
                    yield builder.name("HashMap")
                                 .namespace("java.util", ".")
                                 .build();
                }
                case LIST, SET -> {
                    var builder = Symbol.builder();
                    yield builder.name("ArrayList")
                                 .namespace("java.util", ".")
                                 .build();
                }
                default -> symbol;
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



    public enum AggregateType {
        NONE, LIST, MAP, SET
    }
}
