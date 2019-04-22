package org.aron.boot.filter;

import lombok.extern.slf4j.Slf4j;
import org.aron.server.annotation.WebFilter;
import org.aron.server.core.ServletContext;
import org.aron.server.error.ServletException;
import org.aron.server.servlet.Filter;
import org.aron.server.servlet.FilterChain;
import org.aron.server.servlet.ServletRequest;
import org.aron.server.servlet.ServletResponse;

import java.io.IOException;

/**
 * @author: Y-Aron
 * @create: 2019-03-25 11:10
 */
@WebFilter("/**")
@Slf4j
public class TestFilter implements Filter {

    @Override
    public void init(ServletContext context) throws ServletException {
        log.debug("init");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
        log.debug("do filter");
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        log.debug("destroy");
    }
}
