package epam.hospital.controller.command.admin.head;

import mashko.hospital.controller.HttpCommand;
import mashko.hospital.controller.ParameterName;
import mashko.hospital.controller.command.admin.head.FindProceduresPaging;
import mashko.hospital.entity.PageResult;
import mashko.hospital.service.AdminHeadService;
import mashko.hospital.service.ServiceException;
import org.apache.log4j.Logger;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;

public class FindProceduresPagingTest {
    @Mock
    private Logger logger;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private AdminHeadService service;
    private HttpCommand httpCommand;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        httpCommand = new FindProceduresPaging(service, logger);
    }

    @Test
    public void procedureControl_addActionAndValidRequestParameters_success()
            throws ServiceException, IOException, ServletException {
        Mockito.when(request.getParameter(ParameterName.NAME_PART))
                .thenReturn("qwe");
        Mockito.when(request.getParameter(ParameterName.PAGE_NUMBER))
                .thenReturn(String.valueOf(3));
        Mockito.when(service.findAllProceduresByNamePartPaging("qwe", 3))
                .thenReturn(PageResult.from(Collections.emptyList(), 3));

        Map<String, Object> result = httpCommand.execute(request, response);
        Assert.assertTrue((int) result.get(ParameterName.TOTAL_PAGES) != 0);
    }
}
