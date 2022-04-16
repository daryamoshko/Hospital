package epam.hospital.dao.impl;

import mashko.hospital.dao.DaoException;
import mashko.hospital.dao.DepartmentDao;
import mashko.hospital.dao.DepartmentStaffDao;
import mashko.hospital.dao.UserDao;
import mashko.hospital.dao.impl.DepartmentDaoImpl;
import mashko.hospital.dao.impl.DepartmentStaffDaoImpl;
import mashko.hospital.dao.impl.UserDaoImpl;
import mashko.hospital.entity.Department;
import mashko.hospital.entity.Role;
import mashko.hospital.entity.User;
import epam.hospital.util.Cleaner;
import epam.hospital.util.Provider;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Optional;

@Test(groups = "dao", dependsOnGroups = {"UserDaoImplTest", "DepartmentStaffDaoImplTest"})
public class DepartmentDaoImplTest {
    private DepartmentStaffDao departmentStaffDao;
    private DepartmentDao departmentDao;
    private UserDao userDao;
    private Cleaner cleaner;

    @BeforeMethod
    private void setUp() {
        departmentStaffDao = new DepartmentStaffDaoImpl();
        departmentDao = new DepartmentDaoImpl();
        userDao = new UserDaoImpl();
        cleaner = new Cleaner();
    }

    @Test
    public void findHeadDepartment_correctFind_userPresent() throws DaoException {
        Assert.assertTrue(departmentDao.findHeadDepartment(Department.INFECTIOUS).isPresent());
    }

    @Test(dataProviderClass = Provider.class, dataProvider = "getCorrectUser",
            dependsOnMethods = "findHeadDepartment_correctFind_userPresent")
    public void updateDepartmentHead_correctUpdate_true(User user) throws DaoException {
        User firstHead = departmentDao.findHeadDepartment(Department.INFECTIOUS).orElseThrow(DaoException::new);

        userDao.createClientWithUserDetails(user);
        userDao.addUserRole(user.getLogin(), Role.DOCTOR);
        if (!departmentDao.updateDepartmentHead(Department.INFECTIOUS, user.getLogin())) {
            Assert.fail("UpdateDepartmentHead failed.");
        }

        departmentDao.updateDepartmentHead(Department.INFECTIOUS, firstHead.getLogin());
        cleaner.delete(user);
    }

    @Test(dataProviderClass = Provider.class, dataProvider = "getCorrectUser", expectedExceptions = DaoException.class)
    public void updateDepartmentHead_notDoctor_daoException(User user) throws DaoException {
        userDao.createClientWithUserDetails(user);
        try {
            departmentDao.updateDepartmentHead(Department.INFECTIOUS, user.getLogin());
        } finally {
            cleaner.delete(user);
        }
    }

    @Test(expectedExceptions = DaoException.class)
    public void updateDepartmentHead_nonExistentLogin_false() throws DaoException {
        Assert.assertFalse(departmentDao.updateDepartmentHead(Department.INFECTIOUS, "1"));
    }

    @Test(dataProviderClass = Provider.class, dataProvider = "getCorrectUser")
    public void findDepartment_correctFind_departmentPresent(User user) throws DaoException {
        userDao.createClientWithUserDetails(user);
        departmentStaffDao.makeMedicalWorkerAndAddToDepartment(Department.INFECTIOUS, user.getLogin(), Role.DOCTOR);

        Optional<Department> optionalDepartment = departmentDao.findDepartment(user.getLogin());
        if (optionalDepartment.isEmpty()) {
            cleaner.deleteUserFromDepartment(user);
            cleaner.delete(user);
            Assert.fail("FindDepartment failed.");
        }

        cleaner.deleteUserFromDepartment(user);
        cleaner.delete(user);
    }

    @Test
    public void findDepartment_nonExistentLogin_departmentEmpty() throws DaoException {
        Assert.assertTrue(departmentDao.findDepartment("1").isEmpty());
    }

    @Test
    public void findDepartmentsHeads_correctFind_findAll() throws DaoException {
        Assert.assertEquals(departmentDao.findDepartmentsHeads().values().size(), 9);
    }
}
