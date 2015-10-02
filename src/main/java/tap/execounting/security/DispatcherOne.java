package tap.execounting.security;

import org.apache.tapestry5.ioc.annotations.Inject;

import tap.execounting.data.Entities;
import tap.execounting.entities.Client;
import tap.execounting.entities.Contract;
import tap.execounting.entities.Event;
import tap.execounting.entities.EventType;
import tap.execounting.entities.Facility;
import tap.execounting.entities.Payment;
import tap.execounting.entities.Teacher;
import tap.execounting.entities.User;
import tap.execounting.services.Authenticator;

import static tap.execounting.security.Operation.create;
import static tap.execounting.security.Operation.delete;
import static tap.execounting.security.Operation.update;

public class DispatcherOne implements AuthorizationDispatcher {

	@Inject
	private Authenticator authenticator;

	public <T> boolean getPermission(User user, Class<T> targetEntityType,
			Operation operation) {
		return getPermission(user.getGroup(), targetEntityType, operation);
	}

	public <T> boolean getPermission(String group, Class<T> targetEntityType,
			Operation operation) {
		String admin = User.ADMIN;
		String manager = User.MANAGER;
        String top = User.TOP;
		String target = targetEntityType.getSimpleName();

		if (group.equals(admin) || group.equals(top)) {
			return true;
		}
		if (group.equals(manager)) {
			byte row = Entities.getCode(target);
			byte column = operation.getCode();
			byte[][] permissionMatrix = new byte[7][4];
			permissionMatrix[Entities.CLIENT] = new byte[] { 1, 1, 1, 0 };
			permissionMatrix[Entities.CONTRACT] = new byte[] { 1, 1, 1, 0 };
			permissionMatrix[Entities.EVENT] = new byte[] { 1, 1, 1, 0 };
			permissionMatrix[Entities.EVENT_TYPE] = new byte[] { 0, 1, 1, 0 };
			permissionMatrix[Entities.PAYMENT] = new byte[] { 1, 1, 1, 0 };
			permissionMatrix[Entities.TEACHER] = new byte[] { 0, 1, 1, 0 };
			permissionMatrix[Entities.FACILITY] = new byte[] { 1, 1, 1, 0 };
			return permissionMatrix[row][column] == 1;
		} else
			throw new IllegalArgumentException("Group " + group
					+ " cannot be handled right now");
	}

	private User loggedUser() {
		return authenticator.getLoggedUser();
	}

	// PAYMENT
	public boolean canDeletePayments() {
		return getPermission(loggedUser(), Payment.class, delete);
	}
	public boolean canEditPayments() {
		return getPermission(loggedUser(), Payment.class, update);
	}
	public boolean canCreatePayments() {
		return getPermission(loggedUser(), Payment.class, create);
	}


	// EVENT
	public boolean canCreateEvents() {
		return getPermission(loggedUser(), Event.class, create);
	}
	public boolean canDeleteEvents() {
		return getPermission(loggedUser(), Event.class, delete);
	}
	public boolean canEditEvents() {
		return getPermission(loggedUser(), Event.class, update);
	}


	// CONTRACT
	public boolean canCreateContracts() {
		return getPermission(loggedUser(), Contract.class, create);
	}
	public boolean canDeleteContracts() {
		return getPermission(loggedUser(), Contract.class, delete);
	}
	public boolean canEditContracts() {
		return getPermission(loggedUser(), Contract.class, update);
	}


	// TEACHER
	public boolean canDeleteTeachers() {
		return getPermission(loggedUser(), Teacher.class, delete);
	}
	public boolean canCreateTeachers() {
		return getPermission(loggedUser(), Teacher.class, create);
	}
	public boolean canEditTeachers() {
		return getPermission(loggedUser(), Teacher.class, update);
	}


	// EVENT TYPE
	public boolean canEditEventTypes() {
		return getPermission(loggedUser(), EventType.class, update);
	}
	public boolean canCreateEventTypes() {
		return getPermission(loggedUser(), EventType.class, create);
	}
	public boolean canDeleteEventTypes() {
		return getPermission(loggedUser(), EventType.class, delete);
	}


	// FACILITY
	public boolean canEditFacilities() {
		return getPermission(loggedUser(), Facility.class, update);
	}
	public boolean canCreateFacilities() {
		return getPermission(loggedUser(), Facility.class, create);
	}
	public boolean canDeleteFacilities() {
		return getPermission(loggedUser(), Facility.class, delete);
	}


	// CLIENT
	public boolean canEditClients() {
		return getPermission(loggedUser(), Client.class, update);
	}
	public boolean canCreateClients() {
		return getPermission(loggedUser(), Client.class, create);
	}
	public boolean canDeleteClients() {
		return getPermission(loggedUser(), Client.class, delete);
	}
}
