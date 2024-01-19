package models.gsn.auth;

import com.feth.play.module.pa.user.AuthUser;
import io.ebean.Finder;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class LinkedAccount extends AppModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	public Long id;

	@ManyToOne
	public User user;

	public String providerUserId;
	public String providerKey;

	public static final Finder<Long, LinkedAccount> find = new Finder<>(LinkedAccount.class);

	public static LinkedAccount findByProviderKey(final User user, String key) {
		return find.query().where().eq("user", user).eq("providerKey", key)
				.findOne();
	}

	public static LinkedAccount create(final AuthUser authUser) {
		final LinkedAccount ret = new LinkedAccount();
		ret.update(authUser);
		return ret;
	}
	
	public void update(final AuthUser authUser) {
		this.providerKey = authUser.getProvider();
		this.providerUserId = authUser.getId();
	}

	public static LinkedAccount create(final LinkedAccount acc) {
		final LinkedAccount ret = new LinkedAccount();
		ret.providerKey = acc.providerKey;
		ret.providerUserId = acc.providerUserId;

		return ret;
	}
}
