package mashko.hospital.controller.command.doctor;

import mashko.hospital.controller.HospitalUrl;
import mashko.hospital.controller.HttpCommand;
import mashko.hospital.controller.ParameterName;
import mashko.hospital.entity.CardType;
import mashko.hospital.entity.Therapy;
import mashko.hospital.service.DoctorService;
import mashko.hospital.service.ServiceException;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FindOpenDoctorTherapies implements HttpCommand {
    private static final String MESSAGE_WRONG_RESULT = "You don't have any open therapies.";

    private final DoctorService doctorService;
    private final Logger logger;

    public FindOpenDoctorTherapies(DoctorService doctorService, Logger logger) {
        this.doctorService = doctorService;
        this.logger = logger;
    }

    @Override
    public Map<String, Object> execute(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> result = new HashMap<>();
        String login = String.valueOf(request.getSession().getAttribute(ParameterName.LOGIN_USERNAME));
        CardType cardType = CardType.valueOf(request.getParameter(ParameterName.CARD_TYPE));
        try {
            List<Therapy> therapies = doctorService.findOpenDoctorTherapies(login, cardType);
            if (!therapies.isEmpty()) {
                result.put(ParameterName.THERAPIES_LIST, therapies);
            } else {
                result.put(ParameterName.MESSAGE, MESSAGE_WRONG_RESULT);
            }
            result.put(ParameterName.PAGE_FORWARD, HospitalUrl.PAGE_DOCTOR_THERAPIES);
        } catch (ServiceException e) {
            logger.error(e);
            result.put(ParameterName.COMMAND_EXCEPTION, e.getMessage());
        }
        return result;
    }
}
