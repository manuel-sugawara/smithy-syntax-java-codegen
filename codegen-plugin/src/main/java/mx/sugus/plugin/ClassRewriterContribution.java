package mx.sugus.plugin;

public class ClassRewriterContribution {

    private ClassRewriterContribution(Builder builder) {

    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private ClassRewriter rewriter;

        public Builder rewritter(ClassRewriter rewriter) {
            this.rewriter = rewriter;
            return this;
        }

        public ClassRewriterContribution build() {
            return new ClassRewriterContribution(this);
        }

        public Builder archetype(CodegenArchetype structure) {
            return this;
        }

        public Builder ordering() {
            return this;
        }
    }
}
