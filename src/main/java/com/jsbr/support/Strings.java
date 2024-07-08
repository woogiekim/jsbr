package com.jsbr.support;

public final class Strings {

    public static String toSnakeCase(String soruce) {
        return soruce.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
    }

    public static String lenientFormat(String template, Object... args) {
        template = String.valueOf(template); // null -> "null"

        if (args == null) {
            args = new Object[]{"(Object[])null"};
        } else {
            for (int i = 0; i < args.length; i++) {
                args[i] = lenientToString(args[i]);
            }
        }

        // start substituting the arguments into the '%s' placeholders
        var builder = new StringBuilder(template.length() + 16 * args.length);

        var templateStart = 0;
        var index = 0;

        while (index < args.length) {
            var placeholderStart = template.indexOf("{}", templateStart);

            if (placeholderStart == -1) {
                break;
            }

            builder.append(template, templateStart, placeholderStart);
            builder.append(args[index++]);

            templateStart = placeholderStart + 2;
        }

        builder.append(template, templateStart, template.length());

        // if we run out of placeholders, append the extra args in square braces
        if (index < args.length) {
            builder.append(" [");
            builder.append(args[index++]);

            while (index < args.length) {
                builder.append(", ");
                builder.append(args[index++]);
            }

            builder.append(']');
        }

        return builder.toString();
    }

    private static String lenientToString(Object o) {
        if (o == null) {
            return "null";
        }

        try {
            return o.toString();
        } catch (Exception e) {
            var objectToString = o.getClass().getName() + '@' + Integer.toHexString(System.identityHashCode(o));

            return "<" + objectToString + " threw " + e.getClass().getName() + ">";
        }
    }

}
