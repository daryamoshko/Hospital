package mashko.hospital.controller.command.admin.head;

import mashko.hospital.controller.HospitalUrl;
import mashko.hospital.controller.HttpCommand;
import mashko.hospital.controller.ParameterName;
import mashko.hospital.entity.Department;
import mashko.hospital.entity.Role;
import mashko.hospital.entity.table.UsersFieldName;
import mashko.hospital.service.AdminHeadService;
import mashko.hospital.service.ServiceException;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FindRoleControlAttributes implements HttpCommand {
    private static final String MESSAGE_WRONG_RESULT = "Non existent user.";

    private final AdminHeadService adminHeadService;
    private final Logger logger;

    public FindRoleControlAttributes(AdminHeadService adminHeadService, Logger logger) {
        this.adminHeadService = adminHeadService;
        this.logger = logger;
    }

    @Override
    public Map<String, Object> execute(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> result = new HashMap<>();
        String login = request.getParameter(UsersFieldName.LOGIN);
        try {
            ArrayList<Role> roles = adminHeadService.findUserRoles(login);
            if (!roles.isEmpty()) {
                Department userDepartment = null;
                if (roles.contains(Role.DOCTOR) || roles.contains(Role.MEDICAL_ASSISTANT)) {
                    userDepartment = adminHeadService.findDepartmentByUsername(login)
                            .orElseThrow(ServiceException::new);
                }
                result.put(ParameterName.DEPARTMENT, userDepartment);
                result.put(ParameterName.USER_ROLES, roles);
            } else {
                result.put(ParameterName.MESSAGE, MESSAGE_WRONG_RESULT);
            }
            result.put(ParameterName.PAGE_FORWARD, HospitalUrl.PAGE_ROLE_CONTROL);
        } catch (ServiceException e) {
            logger.error(e);
            result.put(ParameterName.COMMAND_EXCEPTION, e.getMessage());
        }
        return result;
    }
}
