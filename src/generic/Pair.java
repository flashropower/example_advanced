package generic;

/**
 * A simple example used in many tutorials.
 * 
 * Replicates the functionality of Map.Entry, but is perhaps simpler to use.
 */
public class Pair<A, B> {
    // these can be any two types, same or different
    private final A first;
    private final B last;


    public Pair (A key, B value) {  
        this.first = key;
        this.last = value;
    }

    // getters
    public A getFirst () {
        return first;
    }
    public B getLast () {
        return last;
    }

    public String toString() { 
        return "(" + first + ", " + last + ")";  
    }


    // simple example to show generic method, could appear in any class
    public static <T> Pair<T, T> makePair (T value) {
        return new Pair<T, T>(value, value);
    }


    // a simple example to show how to access it
    public static void main (String[] args) {
        Pair<String, Integer> counts = new Pair<>("Hello", 1);
        System.out.println(counts);
        Pair<String, String> values = makePair("What");
        System.out.println(values);
    }
}
