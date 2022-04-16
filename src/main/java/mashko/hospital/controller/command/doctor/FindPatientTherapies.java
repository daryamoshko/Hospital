package mashko.hospital.controller.command.doctor;

import mashko.hospital.controller.HospitalUrl;
import mashko.hospital.controller.HttpCommand;
import mashko.hospital.controller.ParameterName;
import mashko.hospital.entity.CardType;
import mashko.hospital.entity.Therapy;
import mashko.hospital.entity.UserDetails;
import mashko.hospital.entity.table.UsersDetailsFieldName;
import mashko.hospital.service.DoctorService;
import mashko.hospital.service.ServiceException;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FindPatientTherapies implements HttpCommand {
    private static final String MESSAGE_WRONG_RESULT = "Therapies list empty or patient not exist.";

    private final DoctorService doctorService;
    private final Logger logger;

    public FindPatientTherapies(DoctorService doctorService, Logger logger) {
        this.doctorService = doctorService;
        this.logger = logger;
    }

    @Override
    public Map<String, Object> execute(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> result = new HashMap<>();
        UserDetails userDetails = new UserDetails();
        CardType cardType = CardType.valueOf(request.getParameter(ParameterName.CARD_TYPE));
        String surname = request.getParameter(UsersDetailsFieldName.SURNAME);
        String firstName = request.getParameter(UsersDetailsFieldName.FIRST_NAME);
        String lastName = request.getParameter(UsersDetailsFieldName.LAST_NAME);
        Date birthday = Date.valueOf(request.getParameter(UsersDetailsFieldName.BIRTHDAY));
        userDetails.setFirstName(firstName);
        userDetails.setSurname(surname);
        userDetails.setLastName(lastName);
        userDetails.setBirthday(birthday);
        try {
            List<Therapy> therapies = doctorService.findPatientTherapies(userDetails, cardType);
            if (!therapies.isEmpty()) {
                result.put(ParameterName.THERAPIES_LIST, therapies);
            } else {
                result.put(ParameterName.MESSAGE, MESSAGE_WRONG_RESULT);
            }
            result.put(ParameterName.PAGE_FORWARD, HospitalUrl.PAGE_PATIENT_THERAPIES);
        } catch (ServiceException e) {
            logger.error(e);
            result.put(ParameterName.COMMAND_EXCEPTION, e.getMessage());
        }
        return result;
    }
}
