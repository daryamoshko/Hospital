package mashko.hospital.controller.command;

import mashko.hospital.controller.HospitalUrl;
import mashko.hospital.controller.HttpCommand;
import mashko.hospital.controller.ParameterName;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

public class SignOut implements HttpCommand {
    @Override
    public Map<String, Object> execute(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> result = new HashMap<>();
        request.getSession().removeAttribute(ParameterName.LOGIN_USERNAME);
        request.getSession().removeAttribute(ParameterName.LOGIN_ROLES);
        result.put(ParameterName.PAGE_FORWARD, HospitalUrl.PAGE_MAIN);
        return result;
    }
}
