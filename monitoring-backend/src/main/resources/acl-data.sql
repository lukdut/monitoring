INSERT INTO acl_class (id, class) VALUES
(1, 'com.lukdut.monitoring.backend.model.Sensor');

INSERT INTO acl_sid (id, principal, sid) VALUES
  (1, 0, 'ROLE_ADMIN'),
  (2, 0, 'ROLE_MANAGER'),
  (3, 0, 'ROLE_OBSERVER'),
  (4, 1, 'admin');