/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package trab1;

import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import static trab1.Table.BD_LEN;
import static trab1.Table.BLOCKS_AMOUNT;
import static trab1.Table.HEADER_LEN;
import static trab1.Table.INDEX_LEN;

/**
 *
 * @author pccli
 */
public class DatabaseIO {
    
    RandomAccessFile bd;
    
    public DatabaseIO(String folder, String name) throws Exception{
        
        Path path = Paths.get(folder);
        Files.createDirectories(path);
            
        bd = new RandomAccessFile(folder+"\\"+name, "rw");
    }
    
    
    public void createDatabase() throws Exception {

        bd.setLength(BD_LEN);

        //index
        bd.seek(0);
        bd.writeLong(0);

        //freeBlocks
        bd.seek(INDEX_LEN);
        bd.writeLong(BLOCKS_AMOUNT);
        for (long i = 0; i < BLOCKS_AMOUNT; i++) {
            bd.writeLong(i);

        }

        //save blocks
        for (long i = 0; i < BLOCKS_AMOUNT; i++) {

            //save block header
            Long block_id = i;
            Long blockOffset = HEADER_LEN + Block.BLOCK_LEN * block_id;
            bd.seek(blockOffset);
            bd.writeBoolean(false);
        }

    }
    
    
    public void loadIndex(Index index) throws Exception{
    index.clear();
        bd.seek(0);

        //load index
        Long len = bd.readLong();
        for (int i = 0; i < len; i++) {
            Long blockId = bd.readLong();
            Long recordId = bd.readLong();
            Long primaryKey = bd.readLong();
            index.addEntry(blockId, recordId, primaryKey);
        }
    }
    
    public void loadFreeBlocks(DataOrganizer organizer) throws Exception{
    //load freeBlocks
        bd.seek(INDEX_LEN);
        organizer.freeBlocks.clear();
        Long len = bd.readLong();
        for (int i = 0; i < len; i++) {
            Long freeBlock = bd.readLong();
            organizer.freeBlocks.add(freeBlock);
        }
    }
    
    public void loadBlock(Block block, Long block_id) throws Exception{
    
    //start read
        Long blockOffset = HEADER_LEN + Block.BLOCK_LEN * block_id;
        bd.seek(blockOffset);

        //check if block has at least one record
        Boolean used = bd.readBoolean();
        if (!used) {
            for (long i = 0; i < Block.RECORDS_AMOUNT; i++) {
                block.freeRecords.add(i);
            }
            return;
        }

        //load block headder
        Long len = bd.readLong();
        ArrayList<Long> freeRecords = new ArrayList<>();
        for (int i = 0; i < len; i++) {
            Long freeRecord = bd.readLong();
            freeRecords.add(freeRecord);
        }

        //load records
        for (long rec_id = 0; rec_id < Block.RECORDS_AMOUNT; rec_id++) {
            if (freeRecords.contains(rec_id)) {
                continue;
            }
            loadRecord(block, rec_id);

        }
        
    }
    
    private void loadRecord(Block block, long rec_id) throws Exception {
        Long blockOffset = HEADER_LEN + Block.BLOCK_LEN * block.block_id;
        bd.seek(blockOffset + Block.HEADER_LEN + Record.RECORD_SIZE * rec_id);
        Long primaryKey = bd.readLong();
        String content = bd.readUTF();
        Record rec = new LoadedRecord(primaryKey);
        rec.setContent(content);

        block.addRecord(rec, rec_id);

        //block.records.put(rec.getRec_id(), rec);
        //index.put(rec.getContent_id(), rec);
    }
    
    
    public void flushIndex(Hashtable<Long, IndexRecord> index) throws Exception{
    bd.seek(0);
        bd.writeLong(index.size());

        Iterator<IndexRecord> i = index.values().iterator();
        while (i.hasNext()) {
            IndexRecord record = i.next();
            bd.writeLong(record.getBlockId());
            bd.writeLong(record.getRecordId());
            bd.writeLong(record.getPrimaryKey());
        }
    }
    
    public void flushFreeBlocks(Long[] ids)throws Exception{
    
    bd.seek(INDEX_LEN);
        bd.writeLong(ids.length);
        for (int i = 0; i < ids.length; i++) {
            bd.writeLong(ids[i]);
        }
        
    }
    
    public void flushBlocks(Block[] blocks) throws Exception{
        for (int i = 0; i < blocks.length; i++) {
            flushBlock(blocks[i]);
        }
    }
    
    public void flushBlock(Block block) throws Exception {

        if (!block.hasChanged()) {
            return;
        }

        //save block header
        Long blockOffset = HEADER_LEN + Block.BLOCK_LEN * block.block_id;
        bd.seek(blockOffset);
        bd.writeBoolean(true);
        bd.writeLong(block.freeRecords.size());
        for (Long freeBlock : block.freeRecords) {
            bd.writeLong(freeBlock);
        }

        //save record
        for (int x = 0; x < block.getRecordsCount(); x++) {
            Record rec_ = block.getRecord(x);
            if (rec_==null) continue;
            bd.seek(blockOffset + Block.HEADER_LEN + rec_.getRecordId() * Record.RECORD_SIZE);
            bd.writeLong(rec_.getPrimaryKey());
            bd.writeUTF(rec_.getContent());
        }

    }

}
