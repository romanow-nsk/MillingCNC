/*
The MIT License (MIT)

Copyright (c) 2014 CCHall (aka Cyanobacterium aka cyanobacteruim)

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/


package epos.slm3d.stl;

import epos.slm3d.io.I_File;
import epos.slm3d.utils.UNIException;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import static epos.slm3d.utils.Utils.EqualAbout;


/**
 * This object represents a triangle in 3D space.
 * @author CCHall
 */
public class STLTriangle implements I_File{
	private  STLPoint3D[] vertices;
	private STLPoint3D normal;
	/** Только для загрузки из потоков */
	public STLTriangle(){}
	public STLTriangle(STLPoint3D v1, STLPoint3D v2, STLPoint3D v3){
		vertices = new STLPoint3D[3];
		vertices[0] = v1;
		vertices[1] = v2;
		vertices[2] = v3;
		setNormal();
		}
	private void setNormal(){
		STLPoint3D edge1 = vertices[1].sub(vertices[0]);
		STLPoint3D edge2 = vertices[2].sub(vertices[0]);
		normal = STLPoint3D.cross(edge1, edge2).normalize();
		}

	public void rotate(int mode, MyAngle angle){
		vertices[0].rotate(mode, angle);
		vertices[1].rotate(mode, angle);
		vertices[2].rotate(mode, angle);
		}
	public boolean isCrossZ(double zz){
		if ((vertices[0].z()-zz)*(vertices[1].z()-zz)<=0) return true;
		if ((vertices[1].z()-zz)*(vertices[2].z()-zz)<=0) return true;
		if ((vertices[0].z()-zz)*(vertices[2].z()-zz)<=0) return true;
		return false;
		}
	public int equalsCount(double z){
		int count=0;
		if (vertices[0].z()==z) count++;
		if (vertices[1].z()==z) count++;
		if (vertices[2].z()==z) count++;
		return count;
		}
	public int equalsAboutCount(double z){
		int count=0;
		if (EqualAbout(vertices[0].z(),z)) count++;
		if (EqualAbout(vertices[1].z(),z)) count++;
		if (EqualAbout(vertices[2].z(),z)) count++;
		return count;
		}
	private STLLine createline(double x0, double y0, double x1, double y1) throws UNIException {
		STLLine line = new STLLine();
		line.add(new STLPoint2D(x0,y0));
		line.add(new STLPoint2D(x1,y1));
		return line;
		}
	/** Вектор линий пересечения треугольника по высоте z*/
	public ArrayList<STLLine> getCrossLine(double zz) throws UNIException{
		if (!isCrossZ(zz))
			return null;
		int count=0;
		ArrayList<STLLine> out=new ArrayList<>();
		if (EqualAbout(vertices[0].z(),zz) && EqualAbout(vertices[1].z(),zz)){		// Две одинаковые по высоте
			out.add(createline(vertices[0].x(),vertices[0].y(),vertices[1].x(),vertices[1].y()));
			count++;
			}
		if (EqualAbout(vertices[0].z(),zz) && EqualAbout(vertices[2].z(),zz)){		// Две одинаковые по высоте
			out.add(createline(vertices[0].x(),vertices[0].y(),vertices[2].x(),vertices[2].y()));
			count++;
			}
		if (EqualAbout(vertices[1].z(),zz) && EqualAbout(vertices[2].z(),zz)){		// Две одинаковые по высоте
			out.add(createline(vertices[1].x(),vertices[1].y(),vertices[2].x(),vertices[2].y()));
			count++;
			}										// Пересекают
		if (count==3)
			return new ArrayList<>();		// Плоские треугольники тут не рассматриваются
		if (count==1 || count==2)
			return out;
		STLLine two = new STLLine();
		int idx=0;
		STLPoint2D pp;
		double x,y;									// x = x0+(x1-x0)(z-z0)/(z1-z0)
		if ((vertices[0].z()-zz)*(vertices[1].z()-zz)<=0){
			x = vertices[0].x()+(vertices[1].x()-vertices[0].x())*(zz-vertices[0].z())/(vertices[1].z()-vertices[0].z());
			y = vertices[0].y()+(vertices[1].y()-vertices[0].y())*(zz-vertices[0].z())/(vertices[1].z()-vertices[0].z());
			pp = new STLPoint2D(x,y);
			two.add(pp);
			}
		if ((vertices[0].z()-zz)*(vertices[2].z()-zz)<=0){
			x = vertices[0].x()+(vertices[2].x()-vertices[0].x())*(zz-vertices[0].z())/(vertices[2].z()-vertices[0].z());
			y = vertices[0].y()+(vertices[2].y()-vertices[0].y())*(zz-vertices[0].z())/(vertices[2].z()-vertices[0].z());
			pp = new STLPoint2D(x,y);
			two.add(pp);
			}
		if ((vertices[1].z()-zz)*(vertices[2].z()-zz)<=0){
			x = vertices[1].x()+(vertices[2].x()-vertices[1].x())*(zz-vertices[1].z())/(vertices[2].z()-vertices[1].z());
			y = vertices[1].y()+(vertices[2].y()-vertices[1].y())*(zz-vertices[1].z())/(vertices[2].z()-vertices[1].z());
			pp = new STLPoint2D(x,y);
			two.add(pp);
			/*
			if (!two.valid())
				two.add(pp);
			else
				{
				if (!pp.equals(two.one()) && !pp.equals(two.two())){
					System.out.println("Третья точка в пропорции");
					System.out.println(two.one().dump());
					System.out.println(two.two().dump());
					System.out.println(pp.dump());
					}
				}
				*/
			}
		out.add(two);
		if (!two.valid())
			return null;
		return out;
		}

	/**
	 * Moves the triangle in the X,Y,Z direction
	 * @param translation A vector of the delta for each coordinate.
	 */
	public void translate(STLPoint3D translation){
		for(int i = 0; i < vertices.length; i++){
			vertices[i] = vertices[i].add(translation);
		}
	}
	/**
	 * @see java.lang.Object#toString() 
	 * @return A string that provides some information about this triangle
	 */
	@Override public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("Triangle[");
		for(STLPoint3D v : vertices){
			sb.append(v.toString());
		}
		sb.append("]");
		return sb.toString();
	}
	/**
	 * Gets the vertices at the corners of this triangle
	 * @return An array of vertices
	 */
	public STLPoint3D[] getVertices(){
		return vertices;
	}
	/**
	 * Gets the normal vector
	 * @return A vector pointing in a direction perpendicular to the surface of 
	 * the triangle.
	 */
	public STLPoint3D getNormal(){
		return normal;
	}
	
	/**
	 * @see java.lang.Object#equals(java.lang.Object) 
	 * @param obj Object to test equality
	 * @return True if the other object is a triangle whose verticese are the 
	 * same as this one.
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final STLTriangle other = (STLTriangle) obj;
		if (!Arrays.deepEquals(this.vertices, other.vertices)) {
			return false;
		}
		return true;
	}
	/**
	 * @see java.lang.Object#hashCode() 
	 * @return A hashCode for this triangle
	 */
	@Override
	public int hashCode() {
		int hash = 7;
		hash = 67 * hash + Arrays.deepHashCode(this.vertices);
		return hash;
	}

	@Override
	public void load(DataInputStream in) throws IOException {
		vertices = new STLPoint3D[3];
		STLPoint3D v1 = new STLPoint3D();
		v1.load(in);
		vertices[0] = v1;
		STLPoint3D v2 = new STLPoint3D();
		v2.load(in);
		vertices[1] = v2;
		STLPoint3D v3 = new STLPoint3D();
		v3.load(in);
		vertices[2] = v3;
		setNormal();
		}

	@Override
	public void save(DataOutputStream out) throws IOException {
		vertices[0].save(out);
		vertices[1].save(out);
		vertices[2].save(out);
		}
	public void saveFloat(DataOutputStream out) throws IOException {
		getNormal().saveFloat(out);
		vertices[0].saveFloat(out);
		vertices[1].saveFloat(out);
		vertices[2].saveFloat(out);
		out.writeShort(0);
		}
    public void invertY(){
        vertices[0].invertY();
        vertices[1].invertY();
        vertices[2].invertY();
        }

	}
