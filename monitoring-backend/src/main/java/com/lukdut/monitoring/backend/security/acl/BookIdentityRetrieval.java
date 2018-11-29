package com.lukdut.monitoring.backend.security.acl;

import com.lukdut.monitoring.backend.model.Sensor;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.acls.model.ObjectIdentityRetrievalStrategy;


public class BookIdentityRetrieval implements ObjectIdentityRetrievalStrategy {
    @Override
    public ObjectIdentity getObjectIdentity(Object domainObject) {
        if (Sensor.class.equals(domainObject.getClass())) {
            return new ObjectIdentityImpl(Sensor.class, ((Sensor) domainObject).getId());
        }
        return null;
    }
}
