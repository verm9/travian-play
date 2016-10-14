package org.verm9.travian;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.verm9.travian.business.Central;
import org.verm9.travian.dml.dto.Dorf2;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by nonu on 9/29/2016.
 */
public class Launcher {

    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-app.xml");
        Central central = context.getBean(Central.class);
        try {
            central.login();

            central.dorf1Build(5);
            //central.dorf2Build(24, Dorf2.Building.Type.WAREHOUSE);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }
}
