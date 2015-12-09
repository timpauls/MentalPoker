package de.fhwedel.coinflipping.model;

/**
 * Model class for the protocol. Based on http://redmine.fh-wedel.de/attachments/125/protocol.txt
 */
public class Protocol {
    public static final String STATUS_OK = "OK";

    private Integer protocolId;
    private Integer statusId;
    private String statusMessage;
    private ProtocolNegotiation protocolNegotiation;
    private KeyNegotiation keyNegotiation;
    private Payload payload;

    public Integer getProtocolId() {
        return protocolId;
    }

    public void setProtocolId(Integer protocolId) {
        this.protocolId = protocolId;
    }

    public Integer getStatusId() {
        return statusId;
    }

    public void setStatusId(Integer statusId) {
        this.statusId = statusId;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public ProtocolNegotiation getProtocolNegotiation() {
        return protocolNegotiation;
    }

    public void setProtocolNegotiation(ProtocolNegotiation protocolNegotiation) {
        this.protocolNegotiation = protocolNegotiation;
    }

    public KeyNegotiation getKeyNegotiation() {
        return keyNegotiation;
    }

    public void setKeyNegotiation(KeyNegotiation keyNegotiation) {
        this.keyNegotiation = keyNegotiation;
    }

    public Payload getPayload() {
        return payload;
    }

    public void setPayload(Payload payload) {
        this.payload = payload;
    }


    public static class Builder {
        private Protocol mProtocol;
        public Builder() {
            mProtocol = new Protocol();
        }

        public Builder setProtocolId(Integer protocolId) {
            mProtocol.setProtocolId(protocolId);
            return this;
        }

        public Builder setStatusId(Integer statusId) {
            mProtocol.setStatusId(statusId);
            return this;
        }

        public Builder setStatusMessage(String statusMessage) {
            mProtocol.setStatusMessage(statusMessage);
            return this;
        }

        public Builder setProtocolNegotiation(ProtocolNegotiation protocolNegotiation) {
            mProtocol.setProtocolNegotiation(protocolNegotiation);
            return this;
        }

        public Builder setKeyNegotiation(KeyNegotiation keyNegotiation) {
            mProtocol.setKeyNegotiation(keyNegotiation);
            return this;
        }

        public Builder setPayload(Payload payload) {
            mProtocol.setPayload(payload);
            return this;
        }

        public Protocol build() {
            return mProtocol;
        }
    }
}
