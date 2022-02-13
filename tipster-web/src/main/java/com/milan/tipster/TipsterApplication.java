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

        // ВАЖНО --> делать daily plan c текущего дня с 00:00 (чтобы не пропустить скоринг всех)
        // только до следущего дня в 12:00

        // ОШИБКА SPOT_1 вместо SPOT_2 tipId=22684 Μαρσέιγ 2,90 στην Bet365
        // 1) Не определилось - UNKNOWN  Πάμε με τη δ.ε. της φιλοξενούμενης που βρίσκεται στο 1,70 στη Novibet.
            // Πάμε με τη δ.ε. της γηπεδούχου που βρίσκεται στο 1,65 στη Novibet.
            // Θα το προσπεράσουμε.
            // επιλογή της ισοπαλίας στο 2,89 του Stoiximan.
            // odds missing Εύκολα αγοράζεται η διπλή (Χ2) στο 1,85.
            // Определилось ошибочно
            // SPOT_1 Σε τιμές αουτσάιντερ ο άσος, παίζεται με DNB στο 2,03 της Novibet.
            // SPOT_2 Παίζεται και με DNB το διπλό (2,32), η διπλή ευκαιρία (Χ2) στο 1,60 από τη Stoiximan.
            // UNKNOWN Σε αποδόσεις αουτσάιντερ αξίζει η κόντρα, με τη δ.ε. της Αλκμααρ να βρίσκεται στο 2,30 στη Novibet.
            // NOBET -> Το διπλό draw no bet της Εξέλσιορ βρίσκεται στο 1,93 στη Stoiximan.

        // 2) NOBET -> και πλέον χάθηκε η αξία κόντρας του φαβορί.
        // 3) Опредлилось ка SPOT_1 Στο 1,61 το Under 2,5 από τη Novibet.
        // 4) Попало в NOBET -> το 2,02 του Stoiximan στον άσο της πρωταθλήτριας Κλαμπ Μπριζ δεν είναι εύκολο να παραβλεφθεί.
        // 1) Με κάλυψη στην ισοπαλία, στο 2,16 στην Betshop ο άσος. опредляется как SPOT_1 не SPOT_DNB_1

        // 1) nobet --> Θα το προσπεράσουμε.
        // 2) οριακό 1,72 στον Stoiximan  -- не очень то и уверен автор
        // 3) если определилось что DNB но без 1 либо 2, то можно угадать по названию команды
        // 1) Показывать разницу между текущим кэфом и тем который автор указал

        // 1) В ответь на fetch-tips-already-played возвращать статистику за день
        // 2) DONE Разбить day-plan на список для синглов, список для combo и общий список
        // 2) Хранить dayPlan в БД???
        // 3) Определилось по ошибке как NOBET Λογικό ότι μαζεύτηκε ο άσος, αλλά στέκεται στο 1,75 του Stoiximan
        // 4) В fetch-new-games если есть <div class="offer-text"> то не создавать под него game
            // или offers/ не надо <a href="https://www.matchmoney.com.gr/offers/sportingbet-soyper-kap-ispanias-me-apodoseis-poy-den-echeis-xanadei/" class="text-primary


        // 1) Уверенность в решении --> Πολύ οριακά θα έδινα ένα μικρό κλικ στο διπλό του 2,50 στη Novibet ,
        // 2) Понять почему не все матчи идуть на скоринг (score = 0)
            // [
        //  {
        //    "status": "WON",
        //    "pick": "SPOT_2",
        //    "odds": 1.8,
        //    "score_type": "SPOT_2",
        //    "score": 5161.990277190679,
        //    "code": "kadith-sevilli-3-1-22-22-15-paleyontas-me-ton-koronoio"
        //  },
        //  {
        //    "status": "WON",
        //    "pick": "SPOT_2",
        //    "odds": 3,
        //    "score_type": "SPOT_2",
        //    "score": 2891.9487770633054,
        //    "code": "goychan-tsongkingk-3-1-22-09-30-prognostika-stoichimatos"
        //  },
        //  {
        //    "status": "LOST",
        //    "pick": "SPOT_1",
        //    "odds": 2.05,
        //    "score_type": "SPOT_2",
        //    "score": 0,
        //    "code": "ethn-achnas-doxa-k-3-1-22-17-00-prognostika-stoichimatos"
        //  },
        //  {
        //    "status": "LOST",
        //    "pick": "SPOT_1",
        //    "odds": 1.64,
        //    "score_type": "SPOT_2",
        //    "score": 0,
        //    "code": "mantsester-g-goylvs-3-1-22-19-30-trechei-gia-to-chameno-edafos"
        //  },
        //  {
        //    "status": "LOST",
        //    "pick": "SPOT_2",
        //    "odds": 3.2,
        //    "score_type": "SPOT_X",
        //    "score": 0,
        //    "code": "rentingk-ntermpi-3-1-22-17-00-yparchei-zoi-ston-planiti-ntermpi"
        //  },
        //  {
        //    "status": "WON",
        //    "pick": "SPOT_2",
        //    "odds": 3.4,
        //    "score_type": "SPOT_2",
        //    "score": 0,
        //    "code": "stooyk-preston-3-1-22-17-00-prognostika-stoichimatos"
        //  }
        //]

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