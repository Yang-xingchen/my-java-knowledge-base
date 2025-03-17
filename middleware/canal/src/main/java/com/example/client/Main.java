package com.example.client;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.Message;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) throws Exception {
        CanalConnector connector = CanalConnectors.newSingleConnector(
                new InetSocketAddress("192.168.31.201", 11111),
                "example",
                "canal",
                "canal"
        );
        Runtime.getRuntime().addShutdownHook(new Thread(connector::disconnect));
        connector.connect();
        connector.subscribe();
        while (true) {
            Message message = connector.getWithoutAck(1);
            handel(message);
            connector.ack(message.getId());
            TimeUnit.SECONDS.sleep(1);
        }
    }

    private static void handel(Message message) throws Exception {
        for (CanalEntry.Entry entry : message.getEntries()) {
            CanalEntry.EntryType entryType = entry.getEntryType();
            CanalEntry.RowChange rowChange = CanalEntry.RowChange.parseFrom(entry.getStoreValue());
            System.out.println("===== " + entryType + ":" + entry.getHeader().getTableName() + " =====");
            List<CanalEntry.RowData> datasList = rowChange.getRowDatasList();
            System.out.println(rowChange.getEventType());
            for (CanalEntry.RowData rowData : datasList) {
                System.out.println("Before: " + rowData.getBeforeColumnsList().stream().map(column -> column.getName() + ":" + column.getValue()).collect(Collectors.joining(", ")));
                System.out.println("After: " + rowData.getAfterColumnsList().stream().map(column -> column.getName() + ":" + column.getValue()).collect(Collectors.joining(", ")));
            }
        }
    }

}
