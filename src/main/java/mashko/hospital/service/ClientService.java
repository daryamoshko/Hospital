package mashko.hospital.service;

import mashko.hospital.entity.User;
import mashko.hospital.entity.UserDetails;

import java.util.Optional;

public interface ClientService {
    Optional<User> authorization(String login, String password) throws ServiceException;

    Optional<UserDetails> updateUserDetails(String phone, String address, String login)
            throws ServiceException;

    Optional<UserDetails> findUserDetails(String login) throws ServiceException;
}
