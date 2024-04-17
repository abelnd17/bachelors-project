package orchestration_form;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Converts the OrchestratorForm into a JSON String.
 */
public class OrchestratorFormJSONConverter {
    private String json;
    private Logger logger = Logger.getLogger(getClass().getName());

    /**
     * Converts the OrchestratorForm into a JSON String.
     * @param serviceName name of the requested service
     * @param address address of the requesting system
     * @param port port of the requesting system
     * @param system name of the requesting system
     * @return OrchestratorForm converted to a JSON String
     */
    public String convert(String serviceName, String address, int port, String system, Boolean intercloud) {
        OrchestrationForm form = new OrchestrationForm(new ServiceQueryForm(serviceName),new RequesterSystem(address,port,system), intercloud);
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        try {
            json = ow.writeValueAsString(form);
        } catch (JsonProcessingException e){
            logger.log(Level.SEVERE,e.getMessage());
        }
        return json;
    }
}
