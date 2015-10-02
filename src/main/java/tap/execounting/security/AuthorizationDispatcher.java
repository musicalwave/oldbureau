package tap.execounting.security;

import tap.execounting.entities.User;

public interface AuthorizationDispatcher {

	public <T> boolean getPermission(User user, Class<T> targetEntityType,
			Operation operation);

	public <T> boolean getPermission(String group, Class<T> targetEntityType,
			Operation operation);

	public boolean canCreatePayments();

	public boolean canDeletePayments();

	public boolean canEditPayments();

	public boolean canEditEvents();

	public boolean canDeleteEvents();

	public boolean canEditContracts();

	public boolean canDeleteContracts();

	public boolean canDeleteTeachers();

	public boolean canCreateTeachers();

	public boolean canEditTeachers();

	public boolean canCreateEvents();

	public boolean canEditEventTypes();

	public boolean canCreateEventTypes();

	public boolean canDeleteEventTypes();

	public boolean canEditFacilities();

	public boolean canCreateFacilities();

	public boolean canDeleteFacilities();

	public boolean canEditClients();

	public boolean canCreateClients();

	public boolean canDeleteClients();

	public boolean canCreateContracts();
}
