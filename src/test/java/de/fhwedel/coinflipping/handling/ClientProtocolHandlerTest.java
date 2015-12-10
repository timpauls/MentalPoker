package de.fhwedel.coinflipping.handling;

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
    public void testHandleStep3() throws Exception {
        Protocol protocolStep3 = JsonUtil.fromJson(SampleProtocolSteps.STEP_3, Protocol.class);
        Protocol response = ClientProtocolHandler.handleProtocolStep(protocolStep3);

        Payload payload = response.getPayload();
        assertThat(payload).isNotNull();
        assertThat(payload.getInitialCoin().size()).isEqualTo(2);
        assertThat(payload.getEncryptedCoin().size()).isEqualTo(2);
    }


}
