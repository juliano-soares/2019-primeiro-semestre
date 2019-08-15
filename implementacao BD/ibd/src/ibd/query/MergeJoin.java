/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.query;

/**
 *
 * @author SergioI
 */
public class MergeJoin  implements Operation{

    
    
    Operation op1;
    Operation op2;
    
    TableTuple curTuple1;
    TableTuple curTuple2;
    TableTuple nextTuple;

    public MergeJoin(Operation op1, Operation op2) throws Exception {
        super();
        this.op1 = op1;
        this.op2 = op2;
    }
    
    @Override
    public void open() throws Exception{
        
        op1.open();
        op2.open();
        curTuple1=null;
        curTuple2=null;
        nextTuple = null;
        
    }

    @Override
    public Tuple next() throws Exception{
        
        
        if (nextTuple!=null){
            Tuple next_ = nextTuple;
            nextTuple = null;
            return next_;
        }
        
        if (curTuple1==null){
            if (!op1.hasNext())
                    throw new Exception("no more data");
            else curTuple1 = (TableTuple)op1.next();
        }
        
        if (curTuple2==null){
            if (!op2.hasNext())
                    throw new Exception("no more data");
            else curTuple2 = (TableTuple)op2.next();
        }
        
        while (true){
            
            if (curTuple1.primaryKey==curTuple2.primaryKey){
                TableTuple rec = new TableTuple();
                rec.primaryKey = curTuple1.primaryKey;
                rec.content = curTuple1.content+" "+curTuple2.content;
                curTuple1 = null;
                curTuple2 = null;
                return rec;
            }
            else if (curTuple1.primaryKey<curTuple2.primaryKey){
                if (!op1.hasNext())
                    break;
                curTuple1 = (TableTuple)op1.next();
            }
            else 
            {
                if (!op2.hasNext())
                    break;
                curTuple2 = (TableTuple)op2.next();
            }
                
        }
        throw new Exception("No more data");
        
    }
    
    
    @Override
    public boolean hasNext() throws Exception{
    
        if (curTuple1==null){
            if (!op1.hasNext())
                    return false;
            else curTuple1 = (TableTuple)op1.next();
        }
        
        if (curTuple2==null){
            if (!op2.hasNext())
                    return false;
            else curTuple2 = (TableTuple)op2.next();
        }
        
        
        while (true){
            
            if (curTuple1.primaryKey==curTuple2.primaryKey){
                nextTuple = new TableTuple();
                nextTuple.primaryKey = curTuple1.primaryKey;
                nextTuple.content = curTuple1.content+" "+curTuple2.content;
                curTuple1 = null;
                curTuple2 = null;
                return true;
            }
            else if (curTuple1.primaryKey<curTuple2.primaryKey){
                if (!op1.hasNext())
                    break;
                curTuple1 = (TableTuple)op1.next();
            }
            else 
            {
                if (!op2.hasNext())
                    break;
                curTuple2 = (TableTuple)op2.next();
            }
                
        }
        return false;
    }

    @Override
    public void close() {
        
    }
    
}
