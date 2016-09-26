package models;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Id;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Page;

import play.data.validation.Constraints;
import play.db.ebean.Model;

@Entity
public class Role extends Model {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	public Long id;

	@Constraints.Required
	public String name;

	public String description;

	/**
	 * Generic query helper for entity Company with id Long
	 */
	public static Finder<Long, Role> find = new Finder<Long, Role>(Long.class,
			Role.class);

	public static List<Role> all() {
		return find.all();
	}

	public static void create(Role role) {
		role.save();
	}

	public static void delete(Long id) {
		find.ref(id).delete();
	}

	public static Map<String, String> options() {
		LinkedHashMap<String, String> options = new LinkedHashMap<String, String>();
		for (Role r : Role.find.orderBy("name").findList()) {
			options.put(r.id.toString(), r.name);
		}
		return options;
	}

	public static List<Role> getRolesList(String[] ids) {

		List<Role> rolesList = new ArrayList<Role>();

		for (int i = 0; i < ids.length; i++) {

			Long.getLong(ids[i]);
			long id = Long.valueOf(ids[i]).longValue();
			Role role = Role.find.byId(id);

			rolesList.add(role);
		}

		return rolesList;
	}

	/**
	 * Return a page of Role
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
	public static Page<Role> page(int page, int pageSize, String sortBy,
			String order, String filter) {
		return (find.where().ilike("name", "%" + filter + "%")
				.orderBy(sortBy + " " + order).findPagingList(pageSize))
				.setFetchAhead(false).getPage(page);
	}

	public String toString() {
		return id + ": " + name;
	}

	public static List<Role> getHeadOfficeEmployeeTimeSheet() {
		List<Role> roleList = new ArrayList<Role>();

		roleList = Role.find.all();

		return roleList;
	}

}
