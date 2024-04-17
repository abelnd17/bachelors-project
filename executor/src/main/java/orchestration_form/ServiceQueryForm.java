package orchestration_form;

import lombok.Getter;

/**
 * Required by the OrchestrationForm to specify the name of the requested service.
 */
public class ServiceQueryForm {
    @Getter
    private final String serviceDefinitionRequirement;

    /**
     * Creates a ServiceQueryForm
     * @param serviceName name of the requested service
     */
    public ServiceQueryForm(String serviceName){
        serviceDefinitionRequirement=serviceName;
    }
}