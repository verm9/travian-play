package org.verm9.travian;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.verm9.travian.business.Central;
import org.verm9.travian.business.TravianApi;
import org.verm9.travian.controller.ApplicationController;
import org.verm9.travian.dto.Dorf2;
import org.verm9.travian.dto.Village;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by nonu on 9/29/2016.
 */
public class Launcher {

    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-app.xml");
        ApplicationController applicationController = context.getBean(ApplicationController.class);
        Central central = context.getBean(Central.class);


        /*travianApi.login();
        travianApi.setCapital();
        //travianApi.getBuldings();
        travianApi.dorf2Build(27, Dorf2.Building.Type.WAREHOUSE);
        central.buildAllToMaxLevel();*/
        new Thread(){
            @Override
            public void run() {
                central.mainCycle();
            }
        }.start();

        try {
            Thread.sleep(10_000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        int villageId = applicationController.getGameData().getVillages().entrySet().iterator().next().getKey();
        applicationController.buildAtDorf2(villageId, Dorf2.Building.Type.RALLY_POINT);
        applicationController.switchRunningState();

        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        applicationController.switchRunningState();


        // Imitate view actions by calling controller directly.


    }
}
