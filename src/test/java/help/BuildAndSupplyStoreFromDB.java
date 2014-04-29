package help;


import general.Page;
import general.virt.HelpPage;
import general.virt.LoginPage;
import general.virt.MainPage;
import general.virt.StorePage;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by a.sitnikov on 18.02.14.
 */


/*
    Descriprion
    Simple Soap import product and verify them to correct import to MPO ( SP )
*/

public class BuildAndSupplyStoreFromDB extends Page {

//    @Override
//    protected void setUp() throws Exception {
//
//    }
//
    @Override
    protected void tearDown() throws Exception {

    }

    // ТОП-1
    // квала рабочих увеличивается пока максимальное количество рабов дозволеное на предприятии будет едва выше текущего.
    // при этом происходит обучение персонала
    // если ТопРабочих < Рабочих, уменьшить зп пока ТопРабочих ~= Рабочих  [количество]


    @Test
    public void test() throws Throwable {

        List<String> list = new LoginPage(driver).openVirtUrl().login().selectStore().getListAllUnitWithCity();// город урл

        ArrayList<String> subListStore = new ArrayList<String>();
        ArrayList<String> storesToBuild = new HelpPage().getAllDataFromDbStoreBuild(); // город : отдел
        String currentUrl = driver.getCurrentUrl();
        boolean isFoundStore=false;

        for(String storeTobuild: storesToBuild) {
            isFoundStore=false;
            String cityToBuild = storeTobuild.split(";")[0];
            String departmentToBuild = storeTobuild.split(";")[1];
            logMe("");
            logMe(" !!!  Нужно закупить: "+storeTobuild);
            if(isContainsLocalTest(list,storeTobuild)){
                logMe("Ура, есть магазин в городе "+ storeTobuild);
                subListStore= getContainsIntersectLocalTest(list,storeTobuild);
                for(String localStore: subListStore){
                    logMe("Переходим в найденный город для закупок");
                    driver.get(localStore.split(";")[1]);

                    //есть ли отдел для закупок в этом магазине?
                    if(new StorePage(driver).goToTradingRoom().isDepToSell(departmentToBuild,new StorePage(driver).getCurrentTypesDepFromSalesRoom())){
                        logMe("Есть! Апдейтим базу!");
                        new HelpPage().updateBaseStoreBuild(cityToBuild,departmentToBuild,true);
                        new StorePage(driver).goToMainStorePage();
                    }
                    else {
                        // отдела такого нет, мы должны узнать
                        // можно ли добавить отдел?
                        // если да - закупаемся
                        new StorePage(driver).goToMainStorePage();
                        if(new StorePage(driver).getStoreDepMaxSize()>new StorePage(driver).getStoreDepMaxSize()){
                            logMe("Добавляем новый отдел: "+departmentToBuild);
                            new StorePage(driver).autoBuyWithDep(departmentToBuild);
                            logMe("Есть! Апдейтим базу!");
                            isFoundStore=true;
                            new HelpPage().updateBaseStoreBuild(cityToBuild,departmentToBuild,true);
                        }
                    }
                }

                if(isFoundStore){
                    logMe("Магазин переполнен, создаем новый магазин!");
                    if(new HelpPage(driver).createStore(cityToBuild)){
                        new StorePage(driver).autoBuyWithDep(departmentToBuild);
                        logMe("Есть! Апдейтим базу!");
                        new HelpPage().updateBaseStoreBuild(cityToBuild,departmentToBuild,true);
                    }
                }
            }
            else {
                logMe("Нужно построить новый магазин в городе "+ storeTobuild);
                if(new HelpPage(driver).createStore(cityToBuild)){
                    new StorePage(driver).autoBuyWithDep(departmentToBuild);
                    logMe("Есть! Апдейтим базу!");
                    new HelpPage().updateBaseStoreBuild(cityToBuild,departmentToBuild,true);
                }
            }
            list = new MainPage(driver).goToGeneralPlantList().getListAllUnitWithCity();
        }
    }

    public boolean isContainsLocalTest(List<String> mass , String string){
        for(int i=0; i<mass.size(); i++){
            String item = mass.get(i).split(";")[0];
            if(item.equals(string.split(";")[0]))
                return true;
        }
        return false;
    }

    public ArrayList<String> getContainsIntersectLocalTest(List<String> mass , String string){ // город урл : горо отдел
        ArrayList<String> intersect = new ArrayList<String>();
        for(int i=0; i<mass.size(); i++){
            String item = mass.get(i).split(";")[0];
            if(item.equals(string.split(";")[0])){
                intersect.add(mass.get(i));
                logMe("Найден город: "+mass.get(i));
            }

        }
        return intersect;
    }





}