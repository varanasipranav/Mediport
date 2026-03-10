/*
 * package com.ezc.aragenPR.webapp.controller; import lombok.extern.slf4j.Slf4j;
 * 
 * import java.security.Principal; import java.util.Date;
 * 
 * import jakarta.servlet.http.HttpServletRequest;
 * 
 * import org.springframework.beans.factory.annotation.Autowired; import
 * org.springframework.security.authentication.
 * UsernamePasswordAuthenticationToken; import
 * org.springframework.security.core.Authentication; import
 * org.springframework.security.core.authority.AuthorityUtils; import
 * org.springframework.security.core.context.SecurityContextHolder; import
 * org.springframework.security.web.servletapi.
 * SecurityContextHolderAwareRequestWrapper; import
 * org.springframework.stereotype.Controller; import
 * org.springframework.ui.Model; import
 * org.springframework.web.bind.annotation.GetMapping; import
 * org.springframework.web.bind.annotation.PathVariable; import
 * org.springframework.web.servlet.mvc.support.RedirectAttributes;
 * 
 * import com.ezc.aragenPR.webapp.dto.Output; import
 * com.ezc.aragenPR.webapp.model.EzLinkController; import
 * com.ezc.aragenPR.webapp.model.EzPriceRequestHeader; import
 * com.ezc.aragenPR.webapp.model.Users; import
 * com.ezc.aragenPR.webapp.service.IEmailService; import
 * com.ezc.aragenPR.webapp.service.IRequestService; import
 * com.ezc.aragenPR.webapp.service.IUserService; import
 * com.ezc.aragenPR.webapp.util.EzCipher;
 * 
 * import lombok.extern.slf4j.Slf4j;
 * 
 * @Slf4j
 * 
 * @Controller public class OfflineController {
 * 
 * @Autowired private IUserService iUserService;
 * 
 * @Autowired private IEmailService iEmailService;
 * 
 * @Autowired private IRequestService iRequestService;
 * 
 * @GetMapping("/offline/test") public String testLogin(Principal
 * principal,Authentication auth,Model
 * model,SecurityContextHolderAwareRequestWrapper requestWrapper) {
 * log.debug("Inside offline controller:::"); String userId="PRAPPROVE"; Users
 * user=iUserService.findUserByUserId(userId);
 * 
 * Authentication authentication = new UsernamePasswordAuthenticationToken(user,
 * null, AuthorityUtils.createAuthorityList("ROLE_APR"));
 * SecurityContextHolder.getContext().setAuthentication(authentication); return
 * "redirect:/priceapr/view/20201207-113"; }
 * 
 * @GetMapping("/offline/testsavelink") public String testSaveLink(Model model)
 * { log.debug("Inside offline controller:::"); String userId="PRAPPROVE";
 * 
 * EzLinkController linkController= new EzLinkController();
 * linkController.setCreatedOn(new Date());
 * linkController.setEmail("ggangula@answerthink.com");
 * linkController.setExpiryDate(new Date());
 * linkController.setMailContent("test emaill");
 * linkController.setMailSubject("test sub"); linkController.setStatus("S");
 * linkController.setUserId("PRAPPROVE");
 * linkController.setUserRole("ROLE_APR");
 * linkController.setDocId("20201207-119"); EzLinkController
 * linkControllerobj=iEmailService.saveLink(linkController); String encTxt = new
 * EzCipher().ezEncrypt(linkControllerobj.getDocId());
 * log.debug("encTxt:::"+encTxt); return "/"; }
 * 
 * @GetMapping("/offline/priceapr/{contid}") public String
 * fromPriceAprLink(HttpServletRequest request,@PathVariable String
 * contid,Principal principal,Authentication auth,Model
 * model,SecurityContextHolderAwareRequestWrapper requestWrapper, final
 * RedirectAttributes ra) {
 * log.debug("Inside offline controller ::: fromPriceAprLink:::"); EzCipher
 * ezCipher=new EzCipher(); log.debug("contid ::::"+contid); String decTxt =
 * ezCipher.ezDecrypt(contid); log.debug("decTxt ::::"+decTxt); EzLinkController
 * ezLinkController=iEmailService.getDetailsById(Integer.parseInt(decTxt));
 * request.getSession().setAttribute("offline",true);
 * 
 * if(ezLinkController != null) { EzPriceRequestHeader priceReqHeader=
 * iRequestService.getRequestById(ezLinkController.getDocId());
 * if(priceReqHeader != null &&
 * ezLinkController.getStatus().equals(priceReqHeader.getStatus()) && !new
 * Date().after(ezLinkController.getExpiryDate())) { String
 * userId=ezLinkController.getUserId(); String
 * userRole=ezLinkController.getUserRole(); Users
 * user=iUserService.findUserByUserId(userId); Authentication authentication =
 * new UsernamePasswordAuthenticationToken(user, null,
 * AuthorityUtils.createAuthorityList(userRole));
 * SecurityContextHolder.getContext().setAuthentication(authentication);
 * //ra.addFlashAttribute("isOffline","Y"); return
 * "redirect:/priceapr/view/"+ezLinkController.getDocId(); } }
 * 
 * Output output=new Output(); output.setText("Link Expired");
 * output.setType("error"); model.addAttribute("output",output); return
 * "priceapr/confirmpage";
 * 
 * }
 * 
 * }
 * 
 * 
 * 
 */