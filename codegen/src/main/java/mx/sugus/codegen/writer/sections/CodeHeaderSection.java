package mx.sugus.codegen.writer.sections;

import software.amazon.smithy.codegen.core.Symbol;
import software.amazon.smithy.utils.CodeSection;

public record CodeHeaderSection(Symbol symbol) implements CodeSection {
}
