package mashko.hospital.service.impl;

import mashko.hospital.dao.DaoException;
import mashko.hospital.dao.UserDao;
import mashko.hospital.entity.Role;
import mashko.hospital.entity.User;
import mashko.hospital.service.AuthenticationService;
import mashko.hospital.service.ServiceException;

import java.util.Optional;

public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserDao userDao;

    public AuthenticationServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public boolean isHasRole(String login, Role role) throws ServiceException {
        boolean result = false;
        try {
            Optional<User> optionalUser = userDao.findByLogin(login);
            if (optionalUser.isPresent()) {
                result = optionalUser.get().getRoles().contains(role);
            }
        } catch (DaoException e) {
            throw new ServiceException("Authentication failed.", e);
        }
        return result;
    }
}
