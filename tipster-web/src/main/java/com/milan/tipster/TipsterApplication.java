package com.milan.tipster;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.yaml.snakeyaml.Yaml;

import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class TipsterApplication {
    public static void main(String[] args) {

        SpringApplication.run(TipsterApplication.class, args);

        // TODO

        // 1) TODO FIX WON LOST STATUS TipServiceImpl 746

        // 2) Не распозналось
            // Μου αρέσει το διπλό σε DNB στο 2,62 της Bet 365, η Τιαντζίν έδειξε σαφέστατα πιο έτοιμη στην επανέναρξη.
            // Στο 2,06 το διπλό DNB στη Novibet.
        // 1) Если pick NOBET тогда и статус NOBET
        // 2) Если pick.strong пусто or null то pick и статус NOBET
        // 1) DONE --- Дополнить ответ для плана, добавить название матча nameGr
        // 2) Проверить как ранкирование проходит (ocerall_rating) in competition, tipman and tc
        // 3) DONE --- Fix newCoefficient initial value для новых объектов
        // 4) DONE --- Увеличить размер текста и strong

        // 1) DONE --- Если PICK опредлился как NOBET то и статус у него должен быть NOBET а fetch_status = 'FULLY_FETCHED'
        // 2) DONE --- Если поле strong = null то pick NOBET, статус у него должен быть NOBET а fetch_status = 'FULLY_FETCHED'
        // 3) DONE --- Помечать флаг dnb=true для типов SPOT_DNB_*
        // 4) DONE --- В ETipFilter добавить разные ставки для DNB а разные для не DNB
    }
}