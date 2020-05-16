package com.github.huksley.camunda;


import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProspectRepository extends CrudRepository<Prospect, Long> {

    Prospect findByProcessID(String processID);
}