package orchestration_form;

import lombok.Getter;
import java.util.HashMap;
import java.util.Map;

/**
 * Data Transfer Object needed by the Arrowhead Orchestrator to find a registered service.
 */
public class OrchestrationForm {
    @Getter
    private final ServiceQueryForm requestedService;
    @Getter
    private final RequesterSystem requesterSystem;
    @Getter
    private final Map<String,Boolean> orchestrationFlags;

    /**
     * Creates an OrchestrationForm.
     * @param service ServiceQueryForm with the required service name
     * @param system the requester system
     */
    public OrchestrationForm(ServiceQueryForm service, RequesterSystem system, Boolean interCloud){
        requestedService = service;
        requesterSystem = system;
        orchestrationFlags = new HashMap<>();
        orchestrationFlags.put("onlyPreferred",false);
        orchestrationFlags.put("enableQoS",false);
        orchestrationFlags.put("triggerInterCloud",interCloud);
        orchestrationFlags.put("overrideStore",true);
    }
}
