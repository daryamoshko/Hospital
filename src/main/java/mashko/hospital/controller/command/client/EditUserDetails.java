package mashko.hospital.controller.command.client;

import mashko.hospital.controller.HospitalUrl;
import mashko.hospital.controller.HttpCommand;
import mashko.hospital.controller.ParameterName;
import mashko.hospital.entity.UserDetails;
import mashko.hospital.entity.table.UsersDetailsFieldName;
import mashko.hospital.service.ClientService;
import mashko.hospital.service.ServiceException;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class EditUserDetails implements HttpCommand {
    private static final String MESSAGE_UPDATE_UNSUCCESSFUL = "Update failed.";

    private final ClientService clientService;
    private final Logger logger;

    public EditUserDetails(ClientService clientService, Logger logger) {
        this.clientService = clientService;
        this.logger = logger;
    }

    @Override
    public Map<String, Object> execute(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> result = new HashMap<>();
        String phone = request.getParameter(UsersDetailsFieldName.PHONE);
        String address = request.getParameter(UsersDetailsFieldName.ADDRESS);
        String login = String.valueOf(request.getSession().getAttribute(ParameterName.LOGIN_USERNAME));
        try {
            Optional<UserDetails> optionalUserDetails = clientService.updateUserDetails(phone, address, login);
            if (optionalUserDetails.isPresent()) {
                result.put(UsersDetailsFieldName.PASSPORT_ID, optionalUserDetails.get().getPassportId());
                result.put(UsersDetailsFieldName.GENDER, optionalUserDetails.get().getGender());
                result.put(UsersDetailsFieldName.FIRST_NAME, optionalUserDetails.get().getFirstName());
                result.put(UsersDetailsFieldName.LAST_NAME, optionalUserDetails.get().getLastName());
                result.put(UsersDetailsFieldName.SURNAME, optionalUserDetails.get().getSurname());
                result.put(UsersDetailsFieldName.BIRTHDAY, optionalUserDetails.get().getBirthday());
                result.put(UsersDetailsFieldName.ADDRESS, optionalUserDetails.get().getAddress());
                result.put(UsersDetailsFieldName.PHONE, optionalUserDetails.get().getPhone());
            } else {
                result.put(ParameterName.MESSAGE, MESSAGE_UPDATE_UNSUCCESSFUL);
            }
            result.put(ParameterName.PAGE_FORWARD, HospitalUrl.PAGE_PROFILE);
        } catch (ServiceException e) {
            logger.error(e);
            result.put(ParameterName.COMMAND_EXCEPTION, e.getMessage());
        }
        return result;
    }
}
