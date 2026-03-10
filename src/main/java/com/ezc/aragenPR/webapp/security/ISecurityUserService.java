package com.ezc.aragenPR.webapp.security;

public interface ISecurityUserService {

    String validatePasswordResetToken(long id, String token);

}
