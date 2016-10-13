package org.verm9.travian;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.verm9.travian.business.Central;
import org.verm9.travian.dml.dto.Dorf2;

/**
 * Created by nonu on 9/29/2016.
 */
public class Launcher {

    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-app.xml");
        Central central = context.getBean(Central.class);
        central.login();

        central.dorf2Build(24, Dorf2.Building.Type.MAIN_BUILDING);
        //central.dorf1Build(4);

    }
}
