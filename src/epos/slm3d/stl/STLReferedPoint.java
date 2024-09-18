package epos.slm3d.stl;

/**
 * Created by romanow on 08.02.2018.
 */

import epos.slm3d.utils.Values;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/** Отрезок со ссылкой на другую */
public class STLReferedPoint extends STLPoint2D{
    private STLLine reference=null;
    public STLLine reference() {
        return reference;
        }
    public void reference(STLLine reference) {
        this.reference = reference;
        }
    public STLReferedPoint(STLPoint2D one, STLLine reference) {
        super(one);
        this.reference = reference;
        }
    public STLReferedPoint(STLPoint2D one) {
        super(one);
        }
    public STLReferedPoint() {}
    public void load(DataInputStream in) throws IOException {
        super.load(in);
        }
    public void save(DataOutputStream out) throws IOException{
        super.save(out);
        }
    @Override
    public int classId() { return Values.classIdRefered; }
    public void loadFull(DataInputStream in) throws IOException {
        super.load(in);
        reference = new STLLine();
        reference.load(in);
    }
    public void saveFull(DataOutputStream out) throws IOException{
        super.save(out);
        reference.save(out);
    }
    }
