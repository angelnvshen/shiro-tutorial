package com.own.shiro.tutorials;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.mgt.*;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.Factory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by bf50 on 2016/1/19.
 */
public class Tutorial {
    private static final transient Logger log = LoggerFactory.getLogger(Tutorial.class);
    public static void main(String[] args) {
        log.info("My first apache shiro application");

        Factory<org.apache.shiro.mgt.SecurityManager> factory =
                new IniSecurityManagerFactory("classpath:shiro/shiro.ini");
        SecurityManager securityManager = factory.getInstance();
        SecurityUtils.setSecurityManager(securityManager);

        // get the currently executing user
        Subject currentUser = SecurityUtils.getSubject();

        Session session = currentUser.getSession();
        session.setAttribute("someKey", "aValue");
        String value = (String) session.getAttribute("someKey");
        if(value.equals("aValue"))
            log.info("Retrieved the correct value ! [" + value + "]");

         if(!currentUser.isAuthenticated()){
            UsernamePasswordToken token = new UsernamePasswordToken("lonestarr", "vespa");
            token.setRememberMe(true);

            try{
                currentUser.login(token);
            }catch (UnknownAccountException uae){
                log.info("There is no user with username of " + token.getPrincipal());
            }catch (IncorrectCredentialsException ice){
                log.info("Password for accout " + token.getPrincipal() + " is incorrect !");
            }catch (LockedAccountException lae){
                log.info("The account for username " + token.getPrincipal()
                + " is locked, Please contract your administrator to " +
                "unlock it .");
            }
            // … catch more exceptions here (maybe custom ones specific to your application?
            catch (AuthenticationException ae){

            }
         }

        log.info("User [" + currentUser.getPrincipal() + "] " +
                "logged in successfully .");

        // test a role
        if(currentUser.hasRole("schwartz")){
            log.info("May the Schwartz be with you!");
        } else {
            log.info("Hello, mere mortal.");
        }

        if(currentUser.isPermitted("winnebago:drive:eagle5")){
            log.info("You are permitted to 'drive' the winnebago with license plate (id) 'eagle5' . " +
                    "Here are the keys - have fun!");
        } else {
            log.info("Sorry, you aren't allowed to drive the 'eagle5' winnebago!");
        }

        //all done - log out!
        currentUser.logout();

        System.exit(0);
    }
}
