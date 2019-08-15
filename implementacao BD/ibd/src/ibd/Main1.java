/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd;

import static ibd.Utils.createTable;
import ibd.query.BlockNestedLoopJoin;
import ibd.query.BlockScan;
import java.util.logging.Level;
import java.util.logging.Logger;
import ibd.query.MergeJoin;
import ibd.query.NestedLoopJoin;
import ibd.query.Operation;
import ibd.query.OrderedScan;
import ibd.query.TableTuple;
import ibd.query.TableScan;

/**
 *
 * @author Sergio
 */
public class Main1 {

    
    public void testCreation() throws Exception {
        Table table;
        //table = new Table("c:\\teste\\ibd", "table.ibd");
        table = Table.getTable("c:\\teste\\ibd", "table.ibd");
        table.createDatabase();
    }

    public void testLoad() throws Exception {
        Table table;
        //table = new Table("c:\\teste\\ibd", "table.ibd");
        table = Table.getTable("c:\\teste\\ibd", "table.ibd");
        table.initLoad();
    }

    public void testInsertion() throws Exception {
        Table table;
        //table = new Table("c:\\teste\\ibd", "table.ibd");
        table = Table.getTable("c:\\teste\\ibd", "table.ibd");
        table.initLoad();
        Record rec = table.addRecord(3, "lddldf fdçonfd faofhdofh odhfod ofsdf");
        table.flushDB();

    }

    public void testSearch() throws Exception {
        Table table;
        //table = new Table("c:\\teste\\ibd", "table.ibd");
        table = Table.getTable("c:\\teste\\ibd", "table.ibd");
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
        //table = new Table("c:\\teste\\ibd", "table.ibd");
        table = Table.getTable("c:\\teste\\ibd", "table.ibd");
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
        //table = new Table("c:\\teste\\ibd", "table.ibd");
        table = Table.getTable("c:\\teste\\ibd", "table.ibd");
        table.initLoad();

        long[] array = new long[1000];
        for (int i = 0; i < array.length; i++) {
            array[i] = i;
        }

        Utils.shuffleArray(array);

        for (int i = 4; i < array.length; i++) {
            table.addRecord(array[i], "Novo registros " + array[i]);
            System.out.println(i);
        }
        table.flushDB();
    }

    public void testMultipleSearches() throws Exception {
        Table table;
        //table = new Table("c:\\teste\\ibd", "table.ibd");
        table = Table.getTable("c:\\teste\\ibd", "table.ibd");
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
    
    public void testSimpleQuery() throws Exception {
        Table table = createTable("c:\\teste\\ibd","t1.ibd",1000, true, 1);
        Operation scan = new TableScan(table);
        
        scan.open();
        while (scan.hasNext()){
            TableTuple r = (TableTuple)scan.next();
            System.out.println(r.primaryKey + " - "+r.content);
        }

    }
    
    public void testOrderedQuery() throws Exception {
        Table table = createTable("c:\\teste\\ibd","t1.ibd",1000, true, 1);
        //Operation table = new OrderedSource(new TableSource(new Table("c:\\teste\\ibd", "t1.ibd")));
        //Operation scan = new OrderedScan(new TableScan(table));
        Operation scan = new OrderedScan(new TableScan(table));
        
        scan.open();
        while (scan.hasNext()){
            TableTuple r = (TableTuple)scan.next();
            System.out.println(r.primaryKey + " - "+r.content);
        }

    }
    
    public void testMultipleJoinsQuery() throws Exception {
        
        Table table1 = createTable("c:\\teste\\ibd","t1.ibd",1000, false, 1);
        Table table2 = createTable("c:\\teste\\ibd","t2.ibd",1000, false, 1);
        Table table3 = createTable("c:\\teste\\ibd","t3.ibd",1000, false, 1);
        
        Operation scan1 = new TableScan(table1);
        Operation scan2 = new TableScan(table2);
        Operation scan3 = new TableScan(table3);
        
        Operation join1 = new NestedLoopJoin(scan1, scan2);
        Operation join2 = new NestedLoopJoin(join1, scan3);
        
        join2.open();
        while (join2.hasNext()){
            TableTuple r = (TableTuple)join2.next();
            System.out.println(r.primaryKey + " - "+r.content);
        }

    }
    
    public void testNestedLoopJoinQuery() throws Exception {
        
        Table table1 = createTable("c:\\teste\\ibd","t1.ibd",1000, false, 1);
        Table table2 = createTable("c:\\teste\\ibd","t2.ibd",1000, false, 1);
        
        Operation scan1 = new TableScan(table1);
        Operation scan2 = new TableScan(table2);
        
        Operation join1 = new NestedLoopJoin(scan1, scan2);
        
        join1.open();
        while (join1.hasNext()){
            TableTuple r = (TableTuple)join1.next();
            System.out.println(r.primaryKey + " - "+r.content);
        }

    }
    
    public void testBlockNestedLoopJoinQuery() throws Exception {
        
        Table table1 = createTable("c:\\teste\\ibd","t1.ibd",1000, true, 1);
        Table table2 = createTable("c:\\teste\\ibd","t2.ibd",1000, true, 1);
        
        Operation scan1 = new BlockScan(table1);
        Operation scan2 = new BlockScan(table2);
        
        Operation join1 = new BlockNestedLoopJoin(scan1, scan2);
        
    }
    
    public void testMergeJoinQuery1() throws Exception {
        
        Table table1 = createTable("c:\\teste\\ibd","t1.ibd",1000, false, 1);
        Table table2 = createTable("c:\\teste\\ibd","t2.ibd",1000, false, 50);
        
        Operation scan1 = new TableScan(table1);
        Operation scan2 = new TableScan(table2);
        
        Operation join1 = new MergeJoin(scan1, scan2);
        
        
        join1.open();
        while (join1.hasNext()){
            TableTuple r = (TableTuple)join1.next();
            System.out.println(r.primaryKey + " - "+r.content);
        }

    }
    
    public void testMergeJoinQuery2() throws Exception {
        
        Table table1 = createTable("c:\\teste\\ibd","t1.ibd",1000, true, 1);
        Table table2 = createTable("c:\\teste\\ibd","t2.ibd",1000, true, 50);
        
        Operation scan1 = new OrderedScan(new TableScan(table1));
        Operation scan2 = new OrderedScan(new TableScan(table2));
        
        Operation join1 = new MergeJoin(scan1, scan2);
        
        join1.open();
        while (join1.hasNext()){
            TableTuple r = (TableTuple)join1.next();
            System.out.println(r.primaryKey + " - "+r.content);
        }

    }
    
    
    public void testMultipleMergeJoinQuery() throws Exception {
        Table table1 = createTable("c:\\teste\\ibd","table1.ibd",1000, false, 1);
        Table table2 = createTable("c:\\teste\\ibd","table2.ibd",1000, false, 50);
        Table table3 = createTable("c:\\teste\\ibd","table3.ibd",1000, false, 30);
        
        
         Operation scan1 = new TableScan(table1);
         //Operation scan1 = new OrderedSource(new TableSource(table1));
         Operation scan2 = new TableScan(table2);
         //Operation scan2 = new OrderedSource(new TableSource(table2));
         
         Operation scan3 = new TableScan(table3);
         //Operation scan3 = new OrderedSource(new TableSource(table3));
         Operation join1 = new MergeJoin(scan1, scan2);
        Operation join2 = new MergeJoin(scan3, join1);
        
        
        join2.open();
        while (join2.hasNext()){
            TableTuple r = (TableTuple)join2.next();
            System.out.println(r.primaryKey + " - "+r.content);
        }
        
        
    }
    
    

    public static void main(String[] args) {
        try {
            Main1 m = new Main1();
            //m.testCreation();
            //m.testMultipleInsertions();
            //m.testMultipleSearches();
            //m.testIterator();
            //m.testJoin();
            //m.testOrderedQuery();
            //m.testMergeJoinQuery1();
            //m.testMergeJoinQuery2();
            m.testMultipleMergeJoinQuery();
             
            
        } catch (Exception ex) {
            Logger.getLogger(Main1.class.getName()).log(Level.SEVERE, null, ex);
        }
    } 
}
