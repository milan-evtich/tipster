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

        // 1) Не проставляется флаг dnb
        // 2) Не распознали как SPOT_1: Με τον άσο πάντως στο 3,25 της Bet 365 παίρνω τα ρίσκα μου.

        // 1) DONE --- Σωστά μετρημένο το σετ, μένουμε θεατές. не попадает в NOBET
        // 2) DONE --- Στο 1,66 η διπλή, στο 2,40 ο άσος με DNB στη Novibet. определено как SPOT_1
        // 3) DONE --- Ένα κλικ στα αριστερά και στον άσο σε DNB στο 2,04 της Novibet .  определено как SPOT_1
        // 4) DONE --- Ο άσος σε DNB στο 2,10 στην Bet365. определено как SPOT_1

        // 1) DONE --- FIX WON LOST STATUS TipServiceImpl 746
        // 2) DONE - ON TEST --- Не распозналось

        // 2) DONE - ON TEST --- Если pick.strong пусто or null то pick и статус NOBET
        // 1) DONE --- Дополнить ответ для плана, добавить название матча nameGr
        // 2) DONE --- Проверить как ранкирование проходит (ocerall_rating) in competition, tipman and tc
        // 3) DONE --- Fix newCoefficient initial value для новых объектов
        // 4) DONE --- Увеличить размер текста и strong

        // 1) DONE --- Если PICK опредлился как NOBET то и статус у него должен быть NOBET а fetch_status = 'FULLY_FETCHED'
        // 2) DONE --- Если поле strong = null то pick NOBET, статус у него должен быть NOBET а fetch_status = 'FULLY_FETCHED'
        // 3) DONE --- Помечать флаг dnb=true для типов SPOT_DNB_*
        // 4) DONE --- В ETipFilter добавить разные ставки для DNB а разные для не DNB
    }
}