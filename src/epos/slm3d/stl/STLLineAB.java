package epos.slm3d.stl;

public class STLLineAB {
    final public boolean valid;
    final public boolean vertical;
    final public double a;
    final public double b;
    public STLLineAB(double a, double b) {
        this.a = a;
        this.b = b;
        vertical = false;
        valid = true;
        }
    public STLLineAB(double b0) {
        vertical = true;
        a=0;
        b=b0;
        valid = true;
        }
    public STLLineAB() {
        vertical = false;
        a=0;
        b=0;
        valid = false;
    }
    public double y(double x){
        return a * x + b;
        }
    public double x(double y){
        return (y-b)/a;
        }
}
