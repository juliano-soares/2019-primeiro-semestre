/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trab1;

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

    public Table(String folder, String name) throws Exception {
        databaseIO = new DatabaseIO(folder, name);

    }

    public void initLoad() throws Exception {

        index.clear();
        databaseIO.loadIndex(index);

        bufferManager.clear();
        organizer.clear();
        databaseIO.loadFreeBlocks(organizer);

    }

    public Record getRecord(Long primaryKey) throws Exception {

        IndexRecord index_rec = index.getEntry(primaryKey);
        if (index_rec == null) {
            return null;
        }

        Block block = bufferManager.getBlock(index_rec.getBlockId(), databaseIO);

        //now the block contains the record
        return (Record) block.getRecord((int) (long) index_rec.getRecordId());
    }

    public boolean isFull() {
        return (organizer.getFreeBlocksCount() == 0);
    }

    public Record addRecord(long primaryKey, String content) throws Exception {

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

    private void addRecord(Block block, Record rec) throws Exception {
    
    block.addRecord(rec, -1L);

        if (block.isFull()) {
            organizer.removeFreeBlock(block.block_id);
        }

        index.addEntry(block.block_id, rec.getRecordId(), rec.getPrimaryKey());
    }
    
    public void removeRecord(Record record) throws Exception {

        Block block = record.getBlock();
        boolean wasFull = block.isFull();

        block.removeRecord(record);

        if (wasFull) {
            organizer.addFreeBlock(block.block_id);
        }

        index.removeEntry(record.getPrimaryKey());

    }

    public void flushDB() throws Exception {

        databaseIO.flushIndex(index.index);

        databaseIO.flushFreeBlocks(organizer.getFreeBlocksIds());

        databaseIO.flushBlocks(bufferManager.getBufferedBlocks());

    }

    public void createDatabase() throws Exception {
        databaseIO.createDatabase();
    }

    /**************************************************************************/
                            // IMPLEMENTAÇÃO //
    
    private void ordering (Record reg, long blockId) throws Exception {
        Block block = bufferManager.getBlock(blockId, databaseIO);
        if (!block.isFull()) {
            addRecord(block, reg);
        }else if(reg.getPrimaryKey() < block.getBiggestRecord().getPrimaryKey()) {
            Record aux = block.getBiggestRecord();
            removeRecord(block.getBiggestRecord());
            addRecord(block, reg);
            ordering(aux, blockId+1);
        } else {
            ordering(reg, blockId+1);
        }          
    }
    
    private Long selectBlock(long primaryKey) throws Exception{
        for (long i = 0; i < BLOCKS_AMOUNT; i++) {
            Block block = bufferManager.getBlock(i, databaseIO);
            if (!block.isFull()) {
                return block.block_id;
            }
            if(primaryKey < block.getBiggestRecord().getPrimaryKey()) {
                Record regTemp = block.getBiggestRecord();
                removeRecord(block.getBiggestRecord());
                ordering(regTemp, block.block_id+1);                  
                return block.block_id;
            } 
        }
        return null;
    }
    /**************************************************************************/
}