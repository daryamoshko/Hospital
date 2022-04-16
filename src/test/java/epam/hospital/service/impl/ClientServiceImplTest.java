package epam.hospital.service.impl;

import mashko.hospital.dao.DaoException;
import mashko.hospital.dao.UserDao;
import mashko.hospital.dao.UserDetailsDao;
import mashko.hospital.entity.User;
import mashko.hospital.entity.UserDetails;
import mashko.hospital.service.ClientService;
import mashko.hospital.service.ServiceException;
import mashko.hospital.service.impl.ClientServiceImpl;
import epam.hospital.util.Provider;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Optional;

public class ClientServiceImplTest {
    @Mock
    private UserDao userDao;
    @Mock
    private UserDetailsDao userDetailsDao;
    private ClientService clientService;

    @BeforeMethod
    private void setUp() {
        MockitoAnnotations.openMocks(this);
        clientService = new ClientServiceImpl(userDao, userDetailsDao);
    }

    @Test(dataProviderClass = Provider.class, dataProvider = "getCorrectUser")
    public void authorization_correctAuthorization_userPresent(User user) throws DaoException, ServiceException {
        Mockito.when(userDao.findByLogin(user.getLogin()))
                .thenReturn(java.util.Optional.of(user));
        Assert.assertTrue(clientService.authorization(user.getLogin(), user.getPassword()).isPresent());
    }

    @Test(dataProviderClass = Provider.class, dataProvider = "getCorrectUser")
    public void authorization_nonExistentLogin_userEmpty(User user) throws DaoException, ServiceException {
        Mockito.when(userDao.findByLogin(user.getLogin()))
                .thenReturn(java.util.Optional.empty());
        Assert.assertTrue(clientService.authorization(user.getLogin(), user.getPassword()).isEmpty());
    }

    @Test(dataProviderClass = Provider.class, dataProvider = "getCorrectUser")
    public void authorization_wrongPassword_userEmpty(User user) throws DaoException, ServiceException {
        User withAnotherPassword = new User(user.getLogin(), "");
        Mockito.when(userDao.findByLogin(user.getLogin()))
                .thenReturn(java.util.Optional.of(withAnotherPassword));
        Assert.assertTrue(clientService.authorization(user.getLogin(), user.getPassword()).isEmpty());
    }

    @Test(dataProviderClass = Provider.class, dataProvider = "getCorrectUser",
            expectedExceptions = ServiceException.class)
    public void authorization_daoException_serviceException(User user) throws DaoException, ServiceException {
        Mockito.when(userDao.findByLogin(user.getLogin()))
                .thenThrow(DaoException.class);
        Assert.assertTrue(clientService.authorization(user.getLogin(), user.getPassword()).isEmpty());
    }

    @Test(dataProviderClass = Provider.class, dataProvider = "getCorrectUser")
    public void updateUserDetails_existingUser_userDetailsPresent(User user) throws DaoException, ServiceException {
        Mockito.when(userDetailsDao.update(Mockito.any(UserDetails.class), Mockito.anyString()))
                .thenReturn(Optional.of(user.getUserDetails()));
        Assert.assertTrue(clientService.updateUserDetails(user.getUserDetails().getPhone(),
                user.getUserDetails().getAddress(), user.getLogin()).isPresent());
    }

    @Test(dataProviderClass = Provider.class, dataProvider = "getCorrectUser")
    public void updateUserDetails_nonExistentUser_userDetailsEmpty(User user) throws DaoException, ServiceException {
        Mockito.when(userDetailsDao.update(Mockito.any(UserDetails.class), Mockito.anyString()))
                .thenReturn(Optional.empty());
        Assert.assertTrue(clientService.updateUserDetails(user.getUserDetails().getPhone(),
                user.getUserDetails().getAddress(), user.getLogin()).isEmpty());
    }

    @Test(dataProviderClass = Provider.class, dataProvider = "getCorrectUser",
            expectedExceptions = ServiceException.class)
    public void updateUserDetails_daoException_serviceException(User user) throws DaoException, ServiceException {
        Mockito.when(userDetailsDao.update(Mockito.any(UserDetails.class), Mockito.anyString()))
                .thenThrow(DaoException.class);
        clientService.updateUserDetails(user.getUserDetails().getPhone(), user.getUserDetails().getAddress(),
                user.getLogin());
    }

    @Test(dataProviderClass = Provider.class, dataProvider = "getCorrectUser")
    public void findUserDetails_existingUser_userDetailsPresent(User user) throws DaoException, ServiceException {
        Mockito.when(userDao.findByLoginWithUserDetails(user.getLogin()))
                .thenReturn(Optional.of(user));
        Assert.assertTrue(clientService.findUserDetails(user.getLogin()).isPresent());
    }

    @Test(dataProviderClass = Provider.class, dataProvider = "getCorrectUser")
    public void findUserDetails_nonExistentUser_userDetailsEmpty(User user) throws DaoException, ServiceException {
        Mockito.when(userDao.findByLoginWithUserDetails(user.getLogin()))
                .thenReturn(Optional.empty());
        Assert.assertTrue(clientService.findUserDetails(user.getLogin()).isEmpty());
    }

    @Test(dataProviderClass = Provider.class, dataProvider = "getCorrectUser",
            expectedExceptions = ServiceException.class)
    public void findUserDetails_daoException_serviceException(User user) throws DaoException, ServiceException {
        Mockito.when(userDao.findByLoginWithUserDetails(user.getLogin()))
                .thenThrow(DaoException.class);
        clientService.findUserDetails(user.getLogin());
    }
}
