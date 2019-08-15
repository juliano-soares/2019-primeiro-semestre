/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.query;

import ibd.Block;
import ibd.Record;

/**
 *
 * @author SergioI
 */
public class BlockNestedLoopJoin implements Operation {

    Operation op1;
    Operation op2;

    BlockTuple curTuple1;
    BlockTuple curTuple2;
    TableTuple nextTuple;
    
    int offset1 = 0;
    int offset2 = 0;

    public BlockNestedLoopJoin(Operation op1, Operation op2) throws Exception {
        super();
        this.op1 = op1;
        this.op2 = op2;
    }

    @Override
    public void open() throws Exception {

        op1.open();
        op2.open();
        curTuple1 = null;
        nextTuple = null;
        offset1 = 0;
        offset2 = 0;
    }

    

    @Override
    public Tuple next() throws Exception {

        if (nextTuple != null) {
            TableTuple next_ = nextTuple;
            nextTuple = null;
            return next_;
        }

        while (op1.hasNext() || curTuple1 != null) {
            if (curTuple1 == null) {
                curTuple1 = (BlockTuple) op1.next();
                if (curTuple1.block.isEmpty()) {
                        curTuple1 = null;
                        continue;
                    }
                op2.open();
                offset1 = 0;
                offset2 = 0;
            }

            while (op2.hasNext() || curTuple2 != null) {
                if (curTuple2 == null) {
                    curTuple2 = (BlockTuple) op2.next();
                    if (curTuple2.block.isEmpty()) {
                        curTuple2 = null;
                        continue;
                    }
                    offset1 = 0;
                    offset2 = 0;
                }
                while (offset1 < Block.RECORDS_AMOUNT) {
                    Record r1 = curTuple1.block.getRecord(offset1);
                    offset1++;
                    if (r1 == null) {
                        continue;
                    }
                    offset2 = 0;
                    while (offset2 < Block.RECORDS_AMOUNT) {
                        Record r2 = curTuple2.block.getRecord(offset2);
                        offset2++;
                        if (r2 == null) {
                            continue;
                        }
                        if (r1.getPrimaryKey() == r2.getPrimaryKey()) {
                            TableTuple rec = new TableTuple();
                            rec.primaryKey = r1.getPrimaryKey();
                            rec.content = r1.getContent() + " " + r2.getContent();
                            return rec;
                        }

                    }
                }
                curTuple2 = null;
            }
            curTuple1 = null;

            offset1 = 0;
            offset2 = 0;
        }

        throw new Exception("No record after this point");

    }

    @Override
    public boolean hasNext() throws Exception {

        if (nextTuple != null) {
            return true;
        }

        while (op1.hasNext() || curTuple1 != null) {
            if (curTuple1 == null) {
                curTuple1 = (BlockTuple) op1.next();

                if (curTuple1.block.isEmpty()) {
                    curTuple1 = null;
                    continue;
                }

                op2.open();
                offset1 = 0;
                offset2 = 0;
            }

            while (op2.hasNext() || curTuple2 != null) {

                if (curTuple2 == null) {
                    curTuple2 = (BlockTuple) op2.next();
                    offset1 = 0;
                    offset2 = 0;

                    if (curTuple2.block.isEmpty()) {
                        curTuple2 = null;
                        continue;
                    }
                }

                while (offset1 < Block.RECORDS_AMOUNT) {
                    Record r1 = curTuple1.block.getRecord(offset1);
                    offset1++;
                    if (r1 == null) {
                        continue;
                    }
                    offset2 = 0;
                    while (offset2 < Block.RECORDS_AMOUNT) {
                        Record r2 = curTuple2.block.getRecord(offset2);
                        offset2++;
                        if (r2 == null) {
                            continue;
                        }
                        if (r1.getPrimaryKey() == r2.getPrimaryKey()) {
                            nextTuple = new TableTuple();
                            nextTuple.primaryKey = r1.getPrimaryKey();
                            nextTuple.content = r1.getContent() + " " + r2.getContent();
                            return true;
                        }

                    }
                }
                curTuple2 = null;
            }
            curTuple1 = null;

            offset1 = 0;
            offset2 = 0;
        }

        return false;
    }

    @Override
    public void close() {

    }

}
