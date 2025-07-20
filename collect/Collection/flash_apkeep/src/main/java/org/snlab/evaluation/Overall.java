package org.snlab.evaluation;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

import org.jgrapht.alg.util.Pair;
import org.snlab.evaluation.others.APVerifier;
import org.snlab.evaluation.others.AtomVerifier;
import org.snlab.evaluation.others.Checker;
import org.snlab.flash.ModelManager.ConflictFreeChanges;
import org.snlab.flash.ModelManager.InverseModel;
import org.snlab.flash.ModelManager.Ports.ArrayPorts;
import org.snlab.flash.ModelManager.Ports.PersistentPorts;
import org.snlab.flash.ModelManager.Ports.Ports;
import org.snlab.network.Network;
import org.snlab.network.Port;
import org.snlab.network.Rule;
import org.snlab.networkLoader.FuXiNetwork;
import org.snlab.networkLoader.I2Network;
import org.snlab.networkLoader.LNetNetwork;

public class Overall {
    private static final double byte2MB = 1024L * 1024L;
    private static final boolean testDeletion = true;
    private static final int warmupRepeat = 0, testRepeat = 1;
    private static boolean omit = true;

    private static double memoryBefore, ratio;

    public static void breakdown() {
        Network network = I2Network.getNetwork().setName("Internet2");
        apkeep(network, new ArrayPorts(), true);
        seq(network, false);
        seq(network, true);
    }

    // In this function, each line is a setting that cannot be finished within 1 hour
    public static void dead() {
        String ans;
        Scanner scanner = new Scanner(System.in);

        // Table 3
        Overall.omit = false;

        System.out.println("1. Try APKeep* on LNet1 Subspace? (y/n)");
        ans = scanner.nextLine();
        if (ans.equals("y")) {
            Network network = LNetNetwork.getLNET1().setName("LNet1");
            network.filterIntoSubsapce(1L << 24, ((1L << 8) - 1) << 24);
            evaluateOnSnapshot(network, false, true, false);
        }

        // Figure 6: settings w/o subspace
        System.out.println("2. Try Deltanet* (w/o subspace) on LNet1? (y/n)");
        ans = scanner.nextLine();
        if (ans.equals("y")) {
            evaluateOnSnapshot(LNetNetwork.getLNET1().setName("LNet1"), true, false, false);
        }

        System.out.println("3. Try Deltanet* (w/o subspace) on LNet*? (y/n)");
        ans = scanner.nextLine();
        if (ans.equals("y")) {
            evaluateOnSnapshot(LNetNetwork.getLNETStar().setName("LNet*"), true, false, false);
        }

        System.out.println("4. Try APKeep* (w/o subspace) on LNet1? (y/n)");
        ans = scanner.nextLine();
        if (ans.equals("y")) {
            evaluateOnSnapshot(LNetNetwork.getLNET1().setName("LNet1"), false, true, false);
        }

        System.out.println("5. Try APKeep* (w/o subspace) on LNet*? (y/n)");
        ans = scanner.nextLine();
        if (ans.equals("y")) {
            evaluateOnSnapshot(LNetNetwork.getLNETStar().setName("LNet*"), false, true, false);
        }
    }

    public static void run() {

        // Network network = LNetNetwork.getLNET().setName("LNet0");
        // network.filterIntoSubsapce(1L << 24, ((1L << 8) - 1) << 24);
        // evaluateOnSnapshot(network);
        // network = null;
        // System.gc();

        // network = LNetNetwork.getLNET1().setName("LNet1");
        // network.filterIntoSubsapce(1L << 24, ((1L << 8) - 1) << 24);
        // evaluateOnSnapshot(network);
        // network = null;
        // System.gc();

        // network = LNetNetwork.getLNETStar().setName("LNet*");
        // network.filterIntoSubsapce(1L << 24, ((1L << 8) - 1) << 24);
        // evaluateOnSnapshot(network);
        // network = null;
        // System.gc();

        // try {
        //     evaluateOnUpdatesSequence(Airtel1Network.getNetwork().setName("Airtel1"));
        // } catch (IOException e) {
        //     e.printStackTrace();
        // }

        System.out.println("ishehererererer");
        evaluateOnSnapshot(FuXiNetwork.getNetwork().setName("FuXi"));
        // evaluateOnSnapshot(FattreeNetwork.getNetwork().setName("Fattree"));
        // evaluateOnSnapshot(EnterpriseNetwork.getNetwork().setName("Enterprise"));
        // evaluateOnSnapshot(StanfordNetwork.getNetwork().setName("Stanford"));
        // evaluateOnSnapshot(I2Network.getNetwork().setName("Internet2"));
        System.gc();
    }

    private static void printLog(String filename, String networkInfo) {
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(filename, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert fileWriter != null;
        PrintWriter printWriter = new PrintWriter(fileWriter);
        printWriter.println();
        printWriter.println();
        printWriter.println();
        printWriter.println(networkInfo);
        printWriter.println("============= Delta-net*, APKeep*, Flash");
        ratio = 1e9  * (testDeletion ? 2 : 1) * testRepeat;
        printWriter.println(" Total time: " + (s1 / ratio) + ", " + (s2 / ratio) + ", " + (s3 / ratio) + " s.");
        double repeats = warmupRepeat + testRepeat, operationRatio = 1e5 * repeats;
        printWriter.println(" Operations: " + (t1 / operationRatio) + ", " + (t2 / operationRatio) + ", " + (t3 / operationRatio) + " 1e5.");
        printWriter.println(" Memory Usage: " + (m1 / repeats) + ", " + (m2 / repeats) + ", " + (m3 / repeats) + " Mb.");
        printWriter.close();
    }

    private static double s1, s2, s3, s4;
    private static double t1, t2, t3, t4;
    private static double m1, m2, m3, m4;

    // Table 3
    public static void evaluateOnSnapshot(Network network) {
        evaluateOnSnapshot(network, false, true, false);
    }

    public static void evaluateOnSnapshot(Network network, boolean tryDeletanet, boolean tryApkeep, boolean tryFlash) {
        System.gc();
        System.out.println("# Rules: " + network.getInitialRules().size() + " # Switches: " + network.getAllDevices().size());

        s1 = s2 = s3 = s4 = 0;
        t1 = t2 = t3 = t4 = 0;
        m1 = m2 = m3 = m4 = 0;
        System.out.println("+++++++++++++++++++++ " + network.getName() + " +++++++++++++++++++++");
        if (tryDeletanet) {
            for (int i = 0; i < warmupRepeat; i++) deltanet(network);
            System.out.println("==================== Warmed ==================== ");
            for (int i = 0; i < testRepeat; i++) s1 += deltanet(network);
        }
        System.out.println("==================== Ended ==================== ");
        long startTime, endTime;
long apkeepTime = 0, seqTime = 0;

if (tryApkeep) {
    startTime = System.nanoTime(); // 记录开始时间
    if (omit && network.getName().equals("LNet1")) {
        // skip LNet1 for APKeep*, which cannot be finished in 1-hour
    } else {
        for (int i = 0; i < warmupRepeat; i++) apkeep(network, new ArrayPorts(), true);
        System.out.println("==================== Warmed ==================== ");
        for (int i = 0; i < testRepeat; i++) s2 += apkeep(network, new ArrayPorts(), true);
        System.out.println("==================== Ended ==================== ");
    }
    endTime = System.nanoTime(); // 记录结束时间
    apkeepTime = endTime - startTime; // 计算 APKeep 部分的总执行时间
    System.out.println("APKeep execution time: " + apkeepTime / 1_000_000 + " ms");
}

if (tryFlash) {
    startTime = System.nanoTime(); // 记录开始时间
    for (int i = 0; i < warmupRepeat; i++) seq(network, true);
    System.out.println("==================== Warmed ==================== ");
    for (int i = 0; i < testRepeat; i++) s3 += seq(network, true);
    System.out.println("==================== Ended ==================== ");
    endTime = System.nanoTime(); // 记录结束时间
    seqTime = endTime - startTime; // 计算 Flash 部分的总执行时间
    System.out.println("Flash execution time: " + seqTime / 1_000_000 + " ms");
}

        /*
        for (int i = 0; i < warmupRepeat; i ++) seq(network, false);
        System.out.println("==================== Loaded ==================== ");
        for (int i = 0; i < testRepeat; i ++) s4 += seq(network, false);
        System.out.println("==================== Ended ==================== ");
         */
        printLog("overall.txt", network.getName() + " # Rules: " + network.getInitialRules().size() + " # Switches: " + network.getAllDevices().size());
        System.out.println("+++++++++++++++++++++ END " + network.getName() + " END +++++++++++++++++++++");
        try {
            checkAllPairRechabilityAndLoopfree(network);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // This part is deprecated since its time-consumption is negligible compared to model construction.
    // Notice Deltanet* has more #ECs compared to APKeep and Flash, which should be slower in this part.
    private static void checkAllPairRechabilityAndLoopfree(Network network) throws IOException {
        
        InverseModel ver1 = new InverseModel(network, new PersistentPorts());
        ConflictFreeChanges conflictFreeChanges = ver1.insertMiniBatch(network.getInitialRules());
        ver1.update(conflictFreeChanges);

        PrintWriter printWriter = new PrintWriter(new FileWriter("all-pair.txt", true));
        printWriter.println();
        printWriter.println();

        double s;
        HashMap<Port, HashSet<Integer>> model = ver1.getPortToPredicate();

        s = 0;
        for (int i = 0; i < warmupRepeat; i ++) model = ver1.getPortToPredicate();
        s -= System.nanoTime();
        for (int i = 0; i < testRepeat; i ++) model = ver1.getPortToPredicate();
        s += System.nanoTime();
        printWriter.println(network.getName() + " convert Ports to PortToInteger: " + (s / ratio) + " us amoritized per-update.");
        printWriter.println(network.getName() + " convert Ports to PortToInteger: " + (s / testRepeat) + " ns total.");
        printWriter.close();

        printWriter = new PrintWriter(new FileWriter("all-pair.txt", true));
        for (int i = 0; i < warmupRepeat; i ++) Checker.allPair(network, model);
        s = 0;
        for (int i = 0; i < testRepeat; i ++) s += Checker.allPair(network, model);
        printWriter.println(network.getName() + " all-pair and loop: " + (s / ratio) + " us amoritized per-update.");
        printWriter.println(network.getName() + " all-pair and loop: " + (s / testRepeat) + " ns total.");
        printWriter.close();

        printWriter = new PrintWriter(new FileWriter("all-pair.txt", true));
        AtomVerifier ver2 = new AtomVerifier();
        for (Rule rule : network.getInitialRules()) ver2.insertRule(rule);
        s = 0;
        for (int i = 0; i < warmupRepeat; i ++) ver2.checkPECSize();
        s -= System.nanoTime();
        for (int i = 0; i < testRepeat; i ++) ver2.checkPECSize();
        s += System.nanoTime();
        printWriter.println(network.getName() + " atom to ECs: " + (s / ratio) + " us amoritized per-update.");
        printWriter.println(network.getName() + " atom to ECs: " + (s / testRepeat) + " ns total.");
        printWriter.close();

        printWriter.println(" ======  #Atoms: " + ver2.atomSize() + " #ECs: " + ver1.predSize() + " ====== ");
        model = ver2.getPortToPredicate();
        printWriter = new PrintWriter(new FileWriter("all-pair.txt", true));
        for (int i = 0; i < warmupRepeat; i ++) Checker.allPair(network, model);
        s = 0;
        for (int i = 0; i < testRepeat; i ++) s += Checker.allPair(network, model);
        printWriter.println(network.getName() + " all-pair on atom " + (s / ratio) + " us amoritized per-update.");
        printWriter.println(network.getName() + " all-pair on atom " + s + " us total.");
        printWriter.close();
    }

    // The memory estimation is not very precise :(
    private static double printMemory() {
        System.gc();
        double memoryAfter = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory(), ret = ((memoryAfter - memoryBefore) / byte2MB);
        System.out.println("Memory usage (verification): " + ret + " M");
        return ret;
    }

    private static double deltanet(Network network) {
        System.gc();
        memoryBefore = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        AtomVerifier verifier = new AtomVerifier();
        for (Rule rule : network.getInitialRules()) {
            verifier.insertRule(rule);
        }
        m1 += printMemory();
        System.out.println("Deltanet* #Atom (full snapshot): " + verifier.atomSize());
        if (testDeletion) {
            for (Rule rule : network.getInitialRules()) {
                verifier.removeRule(rule);
            }
            System.out.println("Deltanet* #Atom (deleted to empty): " + verifier.atomSize());
        }
        t1 += verifier.opCnt;
        return verifier.printTime(network.getInitialRules().size() * (testDeletion ? 2 : 1));
    }

    private static double apkeep(Network network, Ports base, boolean eagerMerge) {
        System.gc();
        memoryBefore = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        APVerifier verifier = new APVerifier(network, base);
        for (Rule rule : network.getInitialRules()) {
            verifier.insertRule(rule);
            verifier.update(eagerMerge);
        }
        m2 += printMemory();
        System.out.println("APKeep* #EC (full snapshot): " + verifier.predSize());
        if (testDeletion) {
            for (Rule rule : network.getInitialRules()) {
                verifier.removeRule(rule);
                verifier.update(eagerMerge);
            }
            System.out.println("APKeep* #EC (deleted to empty): " + verifier.predSize());
        }
        t2 += verifier.bddEngine.opCnt;
        return verifier.printTime(network.getInitialRules().size() * (testDeletion ? 2 : 1));
    }

    private static double seq(Network network, boolean asBatch) {
        System.gc();
        memoryBefore = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        InverseModel verifier = new InverseModel(network, new PersistentPorts());
        if (asBatch) {
            ConflictFreeChanges conflictFreeChanges = verifier.insertMiniBatch(network.getInitialRules());
            verifier.update(conflictFreeChanges);
            m3 += printMemory();
            if (testDeletion) {
                System.out.println("Flash #EC (full snapshot): " + verifier.predSize() + " with Batch");
                conflictFreeChanges = verifier.miniBatch(new ArrayList<>(), network.getInitialRules());
                verifier.update(conflictFreeChanges);
            }
            t3 += verifier.bddEngine.opCnt;
        } else {
            for (Rule rule : network.getInitialRules()) {
                ConflictFreeChanges conflictFreeChanges = verifier.insertMiniBatch(new ArrayList<>(Collections.singletonList(rule)));
                verifier.update(conflictFreeChanges);
            }
            m4 += printMemory();
            if (testDeletion) {
                System.out.println("Flash #EC (deleted to empty): " + verifier.predSize() + " w/o Batch");
                for (Rule rule : network.getInitialRules()) {
                    ConflictFreeChanges conflictFreeChanges = verifier.miniBatch(new ArrayList<>(), new ArrayList<>(Collections.singletonList(rule)));
                    verifier.update(conflictFreeChanges);
                }
            }
            t4 += verifier.bddEngine.opCnt;
        }
        System.out.println("Flash #EC: " + verifier.predSize() + (asBatch ? " with Batch" : " w/o Batch"));
        return verifier.printTime(network.getInitialRules().size() * (testDeletion ? 2 : 1));
    }

    public static void evaluateOnUpdatesSequence(Network network) { // Table 3
        System.gc();
        System.out.println("# Updates: " + network.updateSequence.size() + " # Switches: " + network.getAllDevices().size());

        s1 = s2 = s3 = s4 = 0;
        t1 = t2 = t3 = t4 = 0;
        m1 = m2 = m3 = m4 = 0;
        System.out.println("+++++++++++++++++++++ " + network.getName() + " +++++++++++++++++++++");
        for (int i = 0; i < warmupRepeat; i ++) deltanetPrime(network);
        System.out.println("==================== Warmed ==================== ");
        for (int i = 0; i < testRepeat; i ++) s1 += deltanetPrime(network);
        System.out.println("==================== Ended ==================== ");
        for (int i = 0; i < warmupRepeat; i ++) apkeepPrime(network, new ArrayPorts());
        System.out.println("==================== Warmed ==================== ");
        for (int i = 0; i < testRepeat; i ++) s2 += apkeepPrime(network, new ArrayPorts());
        System.out.println("==================== Ended ==================== ");
        for (int i = 0; i < warmupRepeat; i ++) seqPrime(network, true);
        System.out.println("==================== Warmed ==================== ");
        for (int i = 0; i < testRepeat; i ++) s3 += seqPrime(network, true);
        System.out.println("==================== Ended ==================== ");
        /*
        for (int i = 0; i < warmupRepeat; i ++) seqPrime(network, false);
        System.out.println("==================== Loaded ==================== ");
        for (int i = 0; i < testRepeat; i ++) s4 += seqPrime(network, false);
        System.out.println("==================== Ended ==================== ");
         */
        printLog("overall.txt", network.getName() + " # Updates: " + network.updateSequence.size() + " # Switches: " + network.getAllDevices().size());
        System.out.println("+++++++++++++++++++++ END " + network.getName() + " END +++++++++++++++++++++");

    }

    private static double deltanetPrime(Network network) {
        System.gc();
        memoryBefore = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        AtomVerifier verifier = new AtomVerifier();
        for (Pair<Boolean, Rule> pair : network.updateSequence) {
            Rule rule = pair.getSecond();
            if (pair.getFirst()) {
                verifier.insertRule(rule);
            } else {
                verifier.removeRule(rule);
            }
        }
        m1 += printMemory();
        System.out.println("Delta-net #Atom: " + verifier.atomSize());
        t1 += verifier.opCnt;
        return verifier.printTime(network.updateSequence.size());
    }

    private static double apkeepPrime(Network network, Ports base) {
        System.gc();
        memoryBefore = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        APVerifier verifier = new APVerifier(network, base);
        for (Pair<Boolean, Rule> pair : network.updateSequence) {
            Rule rule = pair.getSecond();
            if (pair.getFirst()) {
                verifier.insertRule(rule);
                verifier.update(true);
            } else {
                verifier.removeRule(rule);
                verifier.update(true);
            }
        }
        m2 += printMemory();
        System.out.println("APKeep #EC: " + verifier.predSize());
        t2 += verifier.bddEngine.opCnt;
        return verifier.printTime(network.updateSequence.size());
    }

    private static double seqPrime(Network network, boolean asBatch) {
        System.gc();
        memoryBefore = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        InverseModel verifier = new InverseModel(network, new PersistentPorts());
        if (asBatch) {
            ArrayList<Rule> insertion = new ArrayList<>(), deletion = new ArrayList<>();
            for (Pair<Boolean, Rule> pair : network.updateSequence) {
                if (pair.getFirst()) insertion.add(pair.getSecond()); else deletion.add(pair.getSecond());
            }
            ConflictFreeChanges conflictFreeChanges = verifier.miniBatch(insertion, deletion);
            verifier.update(conflictFreeChanges);
            m3 += printMemory();
            t3 += verifier.bddEngine.opCnt;
        } else {
            for (Pair<Boolean, Rule> pair : network.updateSequence) {
                Rule rule = pair.getSecond();
                if (pair.getFirst()) {
                    ConflictFreeChanges conflictFreeChanges = verifier.insertMiniBatch(new ArrayList<>(Collections.singletonList(rule)));
                    verifier.update(conflictFreeChanges);
                } else {
                    ConflictFreeChanges conflictFreeChanges = verifier.miniBatch(new ArrayList<>(), new ArrayList<>(Collections.singletonList(rule)));
                    verifier.update(conflictFreeChanges);
                }
            }
            m4 += printMemory();
            t4 += verifier.bddEngine.opCnt;
        }
        System.out.println("Flash #EC: " + verifier.predSize() + (asBatch ? " with Batch" : " w/o Batch"));
        return verifier.printTime(network.updateSequence.size());
    }
}
