package com.github.huksley.camunda;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProspectService {

    @Autowired
    ProspectRepository prospectRepository;

//
//    public ProspectService(ProspectRepository prospectRepository) {
//        this.prospectRepository = prospectRepository;
//    }

//    public Prospect getProspect(String processID) {
//        return prospectRepository.findByProcessID(processID);
//    }

    public Prospect saveProspect(Prospect prospect) {
        return prospectRepository.save(prospect);
    }

    public Optional<Prospect> getProspectByProcess(String processID) {
        return prospectRepository.findByProcessID(processID);
    }

    public Optional<Prospect> getProspectByID(Long id) {
        return prospectRepository.findById(id);
    }

    public Optional<Prospect> getProspectByIDNumber(String idnumber) {
        return prospectRepository.findByIdNumber(idnumber);
    }
}
