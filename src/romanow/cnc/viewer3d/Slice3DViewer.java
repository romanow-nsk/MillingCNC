package romanow.cnc.viewer3d;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;

import com.sun.j3d.utils.universe.SimpleUniverse;
import romanow.cnc.slicer.SliceLayer;

public class Slice3DViewer extends JFrame implements WindowListener {
    Thread thread=null;
	PCanvas3D canvas;
	PModel model;
	SimpleUniverse universe;
	JCheckBoxMenuItem mnstrp;
	double Scale0 = 0.1;

	public Slice3DViewer(SliceLayer src, boolean onlyError) throws HeadlessException {
		super("Slice Layer Viewer");
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		//this.setBounds(150,150,800,640);
		setPreferredSize(new Dimension(600, 600));
		JMenuBar mbar = new JMenuBar();
		JMenu mtools = new JMenu("");
		mnstrp = new JCheckBoxMenuItem("Regen Normals/Connect strips",true);
		mnstrp.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
			}
		});
		mtools.add(mnstrp);
		mbar.add(mtools);
		setJMenuBar(mbar);
		GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
		canvas = new PCanvas3D(config);
		getContentPane().add(canvas, BorderLayout.CENTER);
		universe = new SimpleUniverse(canvas);
		canvas.initcanvas(universe);
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
		addWindowListener(this);
		if(model != null)
			model.cleanup();
		model = new PModel();
		model.setBnormstrip(mnstrp.isSelected());
        thread = new Thread(()->{       // выполнить в потоке вне GUI
            model.addLayer(src,onlyError);
    		canvas.rendermodel(model, universe);
            });
        thread.start();
		//---- вернуть обратно ----------------------
		//	canvas.homeview(universe);
		}

	@Override
	public void windowOpened(WindowEvent e) {
	}

	@Override
	public void windowClosing(WindowEvent e) {
		universe.removeAllLocales();
		universe.cleanup();
        if (thread!=null){          // Тупо обломить поток
            thread.stop();
            thread=null;
            }
        dispose();
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
