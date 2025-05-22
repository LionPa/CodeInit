package io.lionpa.codeInit;

import com.google.common.reflect.ClassPath;
import io.lionpa.codeInit.register.IRegister;
import io.lionpa.codeInit.register.Register;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

public class CodeInitializer {

    private static final Logger logger = LoggerFactory.getLogger("CodeInitializer");

    private static final HashMap<String, IRegister> registers = new HashMap<>();
    public static void addRegister(String name, IRegister iRegister){
        registers.put(name.toLowerCase(), iRegister);
    }

    @DataRead
    private static final RunOnNode startNode = new RunOnNode(null, new HashMap<>());
    @DataRead
    private static final RunOnNode stopNode = new RunOnNode(null, new HashMap<>());
    @DataRead
    public static final HashMap<String, RunOnNode> nodes = new HashMap<>();

    public static HashMap<String, Object> data = new HashMap<>();

    // Mega shit
    static void register(Set<String> packages, Set<ClassPath.ClassInfo> classInfos){
        try {
            for (ClassPath.ClassInfo classInfo : classInfos){
                if (!collectionHas(packages, classInfo.getPackageName())) continue;
                Class<?> clazz = classInfo.load();
                Method[] methods = clazz.getDeclaredMethods();

                Register register = clazz.getAnnotation(Register.class);
                if (register != null) register(register, clazz);

                for (Method method : methods){
                    RunOn runOn = method.getAnnotation(RunOn.class);
                    if (runOn != null) runOnRegisterMethods(method);
                }
                register(clazz);
            }
            for (ClassPath.ClassInfo classInfo : classInfos){
                if (!collectionHas(packages, classInfo.getPackageName())) continue;
                Class<?> clazz = classInfo.load();
                Method[] methods = clazz.getDeclaredMethods();

                for (Method method : methods){
                    RunOn runOn = method.getAnnotation(RunOn.class);
                    if (runOn != null) runOn(method, runOn);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        start(startNode);
    }

    private static boolean collectionHas(Collection<String> collection, String string){
        for (String s : collection){
            if (string.contains(s)) return true;
        }
        return false;
    }


    private static void runOnRegisterMethods(Method method){
        method.setAccessible(true);
        RunOnNode node = new RunOnNode(method, new HashMap<>());
        String s = method.getDeclaringClass().getSimpleName() + ":" + method.getName();
        nodes.put(s, node);
    }

    private static void runOn(Method method, RunOn runOn){
        if (runOn.on() == RunOnType.TEST) return;
        RunOnNode node = nodes.get(method.getDeclaringClass().getSimpleName() + ":" + method.getName());
        RunOnNode addTo;
        if (runOn.on() == RunOnType.START){
            addTo = startNode;
        } else if (runOn.on() == RunOnType.STOP){
            addTo = stopNode;
        } else {
            return;
        }

        if (!runOn.after().isBlank()){
            RunOnNode a = nodes.get(runOn.after());
            if (a != null) addTo = a;
        }

        if (addTo.sub.containsKey(runOn.priority())) {
            addTo.sub.get(runOn.priority()).add(node);
        } else {
            ArrayList<RunOnNode> list = new ArrayList<>();
            list.add(node);
            addTo.sub.put(runOn.priority(), list);
        }
    }

    public static void start(RunOnNode node){
        ArrayList<Integer> sorted = new ArrayList<>(node.sub.keySet());

        Collections.sort(sorted);

        // Positive
        for (int i : sorted){
            if (i < 0) continue;
            run(i, node);
        }

        // Negative
        for (int i = sorted.size() -1; i >= 0; i--){
            int value = sorted.get(i);
            if (value >= 0) continue;
            run(value, node);
        }
    }

    private static boolean stopped;

    static void stop(){
        if (stopped) return;
        start(stopNode);
        stopped = true;
    }

    private static void run(int i, RunOnNode node){
        for (RunOnNode a : node.sub.get(i)) {
            if (a.method != null) try {
                a.method.invoke(null);
            } catch (Exception e) {
                System.out.println(node.method != null ? node.method.getDeclaringClass().getSimpleName() + ":" + node.method.getName() : "No method");
                e.printStackTrace();
            }
        }

        for (RunOnNode n : node.sub.get(i))
            start(n);
    }


    private static void register(Class<?> clazz){
        for (Field field : clazz.getDeclaredFields()){
            if (field.getAnnotation(DataRead.class) != null) {
                field.setAccessible(true);
                try {
                    data.put(clazz.getSimpleName() + "." + field.getName(), field.get(null));
                } catch (Exception ignored) {}
            }
        }
    }

    public record RunOnNode(@Nullable Method method, HashMap<Integer, ArrayList<RunOnNode>> sub){
        @Override
        public String toString() {
            return "RunOnNode{" +
                    "method=" + ((method == null) ? "null" : method.getDeclaringClass().getSimpleName() + ":" + method.getName()) +
                    ", sub=" + sub +
                    '}';
        }
    }

    private static void register(Register register, Class<?> clazz) {
        if (register.type().isBlank()) return;

        try {
            IRegister r = registers.get(register.type().toLowerCase());

            if (r != null) {
                r.run(register, clazz);
            }
        } catch (Exception e) {
             logger.info("Error while registering {}!", clazz.getSimpleName());
            e.printStackTrace();
        }
    }

    public static class Data {
        private static final WeakHashMap<String, List<String>> dataHash = new WeakHashMap<>();

        public static List<String> getData(String contains) {
            if (dataHash.containsKey(contains)) return dataHash.get(contains);
            List<String> listForData = new ArrayList<>();
            for (String s : data.keySet()){
                if (!s.contains(contains)) continue;
                listForData.add(s);
            }
            if (dataHash.size() > 64) dataHash.clear();
            dataHash.put(contains,listForData);
            return listForData;
        }

        private static final WeakHashMap<String, List<String>> nodesHash = new WeakHashMap<>();

        public static List<String> getNodes(String contains) {
            if (nodesHash.containsKey(contains)) return nodesHash.get(contains);
            List<String> listForNodes = new ArrayList<>();
            for (RunOnNode node : nodes.values()) {
                if (node.method == null) continue;
                String s = node.method.getDeclaringClass().getSimpleName() + ":" + node.method.getName();
                if (!s.contains(contains)) continue;
                listForNodes.add(s);
            }
            if (nodesHash.size() > 64) nodesHash.clear();
            nodesHash.put(contains, listForNodes);
            return listForNodes;
        }
    }
}
