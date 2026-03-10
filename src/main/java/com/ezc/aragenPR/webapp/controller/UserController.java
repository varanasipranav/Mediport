package com.ezc.aragenPR.webapp.controller;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import com.ezc.aragenPR.webapp.model.UserPrivilege;
import com.ezc.aragenPR.webapp.persistance.dao.EzPrivilegesRepo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ezc.aragenPR.webapp.dto.PasswordDto;
import com.ezc.aragenPR.webapp.dto.UserDto;
import com.ezc.aragenPR.webapp.error.InvalidOldPasswordException;
import com.ezc.aragenPR.webapp.model.EzStates;
import com.ezc.aragenPR.webapp.model.EzValueMapping;
import com.ezc.aragenPR.webapp.model.Roles;
import com.ezc.aragenPR.webapp.model.UserForm;
import com.ezc.aragenPR.webapp.model.UserRoles;
import com.ezc.aragenPR.webapp.model.UserZones;
import com.ezc.aragenPR.webapp.model.Users;
import com.ezc.aragenPR.webapp.model.WorkGroup_Users;
import com.ezc.aragenPR.webapp.model.Work_Groups;
import com.ezc.aragenPR.webapp.persistance.dao.EzUserCreationDefaults;
import com.ezc.aragenPR.webapp.security.ActiveUserStore;
import com.ezc.aragenPR.webapp.security.ISecurityUserService;
import com.ezc.aragenPR.webapp.service.IMasterService;
import com.ezc.aragenPR.webapp.service.IUserService;
import com.ezc.aragenPR.webapp.util.GenericResponse;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    ActiveUserStore activeUserStore;

    @Autowired 
    IUserService userService;

    @Autowired
	IMasterService masterService;
    
    @Autowired
    EzUserCreationDefaults userDefaults;

    @Autowired
    EzPrivilegesRepo ezPrivilegesRepo;

    @Autowired
    private IUserService iUserService;
    
    @Autowired
    private MessageSource messages;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private ISecurityUserService securityService;
    
    @Autowired
    private Environment env;
    

    @RequestMapping(value = "/loggedUsers", method = RequestMethod.GET)
    public String getLoggedUsers(final Locale locale, final Model model) {
      model.addAttribute("users", activeUserStore.getUsers());
      return "Content";
  }
    
//  jgjgg
//    'mhjjhmjh'
    @RequestMapping(value = "/next", method = RequestMethod.GET)
    public String nextPage(final Locale locale, final Model model) {
      model.addAttribute("users", activeUserStore.getUsers());
      return "Content2";
  }    
    
    @RequestMapping(value = "/loggedUsersFromSessionRegistry", method = RequestMethod.GET)
    public String getLoggedUsersFromSessionRegistry(final Locale locale, final Model model) {
        model.addAttribute("users", userService.getUsersFromSessionRegistry());
        return "users";
    }
    
    @RequestMapping(value = "/UserCreation", method = RequestMethod.GET) //@GetMapping("/UserCreation")
    public String test(final Locale locale, final Model model) {
    	
    	log.debug(":UserCreation GET:::::::");
    	
    	//List<EzStates> states = userDefaults.getStates();
	   	 List<UserRoles> roles = userDefaults.getRoles();

        List<UserPrivilege> privileges = ezPrivilegesRepo.findAll();
	   	 //List<UserZones> zones = userDefaults.getZones();
	   	 ArrayList<String> arrTypes=new ArrayList<String>();
	     arrTypes.add("LOCATION");
	   //  arrTypes.add("BUS_PLACE");
         arrTypes.add("VENDOR");
        arrTypes.add("DOCTYPE");
	   	 Map<String, List<EzValueMapping>> masterMap = masterService.getValueMapList(arrTypes);
	    	 
	   	 
	    	 model.addAttribute("locations", masterMap.get("LOCATION"));
	    //	model.addAttribute("busplaces", masterMap.get("BUS_PLACE"));
            model.addAttribute("vendors", masterMap.get("VENDOR"));
        model.addAttribute("docType", masterMap.get("DOCTYPE"));
		     //model.addAttribute("states", states);
		     model.addAttribute("roles", roles);
             model.addAttribute("allPrivileges", privileges);
		     //model.addAttribute("zones", zones);
		     model.addAttribute("userForm", new UserForm()); 

		     
			 
        return "user/ezUserCreationForm";

        
    }
    
    @RequestMapping(value = "/UserCreation", method = RequestMethod.POST) //@PostMapping("/UserCreation")
    public String saveUser(@Valid @ModelAttribute UserForm userForm, BindingResult bindingResult, final Model model) {
   	
    	log.debug(":UserCreation POST:::::::");
    	
    	 if (bindingResult.hasErrors()) { 
    		 
    		 //List<EzStates> states = userDefaults.getStates();
        	 List<UserRoles> roles = userDefaults.getRoles();
        	 //List<UserZones> zones = userDefaults.getZones();
        	 ArrayList<String> arrTypes=new ArrayList<String>();
         	 arrTypes.add("LOCATION");
             arrTypes.add("DOCTYPE");
        	 Map<String, List<EzValueMapping>> masterMap = masterService.getValueMapList(arrTypes);
         	 
        	 
         	 model.addAttribute("docType", masterMap.get("DOCTYPE"));
         	//model.addAttribute("busplaces", masterMap.get("BUS_PLACE"));
    	     //model.addAttribute("states", states);
    	     model.addAttribute("roles", roles);
    	     //model.addAttribute("zones", zones);
    	     
    	     
    		 
             return "user/ezUserCreationForm";
         }
    	
    	List<EzStates> list = userDefaults.getStates();

    	UserDto userDto = new UserDto();

    	userDto.setFirstName(userForm.getFirstName());
    	userDto.setLastName(userForm.getLastName());
    	userDto.setPassword("portal");
    	userDto.setEmail(userForm.getEmail());
    	userDto.setRole(userForm.getRole());
        userDto.setPrivilegeIds(userForm.getPrivilegeIds());
        if(Objects.equals(userForm.getRole(), "ROLE_VEND")) {
            userDto.setVendorCode(userForm.getVendorCode());
        }
    	userDto.setUserId(userForm.getUserId());
    	userDto.setLocation(userForm.getLocation());
        userDto.setDocType(userForm.getDocType());
    	//userDto.setBusPlace(userForm.getBusPlace());
    	//userDto.setGroup(userForm.getGroup());
    	//userDto.setZone(userForm.getZone());
    	userDto.setEnabled(true);
    	
    	iUserService.registerNewUserAccount(userDto);
    	
    	List<Users> usersList = iUserService.getUsersList();
   	 	model.addAttribute("usersList", usersList);
       return "user/ezUsersList";
    }
 
    @RequestMapping(value = "/usersList", method = RequestMethod.GET) //@GetMapping("/UserCreation")
    public String getUsrsList(final Locale locale, final Model model) {   	
    	List<Users> usersList = iUserService.getUsersList();
    	 model.addAttribute("usersList", usersList);
    	//log.debug(":::::::usersList:::::"+usersList);
        return "user/ezUsersList";
    }
    
    @PostMapping(value = "/deleteUser/{id}")
    public String deleteUser(@PathVariable("id") Long id,final Locale locale, final Model model) {   
    	
    	Optional<Users> lv_user = iUserService.getUserByID(id); 
		Users lv_User = (Users)lv_user.get();
    	
    	Users user = new Users();
    	user.setId(id);
    	user.setUserId(lv_User.getUserId());
    	
    	iUserService.deleteUser(user);
    	
    	List<Users> usersList = iUserService.getUsersList();
   	 	model.addAttribute("usersList", usersList);
       return "user/ezUsersList";
    }
    
    @GetMapping(value = "/editUser/{id}")
    public String editUser(@PathVariable("id") Long id,final Locale locale, final Model model) {   	
    	 List<UserRoles> roles = userDefaults.getRoles();
    	 List<UserZones> zones = userDefaults.getZones();   
      	
    	 Optional<Users> user = iUserService.getUserByID(id); 
		 Users lv_User = (Users)user.get();
		 List<Roles> userRoles = (List<Roles>)lv_User.getRoles();
 
		 Roles userRole = null;
    	 
		 List<WorkGroup_Users> userGroupList =  iUserService.getGroupsByUserId(lv_User.getUserId());
		 List<String> userGroups = new ArrayList<String>();
    	 List<Work_Groups> wfGroups = null;
      	 try{
    		 userRole = (Roles)userRoles.get(0);
    		 //wfGroups = (List<Work_Groups>)iUserService.getGroupsByRole(userRole.getName());
    		 wfGroups = (List<Work_Groups>)iUserService.getGroupsByRole("ROLE_REQ_CR");
    	 }catch(Exception e){}	
 
      	 
      	UserForm formFields = new UserForm();
      	
      	String userZone = "";
      	for(WorkGroup_Users userGroup : userGroupList)
      	{
      		userZone =userGroup.getZonalGrp(); 
      		userGroups.add(userGroup.getId()+"");
      	}
      	
      	List<WorkGroup_Users> zonalHDSubGroups = (List<WorkGroup_Users>) iUserService.getZonalHeadSubGroups(userZone);
      	
		/*
		 * log.debug("::::zonalHDSubGroups:::::::"+zonalHDSubGroups); Iterator iter =
		 * zonalHDSubGroups.iterator(); while(iter.hasNext()) { WorkGroup_Users lv_wgu =
		 * (WorkGroup_Users)iter.next();
		 * log.debug(lv_wgu.getUserId()+":::::"+lv_wgu.getGroupId()+":::::"+lv_wgu.
		 * getStateGrp()+"::::"+lv_wgu.getZonalGrp()); }
		 */
      	
      	try{
      		userZone = userZone.substring(0,userZone.indexOf("_"));
      	}catch(Exception e){}	
      	
      	log.debug("::::::::::userGroups"+userGroups);
      	
      	formFields.setId(lv_User.getId());
      	formFields.setUserId(lv_User.getUserId());
      	formFields.setFirstName(lv_User.getFirstName());
      	formFields.setLastName(lv_User.getLastName());
      	formFields.setEmail(lv_User.getEmail());
      	formFields.setRole(userRole.getName());
      	formFields.setGroup(userGroups);
      	formFields.setZone(userZone);
        Set<UserPrivilege> privileges = lv_User.getPrivileges();
        formFields.setPrivilegeIds(privileges.stream().map(UserPrivilege::getId).collect(Collectors.toList()));
        formFields.setVendorCode(lv_User.getVendorCode());
 		 
      	 model.addAttribute("userForm", formFields);
    	 model.addAttribute("roles", roles);
	     model.addAttribute("zones", zones);
	     model.addAttribute("wfGroups", wfGroups);
         model.addAttribute("privileges", ezPrivilegesRepo.findAll());
         model.addAttribute("vendorCodes", masterService.getValueMapList(Arrays.asList("VENDOR")).get("VENDOR"));
	     //model.addAttribute("userRole", userRole);
	     //model.addAttribute("userGroup", userGroups);
	     //model.addAttribute("userForm", user);
	     //model.addAttribute("formFields", new UserForm());

        return "user/ezEditUser";
    }
    
    @RequestMapping(value = "/editSave", method = RequestMethod.POST) //@PostMapping("/UserCreation")
    public String editSave(@ModelAttribute UserForm userForm, final Model model) {	
    	
    	
    	//log.debug(":::::::userForm:::::"+userForm);
    	
    	UserDto userDto = new UserDto();
    	
    	userDto.setId(userForm.getId());
    	userDto.setFirstName(userForm.getFirstName());
    	userDto.setLastName(userForm.getLastName());
    	userDto.setEmail(userForm.getEmail());
    	userDto.setRole(userForm.getRole());
    	userDto.setUserId(userForm.getUserId());
        userDto.setPrivilegeIds(userForm.getPrivilegeIds());
        userDto.setVendorCode(userForm.getVendorCode());
    	//userDto.setGroup(userForm.getGroup());
    	//userDto.setZone(userForm.getZone());
    	
    	iUserService.editUser(userDto);
     	
    	List<Users> usersList = iUserService.getUsersList();
   	 	model.addAttribute("usersList", usersList);
       return "user/ezUsersList";
    }
    
    
    @GetMapping(value = "/userByUserId/{userId}")
    public @ResponseBody String getUserByUserId(@PathVariable("userId") String userId) {   	  
    	
    	log.debug(":UsersUsersUsersUsers input:::::::"+userId);
    	Users user = iUserService.findUserByUserId(userId); 
    	
    	log.debug(":UsersUsersUsersUsers:::::::"+user);
    	//log.debug(":getUserByUserId:::userId:::::::"+user.getId());
    	
    	if(user==null)
    		return "N";	
    	else
            return "Y";
    }
    
    // Reset password
    @RequestMapping(value = "/resetPassword", method = RequestMethod.POST)
    @ResponseBody
    public GenericResponse resetPassword(final HttpServletRequest request, @RequestParam("email") final String userEmail) {
        final Users user = userService.findUserByEmail(userEmail);
        if (user != null) {
            final String token = UUID.randomUUID().toString();
            userService.createPasswordResetTokenForUser(user, token);
            mailSender.send(constructResetTokenEmail(getAppUrl(request), request.getLocale(), token, user));
        }
        return new GenericResponse(messages.getMessage("message.resetPasswordEmail", null, request.getLocale()));
    }

    @RequestMapping(value = "/changePassword", method = RequestMethod.GET)
    public String showChangePasswordPage(final Locale locale, final Model model, @RequestParam("id") final long id, @RequestParam("token") final String token) {
        final String result = securityService.validatePasswordResetToken(id, token);
        if (result != null) {
            model.addAttribute("message", messages.getMessage("auth.message." + result, null, locale));
            return "redirect:/login?lang=" + locale.getLanguage();
        }
        return "redirect:/updatePassword.html?lang=" + locale.getLanguage();
    }

    @RequestMapping(value = "/savePassword", method = RequestMethod.POST)
    @ResponseBody
    public GenericResponse savePassword(final Locale locale, @Valid PasswordDto passwordDto) {
        final Users user = (Users) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        userService.changeUserPassword(user, passwordDto.getNewPassword());
        return new GenericResponse(messages.getMessage("message.resetPasswordSuc", null, locale));
    }

    // change user password
    @RequestMapping(value = "/updatePassword", method = RequestMethod.POST)
    @ResponseBody
    public GenericResponse changeUserPassword(final Locale locale, @Valid PasswordDto passwordDto) {
        final Users user = userService.findUserByEmail(((Users) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getEmail());
        if (!userService.checkIfValidOldPassword(user, passwordDto.getOldPassword())) {
            throw new InvalidOldPasswordException();
        }
        userService.changeUserPassword(user, passwordDto.getNewPassword());
        return new GenericResponse(messages.getMessage("message.updatePasswordSuc", null, locale));
    }
    @GetMapping("/bulkUserCreation")
    public String bulkUserCreation(){
        return "user/bulkUserCreation";
    }

    @GetMapping("/downloadSampleExcel")
    public void downloadSampleExcel(HttpServletResponse response) throws IOException {
        List<UserRoles> roles = userDefaults.getRoles();
        ArrayList<String> arrTypes=new ArrayList<String>();
        arrTypes.add("LOCATION");
        arrTypes.add("DOCTYPE");
        arrTypes.add("BUS_PLACE");
        arrTypes.add("VENDOR");
        Map<String, List<EzValueMapping>> masterMap = masterService.getValueMapList(arrTypes);
        List<EzValueMapping> locations = masterMap.get("LOCATION");
        List<EzValueMapping> docType = masterMap.get("DOCTYPE");
        List<EzValueMapping> busPlaces = masterMap.get("BUS_PLACE");
        List<EzValueMapping> vendors = masterMap.get("VENDOR");
        //model.addAttribute("states", states);
        List<UserPrivilege> privileges = ezPrivilegesRepo.findAll();
        List<String> columns = new ArrayList<>(Arrays.asList("FirstName", "LastName", "Email", "Role", "UserId", "Location", "BusPlace", "VendorCode"));
        for (UserPrivilege privilege : privileges) {
            columns.add(privilege.getDesc()); // or privilege.getName()
        }
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Users");
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < columns.size(); i++) {
            headerRow.createCell(i).setCellValue(columns.get(i));
            sheet.autoSizeColumn(i);
        }
        DataValidationHelper validationHelper = sheet.getDataValidationHelper();
//        // Dropdown for Role
//        String[] roleDescs = roles.stream().map(UserRoles::getRoleDesc).toArray(String[]::new);
//        DataValidationConstraint roleConstraint = validationHelper.createExplicitListConstraint(roleDescs);
//        CellRangeAddressList roleAddressList = new CellRangeAddressList(1, 100, 3, 3); // Role column (index 3)
//        DataValidation roleValidation = validationHelper.createValidation(roleConstraint, roleAddressList);
//        roleValidation.setShowErrorBox(true);
//        sheet.addValidationData(roleValidation);
//
//        // Dropdown for VendorCode
//        String[] vendorValues = vendors.stream().map(EzValueMapping::getValue1).toArray(String[]::new);
//        DataValidationConstraint vendorConstraint = validationHelper.createExplicitListConstraint(vendorValues);
//        CellRangeAddressList vendorAddressList = new CellRangeAddressList(1, 100, 7, 7); // VendorCode column (index 7)
//        DataValidation vendorValidation = validationHelper.createValidation(vendorConstraint, vendorAddressList);
//        vendorValidation.setShowErrorBox(true);
//        sheet.addValidationData(vendorValidation);


        String[] allowedValues = {"yes", "no"};
        for (int i = 0; i < privileges.size(); i++) {
            int colIndex = columns.size() - privileges.size() + i; // privilege columns start index
            DataValidationConstraint constraint = validationHelper.createExplicitListConstraint(allowedValues);
            CellRangeAddressList addressList = new CellRangeAddressList(1, 100, colIndex, colIndex); // rows 2-101
            DataValidation validation = validationHelper.createValidation(constraint, addressList);
            validation.setShowErrorBox(true);
            sheet.addValidationData(validation);
        }
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=sample_users.xlsx");
        workbook.write(response.getOutputStream());
        workbook.close();
    }
    @PostMapping("/uploadUsersExcel")
    public String uploadUsersExcel(@RequestParam("file") MultipartFile file, Model model) {
        List<UserRoles> roles = userDefaults.getRoles();
        List<String> errors = new ArrayList<>();
        try (InputStream is = file.getInputStream()) {
            Workbook workbook = new XSSFWorkbook(is);
            Sheet sheet = workbook.getSheetAt(0);
            DataFormatter dataFormatter = new DataFormatter();

            // Read privilege columns from header
            Row headerRow = sheet.getRow(0);
            int privilegeStartIdx = 8; // index after VendorCode
            List<String> privilegeDescs = new ArrayList<>();
            for (int i = privilegeStartIdx; i < headerRow.getLastCellNum(); i++) {
                privilegeDescs.add(headerRow.getCell(i).getStringCellValue());
            }
            // Map privilege desc to id
            List<UserPrivilege> allPrivileges = ezPrivilegesRepo.findAll();
            Map<String, String> descToId = allPrivileges.stream()
                    .collect(Collectors.toMap(UserPrivilege::getDesc, p -> String.valueOf(p.getId())));

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                try {
                    UserDto userDto = new UserDto();
                    userDto.setFirstName(row.getCell(0).getStringCellValue());
                    userDto.setLastName(row.getCell(1).getStringCellValue());
                    userDto.setEmail(row.getCell(2).getStringCellValue());
                    Map<String, String> descToIdRole = roles.stream()
                            .collect(Collectors.toMap(UserRoles::getRoleDesc, UserRoles::getRoleId));

                    String selectedRoleDesc = row.getCell(3).getStringCellValue();
                    String roleId = descToIdRole.getOrDefault(selectedRoleDesc, selectedRoleDesc);
                    userDto.setRole(roleId);
                    userDto.setRole(row.getCell(3).getStringCellValue());
                    userDto.setUserId(row.getCell(4).getStringCellValue());
                    userDto.setLocation(Arrays.asList(row.getCell(5).getStringCellValue().split(",")));
                    userDto.setBusPlace(dataFormatter.formatCellValue(row.getCell(6)));
                    userDto.setVendorCode(dataFormatter.formatCellValue(row.getCell(7)));

                    // Collect privilegeIds based on "yes"/"no"
                    List<String> privilegeIds = new ArrayList<>();
                    for (int j = 0; j < privilegeDescs.size(); j++) {
                        String value = dataFormatter.formatCellValue(row.getCell(privilegeStartIdx + j));
                        if ("yes".equalsIgnoreCase(value.trim())) {
                            String id = descToId.get(privilegeDescs.get(j));
                            if (id != null) privilegeIds.add(id);
                        }
                    }
                    userDto.setPrivilegeIds(privilegeIds);
                    userDto.setPassword("portal");
                    userDto.setEnabled(true);
                    iUserService.registerNewUserAccount(userDto);
                } catch (Exception e) {
                    errors.add("Row " + (i + 1) + ": " + e.getMessage());
                }
            }
            workbook.close();
        } catch (Exception e) {
            errors.add("File processing error: " + e.getMessage());
        }
        model.addAttribute("errors", errors);
        model.addAttribute("usersList", iUserService.getUsersList());
        return "user/ezUsersList";
    }

    

    /******************Non API Methods**********************/
    
    private String getAppUrl(HttpServletRequest request) {
        return "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
    }
    


    private SimpleMailMessage constructResetTokenEmail(final String contextPath, final Locale locale, final String token, final Users user) {
        final String url = contextPath + "/user/changePassword?id=" + user.getId() + "&token=" + token;
        final String message = messages.getMessage("message.resetPassword", null, locale);
        return constructEmail("Reset Password", message + " \r\n" + url, user);
    }

    private SimpleMailMessage constructEmail(String subject, String body, Users user) {
        final SimpleMailMessage email = new SimpleMailMessage();
        email.setSubject(subject);
        email.setText(body);
        email.setTo(user.getEmail());
        email.setFrom(env.getProperty("support.email"));
        return email;
    }

}
