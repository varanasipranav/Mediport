package com.ezc.aragenPR.webapp.persistance.dao;

import org.quartz.Job;
import org.reflections.Reflections;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class JobScanner {

    public List<String> findAllJobClassNames() {
        Reflections reflections = new Reflections("com.ezc.aragenPR.webapp.job");
        Set<Class<? extends Job>> jobClasses = reflections.getSubTypesOf(Job.class);
        return jobClasses.stream()
                .map(Class::getName)
                .collect(Collectors.toList());
    }
}