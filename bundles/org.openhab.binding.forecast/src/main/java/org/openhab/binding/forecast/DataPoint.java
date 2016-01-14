package org.openhab.binding.forecast;

public class DataPoint {

    private long timestamp;
    private double value;

    public DataPoint(net.sourceforge.openforecast.DataPoint dataPoint) {
        this.timestamp = (long) dataPoint.getIndependentValue("timestamp");
        this.value = dataPoint.getDependentValue();
    }

    public double getValue() {
        return value;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "DataPoint [timestamp=" + timestamp + ", value=" + value + "]";
    }
}
