package org.example.demo2;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Listik {
    protected List<Integer> random() {
        List<Integer> list = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < 1000; i++) {
            list.add(random.nextInt(10000));
        }
        return list;
    }

    protected List<String> input() {
        List<String> list = new ArrayList<>();
        Scanner scanner = new Scanner(System.in);
        System.out.println("Введите 10 строк: ");
        for (int i = 0; i < 10; i++) {
            list.add(scanner.nextLine());
        }
        return list;
    }
}
