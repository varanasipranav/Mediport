package com.ezc.aragenPR.webapp.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.ezc.aragenPR.webapp.dto.EmailFormat;
import com.ezc.aragenPR.webapp.model.EzCustomerFormDetails;
import com.ezc.aragenPR.webapp.model.EzCustomerFormHeader;
import com.ezc.aragenPR.webapp.model.EzLinkController;
import com.ezc.aragenPR.webapp.model.Users;
import com.ezc.aragenPR.webapp.persistance.dao.LinkControllerRepo;
import com.ezc.aragenPR.webapp.persistance.dao.UserRepository;
import com.ezc.aragenPR.webapp.util.EzCipher;
import com.google.protobuf.Message;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;


@Service
@Transactional
@Slf4j
public class EmailServiceImpl implements IEmailService {

	@Autowired 
    private JavaMailSender emailSender;
	
	@Autowired 
    private LinkControllerRepo linkRepo;
	
	
	@Autowired 
    private UserRepository userRepository ;
	
	@Autowired 
    private IUserService iUserService;
	
	
	@Value("${site.url}")
	private String siteUrl="";
	
	@Value("${mail.fromaddress}")
	private String fromEmail="";
	
	@Value("${mail.ccmail}")
	private String ccmail="";
	
	@Override
	public void sendSimpleMessage(String to, String subject, String text) {
		SimpleMailMessage message = new SimpleMailMessage(); 
        message.setFrom(fromEmail);
        message.setTo(to); 
        message.setSubject(subject); 
        message.setText(text);
        try {
			emailSender.send(message);
		} catch (Exception e) {
			log.debug("Error in sending mails:::"+e);
		}
	}
	@Override
	public void sendRequestMailToNextPart(EzCustomerFormHeader custFormHeader,List<String> emailList) {
		log.debug("sendRequestMailToNextPart method Started:::docId:::"+custFormHeader.getDocId());
		log.debug("emailList::"+emailList!=null?emailList.toString():"no emails are there in the email List");
		List<InternetAddress> ccAddresses = new ArrayList<>();
		if(emailList.size()>0) {
			 ccAddresses = new ArrayList<>();
			for (String email : emailList) {
			    try {
					ccAddresses.add(new InternetAddress(email));
				} catch (AddressException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		String mailSub = "";
		String mailTxt = "";
		String link="http://10.62.10.138/";
		String loggedInUserRole="";
		boolean sendMail=false;
		EzCipher cipher=new EzCipher();
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		c.add(Calendar.DATE, 5);
		Date expiryDate =c.getTime();
		String plant="%"+custFormHeader.getSupplierPlant()+"%"; 
		
		 List<Users> usersList=userRepository.findUsersByRoleAndPlant(plant,custFormHeader.getNextParticipant());
		 log.debug("usersList size in mail::"+usersList.size()); 
		// for(Users user:usersList) {
			// toEmailList.add(new InternetAddress(user.getEmail())); 
		 //}
		 for(Users user:usersList) {
			 log.debug("user  in mail::::"+user.getUserId()+":::"+user.getEmail());
			if("SUBMITTED".equals(custFormHeader.getStatus()))  
			{
				log.debug(":::status:::"+custFormHeader.getStatus()); 
			
				mailSub = "PPAP Document "+custFormHeader.getMaterialDescription()+" and ID : "+custFormHeader.getDocId()+" has been sent for approval";
				
				log.debug("mailSub:::"+mailSub);
					//mailTxt += "Dear "+(String)objArr[1]+",<br>";
					//mailTxt += "Price Approval Form Id : "+reqHeader.getId()+" has been sent for approval. Please <a href='http://localhost:8081//offline//priceapr//"+cipher.ezEncrypt(linkContOut.getId()+"")+"'>click</a> on the link to approve.";
					try {
						mailTxt=EmailFormat.formatEmail("PPAP APPROVAL", user.getFirstName(),"PPAP Document ID : "+custFormHeader.getDocId()+" has been sent for approval. Please review and take necessary action on it.", link, "","Please click here to Open");
						log.debug("sendRequestMailToNextPart method mailTxt::::::"+mailTxt);
					} catch (Exception e1) {
					}
				    MimeMessage mimeMessage = emailSender.createMimeMessage();
			        try {
						MimeMessageHelper message = new MimeMessageHelper(mimeMessage, false, "UTF-8");
						message.setFrom(fromEmail);
						message.setTo(user.getEmail());
						if(ccmail != null && !"null".equals(ccmail)  && !"".equals(ccmail))
							message.setCc(ccmail);
						if("ROLE_OS_HOS_CEAT".equalsIgnoreCase(custFormHeader.getNextParticipant()) || "ROLE_OS_TECH_CEAT".equalsIgnoreCase(custFormHeader.getNextParticipant())) { 
							if(ccAddresses.size()>0) {
								message.setCc(ccAddresses.toArray(new InternetAddress[0])); 
							} 
						}
						message.setBcc("nagesh.bethi@answerthink.com");
						log.debug("sendRequestMailToNextPart method toMail::::::"+user.getEmail());
						message.setSubject(mailSub); 
						message.setText(mailTxt,true);
					} catch (MessagingException e) {
						
					}
			        try {
						emailSender.send(mimeMessage);
					} catch (Exception e) {
						log.debug("Error in sending mails:::"+e);
					}
					
			}
			else if("APPROVED".equals(custFormHeader.getStatus()))
			{
				log.debug(":::docId:::"+custFormHeader.getDocId());
				log.debug(":::status:::"+custFormHeader.getStatus());
				mailSub = "PPAP Document" +custFormHeader.getMaterialDescription() +" and ID : "+custFormHeader.getDocId()+" has been approved";
				//mailTxt += "Dear "+(String)objArr[1]+",<br>";
				//mailTxt += "Price Approval Form Id : "+reqHeader.getId()+" has been sent for approval. Please <a href='http://localhost:8081//offline//priceapr//"+cipher.ezEncrypt(linkContOut.getId()+"")+"'>click</a> on the link to approve.";
				try {
					mailTxt=EmailFormat.formatEmail("PPAP APPROVAL", user.getFirstName(), "PPAP Document ID : "+custFormHeader.getDocId()+" has been approved.",link, "","Please click here to Open");
					log.debug("sendRequestMailToNextPart method mailTxt::::::"+mailTxt);
				} catch (Exception e1) {
				}
			    MimeMessage mimeMessage = emailSender.createMimeMessage();
		        try {
					MimeMessageHelper message = new MimeMessageHelper(mimeMessage, false, "UTF-8");
					message.setFrom(fromEmail);
					//message.setTo(user.getEmail());
					message.setTo((user.getEmail())); 
					if(ccAddresses.size()>0) {
						message.setCc(ccAddresses.toArray(new InternetAddress[0])); 
					}
					if(ccmail != null && !"null".equals(ccmail)  && !"".equals(ccmail))
						message.setCc(ccmail);
					message.setBcc("nagesh.bethi@answerthink.com");
					log.debug("sendRequestMailToNextPart method toMail::::::"+user.getEmail());
					message.setSubject(mailSub); 
					message.setText(mailTxt,true);
				} catch (MessagingException e) { 
					
				}
		        try {
					emailSender.send(mimeMessage);
				} catch (Exception e) {
					log.debug("Error in sending mails:::"+e);
				}	
			}
			else if("SENDBACK".equals(custFormHeader.getStatus()))  
			{
				log.debug(":::docId:::"+custFormHeader.getDocId());
				log.debug(":::status:::"+custFormHeader.getStatus());
				
				mailSub = "PPAP Document "+custFormHeader.getMaterialDescription()+" and  ID : "+custFormHeader.getDocId()+" has been sent back";
				log.debug("mailSub:::"+mailSub);
				//mailTxt += "Dear "+(String)objArr[1]+",<br>";
				//mailTxt += "Price Approval Form Id : "+reqHeader.getId()+" has been sent for approval. Please <a href='http://localhost:8081//offline//priceapr//"+cipher.ezEncrypt(linkContOut.getId()+"")+"'>click</a> on the link to approve.";
				mailTxt=EmailFormat.formatEmail("PPAP APPROVAL", user.getFirstName(), "PPAP Document ID : "+custFormHeader.getDocId()+" has been SEND BACK.", link, "","Please click here to Open");
				log.debug("sendRequestMailToNextPart method mailTxt::::::"+mailTxt);
			    MimeMessage mimeMessage = emailSender.createMimeMessage();
		        try {
					MimeMessageHelper message = new MimeMessageHelper(mimeMessage, false, "UTF-8");
					message.setFrom(fromEmail);
					//message.setTo((String)objArr[3]); 
					message.setTo(user.getEmail()); 
					message.setBcc("nagesh.bethi@answerthink.com");
					if(ccmail != null && !"null".equals(ccmail)  && !"".equals(ccmail))
						message.setCc(ccmail);
					message.setSubject(mailSub); 
					log.debug("sendRequestMailToNextPart method toMail::::::"+user.getEmail());
					message.setText(mailTxt,true);
				} catch (MessagingException e) {
					
				}
		        try {
					emailSender.send(mimeMessage);
				} catch (Exception e) {
					log.debug("Error in sending mails:::"+e);
				}
			}
			else if("REJECTED".equals(custFormHeader.getStatus()))
			{
				log.debug(":::docId:::"+custFormHeader.getDocId());
				log.debug(":::status:::"+custFormHeader.getStatus());
				
				mailSub = "PPAP Document " +custFormHeader.getMaterialDescription()+" and ID : "+custFormHeader.getDocId()+" has been rejected";
				log.debug("mailSub:::"+mailSub);
				//mailTxt += "Dear "+(String)objArr[1]+",<br>";
				//mailTxt += "Price Approval Form Id : "+reqHeader.getId()+" has been sent for approval. Please <a href='http://localhost:8081//offline//priceapr//"+cipher.ezEncrypt(linkContOut.getId()+"")+"'>click</a> on the link to approve.";
				mailTxt=EmailFormat.formatEmail("PPAP APPROVAL", user.getFirstName(), "PPAP Document ID : "+custFormHeader.getDocId()+" has been Rejected.",link, "","Please click here to Open");
				log.debug("sendRequestMailToNextPart method mailTxt::::::"+mailTxt);
			    MimeMessage mimeMessage = emailSender.createMimeMessage();
		        try {
					MimeMessageHelper message = new MimeMessageHelper(mimeMessage, false, "UTF-8");
					message.setFrom(fromEmail);
					message.setTo(user.getEmail());
					if(ccAddresses.size()>0) {
						message.setCc(ccAddresses.toArray(new InternetAddress[0])); 
					}
					message.setBcc("nagesh.bethi@answerthink.com"); 
					log.debug("sendRequestMailToNextPart method mailTxt::::::"+mailTxt);
					log.debug("sendRequestMailToNextPart method toMail::::::"+user.getEmail());
					//message.setTo((String)objArr[3]);  
					if(ccmail != null && !"null".equals(ccmail)  && !"".equals(ccmail))
						message.setCc(ccmail);
					message.setSubject(mailSub); 
					message.setText(mailTxt,true);
				} catch (MessagingException e) {
					
				}
		        try {
					emailSender.send(mimeMessage); 
				} catch (Exception e) {
					log.debug("Error in sending mails:::"+e);
				}
			}
			
		 }
		
	}
	@Override
	public EzLinkController saveLink(EzLinkController linkController) {
		return linkRepo.save(linkController);
		
	}
	@Override
	public EzLinkController getDetailsById(Integer id) {
		Optional<EzLinkController> linkContOpt=linkRepo.findById(id);
		if(linkContOpt.isPresent())
			return linkContOpt.get();
		else
			return null;
	}

	
}
