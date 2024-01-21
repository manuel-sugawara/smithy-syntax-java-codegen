package mx.sugus.codegen;

import java.lang.ref.WeakReference;
import java.util.Objects;
import mx.sugus.syntax.java.OptionalTrait;
import software.amazon.smithy.model.Model;
import software.amazon.smithy.model.shapes.MemberShape;
import software.amazon.smithy.model.shapes.Shape;
import software.amazon.smithy.model.shapes.StructureShape;
import software.amazon.smithy.model.traits.SparseTrait;

/**
 * An index that checks if a member is nullable.
 */
public class NotNullOptionalityKnowledgeIndex implements OptionalityKnowledgeIndex {

    private final WeakReference<Model> model;

    public NotNullOptionalityKnowledgeIndex(Model model) {
        this.model = new WeakReference<>(model);
    }

    public static NotNullOptionalityKnowledgeIndex of(Model model) {
        return model.getKnowledge(NotNullOptionalityKnowledgeIndex.class,
                                  NotNullOptionalityKnowledgeIndex::new);
    }

    /**
     * Checks if a member is nullable using {@link CheckMode#DEFAULT_NON_NULL}.
     *
     * @param member Member to check.
     * @return Returns true if the member can be represented by a java constant {@code null}.
     * @see #isMemberNullable(MemberShape, CheckMode)
     */
    public boolean isMemberNullable(MemberShape member) {
        return isMemberNullable(member, CheckMode.DEFAULT_NON_NULL);
    }

    /**
     * Checks if a member can is nullable using local nullability rules. See {@link CheckMode}.
     *
     * <p>A {@code checkMode} parameter is required to declare what kind of
     * model consumer is checking if the member is nullable.
     *
     * @param member    Member to check.
     * @param checkMode The mode used when checking if the member is considered nullable.
     * @return Returns true if the member is optional.
     */
    public boolean isMemberNullable(MemberShape member,
                                    CheckMode checkMode) {
        Model m = Objects.requireNonNull(model.get());
        Shape container = m.expectShape(member.getContainer());
        Shape target = m.expectShape(member.getTarget());

        switch (container.getType()) {
            case STRUCTURE:
                return checkMode.isStructureMemberNullable(container.asStructureShape().get(), member, target);
            case UNION:
            case SET:
                // Union and set members are never null.
                return false;
            case MAP:
                // Map keys are never null.
                if (member.getMemberName().equals("key")) {
                    return false;
                }
                // fall-through.
            case LIST:
                // Map values and list members are only null if they have the @sparse trait.
                return container.hasTrait(SparseTrait.class);
            default:
                return false;
        }
    }

    /**
     * Defines the type of model consumer to assume when determining if a member should be considered nullable or always present.
     */
    public enum CheckMode {

        /**
         * All members are considered nullable.
         */
        DEFAULT_NULL {
            @Override
            boolean isStructureMemberNullable(StructureShape container, MemberShape member, Shape target) {
                return true;
            }
        },

        /**
         * All members are considered non-nullable. The {@link OptionalTrait} is honored.
         */
        DEFAULT_NON_NULL {
            @Override
            boolean isStructureMemberNullable(StructureShape container, MemberShape member, Shape target) {
                if (member.hasTrait(OptionalTrait.class)) {
                    return true;
                }
                return false;
            }
        };

        abstract boolean isStructureMemberNullable(StructureShape container, MemberShape member, Shape target);
    }
}
