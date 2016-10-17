package org.verm9.travian;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.verm9.travian.business.Central;
import org.verm9.travian.business.TravianApi;
import org.verm9.travian.dto.Dorf2;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by nonu on 9/29/2016.
 */
public class Launcher {

    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-app.xml");
        TravianApi travianApi = context.getBean(TravianApi.class);
        Central central = context.getBean(Central.class);

        try {
            travianApi.login();
            travianApi.setCapital();
            travianApi.getBuldings(); // update village.dorf2 manually, main cycle still doesn't
            //travianApi.dorf2Build(28, Dorf2.Building.Type.WAREHOUSE);
            central.buildAllToMaxLevel();
            central.mainCycle();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

    }
}
