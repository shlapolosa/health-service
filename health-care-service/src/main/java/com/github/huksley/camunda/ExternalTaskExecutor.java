package com.github.huksley.camunda;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.camunda.bpm.engine.externaltask.LockedExternalTask;
import org.camunda.bpm.engine.variable.VariableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Obviously should be other microservice to handle external tasks,
 * but for the sake of example is implemented here.
 */
@Controller
public class ExternalTaskExecutor {
    static final Logger log = LoggerFactory.getLogger(ExternalTaskExecutor.class);

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    ProspectRepository prospectRepository;

    @Value("${server.port}")
    int port = 8080;

    private String workerId = "sampleWorker";

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    static class TaskInfo {
        String id;
        String processInstanceId;
        int retries;
        Map<String, VariableReference2> variables;
    }

    @Data
    @Builder
    static class FetchRequest {
        String workerId;
        int maxTasks;
        FetchTopicRequest[] topics;
    }

    @Data
    @NoArgsConstructor
    static class Idnumber {
        String value;
    }

    @Data
    @NoArgsConstructor
    static class ResponseVariables {
        Idnumber idnumber;
        Idnumber idnumber2;
    }

    @Data
    @Builder
    static class FetchTopicRequest {
        String topicName;
        long lockDuration;
        String[] variables;
    }

    @Data
    static class VariablesList {
        HashMap<String, VariableReference> variables;
    }

    @Data
    @Builder
    static class VariableReference {
        String value;
    }

    @Data
    @NoArgsConstructor
    static class VariableReference2 {
        String value;
    }

    @Data
    @Builder
    static class CompleteRequest {
        String workerId;
        Map<String, VariableReference> variables;
    }

    @Scheduled(initialDelay = 5000, fixedRate = 5000)
    public void periodicalTaskExecutor() {
        FetchRequest r = FetchRequest.builder().
                maxTasks(1).
                workerId(workerId).
                topics(new FetchTopicRequest[]{
                        FetchTopicRequest.builder().
                                lockDuration(10000).
                                topicName("approveExternal").
                                build()
                }).
                build();

        try {
            String fetchUrl = "http://localhost:" + port + "/engine-rest/external-task/fetchAndLock";
            TaskInfo[] fetchResponse = restTemplate.postForObject(fetchUrl, r, TaskInfo[].class);
            log.info("Got {} external tasks to execute", fetchResponse.length);
            for (TaskInfo task : fetchResponse) {
                log.info("Marking {} as complete", task.getId());
                Prospect prospect = new Prospect();
                prospect.setProcessID(task.getProcessInstanceId());
                prospect.setName(((VariableReference2)task.getVariables().get("name")).getValue());
                prospect.setIsReferral(((VariableReference2)task.getVariables().get("isReferral")).getValue().equals("true"));
                prospect.setIdNumber(((VariableReference2)task.getVariables().get("idNumber")).getValue());
                prospect = prospectRepository.save(prospect);
                String completeUrl = "http://localhost:" + port + "/engine-rest/external-task/" + task.getId() + "/complete";
                CompleteRequest completeRequest = CompleteRequest.builder().
                        workerId(workerId).
                        variables(new HashMap<>()).
                        build();
                completeRequest.variables.put("created", VariableReference.builder().value(String.valueOf(prospect.getId())).build());
                restTemplate.postForEntity(completeUrl, completeRequest, null);
            }
        } catch (Exception e) {
            log.warn("Failed too check for tasks: {}", e.getMessage(), e);
        }
    }
}
