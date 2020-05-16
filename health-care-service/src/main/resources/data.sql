DROP TABLE IF EXISTS prospect;

CREATE TABLE prospect (

  id INT AUTO_INCREMENT  PRIMARY KEY,
  name VARCHAR(250),
  isReferral boolean,
  processID VARCHAR(250) DEFAULT NULL
);