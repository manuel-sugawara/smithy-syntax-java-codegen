package mx.sugus.codegen.generators;

import static org.mockito.Mockito.mock;

import mx.sugus.codegen.SymbolConstants;
import mx.sugus.codegen.writer.CodegenWriter;
import org.junit.jupiter.api.Test;
import software.amazon.smithy.codegen.core.SymbolProvider;
import software.amazon.smithy.codegen.core.WriterDelegator;
import software.amazon.smithy.model.shapes.IntEnumShape;
import software.amazon.smithy.model.shapes.ShapeId;

class IntEnumGeneratorTest {

    @Test
    public void test0() {
        //var writerDelegator = new Mock
        var symbol = SymbolConstants.fromClassName("mx.sugus.example.Suit");
        var shape = IntEnumShape.builder()
                                .id(ShapeId.from("mx.sugus.example#Suit"))
                                .addMember("DIAMOND", 1)
                                .addMember("CLUB", 2)
                                .addMember("HEART", 3)
                                .addMember("SPADE", 4)
                                .build();
        var spec =
            new IntEnumGenerator(null, symbol, shape, mock(SymbolProvider.class), mock(WriterDelegator.class))
                .generateType();
        var writer = new CodegenWriter("mx.sugus.example");
        spec.emit(writer);
        System.out.println(writer.toString());

    }

}