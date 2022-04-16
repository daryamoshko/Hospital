package mashko.hospital.controller.filter;

import mashko.hospital.controller.HospitalUrl;
import mashko.hospital.controller.ParameterName;
import mashko.hospital.dao.impl.UserDaoImpl;
import mashko.hospital.entity.Role;
import mashko.hospital.service.AuthenticationService;
import mashko.hospital.service.ServiceException;
import mashko.hospital.service.impl.AuthenticationServiceImpl;
import org.apache.log4j.Logger;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class AuthenticationFilter implements Filter {
    private static final String FORBIDDEN_MESSAGE = "Access denied.";

    private final Logger logger = Logger.getLogger(AuthenticationFilter.class);
    private final AuthenticationService authenticationService = new AuthenticationServiceImpl(new UserDaoImpl());

    private Role role;

    @Override
    public void init(FilterConfig filterConfig) {
        role = Role.valueOf(filterConfig.getInitParameter(ParameterName.ROLE));
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
        HttpSession session = httpServletRequest.getSession();

        String loginUsername = (String) session.getAttribute(ParameterName.LOGIN_USERNAME);
        try {
            if (loginUsername == null || !authenticationService.isHasRole(loginUsername, role)) {
                httpServletResponse.sendError(HttpServletResponse.SC_FORBIDDEN, FORBIDDEN_MESSAGE);
                return;
            }
            filterChain.doFilter(servletRequest, servletResponse);
        } catch (ServiceException e) {
            servletRequest.setAttribute(ParameterName.MESSAGE, e.getMessage());
            httpServletRequest.getRequestDispatcher(HospitalUrl.PAGE_ERROR).forward(servletRequest, servletResponse);
            logger.error(e);
        }
    }
}
