package com.ezc.aragenPR.webapp.error;

import java.nio.file.AccessDeniedException;
import java.sql.SQLException;
import java.util.Date;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.expression.spel.SpelParseException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandling {

	public GlobalExceptionHandling() {
		 
		
	}
	
	@ExceptionHandler(RequestNotFound.class)
	public String databaseError(Exception exception, Model mav,HttpServletRequest  req) {
		// Nothing to do. Return value 'databaseError' used as logical view name
		// of an error page, passed to view-resolver(s) in usual way.
		log.error("Request raised " + exception.getClass().getSimpleName());
		//model.add
		log.error("Request: " + req.getRequestURI() + " raised " + exception);

		
		mav.addAttribute("exception", exception);
		mav.addAttribute("url", req.getRequestURL());
		mav.addAttribute("timestamp", new Date().toString());
		mav.addAttribute("status", 500);

		
		return "error/500";
		
	}

	@ExceptionHandler(AuthorizationDeniedException.class)
	public String authDeniedEx(Exception exception, Model mav,HttpServletRequest  req) {
	    log.error("Request raised " + exception.getClass().getSimpleName());
		log.error("Request: " + req.getRequestURI() + " raised " + exception);


		mav.addAttribute("exception", exception);
		mav.addAttribute("url", "/dashboard/index.html");
		mav.addAttribute("errorMessage","You do not have Authorization to access this resource.");
		mav.addAttribute("status", 403);
		return "error/403";
	}

	@ExceptionHandler(SpelParseException.class)
	public String roleException(Exception exception, Model mav,HttpServletRequest  req) {
        log.error("Request raised " + exception.getClass().getSimpleName());
		log.error("Request: " + req.getRequestURI() + " raised " + exception);


		mav.addAttribute("exception", exception);
		mav.addAttribute("url", "/dashboard/index.html");
		mav.addAttribute("errorMessage","You do not have the required Role to access this resource.");
		mav.addAttribute("status", 403);
		return "error/403";
	}

	@ExceptionHandler(AccessDeniedException.class)
	public String accessDeniedEx(Exception exception, Model mav,HttpServletRequest  req) {
		log.error("Request raised " + exception.getClass().getSimpleName());
		log.error("Request: " + req.getRequestURI() + " raised " + exception);

		mav.addAttribute("exception", exception);
		mav.addAttribute("url", "/dashboard/index.html");
		mav.addAttribute("errorMessage","You do not have Access to access this resource.");
		mav.addAttribute("status", 403);

		return "error/403";
	}

	@ExceptionHandler(RuntimeException.class)
	public String runTimeError(Exception exception, Model mav,HttpServletRequest  req) {
		// Nothing to do. Return value 'databaseError' used as logical view name
		// of an error page, passed to view-resolver(s) in usual way.
		log.error("Request raised " + exception.getClass().getSimpleName());
		//model.add
		log.error("Request: " + req.getRequestURI() + " raised " + exception);

		
		mav.addAttribute("exception", exception);
		mav.addAttribute("url", req.getRequestURL());
		mav.addAttribute("timestamp", new Date().toString());
		mav.addAttribute("status", 500);

		
		return "error/500";
		
	}

	@ExceptionHandler(SQLException.class)
	public String sqlError(Exception exception, Model mav,HttpServletRequest  req) {
		// Nothing to do. Return value 'databaseError' used as logical view name
		// of an error page, passed to view-resolver(s) in usual way.
		log.error("Request raised " + exception.getClass().getSimpleName());
		//model.add
		log.error("Request: " + req.getRequestURI() + " raised " + exception);

		
		mav.addAttribute("exception", exception);
		mav.addAttribute("url", req.getRequestURL());
		mav.addAttribute("timestamp", new Date().toString());
		mav.addAttribute("status", 500);

		
		return "error/500";
		
	}

		
	

}
