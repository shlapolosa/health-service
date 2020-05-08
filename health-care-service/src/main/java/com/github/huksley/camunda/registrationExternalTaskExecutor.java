package com.github.huksley.camunda;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.camunda.bpm.engine.externaltask.ExternalTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * Obviously should be other microservice to handle external tasks,
 * but for the sake of example is implemented here.
 */
@Controller
public class registrationExternalTaskExecutor {
    static final Logger log = LoggerFactory.getLogger(registrationExternalTaskExecutor.class);

    @Autowired
    private RestTemplate restTemplate;

    @Value("${server.port}")
    int port = 8080;

    private String workerId = "registrationWorker";

    //    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    static class TaskInfo {
        String id;
        String businessKey;
        String processInstanceId;
        ResponseVariables variables;
    }

    //    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    @NoArgsConstructor
    static class ResponseVariables {
        Idnumber idnumber;
        Idnumber idnumber2;
        Name name;
    }

    @Data
    @NoArgsConstructor
    static class Idnumber {
        String value;
    }

    @Data
    @NoArgsConstructor
    static class Name {
        String value;
    }

    @Data
    @Builder
    static class FetchRequest {
        String workerId;
        int maxTasks;
        FetchTopicRequest[] topics;
    }

    @Data
    @Builder
    static class FetchTopicRequest {
        String topicName;
        long lockDuration;
        String[] variables;
    }

    @Data
    @Builder
    static class VariableReference {
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
                                topicName("registerProspect").
                                build()
                }).
                build();

        try {
            String fetchUrl = "http://localhost:" + port + "/engine-rest/external-task/fetchAndLock";
            TaskInfo[] fetchResponse = restTemplate.postForObject(fetchUrl, r, TaskInfo[].class);

            for (TaskInfo task : fetchResponse) {

                log.info("================================================");
                log.info("With variables {} as value", task.toString());
//                log.info("With variables {} as value", task.getProcessInstanceId());
//                log.info("With variables {} as key", task.getBusinessKey());
                log.info("With variables {} as idnumber", task.getVariables());
//                log.info("With variables {} as name", task.getVariables().get("name"));
                log.info("================================================");
//                String completeUrl = "http://localhost:" + port + "/engine-rest/external-task/" + task.getId() + "/complete";
//                CompleteRequest completeRequest = CompleteRequest.builder().
//                        workerId(workerId).
//                        variables(new HashMap<>()).
//                        build();
//                completeRequest.variables.put("prospectRegistered", VariableReference.builder().value("true").build());
//                restTemplate.postForEntity(completeUrl, completeRequest, null);
            }
        } catch (Exception e) {
            log.warn("Failed too check for tasks: {}", e.getMessage(), e);
        }
    }
}
