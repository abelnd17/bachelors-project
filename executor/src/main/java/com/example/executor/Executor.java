package com.example.executor;

import com.example.executor.enums.HTTPMethod;
import com.example.executor.enums.JobStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import orchestration_form.OrchestratorFormJSONConverter;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class Executor {

    @Value("${arrowhead.orchestrator.port}")
    private Integer arrowheadPort;

    @Value("${arrowhead.https.enabled}")
    private Boolean arrowheadHTTPS;

    @Value("${arrowhead.orchestrator.address}")
    private String arrowheadAddress;

    @Value("${service.https.enabled}")
    private Boolean serviceHTTPS;

    @Value("${triggerintercloud.enabled}")
    private Boolean interCloud;

    @JobWorker(type = "execute", fetchAllVariables = true, autoComplete = false)
    public void Executor(final JobClient client, final ActivatedJob job) {
        Logger logger = Logger.getLogger(getClass().getName());
        HTTPRequestSender HTTPSender = new HTTPRequestSender();
        try{
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> map;
            map = job.getVariablesAsMap();
            if (!map.containsKey("service")) {
                client.newFailCommand(job).retries(0).errorMessage("Missing service name!").send();
                return;
            }
            String serviceName = (String) map.get("service");
            map.remove("service");
            String orchestrationString = new OrchestratorFormJSONConverter().convert(serviceName, "test", 1234, "tester",interCloud);
            URL url = new URL("http" + (arrowheadHTTPS ? "s" : "") + "://" + arrowheadAddress + ":" + arrowheadPort + "/orchestrator/orchestration-by-proxy");
            String response = HTTPSender.sendRequest(url, orchestrationString, HTTPMethod.POST);
            if (response == null) {
                client.newFailCommand(job).retries(0).errorMessage("Could not connect to Arrowhead!").send();
                return;
            }
            JSONObject responseJSON = new JSONObject(response);
            if (responseJSON.getJSONArray("response").length() == 0) {
                client.newFailCommand(job).retries(0).errorMessage("Service not found!").send();
                return;
            }
            String address = responseJSON.getJSONArray("response").getJSONObject(0).getJSONObject("provider").getString("address");
            int port = responseJSON.getJSONArray("response").getJSONObject(0).getJSONObject("provider").getInt("port");
            String uri = responseJSON.getJSONArray("response").getJSONObject(0).getString("serviceUri");
            URL url2 = new URL("http" + (serviceHTTPS ? "s" : "") + "://" + address + ":" + port + uri);
            String mapString = mapper.writeValueAsString(map);
            String serviceResponse = HTTPSender.sendRequest(url2, mapString, HTTPMethod.POST);
            if (serviceResponse == null) {
                client.newFailCommand(job).retries(0).errorMessage("Could not connect to the requested service!").send();
                return;
            }
            JSONObject serviceResponseJSON = new JSONObject(serviceResponse);
            JobStatus status = JobStatus.valueOf(serviceResponseJSON.getString("status").toUpperCase());
            switch (status) {
                case SUCCESS -> {
                    Map<String, Object> result = mapper.readValue(serviceResponse, HashMap.class);
                    result.remove("status");
                    client.newCompleteCommand(job).variables(result).send();
                }
                case FAILURE -> {
                    String message = serviceResponseJSON.getString("message");
                    client.newFailCommand(job).retries(0).errorMessage(message).send();
                }
                case ERROR -> {
                    String code = serviceResponseJSON.getString("errorCode");
                    client.newThrowErrorCommand(job).errorCode(code).send();
                }
            }
        } catch (IOException | JSONException e){
            logger.log(Level.SEVERE,e.getMessage());
        }

    }

}
