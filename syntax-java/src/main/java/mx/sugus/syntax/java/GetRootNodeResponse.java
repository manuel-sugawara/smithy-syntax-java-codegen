package mx.sugus.syntax.java;

public final class GetRootNodeResponse {
    private GetRootNodeResponse(Builder builder) {
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
        if (!(obj instanceof GetRootNodeResponse)) {
            return false;
        }
        GetRootNodeResponse other = (GetRootNodeResponse) obj;
        return true;
    }

    @Override
    public int hashCode() {
        int hashCode = 17;
        return hashCode;
    }

    @Override
    public String toString() {
        return "GetRootNodeResponse{" + "}";
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private boolean _built;

        Builder() {
        }

        Builder(GetRootNodeResponse data) {
        }

        public GetRootNodeResponse build() {
            return new GetRootNodeResponse(this);
        }
    }
}
