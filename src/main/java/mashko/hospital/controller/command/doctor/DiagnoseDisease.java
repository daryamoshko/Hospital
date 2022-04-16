package mashko.hospital.controller.command.doctor;

import mashko.hospital.controller.HospitalUrl;
import mashko.hospital.controller.HttpCommand;
import mashko.hospital.controller.ParameterName;
import mashko.hospital.entity.CardType;
import mashko.hospital.entity.User;
import mashko.hospital.entity.UserDetails;
import mashko.hospital.entity.table.DiagnosesFieldName;
import mashko.hospital.entity.table.IcdFieldName;
import mashko.hospital.entity.table.UsersDetailsFieldName;
import mashko.hospital.service.DoctorService;
import mashko.hospital.service.ServiceException;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class DiagnoseDisease implements HttpCommand {
    private static final String MESSAGE_SUCCESS = "Success.";
    private static final String MESSAGE_WRONG_RESULT = "Patient not exist.";
    private static final String MESSAGE_INCORRECT_ICD = "Non existent icd code.";

    private final DoctorService doctorService;
    private final Logger logger;

    public DiagnoseDisease(DoctorService doctorService, Logger logger) {
        this.doctorService = doctorService;
        this.logger = logger;
    }

    @Override
    public Map<String, Object> execute(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> result = new HashMap<>();
        String doctorLogin = String.valueOf(request.getSession().getAttribute(ParameterName.LOGIN_USERNAME));
        UserDetails userDetails = new UserDetails();
        String firstName = request.getParameter(UsersDetailsFieldName.FIRST_NAME);
        String surname = request.getParameter(UsersDetailsFieldName.SURNAME);
        String lastName = request.getParameter(UsersDetailsFieldName.LAST_NAME);
        Date birthday = Date.valueOf(request.getParameter(UsersDetailsFieldName.BIRTHDAY));
        userDetails.setFirstName(firstName);
        userDetails.setSurname(surname);
        userDetails.setLastName(lastName);
        userDetails.setBirthday(birthday);
        String icdCode = request.getParameter(IcdFieldName.CODE);
        String reason = request.getParameter(DiagnosesFieldName.REASON);
        CardType cardType = CardType.valueOf(request.getParameter(ParameterName.CARD_TYPE));
        try {
            Optional<User> patient = doctorService.findPatientByUserDetails(userDetails);
            if (patient.isPresent()) {
                boolean isDiagnosisApply = doctorService
                        .diagnoseDisease(icdCode, reason, doctorLogin, patient.get().getLogin(), cardType);
                result.put(ParameterName.MESSAGE, isDiagnosisApply ?
                        MESSAGE_SUCCESS :
                        MESSAGE_INCORRECT_ICD);
            } else {
                result.put(ParameterName.MESSAGE, MESSAGE_WRONG_RESULT);
            }
            result.put(ParameterName.PAGE_FORWARD, HospitalUrl.PAGE_DIAGNOSE_DISEASE);
        } catch (ServiceException e) {
            logger.error(e);
            result.put(ParameterName.COMMAND_EXCEPTION, e.getMessage());
        }
        return result;
    }
}
