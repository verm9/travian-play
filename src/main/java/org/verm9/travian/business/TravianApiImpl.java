package org.verm9.travian.business;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.verm9.travian.dml.DataManipulatorT42Impl;
import org.verm9.travian.dml.DocumentEvaluator;
import org.verm9.travian.dto.Dorf2;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by nonu on 10/2/2016.
 */
@Component
public class TravianApiImpl implements TravianApi {

    @Autowired
    private DocumentEvaluator documentEvaluator;
    @Autowired
    private DataManipulatorT42Impl dataManipulator;

    @Override
    public void login() throws IOException {
        documentEvaluator.login();
    }

    @Override
    public void dorf1Build(int id) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException, IOException {
        dataManipulator.dorf1Build.invoke(id);

    }

    @Override
    public void getBuldings() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException, IOException {
        dataManipulator.dorf2.invoke();
    }

    @Override
    public void dorf2Build(int idOfPlace, Dorf2.Building.Type whatToBuild) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException, IOException {
        dataManipulator.dorf2Build.invoke(idOfPlace, whatToBuild.getId());
    }

    @Override
    public void changeVillage(int id) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException, IOException {
        dataManipulator.changeVillage.invoke(id);
    }

    @Override
    public void setCapital() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException, IOException {
        dataManipulator.getPlayerPage.invoke();
    }
}
