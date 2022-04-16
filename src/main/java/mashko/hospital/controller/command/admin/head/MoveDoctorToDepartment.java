package mashko.hospital.controller.command.admin.head;

import mashko.hospital.controller.HospitalUrl;
import mashko.hospital.controller.HttpCommand;
import mashko.hospital.controller.ParameterName;
import mashko.hospital.entity.Department;
import mashko.hospital.entity.Role;
import mashko.hospital.entity.table.UsersFieldName;
import mashko.hospital.service.AdminHeadService;
import mashko.hospital.service.ServiceAction;
import mashko.hospital.service.ServiceException;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MoveDoctorToDepartment implements HttpCommand {
    private static final String MESSAGE_SUCCESS = "Success.";
    private static final String MESSAGE_WRONG_RESULT = "Move doctor to another department was not perform.";

    private final AdminHeadService adminHeadService;
    private final Logger logger;

    public MoveDoctorToDepartment(AdminHeadService adminHeadService, Logger logger) {
        this.adminHeadService = adminHeadService;
        this.logger = logger;
    }

    @Override
    public Map<String, Object> execute(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> result = new HashMap<>();
        String login = request.getParameter(UsersFieldName.LOGIN);
        Department department = Department.valueOf(request.getParameter(ParameterName.DEPARTMENT));
        try {
            ArrayList<Role> roles = adminHeadService.findUserRoles(login);
            Role currentRole = Role.CLIENT;
            if (roles.contains(Role.DOCTOR)) {
                currentRole = Role.DOCTOR;
            }
            if (roles.contains(Role.MEDICAL_ASSISTANT)) {
                currentRole = Role.MEDICAL_ASSISTANT;
            }
            if (adminHeadService.updateDepartmentStaff(department, ServiceAction.ADD, login, currentRole)) {
                result.put(UsersFieldName.LOGIN, login);
                result.put(ParameterName.USER_ROLES, roles);
                result.put(ParameterName.DEPARTMENT, department);
                result.put(ParameterName.MESSAGE, MESSAGE_SUCCESS);
            } else {
                result.put(ParameterName.MESSAGE, MESSAGE_WRONG_RESULT);
            }
            result.put(ParameterName.PAGE_FORWARD, HospitalUrl.PAGE_DEPARTMENT_CONTROL);
        } catch (ServiceException e) {
            logger.error(e);
            result.put(ParameterName.COMMAND_EXCEPTION, e.getMessage());
        }
        return result;
    }
}
