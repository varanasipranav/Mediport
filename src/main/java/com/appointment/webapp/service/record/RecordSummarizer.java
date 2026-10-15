package com.appointment.webapp.service.record;

import com.appointment.webapp.model.MedicalProfile;
import com.appointment.webapp.model.User;

import java.util.List;

/**
 * Turns a patient's history into a few lines a doctor can read in seconds.
 * The default implementation uses plain rules. To use an AI model instead, provide another bean of this
 * type (mark it {@code @Primary}); the record page and prescription form need no change.
 * (Patient data would then leave the clinic's server, so get consent and a data-processing agreement first.)
 */
public interface RecordSummarizer {

    List<String> summarize(User patient, MedicalProfile profile, List<PatientRecord.Visit> visitsNewestFirst);
}
