package org.openhab.binding.forecast;

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class DataSet extends AbstractCollection<DataPoint> {

    private Collection<DataPoint> dataPoints = new ArrayList<DataPoint>();

    public DataSet(net.sourceforge.openforecast.DataSet dataSet) {
        for (net.sourceforge.openforecast.DataPoint dataPoint : dataSet) {
            dataPoints.add(new DataPoint(dataPoint));
        }
    }

    @Override
    public Iterator<DataPoint> iterator() {
        return dataPoints.iterator();
    }

    @Override
    public int size() {
        return dataPoints.size();
    }

    @Override
    public String toString() {
        return dataPoints.toString();
    }
}
