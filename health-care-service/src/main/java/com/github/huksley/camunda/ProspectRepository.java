package com.github.huksley.camunda;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProspectRepository extends JpaRepository<Prospect, Long> {

    Optional<Prospect> findByProcessID(String processID);

    Optional<Prospect> findByIdNumber(String idnumber);
}