package romanow.cnc.viewer3d;

import org.jogamp.java3d.*;
import org.jogamp.java3d.utils.geometry.GeometryInfo;
import org.jogamp.java3d.utils.geometry.NormalGenerator;
import org.jogamp.java3d.utils.geometry.Stripifier;
import org.jogamp.vecmath.Color4f;
import org.jogamp.vecmath.Point3f;
import org.jogamp.vecmath.Vector3f;
import org.jogamp.vecmath.Color3f;

import romanow.cnc.Values;
import romanow.cnc.slicer.I_ErrorDraw;
import romanow.cnc.slicer.SliceData;
import romanow.cnc.slicer.SliceError;
import romanow.cnc.slicer.SliceLayer;
import romanow.cnc.stl.*;


import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PModel171 extends BranchGroup {
	private boolean bnormstrip = true;
	public PModel171() {
		init();
        }
	public PModel171(String name) {
		setName(name);
		setCapability(BranchGroup.ALLOW_DETACH);
        }		
	public boolean isBnormstrip() {
		return bnormstrip;
        }
	public void setBnormstrip(boolean bnormstrip) {
		this.bnormstrip = bnormstrip;
        }
	private void init() {
		setName("MODEL");
		setCapability(BranchGroup.ALLOW_DETACH);
        }
	public Appearance setCommon(GeometryInfo gi, float kTranps){
		if (bnormstrip)
			try {
				NormalGenerator ng = new NormalGenerator();
				ng.generateNormals(gi);
				Stripifier st = new Stripifier();
				st.stripify(gi);
			} catch (Exception e) {
				String msg = new String("unable to generate normals or stripify:");
				msg = msg.concat(e.getMessage());
				Logger.getLogger(PModel171.class.getName()).log(Level.WARNING, msg);
			};

		Appearance appearance = new Appearance();
		float f3[]=new float[3];
		Color3f color = new Color3f(Color.yellow.getColorComponents(f3));
		Color3f black = new Color3f(0.0f, 0.0f, 0.0f);
		Color3f white = new Color3f(1.0f, 1.0f, 1.0f);
		Texture texture = new Texture2D();
		TextureAttributes texAttr = new TextureAttributes();
		texAttr.setTextureMode(TextureAttributes.MODULATE);
		texture.setBoundaryModeS(Texture.WRAP);
		texture.setBoundaryModeT(Texture.WRAP);
		texture.setBoundaryColor(new Color4f(0.0f, 1.0f, 0.0f, 0.5f));
		Material mat = new Material(color, black, color, white, 10f);
		appearance.setTextureAttributes(texAttr);
		appearance.setMaterial(mat);
		appearance.setTexture(texture);
		if (kTranps!=0)
			appearance.setTransparencyAttributes(new TransparencyAttributes(TransparencyAttributes.SCREEN_DOOR,kTranps));
		return appearance;
        }
    
	public Shape3D addTriangles(List<STLTriangle> triangles, float kTransp) {
		bnormstrip = true;
		float f3[]=new float[3];
		Color4f color = new Color4f(Color.yellow.getColorComponents(f3));
		int ntri = triangles.size();
		TriangleArray arr = new TriangleArray(ntri*3,GeometryArray.COORDINATES | GeometryArray.COLOR_4);
		for(int i=0;i<ntri*3;i++)
			arr.setColor(i,color);
		Vector3f normarray[] = new Vector3f[ntri];
		int i = 0;
		for (STLTriangle t : triangles) {
			STLPoint3D v = t.getNormal();
			normarray[i] = new Vector3f((float) v.x(), (float) v.y(), (float) v.z());
			STLPoint3D[] vertex = t.getVertices();
			for (int j = 0; j < 3; j++) {
				arr.setCoordinate(i*3+j,new Point3f((float) vertex[j].x(),(float) vertex[j].y(),(float) vertex[j].z()));
                }
			i++;
            }
		GeometryInfo gi = new GeometryInfo(arr);
		gi.setNormals(normarray);
		Appearance appearance = setCommon(gi,kTransp);
		Shape3D shape = new Shape3D();
		shape.setGeometry(gi.getGeometryArray());
		shape.setAppearance(appearance);
		addChild(shape);
        return shape;
		//scene.addNamedObject(objectName, shape);
        }

	private void addLines(ArrayList<STLLine> lines, float z, Appearance colorApperance){
		for (STLLine line : lines) {
			addLine(line,z,colorApperance);
			}
		}
	public static Appearance colorAppearance(Color color){
		Appearance appearanceGreen = new Appearance();
		ColoringAttributes coloringAttributesGreen = new ColoringAttributes();
		float f3[]=new float[3];
		coloringAttributesGreen.setColor(new Color3f(color.getColorComponents(f3)));
		appearanceGreen.setColoringAttributes(coloringAttributesGreen);
		return appearanceGreen;
		}
	public void addPoints(ArrayList<STLReferedPoint> points, float z, Appearance colorApperance) {
		int sz=points.size();
		for(int i=0;i<sz-1;i++)
			addLine(new STLLine(points.get(i),points.get(i+1)),z,colorApperance);
		for(int i=0;i<sz;i++)
			addLine(points.get(i).reference(),z,colorApperance);
		}
    public void addLine(STLLine line, float z, Appearance colorApperance){
		LineArray lineX = new LineArray(2, LineArray.COORDINATES);
		lineX.setCoordinate(0, new Point3f((float)line.one().x(),(float)line.one().y(),z));
		lineX.setCoordinate(1, new Point3f((float)line.two().x(),(float)line.two().y(),z));
		Shape3D shapeLine = new Shape3D(lineX, colorApperance);
		addChild(shapeLine);
		}

	public void setShape(){
		GeometryInfo gi = new GeometryInfo(GeometryInfo.TRIANGLE_ARRAY);
		Appearance appearance = setCommon(gi,0);
		Shape3D shape = new Shape3D();
		shape.setAppearance(appearance);
		addChild(shape);
        }
    public void addSliceData(SliceData data, boolean onlyErrors) {
    	for (int i=0;i<data.size();i++)
  			addLayer0(data.get(i),onlyErrors);
        setShape();
		}
	public void addLayer(SliceLayer layer, boolean onlyErrors) {
        addLayer0(layer, onlyErrors);
        setShape();
        }
	private Color colors[] = {Color.darkGray,Color.green,Color.blue, Color.red};
	public void addLoop(SliceLayer layer){
		for(STLLoop loop : layer.loops()) {
			Appearance app = colorAppearance(colors[loop.loopLineMode()]);
			addLines(loop.lines(),(float) layer.z(),app);
			}
		}
	public void addSource(SliceLayer layer){
    	Appearance app = colorAppearance(Values.ColorDarkRed);
		addLines(layer.lines().lines(),(float) layer.z(),app);
		}
	public void addLoops(SliceData data){
		for (int i=0;i<data.size();i++)
			addLoop(data.get(i));
		}
	private void addLayer0(SliceLayer layer, boolean onlyErrors) {
		bnormstrip = false;
		Appearance app = colorAppearance(Color.black);
		if (onlyErrors){
			if (layer.errorList().size()!=0){
				addLines(layer.lines().lines(),(float) layer.z(),app);
				for(SliceError err : layer.errorList()){
					err.drawAll(new I_ErrorDraw() {
						@Override
						public void onLine(int mode, STLLine line) {
							addLine(line,(float) layer.z(),app);
							}
						@Override
						public void onReferedPoint(int mode, STLReferedPoint point) {
							addLine(point.reference(),(float) layer.z(),app);
							}
						});
					}
				}
			}
			else{
				addLines(layer.lines().lines(),(float) layer.z(),app);
				addLines(layer.segments().lines(),(float) layer.z(),app);
			}
        }
	
	public void cleanup() {
		detach();
		removeAllChildren();
	}

}
