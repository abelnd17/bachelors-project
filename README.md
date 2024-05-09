# Integration tool for the Eclipse Arrowhead Framework with Camunda 8 for automated BPMN workflow execution

<p>An integration tool that allows users to automate BPMN service task execution in Camunda 8 using the Arrowhead Framework. </p>

## Prerequisites

1. Arrowhead should be installed and running with the required services registered in the local cloud.
   
2. Run Camunda 8 if using the self-managed version.
   
3. Configure the `application.properties` file according to your needs:

- `arrowhead.orchestrator.port`: The port of the Arrowhead Orchestrator.
- `arrowhead.https.enabled`: Enable HTTPS for communication with Arrowhead (set to `false` for HTTP).
- `arrowhead.orchestrator.address`: The address of the Arrowhead Orchestrator.
- `service.https.enabled`: Enable HTTPS for communication with services (set to `false` for HTTP).
- `triggerintercloud.enabled`: Enable/disable searching for services in remote clouds (inter-cloud communication)

> NOTE: If using SaaS version of Camunda 8, the Zeebe client needs to be [configured differently](https://github.com/camunda-community-hub/spring-zeebe).

## Usage

<p>The tool requires specific inputs from the BPMN and responses with a particular format from the services.</p>

### In the Camunda Modeler

1. To access the tool from the Camunda Modeler, add a service tesk to your diagram and type `execute` as the task definition type.

![](/executor/images/servicetaskexample.png)

2. Add an input variable called `service` with the name of the required service as registered on Arrowhead.
   
![](/executor/images/servicetaskexample2.png)

3. Once your diagram is finished, save the `.bpmn` file in the `resources` folder.

### Response from Service Endpoint

<p>For the tool to successfully notify Camunda of the result of the service, the service must return a response with a specific format.</p>

#### Case: Successful execution

On successful execution a set of variables can be returned optionally. 

```
{
  "status": "SUCCESS",
  "optionalPayload1" : "value",
  "optionalPayload1" : "value",
  ...
}
```

#### Case: Error in execution

The tool supports the handling of [error events](https://docs.camunda.io/docs/components/modeler/bpmn/error-events/) provided that the correct error code is returned:

```
{
  "status": "ERROR",
  "errorCode": "string"
}
```

#### Case: Fatal Error in execution

On a fatal error a message is required detailing the reason for failure. The execution in Camunda is halted and an [incident](https://docs.camunda.io/docs/components/concepts/incidents/) is raised. 

```
{
  "status": "FAILURE",
  "message": "string"
}
```
