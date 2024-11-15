package de.emilschlampp.schecpuminecraft.util;

import de.emilschlampp.schecpuminecraft.schemilapi.util.ConfigUtil;
import de.emilschlampp.schecpuminecraft.schemilapi.util.SConfig;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

public class RuntimeValues {
    public static String PREFIX = "§8[§6CPU§8] §6";
    public static String ERROR = "§8[§6CPU§8] §6Ein interner Fehler ist aufgetreten.";
    public static String SUCCESS = "§8[§6CPU§8] §6Erfolgreich!";
    public static int CPU$MAX_MULTI_EXEC = 100;
    public static boolean CPU$DISABLE_ON_ERROR = true;
    public static boolean CPU$PRINT_ERROR = false;
    public static boolean CPU$ALLOW_SYSOUT = false;

    private static final Map<String, Object> defaults = new HashMap<>();


    public static void load() {
        boolean def = defaults.isEmpty();

        SConfig config = ConfigUtil.getConfig("runtime");

        for (Field declaredField : RuntimeValues.class.getDeclaredFields()) {
            int mod = declaredField.getModifiers();

            boolean checkPublic = Modifier.isPublic(mod);
            boolean checkStatic = Modifier.isStatic(mod);
            boolean checkFinal = Modifier.isFinal(mod);

            if(checkFinal) {
                continue;
            }
            if(!checkPublic || !checkStatic) {
                continue;
            }

            if(!declaredField.getName().equals(declaredField.getName().toUpperCase())) {
                continue;
            }

            if(declaredField.getName().startsWith("_")) {
                continue;
            }

            try {
                declaredField.setAccessible(true);
            } catch (Throwable ignored) {

            }

            if(!declaredField.isAccessible()) {
                continue;
            }

            String configName = declaredField.getName().toLowerCase().replace("_", "-").replace("$", ".");
            try {
                if(def) {
                    defaults.put(configName, declaredField.get(null));
                }
                if(config.isSet(configName)) {
                    declaredField.set(null, config.get(configName));
                } else {
                    if(def) {
                        config.setDefault(configName, declaredField.get(null));
                    } else {
                        declaredField.set(null, defaults.get(configName));
                        config.setDefault(configName, declaredField.get(null));
                    }
                }
            } catch (Throwable throwable) {

            }
        }
    }
}
