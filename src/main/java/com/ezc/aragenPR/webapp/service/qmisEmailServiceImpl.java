package com.ezc.aragenPR.webapp.service;

import com.ezc.aragenPR.webapp.dto.EmailFormat;
import com.ezc.aragenPR.webapp.model.EqhDocStep;
import com.ezc.aragenPR.webapp.model.EqhRoles;
import com.ezc.aragenPR.webapp.model.EzQmisHeader;
import com.ezc.aragenPR.webapp.model.EzValueMapping;
import com.ezc.aragenPR.webapp.model.UserDefaults;
import com.ezc.aragenPR.webapp.model.Users;
import com.ezc.aragenPR.webapp.persistance.dao.EzQmisHeaderRepository;
import com.ezc.aragenPR.webapp.persistance.dao.UserDefRepository;
import com.ezc.aragenPR.webapp.persistance.dao.ValueMappingRepo;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

@Service
@Slf4j
public  class qmisEmailServiceImpl implements emailServiceModule{

    @Autowired
    private JavaMailSender emailSender;

    @Value("${site.url}")
    private String siteUrl="";

    @Value("${mail.fromaddress}")
    private String fromEmail="";

    @Value("${mail.ccmail}")
    private String ccmail="";

    @Autowired
    private EzQmisHeaderRepository ezQmisHeaderRepository;

    @Autowired
    private UserDefRepository userDefRepository;

    @Autowired
    private ValueMappingRepo valueMappingRepo;

    @Override
    public void sendMonthlyEmail(List<Users> usersList, String subject ) {
        String mailSub = subject;
        String mailTxt = "";
        String link="http://10.62.10.138/";
        System.out.println(usersList);
        for(Users user: usersList){
            mailTxt= EmailFormat.formatEmail("QMIS Data Upload", user.getFirstName(),"QMIS : Please review and Fill all the required data.", link, "","Please click here to Open");
            MimeMessage mimeMessage = emailSender.createMimeMessage();
            try {
                MimeMessageHelper message = new MimeMessageHelper(mimeMessage, false, "UTF-8");
                message.setFrom(fromEmail);
//                message.setTo(user.getEmail());
                message.setTo("sushruth.kontham@answerthink.com");
                if(ccmail != null && !"null".equals(ccmail)  && !"".equals(ccmail))
                    message.setCc(ccmail);
                message.setSubject(mailSub);
                message.setText(mailTxt,true);
            } catch (MessagingException e) {

            }
            try {
                emailSender.send(mimeMessage);
                insertDocument(user);
                System.out.println(mimeMessage);
            } catch (Exception e) {
                log.debug("Error in sending mails:::"+e);
            }
        }
    }
    @Override
    public void sendReminderEmail(List<Users> usersList, String subject ) {
        String mailSub = subject;
        String mailTxt = "";
        String link="http://10.62.10.138/";
        System.out.println(usersList);
        for(Users user: usersList){
            mailTxt= EmailFormat.formatEmail("QMIS Data Upload", user.getFirstName(),"QMIS : Please review and Fill all the required data.", link, "","Please click here to Open");
            MimeMessage mimeMessage = emailSender.createMimeMessage();
            try {
                MimeMessageHelper message = new MimeMessageHelper(mimeMessage, false, "UTF-8");
                message.setFrom(fromEmail);
//                message.setTo(user.getEmail());
                message.setTo("sushruth.kontham@answerthink.com");
                if(ccmail != null && !"null".equals(ccmail)  && !"".equals(ccmail))
                    message.setCc(ccmail);
                message.setSubject(mailSub);
                message.setText(mailTxt,true);
            } catch (MessagingException e) {

            }
            try {
                emailSender.send(mimeMessage);
                System.out.println(mimeMessage);
            } catch (Exception e) {
                log.debug("Error in sending mails:::"+e);
            }
        }
    }

    private void insertDocument(Users user){
        UserDefaults userDefRecord = userDefRepository.findByuserIdAndKey(user.getId(), "LOCATION");
        String plant = userDefRecord.getValue();
        LocalDate now = LocalDate.now();
        int month = now.getMonthValue();
        String shortMonth = now.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
        int year = now.getYear();
        EzValueMapping valueMapDoc = valueMappingRepo.findByMapKeyAndValue1("MATMANU", plant);
        if (valueMapDoc == null || valueMapDoc.getValue2() == null) {
            return;
        }
        List<String> materialType = List.of(valueMapDoc.getValue2().split("##"));
        System.out.println(plant);
        EzValueMapping valueMapDocPlantName = valueMappingRepo.findByMapKeyAndValue1("LOCATION", plant);
        EzValueMapping valueMapDocVendorName = valueMappingRepo.findByMapKeyAndValue1("VENDOR", user.getUserId());
        for (String type : materialType) {
            EzQmisHeader qmisHeader = new EzQmisHeader();
            qmisHeader.setEqhPlant(plant);
            qmisHeader.setEqhPlantName(valueMapDocPlantName.getValue2());
            qmisHeader.setEqhType1(type);
            qmisHeader.setEqhMonth(month);
            qmisHeader.setEqhMonthName(shortMonth);
            qmisHeader.setEqhYear(year);
            qmisHeader.setEqhCurrentStep(String.valueOf(EqhDocStep.QMIS_VENDOR_ENTRY));
            qmisHeader.setEqhNextStep(String.valueOf(EqhDocStep.QMIS_QA_REVIEW));
            qmisHeader.setEqhCurrentParticipant(String.valueOf(EqhRoles.ROLE_VEND));
            qmisHeader.setEqhNextParticipant(String.valueOf(EqhRoles.ROLE_OS_QA_CEAT));
            qmisHeader.setEqhVendor(user.getUserId());
            qmisHeader.setEqhVendorName(valueMapDocVendorName.getValue2());
            ezQmisHeaderRepository.save(qmisHeader);

            System.out.println("saved: " + plant + " - " + type);
        }
        }
}
