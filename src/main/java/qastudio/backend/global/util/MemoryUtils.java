package qastudio.backend.global.util;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;

public class MemoryUtils {

    public static void logMemoryUsage(String message) {
        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage heapMemoryUsage = memoryMXBean.getHeapMemoryUsage();
        MemoryUsage nonHeapMemoryUsage = memoryMXBean.getNonHeapMemoryUsage();

        System.out.println(message);
        System.out.println("Heap 메모리 사용량: " + (heapMemoryUsage.getUsed() / 1024 / 1024) + " MB");
        System.out.println("Non-Heap 메모리 사용량: " + (nonHeapMemoryUsage.getUsed() / 1024 / 1024) + " MB");
        System.out.println("------------------------------------------------");
    }
}