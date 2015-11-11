package tap.execounting.entities;

import org.hibernate.annotations.NaturalId;
import org.hibernate.validator.constraints.Email;
import tap.execounting.security.AccessLevel;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


@Entity
@NamedQueries({
		@NamedQuery(name = User.ALL, query = "from User"),
		@NamedQuery(name = User.BY_USERNAME_OR_EMAIL, query = "Select u from User u where u.username = :username or u.email = :email"),
		@NamedQuery(name = User.BY_CREDENTIALS, query = "Select u from User u where u.username = :username and u.password = :password"),
		@NamedQuery(name = User.BY_ID, query = "from User where id=:userId"),
        @NamedQuery(name = User.BY_FULLNAME, query = "from User where fullname=:fullname")})
@Table(name = "users")
public class User {

	public static final String ALL = "User.all";
	public static final String BY_USERNAME_OR_EMAIL = "User.byUserNameOrEmail";
	public static final String BY_CREDENTIALS = "User.byCredentials";
    public static final String BY_FULLNAME = "User.byFullname";
    public static final String BY_ID = "User.byId";

	public static final String ADMIN = "admin";
	public static final String MANAGER = "manager";
    public static final String TOP = "top";

    @Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@NaturalId
	@Column(nullable = false, unique = true)
	@NotNull
	@Size(min = 3, max = 15)
	private String username;
	
	@Size(min=3,max=15)
	private String group;

	@Column(nullable = false)
	@NotNull
	@Size(min = 3, max = 50)
	private String fullname;

	@Column(nullable = false)
	@NotNull
	@Email
	private String email;

	@Column(nullable = false)
	@Size(min = 6, max = 200)
	@NotNull
	private String password;

	public User() {}

	public User(final String fullname, final String username, final String email) {
		this.fullname = fullname;
		this.username = username;
		this.email = email;
	}

	public User(final String fullname, final String username,
			final String email, final String password) {
		this(fullname, username, email);
		this.password = password;
	}

	public User(int id, String username, String group, String fullname, String email,
			String password) {
		super();
		this.id = id;
		this.username = username;
		this.fullname = fullname;
		this.email = email;
		this.group=group;
		this.password = password;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setFullname(String fullname) {
		this.fullname = fullname;
	}

	public String getFullname() {
		return fullname;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

    @Override
    public String toString(){
        return fullname;
    }

    public boolean isAdmin(){
        return group.equals(ADMIN);
    }
    public boolean isManager(){
        return group.equals(MANAGER);
    }
    public boolean isTop(){
        return group.equals(TOP);
    }

    public AccessLevel getAccessLevel() {
        switch (group) {
            case ADMIN: return AccessLevel.ADMIN;
            case MANAGER: return AccessLevel.MANAGER;
            case TOP: return AccessLevel.TOP;
            default: return AccessLevel.ADMIN;
        }
    }
}