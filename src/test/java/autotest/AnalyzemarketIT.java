package autotest;


import general.Page;
import general.virt.LoginPage;
import help.CreateDB;
import org.junit.Test;

import java.io.File;

/**
 * Created by a.sitnikov on 18.02.14.
 */


/*
    Descriprion
    Simple Soap import product and verify them to correct import to MPO ( SP )
*/

public class AnalyzemarketIT extends Page {

//    @Override
//    protected void setUp() throws Exception {
//
//    }
//
//    @Override
//    protected void tearDown() throws Exception {
//
//    }

    // ТОП-1
    // квала рабочих увеличивается пока максимальное количество рабов дозволеное на предприятии будет едва выше текущего.
    // при этом происходит обучение персонала
    // если ТопРабочих < Рабочих, уменьшить зп пока ТопРабочих ~= Рабочих  [количество]


    @Test
    public void test() throws Throwable {

    int session = Integer.valueOf(formattedDate("MMdd"));

        File file = new File("market.db");
        if(!file.exists()) {
            logMe("creating new database table!");
            new CreateDB().createMarket();
        }

    logMe("today session: "+session);
    new LoginPage(driver).openVirtUrl().login()
            .getAnalyzeMarket("Лекарственные травы", "Аптека", session)
            .getAnalyzeMarket("Природные лекарства", "Аптека", session)
            .getAnalyzeMarket("Натуральный кофе", "Бакалея", session)
            .getAnalyzeMarket("Растворимый кофе", "Бакалея", session)
            .getAnalyzeMarket("Цветы и эфиромасличные культуры", "Бакалея", session)
            .getAnalyzeMarket("Энергетические напитки", "Бакалея", session)
            .getAnalyzeMarket("Ликер", "Бакалея", session)
            .getAnalyzeMarket("Консервированные оливки", "Продукты питания", session)
            .getAnalyzeMarket("Оливковое масло", "Продукты питания", session)
            .getAnalyzeMarket("Специи", "Продукты питания", session)
            .getAnalyzeMarket("Сыр фета", "Продукты питания", session)
            .getAnalyzeMarket("Электроинструмент", "Промышленные товары", session)
    ;

    }

}