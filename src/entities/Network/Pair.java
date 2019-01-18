/*
 * Developed By Saeed Hadadan, INL lab, Sharif University of Technology: www.inl-lab.net
 * Copyright (c) 2019. All rights reserved.
 *
 */

package entities.Network;

import java.util.List;
import java.util.Objects;

public class Pair {
    EndDevice endDevice;
    List preFiltered;

    public Pair(EndDevice endDevice, List preFiltered) {
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
