package org.verm9.travian;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.verm9.travian.business.Central;

/**
 * Created by nonu on 9/29/2016.
 */
public class Launcher {

    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-app.xml");
        Central central = context.getBean(Central.class);
        central.login();

        central.getBuldings();

    }
}
