package com.ezc.aragenPR.webapp.service;

import com.ezc.aragenPR.webapp.model.EzComments;
import com.ezc.aragenPR.webapp.model.EzcEhsActionTable;
import com.ezc.aragenPR.webapp.model.EzcEhsHeader;
import com.ezc.aragenPR.webapp.model.Users;
import com.ezc.aragenPR.webapp.persistance.dao.CommentsRepo;
import com.ezc.aragenPR.webapp.persistance.dao.EzcEhsHeaderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class EzcEhsHeaderServiceImpl implements EzcEhsHeaderService {

		@Autowired
	    private EzcEhsHeaderRepository headerRepository;
		
		 @Autowired
		    private CommentsRepo commentsRepo;


	    @Override
	    public List<EzcEhsHeader> getAllHeaders() {
	        return headerRepository.findAll();  
	    }

		@Override
		public EzcEhsHeader findByDocId(Integer docId) {
			return headerRepository.findByDocId(docId); 
		}
		
		 @Override
		    public EzcEhsHeader save(EzcEhsHeader header) {
		        return headerRepository.save(header);
		    }

		@Override
		public List<EzcEhsHeader> getFilteredHeaders(Date fromDate, Date toDate, String status, String userId) {
			userId = userId != null ? userId.trim() : null;
			return headerRepository.findByVendorFilters(fromDate, toDate, status, userId);

		}

		@Override
		public int getPendingReview() {
			
	    	Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			Users userObj = (Users) authentication.getPrincipal();
		    String userId = userObj.getUserId().trim();
		    return headerRepository.countNewByVendorId(userId);
		}
		
		  @Override
		    public List<EzComments> getCommetnsById(String docId, String docType) {
		        return commentsRepo.findByDocIdAndDocType(docId, docType);
		    }

		    @Override
		    public void saveComments(String comments, Integer docId, String userId, String tab) {
		        if (comments == null || comments.trim().isEmpty()) return;

		        EzComments commentObj = new EzComments();
		        commentObj.setComments(comments);
		        commentObj.setCreatedBy(userId);
		        commentObj.setDate(new Date());
		        commentObj.setDocType("EHSReport"); // Or pass this as a parameter if needed
		        commentObj.setTab(tab);

		        if (docId != null)
		            commentObj.setDocId(docId.toString());

		        commentsRepo.save(commentObj);
		    }

			@Override
			public List<EzcEhsHeader> getFilteredHeaders(Date fromDate, Date toDate, String status) {
				return headerRepository.findSubmittedBetweenDates(fromDate, toDate);
			}

}
