package models;

import java.util.*;

import javax.persistence.*;

import play.Logger;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Page;
import com.avaje.ebean.RawSql;
import com.avaje.ebean.RawSqlBuilder;

import play.db.ebean.*;
import play.db.ebean.Model.Finder;
import play.data.format.*;
import play.data.validation.*;

/**
 * User entity managed by Ebean
 */
@Entity
@Table(name = "account")
public class User extends Model {

	private static final long serialVersionUID = 1L;

	@Id
	public Long id;

	@Constraints.Required
	@Formats.NonEmpty
	public String email;

	@Constraints.Required
	public String name;

	@Constraints.Required
	public String password;

	@ManyToMany(cascade = CascadeType.REMOVE)
	@Constraints.Required
	public List<Role> roles = new ArrayList<Role>();

	// -- Queries
	public User() {

	}

	/**
	 * Retrieve all users.
	 */
	public static List<User> findAll() {
		Logger.info("@M User -->> findAll() -->> ");
		return find.all();
	}

	/**
	 * Generic query helper for entity Company with id Long
	 */
	public static Finder<Long, User> find = new Finder<Long, User>(Long.class,
			User.class);

	/**
	 * Retrieve a User from email.
	 */
	public static User findByEmail(String email) {
		Logger.info("@M User -->> findByEmail(" + email + ") -->> ");
		return find.where().eq("email", email).findUnique();
	}

	/**
	 * Authenticate a User.
	 */
	public static User authenticate(String email, String password) {
		Logger.info("@M User -->> authenticate(" + email + "," + password
				+ ") -->> ");
		return find.where().eq("email", email).eq("password", password)
				.findUnique();
	}

	// --
	public User(String name, String email, String password, List<Role> roles) {
		Logger.info("@M User -->> User(" + name + "," + email + "," + password
				+ ", rolesList) -->> Constructor");
		this.name = name;
		this.email = email;
		this.password = password;
		this.roles = roles;
	}

	public static void create(String name, String email, String password,
			List<Role> roles) {
		Logger.info("@M User -->> create(" + name + "," + email + ","
				+ password + ", rolesList) -->> ");
		User user = new User(name, email, password, roles);
		user.save();
		Logger.info("@M User -->> create(" + name + "," + email + ","
				+ password + ", rolesList) <<--");
	}

	public static void create(User user, List<Role> roles) {
		Logger.info("@M User -->> create(user,rolesList) -->> ");
		user.roles = roles;
		user.save();
		Logger.info("@M User -->> create(user,rolesList) <<--");
	}

	public static void create(User user) {
		Logger.info("@M User -->> create(user) -->> ");
		user.save();
		Logger.info("@M User -->> create(user) <<--");
	}

	public void delete(Long id) {
		Logger.info("@M User -->> delete(" + id + ") -->> ");
		find.ref(id).delete();
		Logger.info("@M User -->> delete(" + id + ") <<--");
	}

	/**
	 * Return a page of User
	 *
	 * @param page
	 *            Page to display
	 * @param pageSize
	 *            Number of computers per page
	 * @param sortBy
	 *            Product property used for sorting
	 * @param order
	 *            Sort order (either or asc or desc)
	 * @param filter
	 *            Filter applied on the name column
	 */
	public static Page<User> page(int page, int pageSize, String sortBy,
			String order, String filter) {
		Logger.info("@M User -->> page(" + page + "," + pageSize + "," + sortBy
				+ "," + order + "," + filter + ") -->> ");
		return (find.where().ilike("name", filter + "%")
				.orderBy(sortBy + " " + order).findPagingList(pageSize))
				.setFetchAhead(false).getPage(page);
	}

	public String toString() {
		return "User(" + email + ")";
	}

}
