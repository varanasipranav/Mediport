package com.ezc.aragenPR.webapp.config;

import java.util.Optional;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.ezc.aragenPR.webapp.model.Users;

class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
       // return Optional.of("Chanakya") ;

    	Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    	  if (authentication == null || !authentication.isAuthenticated()) {
    	   return null;
    	  }else {
    		  return Optional.of(((Users) authentication.getPrincipal()).getUserId());
    	  }
    	

    	
    	
       // return Optional.of("Admin");
    }
}