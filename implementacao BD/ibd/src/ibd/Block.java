/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ibd;

import java.util.TreeSet;

/**
 *
 * @author pccli
 */
public class Block {

    public final static Long RECORDS_AMOUNT = 32L;
    
    public final static Long HEADER_LEN = Long.BYTES + RECORDS_AMOUNT * Long.BYTES;
    
    public final static Long BLOCK_LEN = HEADER_LEN + RECORDS_AMOUNT * Record.RECORD_SIZE;
    
    
    public Long block_id;
    
    TreeSet<Long> freeRecords = new TreeSet<Long>();
    private Record records[] = new Record[(int)(long)RECORDS_AMOUNT];
    
    
    
    
    
    public Block(Long block_id){
    
        this.block_id = block_id;
        clearRecords();
    }
    
    
    public boolean hasChanged()
    {
        for (int i = 0; i < records.length; i++) {
            if (records[i]!=null && records[i].changed)
                return true;
        }
        return false;
    }
    
    private void clearRecords(){
        freeRecords.clear();
        for (int i = 0; i < RECORDS_AMOUNT; i++) {
            freeRecords.add(new Long(i));
        }
    }
    
    
    
    public boolean isFull()
    {
    return freeRecords.size()==0;
    }
    
    public boolean isEmpty()
    {
    return freeRecords.size()==RECORDS_AMOUNT;
    }
    
    public Record getRecord(int recId){
        return records[(int)(long)recId];
    }
    
    public int getRecordsCount(){
        return records.length;
    }
    
    public Record addRecord(Record rec, Long rec_id) throws Exception{
        
    if (rec_id==-1){
        rec_id = freeRecords.first();
    }
    
    rec.setRec_id(rec_id);
    rec.setBlock(this);
    records[(int)(long)rec_id] = rec;

    freeRecords.remove(rec_id);

    //System.out.println("adding record to "+block_id+" now contains free records = "+freeRecords.size());
    
    return rec;
    
    }
    
    public void removeRecord(Record rec) throws Exception{
        
        if (records[(int)(long)rec.getRecordId()]!=rec)
            System.out.println("sdsds");
        
    records[(int)(long)rec.getRecordId()] = null;
    freeRecords.add(rec.getRecordId());
    
    System.out.println("removing record from "+block_id+" now contains free records = "+freeRecords.size());
    
    }

    
    
        
    public Record maxPrimaryKey(){
        Record max = records[0];
        
        for(int i=0;i<records.length;i++)
        {
            if(records[i].getPrimaryKey() > max.getPrimaryKey())
            {
                max = records[i];
            }
        }
        
        return max;
    }

    
    
    
    
}
