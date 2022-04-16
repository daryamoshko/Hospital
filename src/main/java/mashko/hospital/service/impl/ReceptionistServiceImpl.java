package mashko.hospital.service.impl;

import mashko.hospital.dao.DaoException;
import mashko.hospital.dao.UserDao;
import mashko.hospital.entity.User;
import mashko.hospital.entity.UserDetails;
import mashko.hospital.service.ReceptionistService;
import mashko.hospital.service.ServiceException;

import java.util.Optional;

public class ReceptionistServiceImpl implements ReceptionistService {
    private final UserDao userDao;

    public ReceptionistServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public boolean registerClient(User user) throws ServiceException {
        boolean result = false;
        try {
            Optional<User> optionalUser = userDao.findByLogin(user.getLogin());
            if (optionalUser.isEmpty()) {
                result = userDao.createClientWithUserDetails(user) > 0;
            }
        } catch (DaoException e) {
            throw new ServiceException("Registration new client failed.", e);
        }
        return result;
    }

    @Override
    public Optional<User> findUserCredentials(UserDetails userDetails) throws ServiceException {
        Optional<User> optionalUser;
        try {
            optionalUser = userDao.findUserWithUserDetailsByPassportData(userDetails.getFirstName(),
                    userDetails.getSurname(), userDetails.getLastName(), userDetails.getBirthday());
        } catch (DaoException e) {
            throw new ServiceException("FindUserCredentials failed.", e);
        }
        return optionalUser;
    }
}
