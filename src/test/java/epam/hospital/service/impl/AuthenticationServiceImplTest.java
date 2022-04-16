package epam.hospital.service.impl;

import mashko.hospital.dao.DaoException;
import mashko.hospital.dao.UserDao;
import mashko.hospital.entity.Role;
import mashko.hospital.entity.User;
import mashko.hospital.service.AuthenticationService;
import mashko.hospital.service.ServiceException;
import mashko.hospital.service.impl.AuthenticationServiceImpl;
import epam.hospital.util.Provider;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Optional;

public class AuthenticationServiceImplTest {
    @Mock
    private UserDao userDao;
    private AuthenticationService authenticationService;

    @BeforeMethod
    private void setUp() {
        MockitoAnnotations.openMocks(this);
        authenticationService = new AuthenticationServiceImpl(userDao);
    }

    @Test(dataProviderClass = Provider.class, dataProvider = "getCorrectUser")
    public void isHasRole_userHaveRole_true(User user) throws DaoException, ServiceException {
        Mockito.when(userDao.findByLogin(user.getLogin()))
                .thenReturn(Optional.of(user));
        Assert.assertTrue(authenticationService.isHasRole(user.getLogin(), Role.CLIENT));
    }

    @Test(dataProviderClass = Provider.class, dataProvider = "getCorrectUser")
    public void isHasRole_nonExistentUser_false(User user) throws DaoException, ServiceException {
        Mockito.when(userDao.findByLogin(user.getLogin()))
                .thenReturn(Optional.empty());
        Assert.assertFalse(authenticationService.isHasRole(user.getLogin(), Role.CLIENT));
    }

    @Test(dataProviderClass = Provider.class, dataProvider = "getCorrectUser")
    public void isHasRole_userDoesNotHaveRole_False(User user) throws DaoException, ServiceException {
        Mockito.when(userDao.findByLogin(user.getLogin()))
                .thenReturn(Optional.of(user));
        Assert.assertFalse(authenticationService.isHasRole(user.getLogin(), Role.DOCTOR));
    }

    @Test(dataProviderClass = Provider.class, dataProvider = "getCorrectUser",
            expectedExceptions = ServiceException.class)
    public void isHasRole_daoException_serviceException(User user) throws DaoException, ServiceException {
        Mockito.when(userDao.findByLogin(user.getLogin()))
                .thenThrow(DaoException.class);
        Assert.assertFalse(authenticationService.isHasRole(user.getLogin(), Role.DOCTOR));
    }
}
