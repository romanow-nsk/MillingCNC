package romanow.cnc.stl;

// https://stackoverflow.com/questions/41144224/calculate-curvature-for-3-points-x-y
public class STLUtils {
    public static double curvature0(STLPoint2D p0, STLPoint2D p1, STLPoint2D p2) {
        double dx1 = p1.x() - p0.x();
        double dy1 = p1.y() - p0.y();
        double dx2 = p2.x() - p0.x();
        double dy2 = p2.y() - p0.y();
        double area = 0.5 * (dx1 * dy2 - dy1 * dx2);
        double len0 = p0.diffXY(p1);
        double len1 = p1.diffXY(p2);
        double len2 = p2.diffXY(p0);
        return Math.abs((len0 * len1 * len2)/(4 * area));
        }

    public static double curvature(STLPoint2D p1, STLPoint2D p2, STLPoint2D p3){
        // side lengths
        double a = p1.diffXY(p2);
        double b = p2.diffXY(p3);
        double c = p3.diffXY(p1);
        // semi-perimeter
        double s = (a + b + c) / 2;
        // Heron's formula for the area of a triangle
        double area = Math.sqrt(s * (s - a) * (s - b) * (s - c));
        double abc = a * b * c; // becomes 0 if points coincide
        return (abc == 0) ? 0 : abc/(4 * area);
        }
    // Adapted from https://stackoverflow.com/a/4103418
    private static STLPoint2D center( STLPoint2D p0, STLPoint2D p1, STLPoint2D p2) {
        double x0 = p0.x();
        double y0 = p0.y();
        double x1 = p1.x();
        double y1 = p1.y();
        double x2 = p2.x();
        double y2 = p2.y();
        double offset = x1 * x1 + y1 * y1;
        double bc = (x0 * x0 + y0 * y0 - offset) / 2.0;
        double cd = (offset - x2 * x2 - y2 * y2) / 2.0;
        double det = (x0 - x1) * (y1 - y2) - (x1 - x2) * (y0 - y1);
        double invDet = 1 / det;
        double cx = (bc * (y1 - y2) - cd * (y0 - y1)) * invDet;
        double cy = (cd * (x0 - x1) - bc * (x1 - x2)) * invDet;
        return new STLPoint2D(cx, cy);
        }
    public static void main(String ss[]){
        STLPoint2D p1 = new STLPoint2D(0,10);
        STLPoint2D p2 = new STLPoint2D(5,15);
        STLPoint2D p3 = new STLPoint2D(10,10);
        System.out.println("R1="+curvature(p1,p2,p3)+" R2="+curvature0(p1,p2,p3)+" center="+center(p1,p2,p3));
        }
}
