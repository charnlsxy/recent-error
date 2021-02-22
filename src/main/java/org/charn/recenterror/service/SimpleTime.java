package org.charn.recenterror.service;

import org.charn.recenterror.utl.IdProducer;
import org.springframework.stereotype.Component;

@Component
public class SimpleTime implements IdProducer {

    private final long workerId;

    private final long datacenterId;

    private long sequence = 0L;

    private long lastTimestamp = -1L;

    public SimpleTime() {
        this.workerId = 1;
        this.datacenterId = 1;
    }

    public synchronized long nextId() {
        long timestamp = timeGen();

        //如果当前时间小于上一次ID生成的时间戳，说明系统时钟回退过这个时候应当抛出异常
        if (timestamp < lastTimestamp) {
            throw new RuntimeException(
                    String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
        }

        //如果是同一时间生成的，则进行毫秒内序列
        long sequenceBits = 3L;
        if (lastTimestamp == timestamp) {
            long sequenceMask = ~(-1L << sequenceBits);
            sequence = (sequence + 1) & sequenceMask;
            //毫秒内序列溢出
            if (sequence == 0) {
                //阻塞到下一个毫秒,获得新的时间戳
                timestamp = tilNextMillis(lastTimestamp);
            }
        }
        //时间戳改变，毫秒内序列重置
        else {
            sequence = 0L;
        }

        //上次生成ID的时间截
        lastTimestamp = timestamp;

        //移位并通过或运算拼到一起组成64位的ID
        long workerIdBits = 1L;
        long datacenterIdShift = sequenceBits + workerIdBits;
        long datacenterIdBits = 1L;
        long timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits;
        long twepoch = 1612837002222L;
        return ((timestamp - twepoch) << timestampLeftShift) //
                | (datacenterId << datacenterIdShift) //
                | (workerId << sequenceBits) //
                | sequence;
    }


    /**
     * 阻塞到下一个毫秒，直到获得新的时间戳
     * @param lastTimestamp 上次生成ID的时间截
     * @return 当前时间戳
     */
    protected long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    /**
     * 返回以毫秒为单位的当前时间
     * @return 当前时间(毫秒)
     */
    protected long timeGen() {
        return System.currentTimeMillis();
    }


    @Override
    public int newId() {
        return (int) nextId();
    }

}
