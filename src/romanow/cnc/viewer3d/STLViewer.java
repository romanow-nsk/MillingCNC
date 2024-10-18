package romanow.cnc.viewer3d;


import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import com.sun.j3d.utils.universe.SimpleUniverse;
import romanow.cnc.view.BaseFrame;
import romanow.cnc.utils.Events;
import romanow.cnc.m3d.ViewAdapter;
import romanow.cnc.settings.WorkSpace;

public class STLViewer extends BaseFrame implements WindowListener {
    Thread thread=null;
	PCanvas3D canvas;
	PModel model;
	SimpleUniverse universe;
	double Scale0 = 0.1;
    
    @Override
    public synchronized void onEvent(int code, boolean on, int value, String name) {
        if (code==Events.Rotate){
            refresh();
            }
        }
    
    public void refresh(){
		if(model != null)
			model.cleanup();
		model = new PModel();
		model.setBnormstrip(true);
        //thread = new Thread(()->{       // выполнить в потоке вне GUI
            model.addTriangles(WorkSpace.ws().model().triangles(),0);
    		canvas.rendermodel(model, universe);
        //    });
        //thread.start();
        }

	@Override
	public void shutDown() {

	}

	public STLViewer(ViewAdapter view) throws HeadlessException {
        if (!tryToStart()) return;
		setTitle("Просмотр STL");
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		//this.setBounds(150,150,800,640);
		setPreferredSize(new Dimension(600, 600));
		GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
		canvas = new PCanvas3D(config);
		getContentPane().add(canvas, BorderLayout.CENTER);
		universe = new SimpleUniverse(canvas);
		canvas.initcanvas(universe);
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
		addWindowListener(this);
		//---- вернуть обратно ----------------------
		//	canvas.homeview(universe);
        refresh();
		}

	@Override
	public void windowOpened(WindowEvent e) {
	}

	@Override
	public void windowClosing(WindowEvent e) {
		universe.removeAllLocales();
		universe.cleanup();
        //if (thread!=null){          // Тупо обломить поток
        //    thread.stop();
        //    thread=null;
        //    }
        onClose();
	}

	@Override
	public void windowClosed(WindowEvent e) {
	}

	@Override
	public void windowIconified(WindowEvent e) {

	}

	@Override
	public void windowDeiconified(WindowEvent e) {

	}

	@Override
	public void windowActivated(WindowEvent e) {

	}

	@Override
	public void windowDeactivated(WindowEvent e) {

	}

}
