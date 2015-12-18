package de.fhwedel.coinflipping.handling;

import de.fhwedel.coinflipping.model.Protocol;
import de.fhwedel.coinflipping.util.JsonUtil;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by tim on 18.12.15.
 */
public class ServerProtocolHandlerTest {
    @Test
    public void testKonstantinsTestCase() throws Exception {
        String jsonIn = "{\"protocolId\":2,\"statusId\":0,\"statusMessage\":\"OK\",\"protocolNegotiation\":{\"version\":\"1.0\",\"availableVersions\":[{\"versions\":[\"1.0\"]},{\"versions\":[\"1.0\"]}]},\"keyNegotiation\":{\"availableSids\":[{\"sids\":[12,20,21,21]}]}}";
        Protocol protocol = ServerProtocolHandler.handleProtocolStep(JsonUtil.fromJson(jsonIn, Protocol.class));
        assertThat(protocol.isValid());
        assertThat(protocol.getProtocolId()).isEqualTo(3);
        assertThat(protocol.getKeyNegotiation().getSid()).isEqualTo(20);
    }
}
