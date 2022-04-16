package mashko.hospital.controller.command.admin.head;

import mashko.hospital.controller.CommandName;
import mashko.hospital.controller.HospitalUrl;
import mashko.hospital.controller.HttpCommand;
import mashko.hospital.controller.ParameterName;
import mashko.hospital.entity.Medicament;
import mashko.hospital.entity.table.ProceduresFieldName;
import mashko.hospital.service.AdminHeadService;
import mashko.hospital.service.ServiceAction;
import mashko.hospital.service.ServiceException;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MedicamentControl implements HttpCommand {

    private final AdminHeadService service;
    private final Logger logger;

    public MedicamentControl(AdminHeadService service, Logger logger) {
        this.service = service;
        this.logger = logger;
    }

    @Override
    public Map<String, Object> execute(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, Object> result = new HashMap<>();
        String name = request.getParameter(ParameterName.PROCEDURE_OR_MEDICAMENT_NAME);
        boolean isEnabled = Boolean.parseBoolean(request.getParameter(ProceduresFieldName.IS_ENABLED));
        ServiceAction serviceAction = ServiceAction.valueOf(request.getParameter(ParameterName.ACTION));

        try {
            if (serviceAction == ServiceAction.ADD) {
                service.createProcedureOrMedicament(new Medicament(name, true), Medicament.class);
            }
            if (serviceAction == ServiceAction.UPDATE) {
                service.updateEnabledStatusOnProcedureOrMedicament(new Medicament(name, isEnabled),
                        isEnabled, Medicament.class);
            }
            response.sendRedirect(HospitalUrl.MAIN_URL +
                    HospitalUrl.COMMAND_ADMIN_FIND_MEDICATIONS_PAGING +
                    "?command=" + CommandName.ADMIN_FIND_MEDICATIONS_PAGING);
        } catch (ServiceException e) {
            logger.error(e);
            result.put(ParameterName.COMMAND_EXCEPTION, e.getMessage());
        }
        return result;
    }
}
