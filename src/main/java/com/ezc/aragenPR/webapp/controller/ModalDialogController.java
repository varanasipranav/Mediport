package com.ezc.aragenPR.webapp.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ezc.aragenPR.webapp.service.IMasterService;
import com.ezc.aragenPR.webapp.service.IUserService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/modal")
public class ModalDialogController {

	@Autowired
	IMasterService masterService;
	
	@Autowired
    private IUserService iUserService;
	
	
	
	@Value("#{'${city}'.split(',')}")
	private List<String> city;
	
	
	
	@GetMapping(value = "/delete-user/{id}") 
    public String getUserById(@PathVariable("id") long id, Model model) {   	    	
    	model.addAttribute("user", ((Optional)iUserService.getUserByID(id)).get());
		return "modals/deleteUser" ;
    }
	

	@GetMapping(value="/delete-city/{city}")
	public String deleteCityModal(@PathVariable("city") String city, Model model) {
		model.addAttribute("placeMasterDto", masterService.getCityDetails(city));
		return "modals/deleteCity" ;
	}
	  
		
	@GetMapping(value="/att-file")
	public String attachFileModal( ) {
			
		return "modals/attachFile" ; 
	}
}



