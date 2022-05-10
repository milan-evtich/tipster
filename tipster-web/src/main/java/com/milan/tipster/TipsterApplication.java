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
        // контра сто ... это X2 либо X1 пример https://www.matchmoney.com.gr/match/%ce%bd%cf%84%ce%b1%ce%bd%cf%84%ce%b9-%ce%b3%ce%b9%ce%bf%cf%85%ce%bd-%cf%87%ce%b1%cf%81%cf%84%cf%83-24-4-22-1700-%cf%80%cf%81%ce%bf%ce%b3%ce%bd%cf%89%cf%83%cf%84%ce%b9%ce%ba%ce%b1-%cf%83%cf%84/

        // TODO
        // Сделать новую колонку с αμφίσκορο

        // TODO
        // дублирование типов [
        //  {
        //    "tip_id": 31147
        //  },
        //  {
        //    "tip_id": 31146
        //  }
        //]
        //[
        //  {
        //    "tip_id": 31784
        //  },
        //  {
        //    "tip_id": 31783
        //  },
        //  {
        //    "tip_id": 31782
        //  },
        //  {
        //    "tip_id": 31781
        //  },
        //  {
        //    "tip_id": 31780
        //  },
        //  {
        //    "tip_id": 31776
        //  },
        //  {
        //    "tip_id": 31775
        //  },
        //  {
        //    "tip_id": 31772
        //  }
        //]
        // TODO ВАЖНО TEST
        // в ответе fetch-new-games вернут список ошибок если они есть. Какие competition не найдены
        // Competition with nameGr1 ΑΡΓΕΝΤΙΝΗ - ΚΟΠΑ ΝΤΕ ΛΑ ΛΙΓΚΑ ΠΡΟΦΕΣΙΟΝΑΛ or nameGr2 ΚΟΠΑ ΝΤΕ ΛΑ ΛΙΓΚΑ ΠΡΟΦΕΣΙΟΝΑΛ and season _2021_2022 not found in DB!
        // TODO fetch-open-tips-already-played
        // Список ошибочных матчей в ответе Error fetching tip for game:https://www.matchmoney.com.gr/match/zakynthos-of-ierapetra-6-2-22-14-45-prognostika-stoichimatos/ -

        // TODO TEST
        // Error fetching tip for game - добавить gameId и может бы вернуть в ответе список ошибок
        // TODO
        // Поменять систем на x2p и туда добавлять все у которых Х или коэфф от 1.95 до 3.00 с интервалом 45 минут
        // TODO
        // добавить новое поле коэффициент 1Х2 который всегда будет хранится не смотря на тип
        // TODO
        // В fetch-new-games если есть <div class="offer-text"> то не создавать под него game
        // или offers/ не надо <a href="https://www.matchmoney.com.gr/offers/sportingbet-soyper-kap-ispanias-me-apodoseis-poy-den-echeis-xanadei/" class="text-primary

        // Done только до следущего дня в 12:00

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

        // 1) В ответь на fetch-tips-already-played возвращать статистику за день
        // 2) Хранить dayPlan в БД???
        // 3) Определилось по ошибке как NOBET Λογικό ότι μαζεύτηκε ο άσος, αλλά στέκεται στο 1,75 του Stoiximan


        // 1) Уверенность в решении --> Πολύ οριακά θα έδινα ένα μικρό κλικ στο διπλό του 2,50 στη Novibet ,

    }
}