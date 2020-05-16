package com.github.huksley.camunda;

import lombok.Data;
import lombok.ToString;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@ToString
@Data
class Customer{
    String idnumber;
    String name;
    String surname;
    String isReferral;
}

@Data
public class RegisterClient implements JavaDelegate {

//    Expression idnumber;
//    Expression name;
//    Expression surname;
//    Expression isReferral;

    static final Logger log = LoggerFactory.getLogger(RegisterClient.class);

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        Map<String, Object> variables = delegateExecution.getVariables();
        Prospect prospect = new Prospect();
        prospect.setName((String) variables.get("name"));
        prospect.setIsReferral((Boolean) variables.get("isReferral"));
        prospect.setProcessID(delegateExecution.getProcessInstanceId());

        log.info("=========================================================================");
//        log.info(delegateExecution.getVariableTyped("customer").toString());
        log.info((String) variables.get("name"));
        log.info(String.valueOf((Boolean) variables.get("isReferral")));
        log.info(String.valueOf((Customer) variables.get("customer")));
        log.info("=========================================================================");
//        log.info(idnumber.getValue(delegateExecution).toString());
//        log.info(name.getValue(delegateExecution).toString());
//        log.info(surname.toString());
//        log.info(isReferral.toString());
        log.info("=========================================================================");

    }


}
