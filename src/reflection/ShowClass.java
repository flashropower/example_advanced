package reflection;

// This example is from _Java Examples in a Nutshell_. (http://www.oreilly.com)
// Copyright (c) 1997 by David Flanagan
// This example is provided WITHOUT ANY WARRANTY either expressed or implied.
// You may study, use, modify, and distribute it for non-commercial purposes.
// For any commercial use, see http://www.davidflanagan.com/javaexamples

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;


/**
 * A program that displays a class synopsis for the named class
 * 
 * @author David Flanagan
 * @author Robert C. Duvall
 */
public class ShowClass {
    /**
     * The main method. Print info about the named class
     */
    public static void main (String[] args) throws ClassNotFoundException {
        Class<?> clazz = Class.forName((args.length > 0) ? args[0] : "java.lang.String");
        printClass(clazz);
        makeClass(clazz);
    }

    /**
     * Tries to create an object using both a default constructor and one that takes a String.
     */
    public static void makeClass (Class<?> clazz) {
        try {
            // the "simple" way
            Object o = clazz.newInstance();
            System.out.println("Printing: " + o);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        try {
            // the correct, more robust, way
            Constructor<?> ctor = clazz.getDeclaredConstructor(String.class);
            Object o = ctor.newInstance("Test");
            System.out.println("Printing: " + o);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Display the modifiers, name, superclass and interfaces of a class or interface. Then go and
     * list all constructors, fields, and methods.
     */
    public static void printClass (Class<?> c) {
        // Print modifiers, type (class or interface), name and superclass.
        if (c.isInterface()) {
            // The modifiers will include the "interface" keyword here...
            System.out.print(Modifier.toString(c.getModifiers()) + " " + c.getName());
        }
        else {
            System.out.print(Modifier.toString(c.getModifiers()) + " class " + c.getName());
            if (c.getSuperclass() != null) {
                System.out.print(" extends " + c.getSuperclass().getName());
            }
        }

        // Print interfaces or super-interfaces of the class or interface.
        Class<?>[] interfaces = c.getInterfaces();
        if ((interfaces != null) && (interfaces.length > 0)) {
            if (c.isInterface()) {
                System.out.println(" extends ");
            }
            else {
                System.out.print(" implements ");
            }
            printTypes(interfaces);
        }

        System.out.println(" {"); // Begin class member listing.
        // Now look up and display the members of the class.
        System.out.println("  // Constructors");
        Constructor<?>[] constructors = c.getDeclaredConstructors();
        for (Constructor<?> constructor : constructors) {
            printMethodOrConstructor(constructor);
        }

        System.out.println("  // Fields");
        Field[] fields = c.getDeclaredFields();
        for (Field field : fields) {
            printField(field);
        }

        System.out.println("  // Methods");
        Method[] methods = c.getDeclaredMethods();
        for (Method method : methods) {
            printMethodOrConstructor(method);
        }
        System.out.println("}");
    }

    // Print the modifiers, type, and name of a field
    private static void printField (Field f) {
        System.out.println("  " + modifiers(f.getModifiers()) + 
                           typename(f.getType()) + " " + 
                           f.getName() + ";");
    }

    // Print modifiers, return type, name, parameter types and exceptions of method or constructor.
    private static void printMethodOrConstructor (Executable member) {
        // print out name and modifiers
        System.out.print("  " + modifiers(member.getModifiers()));
        if (member instanceof Method) {
            System.out.print(typename(((Method)member).getReturnType()) + " ");
        }
        System.out.print(member.getName() + "(");
        // print any parameters
        printTypes(member.getParameterTypes());
        System.out.print(")");
        // print any exceptions thrown
        Class<?>[] exceptions = member.getExceptionTypes();
        if (exceptions.length > 0) {
            System.out.print(" throws ");
        }
        printTypes(exceptions);
        System.out.println(";");
    }

    // Print a list of types
    private static void printTypes (Class<?>[] types) {
        for (int i = 0; i < types.length; i++) {
            if (i > 0) {
                System.out.print(", ");
            }
            System.out.print(typename(types[i]));
        }
    }
    
    // Return name of an interface or primitive type, handling arrays.
    private static String typename (Class<?> t) {
        String brackets = "";
        while (t.isArray()) {
            brackets += "[]";
            t = t.getComponentType();
        }
        return t.getName() + brackets;
    }

    // Return a string version of modifiers, handling spaces nicely.
    private static String modifiers (int m) {
        if (m == 0)
            return "";
        else
            return Modifier.toString(m) + " ";
    }
}
