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

public class RoleControl implements HttpCommand {
    private static final String MESSAGE_SUCCESS = "Success.";
    private static final String MESSAGE_WRONG_RESULT = "Update roles was not perform.";

    private final AdminHeadService adminHeadService;
    private final Logger logger;

    public RoleControl(AdminHeadService adminHeadService, Logger logger) {
        this.adminHeadService = adminHeadService;
        this.logger = logger;
    }

    @Override
    public Map<String, Object> execute(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> result = new HashMap<>();
        String login = request.getParameter(UsersFieldName.LOGIN);
        Department doctorDepartment = null;
        ServiceAction serviceAction = ServiceAction.valueOf(request.getParameter(ParameterName.ACTION));
        Role role = Role.valueOf(request.getParameter(ParameterName.ROLE));
        boolean isSuccess = false;
        try {
            if (role != Role.DOCTOR && role != Role.MEDICAL_ASSISTANT && role != Role.DEPARTMENT_HEAD) {
                isSuccess = adminHeadService.updateUserRoles(login, serviceAction, role);
            }
            if (role == Role.DEPARTMENT_HEAD) {
                doctorDepartment = Department.valueOf(request.getParameter(ParameterName.DEPARTMENT));
                isSuccess = adminHeadService.appointDepartmentHead(doctorDepartment, login);
            }
            if (role == Role.DOCTOR || role == Role.MEDICAL_ASSISTANT) {
                doctorDepartment = Department.valueOf(request.getParameter(ParameterName.DEPARTMENT));
                isSuccess = adminHeadService
                        .updateDepartmentStaff(doctorDepartment, serviceAction, login, role);
            }
            if (isSuccess) {
                ArrayList<Role> roles = adminHeadService.findUserRoles(login);
                result.put(ParameterName.DEPARTMENT, doctorDepartment);
                result.put(ParameterName.USER_ROLES, roles);
                result.put(ParameterName.MESSAGE, MESSAGE_SUCCESS);
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
