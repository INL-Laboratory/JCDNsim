package entities.logical;

import entities.physical.Server;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IFile {


    private int id;
    private float size;

    public IFile(int id, float size){
        this.id = id;
        this.size = size;
    }

    public float getSize() {
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

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("IFile{");
        sb.append(id);
        sb.append('}');
        return sb.toString();
    }
}
