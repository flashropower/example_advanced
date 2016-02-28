package lambda;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;


// a simple, mutable class
class Counter {
    private int myCount;

    public Counter (int value) {
        myCount = value;
    }

    public void doSomething (int data) {
        myCount += data;
    }

    public void doSomethingElse () {
        myCount *= 2;
    }

    public String toString () {
        return "" + myCount;
    }
}


// a class that wants to hide a collection
class ListHolder<E> implements Iterable<E> {
    private List<E> mySharedItems;
    private List<E> myOriginalItems;

    public ListHolder (List<E> args) {
        // to be truly safe, create your own version of the collection
        myOriginalItems = new ArrayList<E>(args);
        reset();
    }

    // standard get method
    public List<E> getValues () {
        return mySharedItems;
    }

    // get immutable version of the collection
    public List<E> getImmutableValues () {
        // can't trust the outside world
        reset();
        return Collections.unmodifiableList(getValues());
    }

    // get iterator view of collection only to be used within foreach loops
    @Override
    public Iterator<E> iterator () {
        // can't trust the outside world
        reset();
        return getImmutableValues().iterator();
    }

    // accept lambda function, do not reveal collection
    public void apply (Consumer<E> action) {
        // can't trust the outside world
        reset();
        mySharedItems.forEach(action);
        // OR:
        // for (E c : myList) {
        // action.accept(c);
        // }
    }

    // only needed to reset collection after Client destroys it
    private void reset () {
        mySharedItems = new ArrayList<>(myOriginalItems);
    }

    public String toString () {
        return mySharedItems.toString();
    }
}


// a class that wants to combine its data with elements in the collection
class Client {
    private int myData;
    private ListHolder<Counter> myHolder;

    public Client (int data, ListHolder<Counter> holder) {
        myData = data;
        myHolder = holder;
    }

    public void simpleLoop () {
        List<Counter> values = myHolder.getValues();
        for (Counter c : values) {
            c.doSomething(myData);
        }
        // BAD possible outcome
        values.clear();
    }

    public void immutableLoop () {
        List<Counter> values = myHolder.getImmutableValues();
        for (Counter c : values) {
            c.doSomething(myData);
        }
        // throws error
        values.clear();
    }

    public void iteratorLoop () {
        // standard usage
        for (Counter c : myHolder) {
            c.doSomething(myData);
        }
        // explicit usage
        Iterator<Counter> iter = myHolder.iterator();
        while (iter.hasNext()) {
            Counter c = iter.next();
            c.doSomething(myData);
            // throws error
            if (c.toString().startsWith("5")) {
                iter.remove();
            }
        }
    }

    public void lambdaLoop () {
        // call method with parameter directly
        myHolder.apply(c -> c.doSomething(myData));
        // call method with no parameters
        myHolder.apply(Counter::doSomethingElse);
    }

    public String toString () {
        return myHolder.toString();
    }
}


public class Main {
    public static void main (String[] args) {
        // setup
        List<Counter> originals = new ArrayList<>();
        for (int k = 1; k < 10; k++) {
            originals.add(new Counter(k));
        }
        ListHolder<Counter> holder = new ListHolder<>(originals);
        Client client = new Client(13, holder);
        // tests
        System.out.println("Original : " + client);
        printResults("Getter, cleared : ", client, originals, holder, Client::simpleLoop);
        printResults("Immutable, clear attempted: ", client, originals, holder, Client::immutableLoop);
        printResults("Iterator, remove attempted: ", client, originals, holder, Client::iteratorLoop);
        printResults("Lambda, not exposed: ", client, originals, holder, Client::lambdaLoop);
    }

    private static void printResults (String label,
                                      Client client,
                                      List<Counter> originals,
                                      ListHolder<Counter> holder,
                                      Consumer<Client> loop) {
        try {
            System.out.print(label);
            loop.accept(client);
            System.out.println(client);
        }
        catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
