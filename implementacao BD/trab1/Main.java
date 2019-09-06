/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trab1;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Sergio
 */
public class Main {

    public void testCreation() throws Exception {
        Table table;
        table = new Table("c:\\teste\\ibd", "table.ibd");
        table.createDatabase();
    }

    public void testLoad() throws Exception {
        Table table;
        table = new Table("c:\\teste\\ibd", "table.ibd");
        table.initLoad();
    }

    public void testInsertion() throws Exception {
        Table table;
        table = new Table("c:\\teste\\ibd", "table.ibd");
        table.initLoad();
        Record rec = table.addRecord(3, "lddldf fdçonfd faofhdofh odhfod ofsdf");
        table.flushDB();

    }

    public void testSearch() throws Exception {
        Table table;
        table = new Table("c:\\teste\\ibd", "table.ibd");
        table.initLoad();
        Record rec = table.getRecord(3L);
        if (rec == null) {
            System.out.println("não tem");
        } else {
            System.out.println(rec.getContent());
        }
    }

    public void testUpdate() throws Exception {
        Table table;
        table = new Table("c:\\teste\\ibd", "table.ibd");
        table.initLoad();
        Record rec = table.getRecord(3L);
        if (rec == null) {
            System.out.println("não tem");
        } else {
            rec.setContent("mudança de planos");
        }
        table.flushDB();
    }

    public void testMultipleInsertions() throws Exception {
        Table table;
        table = new Table("c:\\teste\\ibd", "table.ibd");
        table.initLoad();

        long[] array = new long[1000];
        for (int i = 0; i < array.length; i++) {
            array[i] = i;
        }

        shuffleArray(array);

        for (int i = 4; i < array.length; i++) {
            table.addRecord(array[i], "Novo registros " + array[i]);
            System.out.println(i);
        }
        table.flushDB();
    }

    public void testMultipleSearches() throws Exception {
        Table table;
        table = new Table("c:\\teste\\ibd", "table.ibd");
        table.initLoad();

        long[] array = new long[1000];
        for (int i = 0; i < array.length; i++) {
            array[i] = i;
        }

        for (int i = 0; i < array.length; i++) {
            Record rec = table.getRecord(array[i]);

            if (rec != null) {
                System.out.println(rec.getContent());
            }
        }
    }

    static void shuffleArray(long[] ar) {
        // If running on Java 6 or older, use `new Random()` on RHS here
        Random rnd = ThreadLocalRandom.current();
        for (int i = ar.length - 1; i > 0; i--) {
            int index = rnd.nextInt(i + 1);
            // Simple swap
            long a = ar[index];
            ar[index] = ar[i];
            ar[i] = a;
        }
    }

    public static void main(String[] args) {
        try {
            new Main().testCreation();
            new Main().testMultipleInsertions();
            new Main().testMultipleSearches();
        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}