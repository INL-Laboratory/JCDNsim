package entities.utilities;/*
 * Author: Lucas A. Doran
 * Last Modified: Sunday, October the 13nd, 2013 (10/13/2013)
 * Description: Implementation of the treap data structure. Modifies a framework
 *					originally authored by Dr. Szumlanski in BST.java.
 */

import java.util.HashSet;
import java.util.Set;

class Node <Anytype extends Comparable<Anytype>>
{
    Anytype data;

    Node <Anytype> left;
    Node <Anytype> right;

    int priority;

    Node(Anytype data, int priority)
    {
        this.data = data;
        this.priority = priority;

        left = null;
        right = null;
    }
}

public class Treap <Anytype extends Comparable<Anytype>> {
    private int numElements;

    private Node<Anytype> root;

    Set<Integer> hs;

    public Treap() {
        hs = new HashSet<Integer>();
    }

    //A right rotation for moving nodes into the correct location
    private Node<Anytype> rightRotation(Node<Anytype> workingNode) {
        //The initial root of the rotation
        Node<Anytype> oldRoot = workingNode;

        //The pivot is the root of rotation's left child
        Node<Anytype> rtLChld = workingNode.left;

        //The only child of of a child of root that changes places
        Node<Anytype> rtLChldRChld = rtLChld.right;

        //Set the left child to be the new root
        workingNode = rtLChld;

        //Set the new root's right child to be the old root
        workingNode.right = oldRoot;

        //Set the old root's left child should be the new root's old right child
        oldRoot.left = rtLChldRChld;

        return workingNode;
    }

    //A left rotation for moving nodes into the correct location
    private Node<Anytype> leftRotation(Node<Anytype> workingNode) {
        //The initial root of the rotation
        Node<Anytype> oldRoot = workingNode;

        //The pivot is the root of rotation's right child
        Node<Anytype> rtRChld = workingNode.right;

        //The only child of of a child of root that changes places
        Node<Anytype> rtRChldLChld = rtRChld.left;

        //Set the right child to be the new root.
        workingNode = rtRChld;

        //The new root's left child should be the old root.
        workingNode.left = oldRoot;

        //The old root's right child should be the new root's old left child.
        oldRoot.right = rtRChldLChld;

        return workingNode;
    }

    //Recursive method that inserts the node and then rotates it into position.
    private Node<Anytype> insert(Node<Anytype> workingNode, Anytype data, int priority) {
        //We found a null (unassigned node), put our new node here!
        if (workingNode == null) {
            //Keep track of the number of elements
            ++numElements;

            //Make sure to keep our priority set up to date
            hs.add(priority);

            //Return the new node!
            return new Node<Anytype>(data, priority);
        }
        //Data is less than the node we are comparing to, insert it to the left.
        else if (data.compareTo(workingNode.data) < 0) {
            workingNode.left = insert(workingNode.left, data, priority);

			/*If the working node's left child has a lower priority,
			 * perform a right rotation*/
            if (workingNode.left.priority < workingNode.priority) {
                workingNode = rightRotation(workingNode);
            }
        }
        //Data is greater than the node we are comparing to, insert it to the right.
        else if (data.compareTo(workingNode.data) > 0) {
            workingNode.right = insert(workingNode.right, data, priority);
			/*If the working node's right child has a lower priority,
			 * perform a left rotation*/
            if (workingNode.right.priority < workingNode.priority) {
                workingNode = leftRotation(workingNode);
            }
        } else {
            // Stylistically, I have this here to explicitly state that we are
            // disallowing insertion of duplicate values.
            ;
        }

        return workingNode;
    }

    public void add(Anytype data) {
		/*Generate ints between [1, Integer.MAX_VALUE] until we get one not in
		 * our priority hashset.*/
        int tempPriority = (int) (Math.random() * Integer.MAX_VALUE);
        tempPriority += 1;
        while (hs.contains(tempPriority)) {
            tempPriority = (int) (Math.random() * Integer.MAX_VALUE);
            tempPriority += 1;
        }

        //Begin the recursive call to add the data
        root = insert(root, data, tempPriority);

    }

    public void add(Anytype data, int priority) {
        //Don't pass duplicate priorities!
        if (hs.contains(priority))
            return;

        //Initiate recursive add method
        root = insert(root, data, priority);
    }

    //Recursive function that walks through the BST
    private Node<Anytype> delete(Node<Anytype> workingNode, Anytype data) {
        //There's nothing here. the tree is either empty or doesn't contain "data". Return null.
        if (workingNode == null) {
            return null;
        }
        //Set the current node's left child equal to the return value of the next iteration of delete
        else if (data.compareTo(workingNode.data) < 0) {
            workingNode.left = delete(workingNode.left, data);
        }
        //Set the current node's right child equal to the return value of the next iteration of delete
        else if (data.compareTo(workingNode.data) > 0) {
            workingNode.right = delete(workingNode.right, data);
        }
        //We found the node to be deleted!
        else {
            //It's a leaf node! Erase the very notion of its existence!
            if (workingNode.left == null && workingNode.right == null) {
                //Keep track of the number of nodes
                --numElements;
                //Keep track of our priorities!
                hs.remove(workingNode.priority);

                return null;
            }
            //There is only a right child
            else if (workingNode.right == null) {
				/*Perform a right rotation so that the node to be deleted is
				 *now the new working node's right child*/
                workingNode = rightRotation(workingNode);

                workingNode.right = delete(workingNode.right, data);
            }
            //There is only a left child
            else if (workingNode.left == null) {
				/*Perform a left rotation so that the node to be deleted is
				 *now the new working node's left child*/
                workingNode = leftRotation(workingNode);
                workingNode.left = delete(workingNode.left, data);
            }
            //It has both!
            else {
				/*Perform a rotation such that the working node's child with the
				 *lowest priority becomes the new root*/
                if (workingNode.left.priority < workingNode.right.priority) {
					/*Perform a right rotation so that the node to be deleted is
				 	 *now the new working node's right child*/
                    workingNode = rightRotation(workingNode);
                    workingNode.right = delete(workingNode.right, data);
                } else {
					/*Perform a left rotation so that the node to be deleted is
				 	 *now the new working node's left child*/
                    workingNode = leftRotation(workingNode);
                    workingNode.left = delete(workingNode.left, data);
                }
            }
        }
        return workingNode;
    }

    public void remove(Anytype data) {
        root = delete(root, data);
    }

    private boolean contains(Node<Anytype> root, Anytype data) {
        //The tree is either null, or doesn't contain "data". Return false.
        if (root == null) {
            return false;
        }
        //If the data is present in the tree, it must be to the left of the current node
        else if (data.compareTo(root.data) < 0) {
            return contains(root.left, data);
        }
        //If the data is present in the tree, it must be to the right of the current node
        else if (data.compareTo(root.data) > 0) {
            return contains(root.right, data);
        }
        //We found it! Return true!
        else {
            return true;
        }
    }

    // Returns true if the value is contained in the BST, false otherwise.
    public boolean contains(Anytype data) {
        return contains(root, data);
    }

    public int size() {
        return numElements;
    }

    //Recursive function that returns the height of the longest sub-tree
    private int getHeight(Node<Anytype> workingNode, int currHeight) {
        //We have reached the end of a sub-tree, just return
        if (workingNode == null)
            return currHeight;

        //Increment the current height
        currHeight += 1;

        //Get both sub-tree heights (with reference to root)
        int leftHeight = getHeight(workingNode.left, currHeight);
        int rightHeight = getHeight(workingNode.right, currHeight);

        //Returns the greater of the two heights
        return (leftHeight > rightHeight ? leftHeight : rightHeight);
    }

    public Node<Anytype> getRoot() {
        return root;
    }

    public int height() {
        //Pass the result of getHeight from root and -1
        return getHeight(root, -1);
    }

    public static double hoursSpent() {
        return 4.5;
    }

    public static double difficultyRating() {
        return 3.0;
    }
}