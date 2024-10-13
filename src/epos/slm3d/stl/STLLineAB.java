package epos.slm3d.stl;

public class STLLineAB {
    final public boolean vertical;
    final public double a;
    final public double b;
    public STLLineAB(double a, double b) {
        this.a = a;
        this.b = b;
        vertical = false;
        }
    public STLLineAB() {
        vertical = true;
        a=b=0;
        }
    public double y(double x){
        return a * x + b;
        }
    public double x(double y){
        return (y-b)/a;
        }
}
