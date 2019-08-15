/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd;

import java.util.Hashtable;

public class Table {

    public final static Long BLOCKS_AMOUNT = 64L;

    //header
    public final static Long INDEX_LEN = Long.BYTES + 3 * BLOCKS_AMOUNT * Block.RECORDS_AMOUNT * Long.BYTES;
    public final static Long FREE_BLOCKS_LEN = Long.BYTES + BLOCKS_AMOUNT * Long.BYTES;
    public final static Long HEADER_LEN = INDEX_LEN + FREE_BLOCKS_LEN;

    public final static Long BD_LEN = HEADER_LEN + BLOCKS_AMOUNT * Block.BLOCK_LEN;

    protected DataOrganizer organizer = new DataOrganizer();
    protected DatabaseIO databaseIO = null;
    protected BufferManager bufferManager = new BufferManager();
    Index index = new Index();
    
    static Hashtable<String, Table> tables = new Hashtable<String, Table>();
    
    public boolean loaded = false;
    
    public String key;

    public Table() {
    }

    public static Table getTable(String folder, String name) throws Exception{
        String key = folder+"\\"+name;
        Table t = tables.get(key);
        if (t!=null) return t;
        t = new Table(folder, name);
        t.key = key;
        tables.put(key, t);
        return t;
    
    }
    public static Table getTable(String key) throws Exception{
        String folder = getTableFolder(key);
        String file = getTableFile(key);
        return getTable(folder, file);
    }
    
    
    public static String getTableFolder(String key){
        
        return key.substring(0, key.lastIndexOf("\\"));
    
    }
    
    public static String getTableFile(String key){
        
        return key.substring(key.lastIndexOf("\\"), key.length());
    
    }
    
    
    
    public Table(String folder, String name) throws Exception {
        databaseIO = new DatabaseIO(folder, name);
    }

    public void initLoad() throws Exception {

        if (loaded) return;
        index.clear();
        databaseIO.loadIndex(index);

        bufferManager.clear();
        organizer.clear();
        databaseIO.loadFreeBlocks(organizer);

        loaded = true;
        
    }

    public Block getBlock(long free_block_id) throws Exception{
        
        if (!loaded) initLoad();
        
        return bufferManager.getBlock(free_block_id, databaseIO);
    }
    
    public Record getRecord(Long primaryKey) throws Exception {

        if (!loaded) initLoad();
        
        IndexRecord index_rec = index.getEntry(primaryKey);
        if (index_rec == null) {
            return null;
        }

        Block block = getBlock(index_rec.getBlockId());

        //now the block contains the record
        return (Record) block.getRecord((int) (long) index_rec.getRecordId());
    }

    public boolean isFull() throws Exception{
        
        if (!loaded) initLoad();
        
        return (organizer.getFreeBlocksCount() == 0);
    }

    public Record addRecord(long primaryKey, String content) throws Exception {

        if (!loaded) initLoad();
        
        if (index.getEntry(primaryKey) != null) {
            throw new Exception("ID already exists");
        }

        if (isFull()) {
            throw new Exception("No Space");
        }

        Record rec = new CreatedRecord(primaryKey);
        rec.setContent(content);

        Long free_block_id = selectBlock(primaryKey);
        Block block = bufferManager.getBlock(free_block_id, databaseIO);

        addRecord(block, rec);

        return rec;

    }
    
    public Record updateRecord(long primaryKey, String content) throws Exception {

        if (!loaded) initLoad();
        
        IndexRecord index_rec = index.getEntry(primaryKey);
        if (index_rec == null) {
            return null;
        }

        Block block = getBlock(index_rec.getBlockId());
        Record rec = (Record) block.getRecord((int) (long) index_rec.getRecordId());
        //now the block contains the updated record
        rec.setContent(content);
        return rec;

    }

    
    
    private void addRecord(Block block, Record rec) throws Exception {
    
        if (!loaded) initLoad();
        
    block.addRecord(rec, -1L);

        if (block.isFull()) {
            organizer.removeFreeBlock(block.block_id);
        }

        index.addEntry(block.block_id, rec.getRecordId(), rec.getPrimaryKey());
    }
    
    
    private Long selectBlock(long primaryKey) {

        return organizer.selectBLock();
    }

    private Long selectBlock_(long primaryKey) throws Exception {

        IndexRecord ir = getLargestSmallerKey(primaryKey);
        Block b = null;
        if (ir != null) {
            b = bufferManager.getBlock(ir.getBlockId(), databaseIO);
        } else {
            b = bufferManager.getBlock(0L, databaseIO);
        }

        if (b.isFull()) {

            Record r = findLargest(b);

            removeRecord(r);

            recursiveSlide(b, r);
            return b.block_id;
        }
        return b.block_id;
    }

        private IndexRecord getLargestSmallerKey(long primaryKey) {
        IndexRecord ir = null;
        for (long i = primaryKey; i >= 0; i--) {
            ir = index.getEntry(i);
            if (ir != null) {
                break;
            }
        }
        return ir;
    }

    
    private void recursiveSlide(Block b, Record rec) throws Exception {

        Long next = b.block_id + 1;
        Block b2 = bufferManager.getBlock(next, databaseIO);

        if (b2.isFull()) {
            Record r2 = findLargest(b2);
            removeRecord(r2);

            addRecord(b2, rec);

            recursiveSlide(b2, r2);

        } else {
            addRecord(b2, rec);

        }
    }

    private Record findLargest(Block b) throws Exception {

        Long max = -1L;
        Record maxR = null;
        for (int x = 0; x < b.getRecordsCount(); x++) {
            Record rec = b.getRecord(x);
            if (rec.getPrimaryKey() > max) {
                max = rec.getPrimaryKey();
                maxR = rec;
            }
        }
        return maxR;
    }


    
    public void removeRecord(Record record) throws Exception {

        if (!loaded) initLoad();
        
        Block block = record.getBlock();
        boolean wasFull = block.isFull();

        block.removeRecord(record);

        if (wasFull) {
            organizer.addFreeBlock(block.block_id);
        }

        index.removeEntry(record.getPrimaryKey());

    }

    public void flushDB() throws Exception {

        if (!loaded) return;
        
        databaseIO.flushIndex(index.index);

        databaseIO.flushFreeBlocks(organizer.getFreeBlocksIds());

        databaseIO.flushBlocks(bufferManager.getBufferedBlocks());

    }

    public void createDatabase() throws Exception {
        databaseIO.createDatabase();
    }

}
