package mx.sugus.codegen.writer;

import java.util.Optional;
import mx.sugus.codegen.SymbolConstants;
import software.amazon.smithy.codegen.core.Symbol;
import software.amazon.smithy.codegen.core.SymbolReference;
import software.amazon.smithy.codegen.core.SymbolWriter;
import software.amazon.smithy.utils.StringUtils;

/**
 *
 */
public class CodegenWriter extends SymbolWriter<CodegenWriter, CodeGenImportContainer> {
    private static final int RIGHT_MARGIN = 130;
    private static final int MIN_LINE_LENGTH = 30;

    private final String packageName;
    private boolean hasPreviousSection = false;

    public CodegenWriter(String packageName) {
        super(new CodeGenImportContainer(packageName));
        this.packageName = packageName;
        putFormatter('T', this::formatJavaType);
    }

    public CodegenWriter startSection() {
        if (hasPreviousSection) {
            writeWithNoFormatting("");
        }
        return this;
    }

    public CodegenWriter endSection() {
        hasPreviousSection = true;
        return this;
    }

    // comments and docs
    public CodegenWriter addJavadoc(Optional<String> docs) {
        if (docs.isPresent()) {
            return addJavadoc(docs.get());
        }
        return this;
    }

    public CodegenWriter addJavadoc(String docs) {
        pushState();
        write("/**");
        setNewlinePrefix(" * ");
        writeWithNoFormatting(formatComment(docs));
        popState();
        write(" */");
        return this;
    }

    public CodegenWriter addComment(String comment) {
        pushState();
        setNewlinePrefix("// ");
        writeWithNoFormatting(formatComment(comment));
        popState();
        return this;
    }

    // Control Flow
    public CodegenWriter beginControlFlow(String format, Object... args) {
        openBlock(format + " {", args);
        return this;
    }

    public CodegenWriter nextControlFlow(String format, Object... args) {
        return dedent().write(format + " {", args).indent();
    }

    public CodegenWriter endControlFlow() {
        closeBlock("}");
        return this;
    }

    // statement
    public CodegenWriter addStatement(String format, Object... args) {
        return write(format + ";", args);
    }

    public CodegenWriter separator() {
        write("");
        return this;
    }

    // Blocks with runnable
    public CodegenWriter openJavaBlock(String textBeforeNewline, Object arg1, Runnable runnable) {
        return openBlock(textBeforeNewline + " {", "}", arg1, runnable);
    }

    public CodegenWriter openJavaBlock(String textBeforeNewline, Object arg1, Object arg2, Runnable runnable) {
        return openBlock(textBeforeNewline + " {", "}", arg1, arg2, runnable);
    }

    public CodegenWriter openJavaBlock(String textBeforeNewline, Object arg1, Object arg2, Object arg3, Runnable runnable) {
        return openBlock(textBeforeNewline + " {", "}", arg1, arg2, arg3, runnable);
    }

    public CodegenWriter openJavaBlock(String textBeforeNewline, Runnable runnable) {
        return openBlock(textBeforeNewline + " {", "}", runnable);
    }

    public CodegenWriter emptyJavaBlock(String textBeforeNewline) {
        return openBlock(textBeforeNewline + " {")
            .closeBlock("}");
    }

    @Override
    public String toString() {
        return fileHeader() +
               "\n" +
               packageDeclaration() +
               "\n" +
               getImportContainer().toString() +
               "\n" +
               super.toString();
    }

    int computeWrapLength() {
        var prefixLength = (getIndentLevel() * getIndentText().length()) + 3;
        var computedRightMargin = RIGHT_MARGIN;
        if (prefixLength > RIGHT_MARGIN || (RIGHT_MARGIN - prefixLength) < MIN_LINE_LENGTH) {
            computedRightMargin = MIN_LINE_LENGTH;
        } else {
            computedRightMargin = RIGHT_MARGIN - prefixLength;
        }
        return computedRightMargin;
    }

    String formatComment(String comment) {
        var wrapLength = computeWrapLength();
        comment = comment.replaceAll("\\$", "\\$\\$");
        return StringUtils.wrap(comment, wrapLength);
    }

    String packageDeclaration() {
        return "package " + packageName + ";\n";
    }

    String fileHeader() {
        return "";
    }

    String formatJavaType(Object arg, String indent) {
        var symbol = SymbolConstants.toSymbol(arg);
        addImport(symbol, symbol.getName());
        if (symbol.getReferences().isEmpty()) {
            return symbol.getName();
        }

        var buf = new StringBuilder();
        formatSymbol(buf, symbol);
        return buf.toString();
    }

    void formatSymbol(StringBuilder buf, Symbol symbol) {
        if (symbol.getReferences().isEmpty()) {
            buf.append(symbol.getName());
            return;
        }
        buf.append(symbol.getName());
        buf.append("<");
        var symbols = symbol.getReferences();
        appendAndImportSymbol(buf, symbols.get(0));
        for (var index = 1; index < symbols.size(); ++index) {
            buf.append(", ");
            var reference = symbols.get(index);
            appendAndImportSymbol(buf, reference);
        }
        buf.append(">");
    }

    void appendAndImportSymbol(StringBuilder buf, SymbolReference reference) {
        var referred = reference.getSymbol();
        addImport(referred, referred.getName());
        formatSymbol(buf, referred);
    }
}
