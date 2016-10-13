package org.verm9.travian.business;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.verm9.travian.dml.DataManipulatorT42Impl;
import org.verm9.travian.dml.DocumentEvaluator;
import org.verm9.travian.dml.dto.Dorf2;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by nonu on 10/2/2016.
 */
@Component
public class CentralImpl implements Central {

    @Autowired
    private DocumentEvaluator documentEvaluator;
    @Autowired
    private DataManipulatorT42Impl dataManipulator;

    @Override
    public void login() {
        try {
            documentEvaluator.login();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void dorf1Build(int id) {
        try {
            dataManipulator.dorf1Build.invoke(1);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void getBuldings() {
        try {
            dataManipulator.dorf2.invoke();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void dorf2Build(int idOfPlace, Dorf2.Building.Type whatToBuild) {
        try {
            dataManipulator.dorf2Build.invoke(idOfPlace, whatToBuild);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
