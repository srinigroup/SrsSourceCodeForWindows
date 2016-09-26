package controllers;

import play.mvc.*;
import play.mvc.Http.*;

import models.*;

public class Secured extends Security.Authenticator {
    
    @Override
    public String getUsername(Context ctx) {
    	//System.out.println("Secured getUsername() " + ctx.session().get("email"));
        return ctx.session().get("email");
    }
    
    @Override
    public Result onUnauthorized(Context ctx) {
    	//System.out.println("Secured onUnauthorized() " );
        return redirect(routes.Application.login());
    }
    
    
    
    
}