package org.verm9.travian;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.verm9.travian.business.Central;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Created by nonu on 10/27/2016.
 */
public class AutoRunServletContextListener implements ServletContextListener {
    private static final Logger LOG = getLogger(AutoRunServletContextListener.class);

    @Autowired
    private Central central;

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // Notification that the servlet context is about to be shut down.
        LOG.info("Context destroyed.");
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // Notification that the web application initialization process is starting.
        LOG.info("Context initialized.");

        // Doesn't make testing difficult since doesn't use getBean.
        // http://stackoverflow.com/a/11253751/6919581
        WebApplicationContextUtils
                .getRequiredWebApplicationContext(sce.getServletContext())
                .getAutowireCapableBeanFactory()
                .autowireBean(this);

        central.start();
    }

}