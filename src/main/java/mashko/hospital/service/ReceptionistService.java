package mashko.hospital.service;

import mashko.hospital.entity.User;
import mashko.hospital.entity.UserDetails;

import java.util.Optional;

public interface ReceptionistService {
    boolean registerClient(User user) throws ServiceException;

    Optional<User> findUserCredentials(UserDetails userDetails) throws ServiceException;
}
