import java.util.Collection;
import java.util.ArrayList;
import java.util.LinkedList
import java.util.Vector;
import java.util.List;

public static void syncIterate(Collection<Integer> elements, int number)
{
    synchronized (elements){
        nonSyncIterate(elements, number);
    }
}
public static void main(String[] args){
    Thread threads = new Thread[1];
    List<Integer> elements = new ArrayList<>();
}