package mashko.hospital.controller.command.admin.head;

import mashko.hospital.controller.HospitalUrl;
import mashko.hospital.controller.HttpCommand;
import mashko.hospital.controller.ParameterName;
import mashko.hospital.entity.Medicament;
import mashko.hospital.entity.PageResult;
import mashko.hospital.service.AdminHeadService;
import mashko.hospital.service.ServiceException;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

public class FindMedicationsPaging implements HttpCommand {
    private final AdminHeadService service;
    private final Logger logger;

    public FindMedicationsPaging(AdminHeadService service, Logger logger) {
        this.service = service;
        this.logger = logger;
    }

    @Override
    public Map<String, Object> execute(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> result = new HashMap<>();
        String namePart = request.getParameter(ParameterName.NAME_PART);
        int pageNumber = Integer.parseInt(request.getParameter(ParameterName.PAGE_NUMBER) == null ?
                "0" :
                request.getParameter(ParameterName.PAGE_NUMBER));
        try {
            PageResult<Medicament> pageResult = service.findAllMedicationsByNamePartPaging(namePart, pageNumber);
            result.put(ParameterName.TOTAL_PAGES, pageResult.getTotalPages());
            result.put(ParameterName.PAGE_NUMBER, pageNumber);
            result.put(ParameterName.MEDICAMENT_LIST, pageResult.getList());
            result.put(ParameterName.PAGE_FORWARD, HospitalUrl.PAGE_MEDICAMENT_CONTROL);
        } catch (ServiceException e) {
            logger.error(e);
            result.put(ParameterName.COMMAND_EXCEPTION, e.getMessage());
        }
        return result;
    }
}
