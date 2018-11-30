package com.lukdut.monitoring.backend.security.acl;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ImeisArrayFilter {
    public boolean filter(Authentication authentication, List<Long> imeis) {
        //TODO: check that all imeis allowed
        return true;
    }
}
