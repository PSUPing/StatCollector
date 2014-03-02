package edu.drexel.StatCollector.dataaccess;

public class RegressionInfo {
    private Double m = 0.0d;
    private Double b = 0.0d;

    public RegressionInfo() {
    }

    public RegressionInfo(Double initM, Double initB) {
        m = initM;
        b = initB;
    }

    public Double getM() { return m; }
    public void setM(Double setM) { m = setM; }

    public Double getB() { return b; }
    public void setB(Double setB) { b = setB; }
}
