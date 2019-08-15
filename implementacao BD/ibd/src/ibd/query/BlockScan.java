/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.query;

import ibd.Table;
import ibd.Block;
import ibd.Record;

/**
 *
 * @author Sergio
 */
public class BlockScan implements Operation {

    public long currentBlock;

    BlockTuple nextTuple = null;
    Table table;

    public BlockScan(Table table) throws Exception {
        this.table = table;
    }

    @Override
    public void open() throws Exception {

        table.initLoad();
        currentBlock = -1;
        nextTuple = null;
    }

    @Override
    public Tuple next() throws Exception {

        if (nextTuple != null) {
            BlockTuple next_ = nextTuple;
            nextTuple = null;
            return next_;
        }

        currentBlock++;
        if (currentBlock == Table.BLOCKS_AMOUNT) {
            throw new Exception("No records after this point");
        }

        BlockTuple blockTuple = new BlockTuple();
        blockTuple.block = table.getBlock(currentBlock);
        return blockTuple;

    }

    @Override
    public boolean hasNext() throws Exception {

        if (nextTuple != null) {
            return true;
        }

        if (currentBlock + 1 >= Table.BLOCKS_AMOUNT) 
            return false;
        
        currentBlock++;
        nextTuple = new BlockTuple();
        nextTuple.block = table.getBlock(currentBlock);
        return true;

    }

    @Override
    public void close() {

    }

}
