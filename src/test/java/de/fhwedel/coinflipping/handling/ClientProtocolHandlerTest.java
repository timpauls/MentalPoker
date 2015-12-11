package de.fhwedel.coinflipping.handling;

import de.fhwedel.coinflipping.config.ClientConfig;
import de.fhwedel.coinflipping.model.Payload;
import de.fhwedel.coinflipping.model.Protocol;
import de.fhwedel.coinflipping.model.SampleProtocolSteps;
import de.fhwedel.coinflipping.util.JsonUtil;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by tim on 10.12.15.
 */
public class ClientProtocolHandlerTest {
    @Test
    public void testInitiation() throws Exception {
        Protocol protocol = ClientProtocolHandler.initiateProtocol();

        assertThat(protocol.getProtocolId()).isEqualTo(0);
        assertThat(protocol.getStatusId()).isEqualTo(Protocol.STATUS_ID_OK);
        assertThat(protocol.getProtocolNegotiation()).isNotNull();
        assertThat(protocol.getProtocolNegotiation().getAvailableVersions().get(0).getVersions().size()).isEqualTo(ClientConfig.SUPPORTED_PROTOCOL_VERSIONS.length);
    }

    @Test
    public void testHandleStep1() throws Exception {
        Protocol protocolStep1 = JsonUtil.fromJson(SampleProtocolSteps.STEP_1, Protocol.class);
        Protocol response = ClientProtocolHandler.handleProtocolStep(protocolStep1);

        assertThat(response.getProtocolId()).isEqualTo(2);
        assertThat(response.getKeyNegotiation()).isNotNull();
        assertThat(response.getKeyNegotiation().getAvailableSids().get(0).getSids().size()).isEqualTo(ClientConfig.SUPPORTED_SIDS.length);
    }

    @Test
    public void testHandleStep3() throws Exception {
        Protocol protocolStep3 = JsonUtil.fromJson(SampleProtocolSteps.STEP_3, Protocol.class);
        Protocol response = ClientProtocolHandler.handleProtocolStep(protocolStep3);

        assertThat(response.getProtocolId()).isEqualTo(4);
        Payload payload = response.getPayload();
        assertThat(payload).isNotNull();
        assertThat(payload.getInitialCoin().size()).isEqualTo(ClientConfig.INITIAL_COINS.length);
        assertThat(payload.getEncryptedCoin().size()).isEqualTo(ClientConfig.INITIAL_COINS.length);
    }

    @Test
    public void testHandleStep5() throws Exception {
        // TODO: not working! BadPadding
        // needed to setup CryptoUtils
        Protocol protocolStep3 = JsonUtil.fromJson(SampleProtocolSteps.STEP_3, Protocol.class);
        Protocol irrelevant = ClientProtocolHandler.handleProtocolStep(protocolStep3);

        Protocol protocolStep5 = JsonUtil.fromJson(SampleProtocolSteps.STEP_5, Protocol.class);
        Protocol response = ClientProtocolHandler.handleProtocolStep(protocolStep5);

        assertThat(response.getProtocolId()).isEqualTo(6);
        Payload payload = response.getPayload();
        assertThat(payload).isNotNull();
        assertThat(payload.getDeChosenCoin()).isNotEmpty();
        assertThat(payload.getKeyA().size()).isEqualTo(2);
    }

}
