/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 *
 * @author Sergio
 */
public class OrderedScan implements Operation {

    
    Operation op;

    TableTuple nextTuple = null;
    
    int currentIndex = -1;
    
    ArrayList<TableTuple> tt;
    
    boolean opened = false;

    public OrderedScan(Operation op) throws Exception {
        this.op = op;
    }

    @Override
    public void open() throws Exception {
        currentIndex = -1;
        
        if (opened) return;
        
        tt = new ArrayList<>();
        op.open();
        while (op.hasNext()){
            TableTuple tuple = (TableTuple)op.next();
            tt.add(tuple);
            
        }
        
        Collections.sort(tt, new TableTupleComparator() );
        
        opened = true;
        
    }
    
    public class TableTupleComparator implements Comparator<TableTuple> {
  
    @Override
    public int compare(TableTuple tt1, TableTuple tt2) {
       return Long.compare(tt1.primaryKey, tt2.primaryKey);
    }
}

    @Override
    public Tuple next() throws Exception {

        if (nextTuple != null) {
            TableTuple next_ = nextTuple;
            nextTuple = null;
            return next_;
        }

        currentIndex++;
        if (currentIndex == tt.size()) {
            throw new Exception("No records after this point");
        }

        
        return tt.get(currentIndex);

        
    }

    @Override
    public boolean hasNext() throws Exception {

        if (nextTuple != null) 
            return true;
        
        if (currentIndex+1 >= tt.size())
            return false;
        
        currentIndex++;
        nextTuple = tt.get(currentIndex);
        return true;
    }

    @Override
    public void close() {

    }

}
