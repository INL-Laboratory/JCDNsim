package entities.logical;

import entities.physical.Server;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IFile {
    public static Map<Integer, List<Server>> serversHavingFile = new HashMap<>();
    //TODO: for all files we should fill this map using getServersHavingFile Method in NetworkGraph


    private int id;
    private int size;

    public IFile(int id, int size){
        this.id = id;
        this.size = size;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
