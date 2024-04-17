package orchestration_form;

import lombok.Getter;

/**
 * Describes the system requesting the service. Requires for the OrchestrationForm.
 */
public class RequesterSystem {
    @Getter
    private final String address;
    @Getter
    private final int port;
    @Getter
    private final String systemName;

    /**
     * Creates a RequesterSystem.
     * @param address address of the system
     * @param port port of the system
     * @param system name of the system
     */
    public RequesterSystem(String address, int port, String system){
        this.address=address;
        this.port=port;
        systemName=system;
    }

}
