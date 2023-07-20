package mx.sugus.codegen.util;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.util.Arrays;
import org.junit.jupiter.api.Test;

class NamingTest {

    @Test
    public void testToLowerCamel0() {
        var name = "";
        var res = Naming.toLowerCamel(name);

        assertThat(res, equalTo(""));
    }

    @Test
    public void testToLowerCamel1() {
        var name = "12345";
        var res = Naming.toLowerCamel(name);

        assertThat(res, equalTo("12345"));
    }

    @Test
    public void testToLowerCamel2() {
        var name = "ALLUPPER";
        var res = Naming.toLowerCamel(name);

        assertThat(res, equalTo("allupper"));
    }

    @Test
    public void testToLowerCamel3() {
        var name = "alllower";
        var res = Naming.toLowerCamel(name);

        assertThat(res, equalTo("alllower"));
    }

    @Test
    public void testToLowerCamel4() {
        var name = "mIxEdCaSe";
        var res = Naming.toLowerCamel(name);

        assertThat(res, equalTo("mIxEdCaSe"));
    }

    @Test
    public void testToLowerCamel5() {
        var name = "MiXeDcAsE";
        var res = Naming.toLowerCamel(name);

        assertThat(res, equalTo("miXeDcAsE"));
    }

    @Test
    public void testToLowerCamel6() {
        var name = "lowerUPPER";
        var res = Naming.toLowerCamel(name);

        assertThat(res, equalTo("lowerUpper"));
    }

    @Test
    public void testToLowerCamel7() {
        var name = "UPPERlower";
        var res = Naming.toLowerCamel(name);

        assertThat(res, equalTo("uppeRlower"));
    }

    @Test
    public void testToLowerCamel8() {
        var name = "lowerUPPERlower";
        var res = Naming.toLowerCamel(name);

        assertThat(res, equalTo("lowerUppeRlower"));
    }

    @Test
    public void testToLowerCamel9() {
        var name = "lower9UPPERlower";
        var res = Naming.toLowerCamel(name);

        assertThat(res, equalTo("lower9UppeRlower"));
    }

    @Test
    public void testToLowerCamel10() {
        var name = "lowerUPPERlower";
        var res = Naming.toLowerCamel(name);

        assertThat(res, equalTo("lowerUppeRlower"));
    }

    @Test
    public void testToLowerCamel11() {
        var name = "SSECustomerKeyMD5";
        var res = Naming.toLowerCamel(name);

        assertThat(res, equalTo("sseCustomerKeyMd5"));
    }

    @Test
    public void testToUpperCamel11() {
        var name = "SSECustomerKeyMD5";
        var res = Naming.toUpperCamel(name);

        assertThat(res, equalTo("SseCustomerKeyMd5"));
    }


    @Test
    public void testToUpperCamel12() {
        var name = "SSECustomerKeyMD5";
        var res = Naming.toUpperCamel(Naming.toUpperCamel(name));

        assertThat(res, equalTo("SseCustomerKeyMd5"));
    }

    @Test
    public void testToUpperName0() {
        var name = "SSECustomerKeyMD5";
        var res = Naming.toUpperName(name);

        assertThat(res, equalTo("SSE_CUSTOMER_KEY_MD5"));
    }


    @Test
    public void testToUpperName1() {
        var name = "sseCustomerKeyMD5";
        var res = Naming.toUpperName(name);

        assertThat(res, equalTo("SSE_CUSTOMER_KEY_MD5"));
    }

    @Test
    public void testToUpperName2() {
        var name = "SSECustomerMD5Key";
        var res = Naming.toUpperName(name);

        assertThat(res, equalTo("SSE_CUSTOMER_MD5_KEY"));
    }

    @Test
    public void test00() {
        //var name = "SSECustomerKeyMD5";
        //var name = "SSECustomerKeyMD5";
        for (var name : Arrays.asList("SSECustomerKeyMD5")) {

            var done = false;
            var prev = 0;
            var cnt = 0;
            var buf = new StringBuilder();
            while (!done) {
                if (prev >= name.length()) {
                    break;
                }
                var idx = Naming.findNextUpperEdge(name, prev);
                System.out.printf("prev: %d, idx: %d\n", prev, idx);


                var value = switch (idx) {
                    case -1 -> {
                        done = true;
                        yield name.substring(prev);
                    }
                    default -> name.substring(prev, idx);

                };
                buf.append(value);
                if (idx != -1 && idx + 1 < name.length()) {
                    buf.append("_").append(name.charAt(idx));
                }
                System.out.printf("==================== %s (%d) -->%s<\n", name, idx, value);
                prev = idx + 1;
                if (cnt++ >= 10) {
                    break;
                }
            }
            System.out.printf("==========>> DONE: >>%s<<\n", buf);
        }
    }
}
 