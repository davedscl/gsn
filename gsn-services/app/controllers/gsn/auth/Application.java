package controllers.gsn.auth;

import be.objectify.deadbolt.java.actions.Group;
import be.objectify.deadbolt.java.actions.Restrict;
import com.feth.play.module.pa.PlayAuthenticate;
import models.gsn.auth.User;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import providers.gsn.GSNUsernamePasswordAuthProvider;
import providers.gsn.GSNUsernamePasswordAuthProvider.MyLogin;
import providers.gsn.GSNUsernamePasswordAuthProvider.MySignup;
import providers.gsn.GSNUsernamePasswordAuthUser;
import service.gsn.UserProvider;
import views.html.*;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.Date;

import controllers.gsn.auth.routes;

import play.mvc.Http.Session;
import com.feth.play.module.pa.user.AuthUser;
import play.i18n.MessagesApi;
import play.Logger;

public class Application extends Controller {

	public static final String FLASH_MESSAGE_KEY = "message";
	public static final String FLASH_ERROR_KEY = "error";
	public static final String USER_ROLE = "user";
	public static final String ADMIN_ROLE = "admin";

	private final PlayAuthenticate auth;

	private final GSNUsernamePasswordAuthProvider provider;

	private final UserProvider userProvider;

	private final MessagesApi msg;


	public static String formatTimestamp(final long t) {
		return new SimpleDateFormat("yyyy-dd-MM HH:mm:ss").format(new Date(t));
	}

	@Inject
	public Application(final PlayAuthenticate auth, final GSNUsernamePasswordAuthProvider provider,
					   final UserProvider userProvider,final MessagesApi msg) {
		this.auth = auth;
		this.provider = provider;
		this.userProvider = userProvider;
		this.msg=msg;
	}

	public Result index() {
		return ok(index.render(this.userProvider));
	}

	@Restrict(@Group(Application.USER_ROLE))
	public Result restricted() {
		final User localUser = this.userProvider.getUser(session());
		return ok(restricted.render(this.userProvider, localUser));
	}

	@Restrict(@Group(Application.USER_ROLE))
	public Result profile() {
		final User localUser = userProvider.getUser(session());
		return ok(profile.render(this.auth, this.userProvider, localUser));
	}

	public Result login() {
		return ok(login.render(this.auth, this.userProvider,  this.provider.getLoginForm()));
	}

	public Result doLogin() {
		com.feth.play.module.pa.controllers.Authenticate.noCache(response());
		final Form<MyLogin> filledForm = this.provider.getLoginForm()
				.bindFromRequest();
		if (filledForm.hasErrors()) {
			// User did not fill everything properly
			return badRequest(login.render(this.auth, this.userProvider, filledForm));
		} else {
			// Everything was filled
			return this.provider.handleLogin(ctx());
		}
	}

	public Result signup() {
		return ok(signup.render(this.auth, this.userProvider, this.provider.getSignupForm()));
	}

	public Result jsRoutes() {
		return ok(
				play.routing.JavaScriptReverseRouter.create("jsRoutes",
						routes.javascript.Signup.forgotPassword()))
				.as("text/javascript");

	}

	public Result doSignup() {
		com.feth.play.module.pa.controllers.Authenticate.noCache(response());
		final Form<MySignup> filledForm = this.provider.getSignupForm().bindFromRequest();
		if (filledForm.hasErrors()) {
			// User did not fill everything properly
			return badRequest(signup.render(this.auth, this.userProvider, filledForm));
		} else {
			// Everything was filled
			// do something with your part of the form before handling the user
			// signup
			return this.provider.handleSignup(ctx());
		}
	}



	public Result adduser() {
		return ok(adduser.render(this.provider.getSignupForm(),this.userProvider));
	}

	
	public Result doAdduser() {
		com.feth.play.module.pa.controllers.Authenticate.noCache(response());
		final Form<MySignup> filledForm = this.provider.getSignupForm()
				.bindFromRequest();
		if (filledForm.hasErrors()) {
			return badRequest(adduser.render(filledForm,this.userProvider));
		} else {
			GSNUsernamePasswordAuthUser guser = new GSNUsernamePasswordAuthUser(filledForm.get());
			final User u = User.findByUsernamePasswordIdentity(guser);
			if (u != null) {
				flash(Application.FLASH_ERROR_KEY,
						this.msg.preferred(request()).at("playauthenticate.user.exists.message"));
				return badRequest(adduser.render(filledForm,this.userProvider));
			}
			// The user either does not exist or is inactive - create a new one
			// manually created users are directly validated
			@SuppressWarnings("unused")
			final User newUser = User.create(guser);
			newUser.emailValidated = true;
			newUser.save();
			return ok(adduser.render(provider.getSignupForm(),this.userProvider));
		}
	}

}