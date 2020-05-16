DROP TABLE IF EXISTS prospect;

CREATE TABLE prospect (

  id INT AUTO_INCREMENT  PRIMARY KEY,
  name VARCHAR(250),
  is_referral boolean,
  id_number VARCHAR(30),
  processID VARCHAR(250) DEFAULT NULL
);