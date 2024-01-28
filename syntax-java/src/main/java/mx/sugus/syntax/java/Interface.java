package mx.sugus.syntax.java;

public final class Interface {
    private Interface(Builder builder) {
    }

    public Builder toBuilder() {
        return new Builder(this);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Interface)) {
            return false;
        }
        Interface other = (Interface) obj;
        return true;
    }

    @Override
    public int hashCode() {
        int hashCode = 17;
        return hashCode;
    }

    @Override
    public String toString() {
        return "interface{" + "}";
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private boolean _built;

        Builder() {
        }

        Builder(Interface data) {
        }

        public Interface build() {
            return new Interface(this);
        }
    }
}
