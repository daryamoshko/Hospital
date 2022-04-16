package mashko.hospital.controller.command;

import mashko.hospital.controller.HttpCommand;
import mashko.hospital.controller.HospitalUrl;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class FirstVisit implements HttpCommand {
    @Override
    public Map<String, Object> execute(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        request.getRequestDispatcher(HospitalUrl.PAGE_MAIN).forward(request, response);
        return new HashMap<>();
    }
}
