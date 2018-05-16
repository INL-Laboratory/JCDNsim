package entities.utilities;
/* Generic Min/Max Binary Heap
 * for /r/javaexamples
 *
 */
import java.util.Arrays;

@SuppressWarnings("unchecked")

/**
 * Creates an array-based binary heap. Defaults to 'min' (Priority Queue)
 *
 * @author /u/Philboyd_Studge
 */
public class BinaryHeap<T extends Comparable>
{
    private static final int DEFAULT_CAPACITY = 10;
    private T[] heap;
    private int length;
    private boolean min;

    /**
     * Default Constructor
     * <p>default capacity of 9 (0 index is not used)
     * <p>default type of heap is min
     */
    public BinaryHeap()
    {
        heap = (T[]) new Comparable[DEFAULT_CAPACITY];
        length = 0;
        min = true;
    }

    /**
     * Constructor - takes an array of type T and a boolean for min/max
     * @param array T[] array
     * @param min boolean true = min heap, false = max heap
     */
    public BinaryHeap(T[] array, boolean min)
    {
        heap = (T[]) new Comparable[DEFAULT_CAPACITY];
        length = 0;
        this.min = min;

        // add each element to the heap
        for (T each : array)
        {
            add(each);
        }
    }

    /**
     * Constructor - specify boolean true = min heap, false = max heap
     * @param min boolean true = min heap, false = max heap
     */
    public BinaryHeap(boolean min)
    {
        heap = (T[]) new Comparable[DEFAULT_CAPACITY];
        length = 0;
        this.min = min;

    }

    /**
     * get the heap array
     * note: can cause casting issues
     * @return Array of type T[]
     */
    public T[] getHeap()
    {
        return Arrays.copyOfRange(heap, 1, length + 1);
    }

    /**
     * adds a generic type T to heap
     * <p>percolates down the tree
     * @param value type T value
     */
    public void add(T value)
    {
        // resize if needed
        if (this.length >= heap.length - 1)
        {
            heap = this.resize();
        }

        length++;
        heap[length] = value;
        bubbleUp();
    }

    /**
     * Removes min or max item from heap
     * <p>re-heapifies
     * @return value of T that is minimum or maximum value in heap
     */
    public T remove()
    {
        T result = peek();

        swap(1, length);
        heap[length] = null;
        length--;

        bubbleDown();

        return result;
    }

    /**
     * Remove specific object from heap
     * @param value type T
     * @return true if found and removed
     */
    public boolean remove(T value)
    {
        for (int i = 0; i < heap.length; i++)
        {
            if (value.equals(heap[i]))
            {
                System.out.println(i);
                swap(i, length);
                heap[length] = null;
                length--;
                //bubbleUp();
                bubbleDown();
                return true;
            }
        }
        return false;
    }

    /**
     * Removes min or max item from heap
     * same as <code>remove()</code> but does not throw exception on empty
     * <p>re-heapifies
     * @return value of T that is minimum or maximum value in heap;  or <code>null</code> if empty
     */
    public T poll()
    {
        if (isEmpty()) return null;

        T result = peek();

        swap(1, length);
        heap[length] = null;
        length--;

        bubbleDown();

        return result;
    }

    /**
     * Checks if heap is empty
     * @return <code>true</code> if empty
     */
    public boolean isEmpty()
    {
        return length == 0;
    }

    /**
     * returns min/max value without removing it
     * @return value type T
     * @throws IllegalStateException if empty
     */
    public T peek()
    {
        if (isEmpty()) throw new IllegalStateException();
        return heap[1];
    }

    /**
     * Length/size of heap
     * @return int size of heap
     */
    public int length()
    {
        return length;
    }

    /**
     * Add DEFAULT_CAPACITY to length of <code>heap</code> array
     * @return new array of type T
     */
    private T[] resize()
    {
        // add 10 to array capacity
        return Arrays.copyOf(heap, heap.length + DEFAULT_CAPACITY);
    }

    /**
     * percolates new values up based on min/max
     */
    private void bubbleUp()
    {
        int index = length;
        if (min)
        {
            while (hasParent(index) && (parent(index).compareTo(heap[index]) > 0))
            {
                swap(index, parentIndex(index));
                index = parentIndex(index);
            }
        }
        else
        {
            while (hasParent(index) && (parent(index).compareTo(heap[index]) < 0))
            {
                swap(index, parentIndex(index));
                index = parentIndex(index);
            }

        }
    }

    /**
     * percolates values down based on min/max
     */
    private void bubbleDown()
    {
        int index = 1;
        if (min) // min heap
        {

            while (hasLeftChild(index))
            {
                // find smaller of child values
                int smaller = leftIndex(index);
                if (hasRightChild(index) && heap[leftIndex(index)].compareTo(heap[rightIndex(index)]) > 0)
                {
                    smaller = rightIndex(index);
                }
                if (heap[index].compareTo(heap[smaller]) > 0)
                {
                    swap(index, smaller);
                }
                else break;

                index = smaller;
            }
        }
        else // max heap
        {
            while (hasLeftChild(index))
            {
                // find larger of child values
                int larger = leftIndex(index);
                if (hasRightChild(index) && heap[leftIndex(index)].compareTo(heap[rightIndex(index)]) < 0)
                {
                    larger = rightIndex(index);
                }
                if (heap[index].compareTo(heap[larger]) < 0)
                {
                    swap(index, larger);
                }
                else break;

                index = larger;
            }

        }
    }

    /**
     * if child has a parent
     * @param i integer - index
     * @return true if index > 1
     */
    private boolean hasParent(int i)
    {
        return i > 1;
    }

    /**
     * Get left index mathematically
     * @param i index
     * @return index of left node from index i
     */
    private int leftIndex(int i)
    {
        return i * 2;
    }

    /**
     * Get right index mathematically
     * @param i index
     * @return index of right node from index i
     */
    private int rightIndex(int i)
    {
        return i * 2 + 1;
    }

    /**
     * Test to see if node has left child
     * @param i index
     * @return true if it does
     */
    private boolean hasLeftChild(int i)
    {
        return leftIndex(i) <= length;
    }

    /**
     * Test to see if node has right child
     * @param i index
     * @return true if it does
     */
    private boolean hasRightChild(int i)
    {
        return rightIndex(i) <= length;
    }

    /**
     * get index of parent from child node
     * @param i index
     * @return index of parent
     */
    private int parentIndex(int i)
    {
        return i / 2;
    }

    /**
     * get parent value
     * @param i index
     * @return value of type T
     */
    private T parent(int i)
    {
        return heap[parentIndex(i)];
    }

    /**
     * Swap two values in heap
     * @param index1 int first index
     * @param index2 int second index
     */
    private void swap(int index1, int index2)
    {
        T temp = heap[index1];
        heap[index1] = heap[index2];
        heap[index2] = temp;
    }

    /**
     * Overridden toString method
     * @return String all values in heap without null values
     */
    @Override
    public String toString()
    {
        String retval = "";
        for (T each : heap)
        {
            if (each != null) retval += each + " : ";
        }
        return retval + "\n";

    }

}