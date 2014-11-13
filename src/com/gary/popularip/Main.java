package com.gary.popularip;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class Main {

    static long numberOfIPsToGenerate = 0xffffffl;
    static String ipDir = "/home/gary/ip_test/";
    static String ipTmpDir = "/home/gary/ip_test/tmp/";
    static String ipFileName = ipDir + "ipFile.txt";

    public static void main(String[] args) throws Exception {
        // generate a file containing the IPs
        long genStart = System.currentTimeMillis();

        generateIPFile(numberOfIPsToGenerate);

        long genEnd = System.currentTimeMillis();

        System.out.println("Generation time elapsed(in seconds): " + (genEnd - genStart) / 1000);

        // start the process to find the most popular IP
        long start = System.currentTimeMillis();

        segmentToSmallFiles();

        findMostPopularIp();

        long end = System.currentTimeMillis();

        System.out.println("Total time elapsed(in seconds): " + (end - start) / 1000);
    }

    static void findMostPopularIp() throws IOException {
        long mostPopularIP = 0l;
        long mostVisitTimes = 0l;
        for (int i = 0; i < 256; i++) {
            long result[] = findMostPopularIpInSegment(i);
            if (mostVisitTimes < result[1]) {
                mostPopularIP = result[0];
                mostVisitTimes = result[1];
            }
        }
        System.out.println("mostPopularIP: " + formatIp(mostPopularIP) + ", mostVisitTimes: " + mostVisitTimes);
    }

    static long[] findMostPopularIpInSegment(int index) throws IOException {
        File file = new File(ipTmpDir + "file" + index + ".txt");

        long ipRecords[] = new long[0xffffff + 1];
        for (int i = 0; i <= 0xffffff; i++) {
            ipRecords[i] = 0;
        }

        BufferedReader br = new BufferedReader(new FileReader(file));
        try {
            String ipString = br.readLine();
            while (ipString != null) {
                int dotted[] = getDottedIpValues(ipString);
                int suffixedIp = (dotted[1] << 16) + (dotted[2] << 8) + dotted[3];
                ipRecords[suffixedIp]++;
                ipString = br.readLine();
            }
        } finally {
            br.close();
        }

        long theIp = 0;
        long theTimes = 0;
        for (int i = 0; i <= 0xffffff; i++) {
            if (theTimes < ipRecords[i]) {
                theIp = (((long) (index)) << 24) + i;
                theTimes = ipRecords[i];
            }
        }

        return new long[] { theIp, theTimes };
    }

    static int[] getDottedIpValues(String ipString) {
        String splitted[] = ipString.split("\\.");
        int dotted[] = new int[4];
        for (int i = 0; i < 4; i++) {
            dotted[i] = Integer.parseInt(splitted[i]);
        }
        return dotted;
    }

    static void segmentToSmallFiles() throws Exception {
        File tmpDir = new File(ipTmpDir);
        if (tmpDir.exists()) {
            File[] files = tmpDir.listFiles();
            for (File file : files) {
                file.delete();
            }
        }

        Map<String, PrintWriter> tmpWriters = new HashMap<String, PrintWriter>();
        for (int i = 0; i < 256; i++) {
            File file = new File(ipTmpDir + "file" + i + ".txt");
            file.createNewFile();
            tmpWriters.put(String.valueOf(i), new PrintWriter(file));
        }
        try {
            BufferedReader br = new BufferedReader(new FileReader(new File(ipFileName)));
            try {
                String ipString = br.readLine();
                while (ipString != null) {
                    String index = ipString.split("\\.")[0];
                    tmpWriters.get(index).println(ipString);
                    ipString = br.readLine();
                }
            } finally {
                br.close();
            }
        } finally {
            for (PrintWriter pw : tmpWriters.values()) {
                pw.close();
            }
        }
    }

    static void generateIPFile(long size) throws Exception {
        File file = new File(ipFileName);
        if (file.exists()) {
            file.delete();
        }

        file.createNewFile();
        PrintWriter pw = new PrintWriter(file);
        try {
            for (long i = 0; i < size; i++) {
                pw.println(generateOneRandomIP());
            }
        } finally {
            pw.close();
        }
    }

    static String generateOneRandomIP() {
        return formatIp((long) (0xffffffffl * Math.random()));
    }

    static String formatIp(long ip) {
        StringBuffer buffer = new StringBuffer();
        buffer.append((ip & 0xff000000l) >>> 24).append(".");
        buffer.append((ip & 0xff0000l) >>> 16).append(".");
        buffer.append((ip & 0xff00l) >>> 8).append(".");
        buffer.append(ip & 0xffl);
        return buffer.toString();
    }
}
