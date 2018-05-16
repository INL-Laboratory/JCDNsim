package entities.logical;

import entities.physical.EndDevice;
import entities.physical.Server;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Pair {
    EndDevice endDevice;
    List<Server> preFiltered;

    public Pair(EndDevice endDevice, List<Server> preFiltered) {
        this.endDevice = endDevice;
        this.preFiltered = preFiltered;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pair pair = (Pair) o;
        return Objects.equals(endDevice, pair.endDevice) &&
                Objects.equals(preFiltered, pair.preFiltered);
    }

    @Override
    public int hashCode() {

        return Objects.hash(endDevice, preFiltered);
    }
}
