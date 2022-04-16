package epam.hospital.dao.impl;

import mashko.hospital.dao.DaoException;
import mashko.hospital.dao.UserDao;
import mashko.hospital.dao.UserDetailsDao;
import mashko.hospital.dao.impl.UserDaoImpl;
import mashko.hospital.dao.impl.UserDetailsDaoImpl;
import mashko.hospital.entity.User;
import mashko.hospital.entity.UserDetails;
import epam.hospital.util.Cleaner;
import epam.hospital.util.Provider;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Optional;

@Test(groups = {"dao", "UserDetailsDaoImplTest"}, dependsOnGroups = "UserDaoImplTest")
public class UserDetailsDaoImplTest {
    private UserDetailsDao userDetailsDao;
    private UserDao userDao;
    private Cleaner cleaner;

    @BeforeMethod
    private void setUp() {
        userDetailsDao = new UserDetailsDaoImpl();
        userDao = new UserDaoImpl();
        cleaner = new Cleaner();
    }

    @Test(dataProviderClass = Provider.class, dataProvider = "getCorrectUser")
    public void update_correctUpdate_userDetailsPresent(User user) throws DaoException {
        UserDetails newUserDetails = new UserDetails();
        newUserDetails.setAddress(user.getUserDetails().getAddress() + "1");
        newUserDetails.setPhone(user.getUserDetails().getPhone());

        userDao.createClientWithUserDetails(user);
        Optional<UserDetails> optionalUserDetails = userDetailsDao.update(newUserDetails, user.getLogin());
        cleaner.delete(user);

        Assert.assertTrue(optionalUserDetails.isPresent());
    }

    @Test(dataProviderClass = Provider.class, dataProvider = "getCorrectUser")
    public void update_nonExistentUserId_userDetailsEmpty(User user) throws DaoException {
        Assert.assertTrue(userDetailsDao
                .update(user.getUserDetails(), user.getLogin()).isEmpty());
    }
}
