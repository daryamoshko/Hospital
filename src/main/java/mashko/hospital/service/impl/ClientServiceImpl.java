package mashko.hospital.service.impl;

import mashko.hospital.dao.DaoException;
import mashko.hospital.dao.UserDao;
import mashko.hospital.dao.UserDetailsDao;
import mashko.hospital.entity.User;
import mashko.hospital.entity.UserDetails;
import mashko.hospital.service.ClientService;
import mashko.hospital.service.ServiceException;

import java.util.Optional;

public class ClientServiceImpl implements ClientService {
    private final UserDao userDao;
    private final UserDetailsDao userDetailsDao;

    public ClientServiceImpl(UserDao userDao, UserDetailsDao userDetailsDao) {
        this.userDao = userDao;
        this.userDetailsDao = userDetailsDao;
    }

    @Override
    public Optional<User> authorization(String login, String password) throws ServiceException {
        Optional<User> optionalUser = Optional.empty();
        Optional<User> userFromDb;
        try {
            userFromDb = userDao.findByLogin(login);
            if (userFromDb.isPresent() && userFromDb.get().getPassword().equals(password)) {
                optionalUser = userFromDb;
            }
        } catch (DaoException e) {
            throw new ServiceException("Authorization failed.", e);
        }
        return optionalUser;
    }

    @Override
    public Optional<UserDetails> updateUserDetails(String phone, String address, String login)
            throws ServiceException {
        Optional<UserDetails> optionalUserDetails;
        UserDetails userDetails = new UserDetails();
        userDetails.setPhone(phone);
        userDetails.setAddress(address);
        try {
            optionalUserDetails = userDetailsDao.update(userDetails, login);
        } catch (DaoException e) {
            throw new ServiceException("UpdateUserDetails failed.", e);
        }
        return optionalUserDetails;
    }

    @Override
    public Optional<UserDetails> findUserDetails(String login) throws ServiceException {
        Optional<UserDetails> optionalUserDetails = Optional.empty();
        try {
            Optional<User> optionalUser = userDao.findByLoginWithUserDetails(login);
            if (optionalUser.isPresent()) {
                optionalUserDetails = Optional.of(optionalUser.get().getUserDetails());
            }
        } catch (DaoException e) {
            throw new ServiceException("FindUserDetails failed.", e);
        }
        return optionalUserDetails;
    }
}
