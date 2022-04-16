package epam.hospital.controller.command;

import mashko.hospital.controller.HttpCommand;
import mashko.hospital.controller.ParameterName;
import mashko.hospital.controller.command.Authorization;
import mashko.hospital.entity.User;
import mashko.hospital.entity.table.UsersFieldName;
import mashko.hospital.service.ClientService;
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
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Optional;

public class AuthorizationTest {
    @Mock
    private Logger logger;
    @Mock
    private HttpSession httpSession;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private ClientService clientService;
    private HttpCommand httpCommand;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        httpCommand = new Authorization(clientService, logger);
    }

    @Test(dataProviderClass = Provider.class, dataProvider = "getCorrectUser")
    public void execute_correctAuthorization_mapWithOnlyPageForward(User user)
            throws ServiceException, IOException, ServletException {
        Mockito.when(request.getParameter(UsersFieldName.LOGIN))
                .thenReturn(user.getLogin());
        Mockito.when(request.getParameter(UsersFieldName.PASSWORD))
                .thenReturn(user.getPassword());
        Mockito.when(clientService.authorization(user.getLogin(), user.getPassword()))
                .thenReturn(Optional.of(user));

        Mockito.when(request.getSession()).thenReturn(httpSession);
        Mockito.doNothing().when(httpSession).setAttribute(ParameterName.LOGIN_USERNAME, user.getLogin());
        Mockito.doNothing().when(httpSession).setAttribute(ParameterName.LOGIN_ROLES, user.getRoles());
        Assert.assertTrue(httpCommand.execute(request, response).containsKey(ParameterName.PAGE_FORWARD));
    }

    @Test(dataProviderClass = Provider.class, dataProvider = "getCorrectUser")
    public void execute_wrongAuthorization_mapWithWrongResultMessage(User user)
            throws ServiceException, IOException, ServletException {
        Mockito.when(request.getParameter(UsersFieldName.LOGIN))
                .thenReturn(user.getLogin());
        Mockito.when(request.getParameter(UsersFieldName.PASSWORD))
                .thenReturn(user.getPassword());
        Mockito.when(clientService.authorization(user.getLogin(), user.getPassword()))
                .thenReturn(Optional.empty());
        Assert.assertTrue(httpCommand.execute(request, response).containsKey(ParameterName.MESSAGE));
    }

    @Test(dataProviderClass = Provider.class, dataProvider = "getCorrectUser")
    public void execute_serviceException_commandExceptionParameter(User user)
            throws ServiceException, IOException, ServletException {
        Mockito.when(request.getParameter(UsersFieldName.LOGIN))
                .thenReturn(user.getLogin());
        Mockito.when(request.getParameter(UsersFieldName.PASSWORD))
                .thenReturn(user.getPassword());
        Mockito.when(clientService.authorization(user.getLogin(), user.getPassword()))
                .thenThrow(ServiceException.class);
        Assert.assertTrue(httpCommand.execute(request, response).containsKey(ParameterName.COMMAND_EXCEPTION));
    }
}
