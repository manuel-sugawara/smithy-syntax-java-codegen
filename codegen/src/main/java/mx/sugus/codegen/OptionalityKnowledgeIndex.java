package mx.sugus.codegen;

import software.amazon.smithy.model.knowledge.KnowledgeIndex;
import software.amazon.smithy.model.shapes.MemberShape;

public interface OptionalityKnowledgeIndex extends KnowledgeIndex {

    /**
     * Checks if a member is nullable.
     *
     * @param member Member to check.
     * @return Returns true if the member can be represented by a java constant {@code null}.
\     */
    boolean isMemberNullable(MemberShape member);
}
