package epam.hospital.controller.command.admin.head;

import mashko.hospital.controller.HospitalUrl;
import mashko.hospital.controller.HttpCommand;
import mashko.hospital.controller.ParameterName;
import mashko.hospital.controller.command.admin.head.FindRoleControlAttributes;
import mashko.hospital.entity.Department;
import mashko.hospital.entity.Role;
import mashko.hospital.entity.User;
import mashko.hospital.entity.table.UsersFieldName;
import mashko.hospital.service.AdminHeadService;
import mashko.hospital.service.ServiceException;
import epam.hospital.util.Provider;
import org.apache.log4j.Logger;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class FindRoleControlAttributesTest {
    private static final String MESSAGE_WRONG_RESULT = "Non existent user.";

    @Mock
    private Logger logger;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private AdminHeadService adminHeadService;
    private HttpCommand httpCommand;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        httpCommand = new FindRoleControlAttributes(adminHeadService, logger);
    }

    @Test(dataProviderClass = Provider.class, dataProvider = "getCorrectUser")
    public void execute_roleDoctor_mapWithSuccessMessage(User user)
            throws ServiceException, IOException, ServletException {
        ArrayList<Role> roles = user.getRoles();
        roles.add(Role.DOCTOR);
        Map<String, Object> expected = new HashMap<>();
        expected.put(ParameterName.DEPARTMENT, Department.INFECTIOUS);
        expected.put(ParameterName.USER_ROLES, roles);
        expected.put(ParameterName.PAGE_FORWARD, HospitalUrl.PAGE_ROLE_CONTROL);
        Mockito.when(request.getParameter(UsersFieldName.LOGIN))
                .thenReturn(user.getLogin());
        Mockito.when(adminHeadService.findUserRoles(user.getLogin()))
                .thenReturn(roles);
        Mockito.when(adminHeadService.findDepartmentByUsername(user.getLogin()))
                .thenReturn(Optional.of(Department.INFECTIOUS));

        Map<String, Object> result = httpCommand.execute(request, response);
        Assert.assertEquals(result, expected);
    }

    @Test(dataProviderClass = Provider.class, dataProvider = "getCorrectUser")
    public void execute_roleMedicalAssistant_mapWithSuccessMessage(User user)
            throws ServiceException, IOException, ServletException {
        ArrayList<Role> roles = user.getRoles();
        roles.add(Role.MEDICAL_ASSISTANT);
        Map<String, Object> expected = new HashMap<>();
        expected.put(ParameterName.DEPARTMENT, Department.INFECTIOUS);
        expected.put(ParameterName.USER_ROLES, roles);
        expected.put(ParameterName.PAGE_FORWARD, HospitalUrl.PAGE_ROLE_CONTROL);
        Mockito.when(request.getParameter(UsersFieldName.LOGIN))
                .thenReturn(user.getLogin());
        Mockito.when(adminHeadService.findUserRoles(user.getLogin()))
                .thenReturn(roles);
        Mockito.when(adminHeadService.findDepartmentByUsername(user.getLogin()))
                .thenReturn(Optional.of(Department.INFECTIOUS));

        Map<String, Object> result = httpCommand.execute(request, response);
        Assert.assertEquals(result, expected);
    }

    @Test(dataProviderClass = Provider.class, dataProvider = "getCorrectUser")
    public void execute_rolesWithoutMedicalRole_mapWithSuccessMessage(User user)
            throws ServiceException, IOException, ServletException {
        ArrayList<Role> roles = user.getRoles();
        Map<String, Object> expected = new HashMap<>();
        expected.put(ParameterName.DEPARTMENT, null);
        expected.put(ParameterName.USER_ROLES, roles);
        expected.put(ParameterName.PAGE_FORWARD, HospitalUrl.PAGE_ROLE_CONTROL);
        Mockito.when(request.getParameter(UsersFieldName.LOGIN))
                .thenReturn(user.getLogin());
        Mockito.when(adminHeadService.findUserRoles(user.getLogin()))
                .thenReturn(roles);

        Map<String, Object> result = httpCommand.execute(request, response);
        Assert.assertEquals(result, expected);
    }

    @Test(dataProviderClass = Provider.class, dataProvider = "getCorrectUser")
    public void execute_nonExistentUser_mapWithWrongResultMessage(User user)
            throws ServiceException, IOException, ServletException {
        Mockito.when(request.getParameter(UsersFieldName.LOGIN))
                .thenReturn(user.getLogin());
        Mockito.when(adminHeadService.findUserRoles(user.getLogin()))
                .thenReturn(new ArrayList<>());

        Map<String, Object> result = httpCommand.execute(request, response);
        Assert.assertEquals(result.get(ParameterName.MESSAGE), MESSAGE_WRONG_RESULT);
    }

    @Test(dataProviderClass = Provider.class, dataProvider = "getCorrectUser")
    public void execute_serviceException_commandExceptionParameter(User user)
            throws ServiceException, IOException, ServletException {
        Mockito.when(request.getParameter(UsersFieldName.LOGIN))
                .thenReturn(user.getLogin());
        Mockito.when(adminHeadService.findUserRoles(user.getLogin()))
                .thenThrow(ServiceException.class);
        Map<String, Object> result = httpCommand.execute(request, response);
        Assert.assertTrue(result.containsKey(ParameterName.COMMAND_EXCEPTION));
    }
}
