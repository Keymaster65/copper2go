package io.github.keymaster65.copper2go.engine.vanilla.workflow;

import io.github.keymaster65.copper2go.api.workflow.WorkflowData;
import io.github.keymaster65.copper2go.engine.vanilla.VanillaEngine;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class Pricing1Test {

    public static final String UUID = "uuid";
    public static final String PAYLOAD = "payload";

    @Test
    void main() {
        final VanillaEngine engine = Mockito.mock(VanillaEngine.class);
        final Pricing1 pricing1 = new Pricing1(engine);

        pricing1.main(new WorkflowData(UUID, PAYLOAD));

        Mockito.verify(engine).reply(UUID, "5 cent.");
    }

    @Test
    void mainOneway() {
        final VanillaEngine engine = Mockito.mock(VanillaEngine.class);
        final Pricing1 pricing1 = new Pricing1(engine);

        pricing1.main(new WorkflowData(null, PAYLOAD));

        Mockito.verifyNoInteractions(engine);
    }
}