package epam.hospital.dao.impl;

import mashko.hospital.dao.DaoException;
import mashko.hospital.dao.IcdDao;
import mashko.hospital.dao.impl.IcdDaoImpl;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Test(groups = {"dao", "IcdDaoImplTest"})
public class IcdDaoImplTest {
    private IcdDao icdDao;

    @BeforeMethod
    private void setUp() {
        icdDao = new IcdDaoImpl();
    }

    @Test
    public void findByCode_existingCode_icdPresent() throws DaoException {
        Assert.assertTrue(icdDao.findByCode("XW0DXJ5").isPresent());
    }
}
