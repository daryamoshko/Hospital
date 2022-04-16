package mashko.hospital.service;

import mashko.hospital.entity.Role;

public interface AuthenticationService {
    boolean isHasRole(String login, Role role) throws ServiceException;
}
