package com.github.huksley.camunda;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProspectService {

    @Autowired
    ProspectRepository prospectRepository;

//
//    public ProspectService(ProspectRepository prospectRepository) {
//        this.prospectRepository = prospectRepository;
//    }

    public Prospect getProspect(String processID) {
        return prospectRepository.findByProcessID(processID);
    }

    public Prospect saveProspect(Prospect prospect) {
        return prospectRepository.save(prospect);
    }
}
