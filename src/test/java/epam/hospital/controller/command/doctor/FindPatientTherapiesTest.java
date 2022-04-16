package epam.hospital.controller.command.doctor;

import mashko.hospital.controller.HttpCommand;
import mashko.hospital.controller.ParameterName;
import mashko.hospital.controller.command.doctor.FindPatientTherapies;
import mashko.hospital.entity.CardType;
import mashko.hospital.entity.Therapy;
import mashko.hospital.entity.User;
import mashko.hospital.entity.UserDetails;
import mashko.hospital.entity.table.UsersDetailsFieldName;
import mashko.hospital.service.DoctorService;
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
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class FindPatientTherapiesTest {
    @Mock
    private Logger logger;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private DoctorService doctorService;
    private HttpCommand httpCommand;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        httpCommand = new FindPatientTherapies(doctorService, logger);
    }

    @Test(dataProviderClass = Provider.class, dataProvider = "getCorrectUser")
    public void execute_correctAuthorization_mapWithMessageSuccess(User patient)
            throws ServiceException, IOException, ServletException {
        UserDetails userDetails = new UserDetails();
        userDetails.setFirstName(patient.getUserDetails().getFirstName());
        userDetails.setSurname(patient.getUserDetails().getSurname());
        userDetails.setLastName(patient.getUserDetails().getLastName());
        userDetails.setBirthday(patient.getUserDetails().getBirthday());
        List<Therapy> therapies = new ArrayList<>(List.of(new Therapy[]{new Therapy()}));
        Mockito.when(request.getParameter(UsersDetailsFieldName.FIRST_NAME))
                .thenReturn(patient.getUserDetails().getFirstName());
        Mockito.when(request.getParameter(UsersDetailsFieldName.SURNAME))
                .thenReturn(patient.getUserDetails().getSurname());
        Mockito.when(request.getParameter(UsersDetailsFieldName.LAST_NAME))
                .thenReturn(patient.getUserDetails().getLastName());
        Mockito.when(request.getParameter(UsersDetailsFieldName.BIRTHDAY))
                .thenReturn(String.valueOf(patient.getUserDetails().getBirthday()));
        Mockito.when(request.getParameter(ParameterName.CARD_TYPE))
                .thenReturn(CardType.AMBULATORY.name());
        Mockito.when(doctorService.findPatientByUserDetails(patient.getUserDetails()))
                .thenReturn(Optional.of(patient));
        Mockito.when(doctorService.findPatientTherapies(userDetails, CardType.AMBULATORY))
                .thenReturn(therapies);

        Map<String, Object> result = httpCommand.execute(request, response);
        Assert.assertTrue(result.containsKey(ParameterName.THERAPIES_LIST));
    }

    @Test(dataProviderClass = Provider.class, dataProvider = "getCorrectUser")
    public void execute_correctAuthorization_mapWithMessageIncorrectIcd(User patient)
            throws ServiceException, IOException, ServletException {
        UserDetails userDetails = new UserDetails();
        userDetails.setFirstName(patient.getUserDetails().getFirstName());
        userDetails.setSurname(patient.getUserDetails().getSurname());
        userDetails.setLastName(patient.getUserDetails().getLastName());
        userDetails.setBirthday(patient.getUserDetails().getBirthday());
        Mockito.when(request.getParameter(UsersDetailsFieldName.FIRST_NAME))
                .thenReturn(patient.getUserDetails().getFirstName());
        Mockito.when(request.getParameter(UsersDetailsFieldName.SURNAME))
                .thenReturn(patient.getUserDetails().getSurname());
        Mockito.when(request.getParameter(UsersDetailsFieldName.LAST_NAME))
                .thenReturn(patient.getUserDetails().getLastName());
        Mockito.when(request.getParameter(UsersDetailsFieldName.BIRTHDAY))
                .thenReturn(String.valueOf(patient.getUserDetails().getBirthday()));
        Mockito.when(request.getParameter(ParameterName.CARD_TYPE))
                .thenReturn(CardType.AMBULATORY.name());
        Mockito.when(doctorService.findPatientByUserDetails(patient.getUserDetails()))
                .thenReturn(Optional.of(patient));
        Mockito.when(doctorService.findPatientTherapies(userDetails, CardType.AMBULATORY))
                .thenReturn(new ArrayList<>());

        Map<String, Object> result = httpCommand.execute(request, response);
        Assert.assertTrue(result.containsKey(ParameterName.MESSAGE));
    }

    @Test(dataProviderClass = Provider.class, dataProvider = "getCorrectUser")
    public void execute_correctAuthorization_mapWithCommandException(User user)
            throws ServiceException, IOException, ServletException {
        UserDetails userDetails = new UserDetails();
        userDetails.setFirstName(user.getUserDetails().getFirstName());
        userDetails.setSurname(user.getUserDetails().getSurname());
        userDetails.setLastName(user.getUserDetails().getLastName());
        userDetails.setBirthday(user.getUserDetails().getBirthday());
        Mockito.when(request.getParameter(UsersDetailsFieldName.FIRST_NAME))
                .thenReturn(user.getUserDetails().getFirstName());
        Mockito.when(request.getParameter(UsersDetailsFieldName.SURNAME))
                .thenReturn(user.getUserDetails().getSurname());
        Mockito.when(request.getParameter(UsersDetailsFieldName.LAST_NAME))
                .thenReturn(user.getUserDetails().getLastName());
        Mockito.when(request.getParameter(UsersDetailsFieldName.BIRTHDAY))
                .thenReturn(String.valueOf(user.getUserDetails().getBirthday()));
        Mockito.when(request.getParameter(ParameterName.CARD_TYPE))
                .thenReturn(CardType.AMBULATORY.name());
        Mockito.when(doctorService.findPatientByUserDetails(user.getUserDetails()))
                .thenReturn(Optional.of(user));
        Mockito.when(doctorService.findPatientTherapies(userDetails, CardType.AMBULATORY))
                .thenThrow(ServiceException.class);

        Map<String, Object> result = httpCommand.execute(request, response);
        Assert.assertTrue(result.containsKey(ParameterName.COMMAND_EXCEPTION));
    }
}