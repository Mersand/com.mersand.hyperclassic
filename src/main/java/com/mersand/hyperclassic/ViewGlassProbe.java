// 在 SystemUI 进程里探针：验证 View 的玻璃方法是否真实存在且可用
package com.mersand.hyperclassic;

public final class ViewGlassProbe {
    private ViewGlassProbe() {
    }

    public static String probe() {
        StringBuilder sb = new StringBuilder();
        String[] names = {
                "setMiViewMaterialType",
                "setMiGlass",
                "setMiGlassBlurRadius",
                "setMiGlassSdfMaxSize"
        };
        for (String n : names) {
            try {
                java.lang.reflect.Method[] ms = android.view.View.class.getMethods();
                boolean found = false;
                for (java.lang.reflect.Method m : ms) {
                    if (n.equals(m.getName())) {
                        found = true;
                        sb.append(n).append("(");
                        Class<?>[] ps = m.getParameterTypes();
                        for (int i = 0; i < ps.length; i++) {
                            if (i > 0) sb.append(", ");
                            sb.append(ps[i].getSimpleName());
                        }
                        sb.append(") OK; ");
                        break;
                    }
                }
                if (!found) {
                    sb.append(n).append(" NOT FOUND; ");
                }
            } catch (Throwable t) {
                sb.append(n).append(" ERR:").append(t).append("; ");
            }
        }
        return sb.toString();
    }
}
