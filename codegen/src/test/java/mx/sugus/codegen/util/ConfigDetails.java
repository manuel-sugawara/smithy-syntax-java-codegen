package mx.sugus.codegen.util;

import java.util.NoSuchElementException;

public final class ConfigDetails {

    private final Object value;
    private final Tag tag;

    public ConfigDetails(Object value, Tag tag) {
        this.value = value;
        this.tag = tag;
    }

    public String endpointDetails() {
        if (tag == Tag.ENDPOINT_DETAILS) {
            return (String) value;
        }
        throw new NoSuchElementException();
    }

    public String antennaDemodDecodeDetails() {
        if (tag == Tag.ANTENNA_DEMOD_DECODE_DETAILS) {
            return (String) value;
        }
        throw new NoSuchElementException();
    }

    public String s3RecordingDetails() {
        if (tag == Tag.S3_RECORDING_DETAILS) {
            return (String) value;
        }
        throw new NoSuchElementException();
    }

    public void visit(ConfigDetailsVisitor visitor) {
        tag.visit(this, visitor);
    }

    public Object value() {
        return value;
    }

    public Tag tag() {
        return tag;
    }

    public enum Tag {
        ENDPOINT_DETAILS("endpointDetails", String.class) {
            @Override
            public void visit(ConfigDetails details, ConfigDetailsVisitor visitor) {
                visitor.visitEndpointDetails(details.endpointDetails());
            }
        },

        ANTENNA_DEMOD_DECODE_DETAILS("antennaDemodDecodeDetails", String.class) {
            @Override
            public void visit(ConfigDetails details, ConfigDetailsVisitor visitor) {
                visitor.visitAntennaDemodDecodeDetails(details.antennaDemodDecodeDetails());
            }

        },

        S3_RECORDING_DETAILS("s3RecordingDetails", String.class) {
            @Override
            public void visit(ConfigDetails details, ConfigDetailsVisitor visitor) {
                visitor.visitS3RecordingDetails(details.s3RecordingDetails());
            }
        };

        private final String name;
        private final Class<?> clazz;

        Tag(String name, Class<?> clazz) {
            this.name = name;
            this.clazz = clazz;
        }

        public abstract void visit(ConfigDetails details, ConfigDetailsVisitor visitor);
    }


    interface ConfigDetailsVisitor {
        void visitEndpointDetails(String value);

        void visitAntennaDemodDecodeDetails(String value);

        void visitS3RecordingDetails(String value);
    }
}
