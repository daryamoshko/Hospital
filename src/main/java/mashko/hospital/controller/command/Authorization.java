package mashko.hospital.controller.command;

import mashko.hospital.controller.HospitalUrl;
import mashko.hospital.controller.HttpCommand;
import mashko.hospital.controller.ParameterName;
import mashko.hospital.entity.User;
import mashko.hospital.entity.table.UsersFieldName;
import mashko.hospital.service.ClientService;
import mashko.hospital.service.ServiceException;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Authorization implements HttpCommand {
    private static final String MESSAGE_WRONG_RESULT = "Authorization was not perform.";

    private final ClientService clientService;
    private final Logger logger;

    public Authorization(ClientService clientService, Logger logger) {
        this.clientService = clientService;
        this.logger = logger;
    }

    @Override
    public Map<String, Object> execute(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> result = new HashMap<>();
        String login = request.getParameter(UsersFieldName.LOGIN);
        String password = request.getParameter(UsersFieldName.PASSWORD);
        try {
            Optional<User> optionalUser = clientService.authorization(login, password);
            if (optionalUser.isPresent()) {
                request.getSession().setAttribute(ParameterName.LOGIN_USERNAME, optionalUser.get().getLogin());
                request.getSession().setAttribute(ParameterName.LOGIN_ROLES, optionalUser.get().getRoles());
            } else {
                result.put(ParameterName.MESSAGE, MESSAGE_WRONG_RESULT);
            }
            result.put(ParameterName.PAGE_FORWARD, HospitalUrl.PAGE_MAIN);
        } catch (ServiceException e) {
            logger.error(e);
            result.put(ParameterName.COMMAND_EXCEPTION, e.getMessage());
        }
        return result;
    }
}
