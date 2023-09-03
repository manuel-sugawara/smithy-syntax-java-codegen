package mx.sugus.codegen.util;

public final class NamingStrategy {

    public SimpleName convert(NamingContext context, SimpleName src) {
        return src.convertTo(context.casing());
    }

    public enum NamingContext {
        TYPE(SimpleName.NameCasing.PASCAL),
        STATIC_FIELD(SimpleName.NameCasing.SCREAMING),
        ENUM_CONSTANT(SimpleName.NameCasing.SCREAMING),
        FIELD(SimpleName.NameCasing.CAMEL),
        METHOD(SimpleName.NameCasing.CAMEL),
        LOCAL(SimpleName.NameCasing.CAMEL);

        private final SimpleName.NameCasing casing;

        NamingContext(SimpleName.NameCasing casing) {
            this.casing = casing;
        }

        public SimpleName.NameCasing casing() {
            return casing;
        }
    }
}
